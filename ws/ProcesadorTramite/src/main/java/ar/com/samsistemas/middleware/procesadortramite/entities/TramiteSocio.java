package ar.com.samsistemas.middleware.procesadortramite.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tramite")
public class TramiteSocio {

    private String socioContrato;
    private String tipo;
    private String descripcion;

    @XmlElement(name = "socio")
    public String getSocioContrato() {
        return socioContrato;
    }

    public void setSocioContrato(String socioContrato) {
        this.socioContrato = socioContrato;
    }

    @XmlElement
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @XmlElement
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
