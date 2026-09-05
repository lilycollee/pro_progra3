package presentation.reservas;

import logic.entities.CategoriaRecurso;
import logic.entities.Reserva;
import presentation.AbstractModel;

import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel{
    private Reserva current;
    private List<Reserva> reservas;
    private List<CategoriaRecurso> categorias;

    public static final String CURRENT = "current";
    public static final String RESERVAS = "reservas";
    public static final String CATEGORIAS = "categorias";

    public Model(){
        current = new Reserva();
        reservas = new ArrayList<>();
        categorias = new ArrayList<>();
    }
    public Reserva getCurrent(){
        return current;
    }
    public void setCurrent(Reserva current){
        this.current = current;
        firePropertyChange(CURRENT);
    }
    public List<Reserva> getReservas(){
        return reservas;
    }
    public void setReservas(List<Reserva> reservas){
        this.reservas = reservas;
        firePropertyChange(RESERVAS);
    }
    public List<CategoriaRecurso> getCategorias(){
        return categorias;
    }
    public void setCategorias(List<CategoriaRecurso> categorias){
        this.categorias = categorias;
        firePropertyChange(CATEGORIAS);
    }
}