package reservationSystem.presentation.estadisticas;

import reservationSystem.logic.entities.EstadisticaItem;
import reservationSystem.presentation.AbstractModel;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    private List<EstadisticaItem> listaRecursos;
    private List<EstadisticaItem> listaActividades;

    public static final String LISTA_RECURSOS = "listaRecursos";
    public static final String LISTA_ACTIVIDADES = "listaActividades";

    public Model() {
        listaRecursos = new ArrayList<>();
        listaActividades = new ArrayList<>();
    }
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        firePropertyChange(LISTA_RECURSOS);
        firePropertyChange(LISTA_ACTIVIDADES);
    }
    public List<EstadisticaItem> getListaRecursos() {
        return listaRecursos;
    }
    public void setListaRecursos(List<EstadisticaItem> listaRecursos) {
        this.listaRecursos = listaRecursos;
        firePropertyChange(LISTA_RECURSOS);
    }

    public List<EstadisticaItem> getListaActividades() {
        return listaActividades;
    }
    public void setListaActividades(List<EstadisticaItem> listaActividades) {
        this.listaActividades = listaActividades;
        firePropertyChange(LISTA_ACTIVIDADES);
    }
}
