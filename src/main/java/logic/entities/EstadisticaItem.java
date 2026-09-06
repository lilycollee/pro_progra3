package logic.entities;

public class EstadisticaItem {
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