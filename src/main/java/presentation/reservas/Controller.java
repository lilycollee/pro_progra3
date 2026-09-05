package presentation.reservas;

import logic.Service;
import logic.entities.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class Controller {
    private ReservasView view;
    private Model model;
    private Service service;
    private Funcionario funcionarioActual;

    public Controller(ReservasView view, Model model, Service service, Funcionario funcionarioActual) {
        this.view = view;
        this.model = model;
        this.funcionarioActual = funcionarioActual;
        this.service = service;

        view.setController(this);
        view.setModel(model);

        view.addReservarListener(e-> reservar());
        view.addLimpiarListener(e-> limpiar());
        view.addCancelarReservaListener(e-> cancelarReserva());
        view.addImprimirListener(e -> imprimir());
        view.addSeleccionReservaListener(e-> {if (!e.getValueIsAdjusting()) seleccionarFila();});

        model.setCategorias(service.obtenerCategorias());
        actualizarListado();
    }
    private void actualizarListado() {
        model.setReservas(service.obtenerReservas());
    }
    private void reservar() {
        String actividad = view.getActividad();
        LocalDate fecha = view.getFecha();
        LocalTime horaInicio = view.getHoraInicio();
        LocalTime horaFinal = view.getHoraFinal();
        var categorias = view.getCategoriasSeleccionadas();

        if (actividad.isBlank()){
            view.mostrarError("Ingrese la actividad de la reserva.");
        }
        if (fecha == null) {
            view.mostrarError("La fecha no tiene un formato válido (dd/MM/yyyy).");
            return;
        }
        if (horaInicio == null || horaFinal == null) {
            view.mostrarError("Seleccione hora de inicio y hora de fin.");
            return;
        }
        if (!horaInicio.isBefore(horaFinal)) {
            view.mostrarError("La hora de inicio debe ser antes que la hora de fin.");
            return;
        }
        if (categorias.isEmpty()) {
            view.mostrarError("Seleccione al menos una categoría de recurso.");
            return;
        }

        Reserva reserva = new Reserva(null, funcionarioActual,actividad,fecha,horaInicio,horaFinal);
        for (CategoriaRecurso categoriaRecurso : categorias){
            reserva.agregarDetalleReserva(new DetalleReserva(categoriaRecurso));
        }
        ResultadoReserva resultadoReserva = service.crearReserva(reserva);
        if (resultadoReserva.esExitosa()) {
            limpiar();
            actualizarListado();
        } else {
            StringBuilder mgs = new StringBuilder("No hay recursos disponibles para:");
            for (CategoriaRecurso c : resultadoReserva.getCategoriasNoDisponibles()){
                mgs.append(c.getDescripcion()).append(" ");
            }
            view.mostrarError(mgs.toString());
        }
    }
    private void cancelarReserva() {
        Reserva seleccionada = model.getCurrent();
        if (seleccionada == null || seleccionada.getId() == null) {
            view.mostrarError("Seleccione una reserva de la tabla para cancelar.");
            return;
        }
        service.cancelarReserva(seleccionada.getId());
        limpiar();
        actualizarListado();
    }
    private void imprimir() {
        view.mostrarError("Generación de PDF pendiente de implementar.");
    }
    private void limpiar() {
        model.setCurrent(new  Reserva());
        view.limpiarCampos();
    }
    private void seleccionarFila() {
        int fila = view.getFilasReservaSeleccionada();
        if (fila >= 0) {
            model.setCurrent(model.getReservas().get(fila));
        }
    }
}
