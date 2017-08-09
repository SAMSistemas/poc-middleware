package ar.com.samsistemas.middleware.procesadortramite.entities;

public class TramiteSocio {

    private String socioContrato;
    private String tipo;
    private String descripcion;

    public String getSocioContrato() {
        return socioContrato;
    }

    public void setSocioContrato(String socioContrato) {
        this.socioContrato = socioContrato;
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
}
