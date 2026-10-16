package reservationSystem.presentation.funcionarios;

import reservationSystem.logic.entities.Funcionario;
import reservationSystem.presentation.AbstractModel;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    Funcionario current;
    List<Funcionario> listaFuncionarios;

    public static final String CURRENT = "current";
    public static final String LIST = "listaFuncionarios";

    public Model() {
        current = new Funcionario();
        listaFuncionarios = new ArrayList<>();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        firePropertyChange(CURRENT);
        firePropertyChange(LIST);
    }

    public Funcionario getCurrent() {
        return current;
    }
    public void setCurrent(Funcionario current) {
        this.current = current;
        firePropertyChange(CURRENT);
    }

    public List<Funcionario> getListaFuncionarios() {
        return listaFuncionarios;
    }
    public void setListaFuncionarios(List<Funcionario> listaFuncionarios) {
        this.listaFuncionarios = listaFuncionarios;
        firePropertyChange(LIST);
    }
}
