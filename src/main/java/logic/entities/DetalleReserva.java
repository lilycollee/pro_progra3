package logic.entities;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlAccessorType(XmlAccessType.FIELD)
public class DetalleReserva {
    private String idCategoriaSolicitada;
    private String idRecursoAsignado;

    public DetalleReserva() {}
    public DetalleReserva(String idCategoriaSolicitada) {
        this.idCategoriaSolicitada = idCategoriaSolicitada;
    }
    public String getIdCategoriaSolicitada() {
        return idCategoriaSolicitada;
    }
    public String getIdRecursoAsignado() {
        return idRecursoAsignado;
    }
    public void setIdRecursoAsignado(String idRecursoAsignado) {
        this.idRecursoAsignado = idRecursoAsignado;
    }
    @Override
    public String toString() {
        return idRecursoAsignado;
    }
}
