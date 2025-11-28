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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrden(@PathVariable Integer id) {
        ordenService.deleteOrden(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
