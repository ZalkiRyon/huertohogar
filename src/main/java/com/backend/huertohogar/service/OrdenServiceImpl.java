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
    private final UserRepository userRepository;
    private final ProductoRepository productoRepository;
    private final EstadoRepository estadoRepository;

    @Autowired
    public OrdenServiceImpl(OrdenRepository ordenRepository,
            UserRepository userRepository,
            ProductoRepository productoRepository,
            EstadoRepository estadoRepository) {
        this.ordenRepository = ordenRepository;
        this.userRepository = userRepository;
        this.productoRepository = productoRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    @Transactional
    public OrdenResponseDTO createOrden(OrdenRequestDTO ordenDTO) {
        // 1. Validar Usuario
        User usuario = userRepository.findById(ordenDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con ID: " + ordenDTO.getClienteId()));

        // 2. Obtener Estado Inicial (Pendiente)
        Estado estado = estadoRepository.findByNombre("Pendiente")
                .orElseThrow(
                        () -> new ResourceNotFoundException("Estado 'Pendiente' no encontrado en la base de datos"));

        // 3. Crear Orden
        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setEstado(estado);
        orden.setFecha(LocalDate.now());
        orden.setComentario(ordenDTO.getComentario());
        orden.setNumeroOrden("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // 4. Procesar Detalles y Calcular Total
        List<DetalleOrden> detalles = new ArrayList<>();
        int montoTotal = 0;

        for (DetalleOrdenRequestDTO detalleDTO : ordenDTO.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con ID: " + detalleDTO.getProductoId()));

            if (producto.getStock() < detalleDTO.getCantidad()) {
                throw new ValidationException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            // Descontar stock
            producto.setStock(producto.getStock() - detalleDTO.getCantidad());
            productoRepository.save(producto);

            DetalleOrden detalle = new DetalleOrden();
            detalle.setOrden(orden);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());

            int subtotal = producto.getPrecio() * detalleDTO.getCantidad();
            detalle.setSubtotal(subtotal);

            detalles.add(detalle);
            montoTotal += subtotal;
        }

        orden.setDetalles(detalles);
        orden.setMontoTotal(montoTotal);

        // 5. Guardar Orden (Cascade guardará los detalles)
        Orden savedOrden = ordenRepository.save(orden);

        return new OrdenResponseDTO(savedOrden);
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
}
