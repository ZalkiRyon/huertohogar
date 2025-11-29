package com.backend.huertohogar.dto;

import com.backend.huertohogar.model.DetalleOrden;

public class DetalleOrdenResponseDTO {

    private Integer id;
    private String nombreProductoSnapshot;
    private Integer precioUnitarioSnapshot;
    private Integer cantidad;
    private Integer subtotal;
    private String imagen;

    public DetalleOrdenResponseDTO(DetalleOrden detalle) {
        this.id = detalle.getId();
        this.nombreProductoSnapshot = detalle.getNombreProductoSnapshot();
        this.precioUnitarioSnapshot = detalle.getPrecioUnitarioSnapshot();
        this.cantidad = detalle.getCantidad();
        this.subtotal = detalle.getSubtotal();
        // Extraer imagen del producto si existe
        if (detalle.getProducto() != null) {
            this.imagen = detalle.getProducto().getImagen();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreProductoSnapshot() {
        return nombreProductoSnapshot;
    }

    public void setNombreProductoSnapshot(String nombreProductoSnapshot) {
        this.nombreProductoSnapshot = nombreProductoSnapshot;
    }

    public Integer getPrecioUnitarioSnapshot() {
        return precioUnitarioSnapshot;
    }

    public void setPrecioUnitarioSnapshot(Integer precioUnitarioSnapshot) {
        this.precioUnitarioSnapshot = precioUnitarioSnapshot;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Integer subtotal) {
        this.subtotal = subtotal;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
