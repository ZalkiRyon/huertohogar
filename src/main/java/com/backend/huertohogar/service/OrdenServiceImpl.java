package com.backend.huertohogar.service;

import com.backend.huertohogar.dto.DetalleOrdenRequestDTO;
import com.backend.huertohogar.dto.OrdenRequestDTO;
import com.backend.huertohogar.dto.OrdenResponseDTO;
import com.backend.huertohogar.exception.ResourceNotFoundException;
import com.backend.huertohogar.exception.ValidationException;
import com.backend.huertohogar.model.*;
import com.backend.huertohogar.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrdenServiceImpl implements OrdenService {

    private final OrdenRepository ordenRepository;
    private final ProductoRepository productoRepository;
    private final UserRepository userRepository;
    private final EstadoRepository estadoRepository;

    @Autowired
    public OrdenServiceImpl(OrdenRepository ordenRepository,
            ProductoRepository productoRepository,
            UserRepository userRepository,
            EstadoRepository estadoRepository) {
        this.ordenRepository = ordenRepository;
        this.productoRepository = productoRepository;
        this.userRepository = userRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> getAllOrdenes() {
        return ordenRepository.findAll().stream()
                .map(OrdenResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdenResponseDTO> getOrdenById(Integer id) {
        return ordenRepository.findById(id).map(OrdenResponseDTO::new);
    }

    @Override
    @Transactional
    public OrdenResponseDTO createOrden(OrdenRequestDTO ordenDTO) {
        // 1. Validar Cliente
        User cliente = userRepository.findById(ordenDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con ID: " + ordenDTO.getClienteId()));

        // 2. Crear Orden
        Orden orden = new Orden();
        // Generar número de orden consecutivo basado en el último registro
        String numeroOrden = generarNumeroOrden();
        orden.setNumeroOrden(numeroOrden);

        orden.setFecha(LocalDate.now());
        orden.setComentario(ordenDTO.getComentario());
        orden.setUsuario(cliente);

        // 3. Snapshot de Datos del Cliente
        orden.setNombreClienteSnapshot(cliente.getNombre() + " " + cliente.getApellido());
        orden.setEmailClienteSnapshot(cliente.getEmail());
        orden.setDireccionEnvio(cliente.getDireccion());
        orden.setRegionEnvio(cliente.getRegion());
        orden.setComunaEnvio(cliente.getComuna());
        orden.setTelefonoContacto(cliente.getTelefono());

        // 4. Procesar Detalles y Calcular Totales
        List<DetalleOrden> detalles = new ArrayList<>();
        int montoTotal = 0;
        boolean stockInsuficiente = false;

        // Verificar stock primero
        for (DetalleOrdenRequestDTO detalleDTO : ordenDTO.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con ID: " + detalleDTO.getProductoId()));

            if (producto.getStock() < detalleDTO.getCantidad()) {
                stockInsuficiente = true;
            }
        }

        // 5. Asignar Estado
        java.util.Random random = new java.util.Random();
        Estado estadoOrden;
        if (stockInsuficiente) {
            // Buscar estado "Cancelado" (ID 3)
            estadoOrden = estadoRepository.findById(3)
                    .orElseThrow(() -> new ResourceNotFoundException("Estado 'Cancelado' no encontrado."));
        } else {
            // Asignar estado aleatorio entre Enviado (1), Pendiente (2), Procesando (4)
            // Excluyendo Cancelado (3)
            int[] estadosValidos = { 1, 2, 4 };
            int randomEstadoId = estadosValidos[random.nextInt(estadosValidos.length)];
            estadoOrden = estadoRepository.findById(randomEstadoId)
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Estado con ID " + randomEstadoId + " no encontrado"));
        }
        orden.setEstado(estadoOrden);

        // 6. Crear detalles y actualizar stock (solo si no es fallida)
        for (DetalleOrdenRequestDTO detalleDTO : ordenDTO.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con ID: " + detalleDTO.getProductoId()));

            // Descontar Stock solo si la orden NO es fallida
            if (!stockInsuficiente) {
                producto.setStock(producto.getStock() - detalleDTO.getCantidad());
                productoRepository.save(producto);
            }

            // Crear Detalle
            DetalleOrden detalle = new DetalleOrden();
            detalle.setOrden(orden);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());

            // Snapshot de Producto
            detalle.setNombreProductoSnapshot(producto.getNombre());
            detalle.setPrecioUnitarioSnapshot(producto.getPrecio());

            // Calcular Subtotal
            int subtotal = producto.getPrecio() * detalleDTO.getCantidad();
            detalle.setSubtotal(subtotal);

            detalles.add(detalle);
            montoTotal += subtotal;
        }

        orden.setDetalles(detalles);
        
        // 7. Costo de envío aleatorio
        int costoEnvio = generarCostoEnvioAleatorio();
        orden.setCostoEnvio(costoEnvio);
        
        // 8. Calcular monto total = subtotal productos + costo envío
        orden.setMontoTotal(montoTotal + costoEnvio);

        // 9. Guardar Orden
        Orden savedOrden = ordenRepository.save(orden);
        return new OrdenResponseDTO(savedOrden);
    }

    private String generarNumeroOrden() {
        // Obtener el último número de orden
        List<Orden> ordenes = ordenRepository.findAll();
        int ultimoNumero = 1020; // Número inicial basado en tus datos de prueba
        
        for (Orden o : ordenes) {
            String numOrden = o.getNumeroOrden();
            if (numOrden != null && numOrden.startsWith("SO")) {
                try {
                    // Extraer el número después de "SO"
                    String numeroStr = numOrden.replace("SO", "").replace("-", "");
                    int numero = Integer.parseInt(numeroStr);
                    if (numero > ultimoNumero) {
                        ultimoNumero = numero;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar formatos inválidos
                }
            }
        }
        
        // Generar el siguiente número
        return "SO" + (ultimoNumero + 1);
    }

    private int generarCostoEnvioAleatorio() {
        java.util.Random random = new java.util.Random();
        int min = 3000;
        int max = 7000;
        return random.nextInt(max - min + 1) + min;
    }

    @Override
    @Transactional
    public OrdenResponseDTO updateOrden(Integer id, OrdenRequestDTO ordenDTO) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));

        // Actualizar comentario
        if (ordenDTO.getComentario() != null) {
            orden.setComentario(ordenDTO.getComentario());
        }

        return new OrdenResponseDTO(ordenRepository.save(orden));
    }

    @Override
    @Transactional
    public void deleteOrden(Integer id) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));

        // Reponer stock antes de eliminar (solo si el producto aún existe)
        for (DetalleOrden detalle : orden.getDetalles()) {
            Producto producto = detalle.getProducto();
            if (producto != null) {
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
        }

        ordenRepository.delete(orden);
    }

    @Override
    public int calcularCostoEnvio(String region, String comuna) {
        // Genera un costo de envío aleatorio entre 3000 y 7000
        // Puedes personalizar esta lógica según la región/comuna si lo deseas
        return generarCostoEnvioAleatorio();
    }
}
