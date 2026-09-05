package logic.entities;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlAccessorType(XmlAccessType.FIELD)
public class DetalleReserva {
    private CategoriaRecurso categoriaSolicitada;
    private Recurso recursoAsignado;

    public DetalleReserva(){}
    public DetalleReserva(CategoriaRecurso categoriaSolicitada){
        this.categoriaSolicitada = categoriaSolicitada;
    }

    public CategoriaRecurso getCategoriaSolicitada() {
        return categoriaSolicitada;
    }
    public Recurso getRecursoAsignado() {
        return recursoAsignado;
    }
    public void setRecursoAsignado(Recurso recursoAsignado) {
        this.recursoAsignado = recursoAsignado;
    }

    public String toString(){
        String recurso = recursoAsignado.getId();
        return recurso;
    }
}
