package com.backend.huertohogar.service;

import com.backend.huertohogar.dto.ProductoRequestDTO;
import com.backend.huertohogar.dto.ProductoResponseDTO;
import com.backend.huertohogar.exception.ResourceNotFoundException;
import com.backend.huertohogar.exception.ValidationException;
import com.backend.huertohogar.model.Categoria;
import com.backend.huertohogar.model.Producto;
import com.backend.huertohogar.repository.CategoriaRepository;
import com.backend.huertohogar.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Autowired
    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<ProductoResponseDTO> getAllProductos() {
        // Filtrar solo los activos si se desea, o todos.
        // Para este caso, mostraremos todos pero el borrado es lógico.
        // Si se quisiera solo activos: return productoRepository.findByActivoTrue()...
        return productoRepository.findAll().stream()
                .filter(p -> p.getActivo() == null || p.getActivo()) // Asumiendo null como activo por compatibilidad
                .map(ProductoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductoResponseDTO> getProductoById(Integer id) {
        return productoRepository.findById(id)
                .filter(p -> p.getActivo() == null || p.getActivo())
                .map(ProductoResponseDTO::new);
    }

    @Override
    public ProductoResponseDTO saveProducto(ProductoRequestDTO productoDTO) {
        // Validaciones
        if (productoDTO.getNombre() == null || productoDTO.getNombre().trim().isEmpty()) {
            throw new ValidationException("El nombre del producto es obligatorio y no puede estar vacío");
        }
        if (productoDTO.getCategoria() == null || productoDTO.getCategoria().trim().isEmpty()) {
            throw new ValidationException("La categoría es obligatoria y no puede estar vacía");
        }
        if (productoDTO.getPrecio() == null || productoDTO.getPrecio() <= 0) {
            throw new ValidationException("El precio es obligatorio y debe ser mayor a 0");
        }
        if (productoDTO.getStock() == null || productoDTO.getStock() < 0) {
            throw new ValidationException("El stock es obligatorio y no puede ser negativo");
        }

        // Buscar categoría
        Categoria categoria = categoriaRepository.findByNombre(productoDTO.getCategoria())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Categoría no encontrada: " + productoDTO.getCategoria()));

        // Generar código del producto basado en el prefijo de la categoría
        String codigoProducto = generarCodigoProducto(categoria);
        
        // Crear el nombre completo con el formato: PREFIJO### - Nombre
        String nombreCompleto = codigoProducto + " - " + productoDTO.getNombre();
        
        // Validación de duplicados con el nombre completo
        if (productoRepository.existsByNombre(nombreCompleto)) {
            throw new ValidationException("Ya existe un producto con el nombre: " + nombreCompleto);
        }

        // Crear producto
        Producto producto = new Producto();
        producto.setNombre(nombreCompleto);
        producto.setCategoria(categoria);
        producto.setPrecio(productoDTO.getPrecio());
        producto.setStock(productoDTO.getStock());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setImagen(productoDTO.getImagen());
        producto.setActivo(true);

        Producto savedProducto = productoRepository.save(producto);
        return new ProductoResponseDTO(savedProducto);
    }

    /**
     * Genera un código de producto único basado en el prefijo de la categoría.
     * Formato: PREFIJO### (ejemplo: FR001, VR002, PO003)
     * 
     * @param categoria La categoría del producto
     * @return Código único del producto
     */
    private String generarCodigoProducto(Categoria categoria) {
        String prefijo = categoria.getPrefijo();
        
        // Obtener todos los productos activos de esta categoría
        List<Producto> productosCategoria = productoRepository.findAll().stream()
                .filter(p -> (p.getActivo() == null || p.getActivo()) && 
                             p.getCategoria().getId().equals(categoria.getId()))
                .collect(Collectors.toList());
        
        // Encontrar el número más alto usado en esta categoría
        int maxNumero = 0;
        for (Producto p : productosCategoria) {
            String nombre = p.getNombre();
            // Formato esperado: "PREFIJO### - Nombre"
            if (nombre.startsWith(prefijo)) {
                try {
                    String parteNumero = nombre.substring(prefijo.length(), nombre.indexOf(" - "));
                    int numero = Integer.parseInt(parteNumero);
                    if (numero > maxNumero) {
                        maxNumero = numero;
                    }
                } catch (Exception e) {
                    // Si hay algún error al parsear, continuar con el siguiente
                    continue;
                }
            }
        }
        
        // Generar el nuevo código incrementando en 1
        int nuevoNumero = maxNumero + 1;
        return String.format("%s%03d", prefijo, nuevoNumero);
    }

    @Override
    public ProductoResponseDTO updateProducto(Integer id, ProductoRequestDTO productoDTO) {
        Producto existingProducto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Validaciones
        if (productoDTO.getNombre() == null || productoDTO.getNombre().trim().isEmpty()) {
            throw new ValidationException("El nombre del producto es obligatorio y no puede estar vacío");
        }
        if (productoDTO.getCategoria() == null || productoDTO.getCategoria().trim().isEmpty()) {
            throw new ValidationException("La categoría es obligatoria y no puede estar vacía");
        }
        if (productoDTO.getPrecio() == null || productoDTO.getPrecio() <= 0) {
            throw new ValidationException("El precio es obligatorio y debe ser mayor a 0");
        }
        if (productoDTO.getStock() == null || productoDTO.getStock() < 0) {
            throw new ValidationException("El stock es obligatorio y no puede ser negativo");
        }

        // Buscar categoría
        Categoria categoria = categoriaRepository.findByNombre(productoDTO.getCategoria())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Categoría no encontrada: " + productoDTO.getCategoria()));

        // Extraer el código del nombre existente
        String codigoExistente = "";
        String nombreExistente = existingProducto.getNombre();
        if (nombreExistente.contains(" - ")) {
            codigoExistente = nombreExistente.substring(0, nombreExistente.indexOf(" - "));
        }

        // Limpiar el nombre recibido del frontend, removiendo cualquier prefijo si existe
        String nombreLimpio = productoDTO.getNombre();
        if (nombreLimpio.contains(" - ")) {
            // Si el nombre tiene formato "PREFIJO### - Nombre", extraer solo el nombre
            nombreLimpio = nombreLimpio.substring(nombreLimpio.indexOf(" - ") + 3);
        }

        // Si la categoría cambió, generar nuevo código
        String nombreCompleto;
        if (!existingProducto.getCategoria().getId().equals(categoria.getId())) {
            String nuevoCodigo = generarCodigoProducto(categoria);
            nombreCompleto = nuevoCodigo + " - " + nombreLimpio;
        } else {
            // Mantener el código existente si la categoría no cambió
            nombreCompleto = codigoExistente + " - " + nombreLimpio;
        }

        // Validación de duplicados (excluyendo el actual)
        if (productoRepository.existsByNombreAndIdNot(nombreCompleto, id)) {
            throw new ValidationException("Ya existe otro producto con el nombre: " + nombreCompleto);
        }

        // Actualizar producto
        existingProducto.setNombre(nombreCompleto);
        existingProducto.setCategoria(categoria);
        existingProducto.setPrecio(productoDTO.getPrecio());
        existingProducto.setStock(productoDTO.getStock());
        existingProducto.setDescripcion(productoDTO.getDescripcion());
        existingProducto.setImagen(productoDTO.getImagen());
        // No cambiamos el estado activo aquí, eso es en delete o un endpoint específico

        Producto updatedProducto = productoRepository.save(existingProducto);
        return new ProductoResponseDTO(updatedProducto);
    }

    @Override
    public Optional<String> getProductNameById(Integer id) {
        return productoRepository.findById(id)
                .map(Producto::getNombre);
    }

    @Override
    public void deleteProducto(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Borrado lógico
        producto.setActivo(false);
        productoRepository.save(producto);
    }
}

