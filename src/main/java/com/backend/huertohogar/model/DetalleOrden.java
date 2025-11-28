package com.backend.huertohogar.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalles_orden")
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer cantidad;

    // Snapshot fields
    @Column(name = "nombre_producto_snapshot", nullable = false)
    private String nombreProductoSnapshot;

    @Column(name = "precio_unitario_snapshot", nullable = false)
    private Integer precioUnitarioSnapshot;

    @Column(nullable = false)
    private Integer subtotal;

    @ManyToOne
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    public DetalleOrden() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
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

    public Integer getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Integer subtotal) {
        this.subtotal = subtotal;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}
