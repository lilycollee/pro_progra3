package presentation.categorias;

import logic.entities.CategoriaRecurso;
import presentation.AbstractModel;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    public static final String CURRENT = "current";
    public static final String CATEGORIAS = "categorias";

    private CategoriaRecurso current;
    private List<CategoriaRecurso> categorias;

    public Model() {
        current = new CategoriaRecurso();
        categorias = new ArrayList<>();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        firePropertyChange(CURRENT);
        firePropertyChange(CATEGORIAS);
    }

    public CategoriaRecurso getCurrent() {
        return current;
    }

    public void setCurrent(CategoriaRecurso current) {
        this.current = current;
        firePropertyChange(CURRENT);
    }

    public List<CategoriaRecurso> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<CategoriaRecurso> categorias) {
        this.categorias = categorias;
        firePropertyChange(CATEGORIAS);
    }
}
