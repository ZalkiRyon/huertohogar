package com.backend.huertohogar.controller;

import com.backend.huertohogar.dto.OrdenRequestDTO;
import com.backend.huertohogar.dto.OrdenResponseDTO;
import com.backend.huertohogar.service.OrdenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin(origins = "*")
public class OrdenController {

    private final OrdenService ordenService;

    @Autowired
    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @GetMapping
    public ResponseEntity<List<OrdenResponseDTO>> getAllOrdenes() {
        List<OrdenResponseDTO> ordenes = ordenService.getAllOrdenes();
        return new ResponseEntity<>(ordenes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponseDTO> getOrdenById(@PathVariable Integer id) {
        return ordenService.getOrdenById(id)
                .map(orden -> new ResponseEntity<>(orden, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<OrdenResponseDTO> createOrden(@Valid @RequestBody OrdenRequestDTO ordenDTO) {
        OrdenResponseDTO newOrden = ordenService.createOrden(ordenDTO);
        return new ResponseEntity<>(newOrden, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenResponseDTO> updateOrden(@PathVariable Integer id,
            @Valid @RequestBody OrdenRequestDTO ordenDTO) {
        OrdenResponseDTO updatedOrden = ordenService.updateOrden(id, ordenDTO);
        return new ResponseEntity<>(updatedOrden, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrden(@PathVariable Integer id) {
        ordenService.deleteOrden(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
