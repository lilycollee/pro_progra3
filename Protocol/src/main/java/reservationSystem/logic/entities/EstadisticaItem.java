package reservationSystem.logic.entities;

import java.io.Serializable;

public class EstadisticaItem implements Serializable {
    private String etiqueta;
    private long cantidad;
    public EstadisticaItem() {}
    public EstadisticaItem(String etiqueta, long cantidad) {
        this.etiqueta = etiqueta;
        this.cantidad = cantidad;
    }
    public String getEtiqueta() {
        return etiqueta;
    }
    public long getCantidad() {
        return cantidad;
    }
}