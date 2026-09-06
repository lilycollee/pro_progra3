package presentation.recursos;

import logic.Service;
import logic.entities.CategoriaRecurso;
import logic.entities.Recurso;

public class Controller {
    private final RecursosView view;
    private final Model model;
    private final Service service;

    public Controller(RecursosView view, Model model, Service service) {
        this.view = view;
        this.model = model;
        this.service = service;

        view.setController(this);
        view.setModel(model);

        view.addGuardarListener(e -> guardar());
        view.addLimpiarListener(e -> limpiar());
        view.addSeleccionListener(e -> {
            if (!e.getValueIsAdjusting()) seleccionarFila();
        });

        actualizarCategorias();
        actualizarListado();
    }

    private void actualizarListado() {
        model.setRecursos(service.obtenerRecursos());
    }

    public void actualizarCategorias() {
        model.setCategorias(service.obtenerCategorias());
    }

    private void guardar() {
        String id = view.getId();
        String descripcion = view.getDescripcion();
        CategoriaRecurso categoria = view.getCategoriaSeleccionada();

        if (id.isBlank() || descripcion.isBlank()) {
            view.mostrarError("El ID y la descripción son obligatorios.");
            return;
        }

        if (categoria == null) {
            view.mostrarError("Seleccione una categoría existente.");
            return;
        }

        // La categoría se toma directamente de la lista existente en Service.
        Recurso recurso = new Recurso(id, descripcion, categoria);
        service.guardarRecurso(recurso);

        limpiar();
        actualizarListado();
    }

    private void limpiar() {
        model.setCurrent(new Recurso());
        view.limpiarCampos();
    }

    private void seleccionarFila() {
        int fila = view.getFilaSeleccionada();
        if (fila >= 0) {
            model.setCurrent(model.getRecursos().get(fila));
        }
    }
}
