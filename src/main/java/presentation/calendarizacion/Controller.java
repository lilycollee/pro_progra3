package presentation.calendarizacion;

import logic.Service;
import logic.entities.CategoriaRecurso;
import logic.entities.Recurso;
import logic.entities.Reserva;
import java.time.LocalDate;
import java.util.List;

public class Controller {
    private final CalendarizacionView view;
    private final Model model;
    private final Service service;

    public Controller(CalendarizacionView view, Model model, Service service) {
        this.view = view;
        this.model = model;
        this.service = service;

        view.setController(this);
        view.setModel(model);

        view.addConsultarListener(e -> consultar());

        model.setCategorias(service.obtenerCategorias());
    }

    private void consultar() {
        LocalDate fecha = view.getFecha();
        CategoriaRecurso categoria = view.getCategoriaSeleccionada();

        if (fecha == null) {
            view.mostrarError("Seleccione una fecha");
            return;
        }
        if (categoria == null) {
            view.mostrarError("Seleccione una categoría de recurso");
            return;
        }

        List<Recurso> recursos = service.obtenerRecursosPorCategoria(categoria.getId());
        List<Reserva> reservas = service.obtenerReservasPorFechaYCategoria(fecha, categoria.getId());
        model.setCalendario(recursos, reservas);
    }
}