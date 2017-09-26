package ar.com.samsistemas.middleware.notificadortramite.entities;

import java.time.LocalDateTime;

public class TramiteSocioDTO {

    private Integer id;
    private String tipo;
    private String descripcion;
    private String estado;
    private LocalDateTime timestamp;
    private Integer id_socio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getId_socio() {
        return id_socio;
    }

    public void setId_socio(Integer id_socio) {
        this.id_socio = id_socio;
    }
}
