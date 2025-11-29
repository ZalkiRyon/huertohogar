package com.backend.huertohogar.dto;

public class CostoEnvioDTO {
    private Integer costoEnvio;

    public CostoEnvioDTO() {
    }

    public CostoEnvioDTO(Integer costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public Integer getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(Integer costoEnvio) {
        this.costoEnvio = costoEnvio;
    }
}
