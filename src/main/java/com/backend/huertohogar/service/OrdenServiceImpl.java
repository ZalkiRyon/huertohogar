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
        orden.setNumeroOrden(UUID.randomUUID().toString()); // Generar número único
        orden.setFecha(LocalDate.now());
        orden.setComentario(ordenDTO.getComentario());
        orden.setUsuario(cliente);

        // 3. Asignar Estado Inicial (Pendiente)
        Estado estadoPendiente = estadoRepository.findById(2) // Asumiendo ID 2 es Pendiente
                .orElseThrow(() -> new ResourceNotFoundException("Estado 'Pendiente' no encontrado"));
        orden.setEstado(estadoPendiente);

        // 4. Snapshot de Datos del Cliente
        orden.setNombreClienteSnapshot(cliente.getNombre() + " " + cliente.getApellido());
        orden.setEmailClienteSnapshot(cliente.getEmail());
        orden.setDireccionEnvio(cliente.getDireccion());
        orden.setRegionEnvio(cliente.getRegion());
        orden.setComunaEnvio(cliente.getComuna());
        orden.setTelefonoContacto(cliente.getTelefono());

        // 5. Procesar Detalles y Calcular Totales
        List<DetalleOrden> detalles = new ArrayList<>();
        int montoTotal = 0;

        for (DetalleOrdenRequestDTO detalleDTO : ordenDTO.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con ID: " + detalleDTO.getProductoId()));

            // Validar Stock
            if (producto.getStock() < detalleDTO.getCantidad()) {
                throw new ValidationException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            // Descontar Stock
            producto.setStock(producto.getStock() - detalleDTO.getCantidad());
            productoRepository.save(producto);

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
        orden.setMontoTotal(montoTotal);

        // Costo de envío fijo por ahora (o lógica personalizada)
        orden.setCostoEnvio(0);

        // 6. Guardar Orden
        Orden savedOrden = ordenRepository.save(orden);
        return new OrdenResponseDTO(savedOrden);
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
}
