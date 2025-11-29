package com.backend.huertohogar.dto;

import com.backend.huertohogar.model.Orden;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class OrdenResponseDTO {

    private Integer id;
    private String numeroOrden;
    private LocalDate fecha;
    private String estado;
    private Integer montoTotal;
    private Integer costoEnvio;
    private String comentario;

    // Snapshot fields
    private String nombreClienteSnapshot;
    private String emailClienteSnapshot;
    private String direccionEnvio;
    private String regionEnvio;
    private String comunaEnvio;
    private String telefonoContacto;

    private List<DetalleOrdenResponseDTO> detalles;

    public OrdenResponseDTO(Orden orden) {
        this.id = orden.getId();
        this.numeroOrden = orden.getNumeroOrden();
        this.fecha = orden.getFecha();
        this.estado = orden.getEstado().getNombre();
        this.montoTotal = orden.getMontoTotal();
        this.costoEnvio = orden.getCostoEnvio();
        this.comentario = orden.getComentario();

        // Map snapshot fields
        this.nombreClienteSnapshot = orden.getNombreClienteSnapshot();
        this.emailClienteSnapshot = orden.getEmailClienteSnapshot();
        this.direccionEnvio = orden.getDireccionEnvio();
        this.regionEnvio = orden.getRegionEnvio();
        this.comunaEnvio = orden.getComunaEnvio();
        this.telefonoContacto = orden.getTelefonoContacto();

        this.detalles = orden.getDetalles().stream()
                .map(DetalleOrdenResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(Integer montoTotal) {
        this.montoTotal = montoTotal;
    }

    public Integer getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(Integer costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getNombreClienteSnapshot() {
        return nombreClienteSnapshot;
    }

    public void setNombreClienteSnapshot(String nombreClienteSnapshot) {
        this.nombreClienteSnapshot = nombreClienteSnapshot;
    }

    public String getEmailClienteSnapshot() {
        return emailClienteSnapshot;
    }

    public void setEmailClienteSnapshot(String emailClienteSnapshot) {
        this.emailClienteSnapshot = emailClienteSnapshot;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public String getRegionEnvio() {
        return regionEnvio;
    }

    public void setRegionEnvio(String regionEnvio) {
        this.regionEnvio = regionEnvio;
    }

    public String getComunaEnvio() {
        return comunaEnvio;
    }

    public void setComunaEnvio(String comunaEnvio) {
        this.comunaEnvio = comunaEnvio;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public List<DetalleOrdenResponseDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleOrdenResponseDTO> detalles) {
        this.detalles = detalles;
    }
}
