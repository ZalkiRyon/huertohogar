package com.backend.huertohogar.service;

import com.backend.huertohogar.dto.OrdenRequestDTO;
import com.backend.huertohogar.dto.OrdenResponseDTO;
import java.util.List;
import java.util.Optional;

public interface OrdenService {
    List<OrdenResponseDTO> getAllOrdenes();

    Optional<OrdenResponseDTO> getOrdenById(Integer id);

    void deleteOrden(Integer id);
}
