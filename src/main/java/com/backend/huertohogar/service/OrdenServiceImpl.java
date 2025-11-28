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

    @Autowired
    public OrdenServiceImpl(OrdenRepository ordenRepository,
            ProductoRepository productoRepository) {
        this.ordenRepository = ordenRepository;
        this.productoRepository = productoRepository;
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
