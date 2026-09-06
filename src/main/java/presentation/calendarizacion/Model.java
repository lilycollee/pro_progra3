package presentation.calendarizacion;

import logic.entities.CategoriaRecurso;
import logic.entities.Recurso;
import logic.entities.Reserva;
import presentation.AbstractModel;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    private List<CategoriaRecurso> categorias;
    private List<Recurso> recursos;
    private List<Reserva> reservas;

    public static final String CATEGORIAS = "categorias";
    public static final String CALENDARIO = "calendario";

    public Model() {
        categorias = new ArrayList<>();
        recursos = new ArrayList<>();
        reservas = new ArrayList<>();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        firePropertyChange(CATEGORIAS);
        firePropertyChange(CALENDARIO);
    }

    public List<CategoriaRecurso> getCategorias() {
        return categorias;
    }
    public void setCategorias(List<CategoriaRecurso> categorias) {
        this.categorias = categorias;
        firePropertyChange(CATEGORIAS);
    }

    public List<Recurso> getRecursos() {
        return recursos;
    }
    public List<Reserva> getReservas() {
        return reservas;
    }
    public void setCalendario(List<Recurso> recursos, List<Reserva> reservas) {
        this.recursos = recursos;
        this.reservas = reservas;
        firePropertyChange(CALENDARIO);
    }
}