package reservationSystem.logic.entities;

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
