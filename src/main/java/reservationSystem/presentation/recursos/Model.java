package reservationSystem.presentation.recursos;

import reservationSystem.logic.entities.CategoriaRecurso;
import reservationSystem.logic.entities.Recurso;
import reservationSystem.presentation.AbstractModel;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    private Recurso current;
    private List<Recurso> recursos;
    private List<CategoriaRecurso> categorias;

    public static final String CURRENT = "current";
    public static final String RECURSOS = "recursos";
    public static final String CATEGORIAS = "categorias";

    public Model() {
        current = new Recurso();
        recursos = new ArrayList<>();
        categorias = new ArrayList<>();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        firePropertyChange(CURRENT);
        firePropertyChange(RECURSOS);
        firePropertyChange(CATEGORIAS);
    }

    public Recurso getCurrent() {
        return current;
    }

    public void setCurrent(Recurso current) {
        this.current = current;
        firePropertyChange(CURRENT);
    }

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<Recurso> recursos) {
        this.recursos = recursos;
        firePropertyChange(RECURSOS);
    }

    public List<CategoriaRecurso> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<CategoriaRecurso> categorias) {
        this.categorias = categorias;
        firePropertyChange(CATEGORIAS);
    }
}
