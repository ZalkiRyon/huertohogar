package com.backend.huertohogar.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero_orden", nullable = false, unique = true)
    private String numeroOrden;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "monto_total", nullable = false)
    private Integer montoTotal;

    @Column(name = "costo_envio", nullable = false)
    private Integer costoEnvio;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    // Snapshot fields
    @Column(name = "nombre_cliente_snapshot", nullable = false)
    private String nombreClienteSnapshot;

    @Column(name = "email_cliente_snapshot", nullable = false)
    private String emailClienteSnapshot;

    // Dispatch fields
    @Column(name = "direccion_envio", nullable = false)
    private String direccionEnvio;

    @Column(name = "region_envio", nullable = false)
    private String regionEnvio;

    @Column(name = "comuna_envio", nullable = false)
    private String comunaEnvio;

    @Column(name = "telefono_contacto")
    private String telefonoContacto;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private Estado estado;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleOrden> detalles;

    public Orden() {
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

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public List<DetalleOrden> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleOrden> detalles) {
        this.detalles = detalles;
    }
}
