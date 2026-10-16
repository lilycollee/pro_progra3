package reservationSystem.presentation.agenda;

import reservationSystem.logic.entities.Reserva;
import reservationSystem.presentation.AbstractModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {
    public static final String SEMANA = "semana";
    public static final String RESERVAS = "reservas";

    private LocalDate inicioSemana;
    private List<Reserva> reservas;

    public Model() {
        inicioSemana = LocalDate.now();
        reservas = new ArrayList<>();
    }

    public LocalDate getInicioSemana() {
        return inicioSemana;
    }

    public void setInicioSemana(LocalDate inicioSemana) {
        this.inicioSemana = inicioSemana;
        firePropertyChange(SEMANA);
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
        firePropertyChange(RESERVAS);
    }
}
