package presentation.agenda;

import logic.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class Controller {
    private final AgendaView view;
    private final Model model;
    private final Service service;

    public Controller(AgendaView view, Model model, Service service) {
        this.view = view;
        this.model = model;
        this.service = service;

        view.setController(this);
        view.setModel(model);

        view.addMostrarSemanaListener(e -> mostrarSemanaSeleccionada());
        view.addSemanaAnteriorListener(e -> cambiarSemana(-1));
        view.addSemanaSiguienteListener(e -> cambiarSemana(1));

        cargarSemana(LocalDate.now());
    }

    private void mostrarSemanaSeleccionada() {
        LocalDate fecha = view.getFechaSeleccionada();
        if (fecha == null) {
            view.mostrarError("Seleccione una fecha para mostrar su semana.");
            return;
        }
        cargarSemana(fecha);
    }

    private void cambiarSemana(int cantidadSemanas) {
        LocalDate inicioActual = model.getInicioSemana();
        if (inicioActual == null) inicioActual = LocalDate.now();
        cargarSemana(inicioActual.plusWeeks(cantidadSemanas));
    }

    private void cargarSemana(LocalDate fecha) {
        LocalDate lunes = fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        model.setInicioSemana(lunes);
        model.setReservas(service.obtenerReservasSemana(lunes));
        view.setFechaSeleccionada(lunes);
    }
}
