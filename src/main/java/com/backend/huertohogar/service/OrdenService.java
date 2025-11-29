package com.backend.huertohogar.service;

import com.backend.huertohogar.dto.OrdenRequestDTO;
import com.backend.huertohogar.dto.OrdenResponseDTO;
import java.util.List;
import java.util.Optional;

public interface OrdenService {
    List<OrdenResponseDTO> getAllOrdenes();

    Optional<OrdenResponseDTO> getOrdenById(Integer id);

    OrdenResponseDTO createOrden(OrdenRequestDTO ordenDTO);

    OrdenResponseDTO updateOrden(Integer id, OrdenRequestDTO ordenDTO);

    void deleteOrden(Integer id);

    int calcularCostoEnvio(String region, String comuna);
}
