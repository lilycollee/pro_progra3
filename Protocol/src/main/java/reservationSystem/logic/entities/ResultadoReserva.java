package reservationSystem.logic.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultadoReserva implements Serializable {
    private boolean exitosa;
    private List<CategoriaRecurso>  categoriasNoDisponibles = new ArrayList<>();

    public ResultadoReserva() {}
    public static ResultadoReserva exito() {
        ResultadoReserva resultadoReserva = new ResultadoReserva();
        resultadoReserva.exitosa = true;
        return resultadoReserva;
    }
    public static ResultadoReserva fracaso (List<CategoriaRecurso> categoriasNoDisponibles) {
        ResultadoReserva resultadoReserva = new ResultadoReserva();
        resultadoReserva.exitosa = false;
        resultadoReserva.categoriasNoDisponibles = categoriasNoDisponibles;
        return resultadoReserva;
    }

    public boolean esExitosa() {
        return exitosa;
    }
    public List<CategoriaRecurso> getCategoriasNoDisponibles() {
        return categoriasNoDisponibles;
    }
}
