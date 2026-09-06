package presentation.categorias;

import logic.Service;
import logic.entities.CategoriaRecurso;

public class Controller {
    private final CategoriasView view;
    private final Model model;
    private final Service service;
    private final Runnable alGuardarCategoria;

    public Controller(CategoriasView view, Model model, Service service) {
        this(view, model, service, null);
    }

    public Controller(CategoriasView view, Model model, Service service, Runnable alGuardarCategoria) {
        this.view = view;
        this.model = model;
        this.service = service;
        this.alGuardarCategoria = alGuardarCategoria;

        view.setController(this);
        view.setModel(model);

        view.addGuardarButtonListener(e -> guardar());
        view.addLimpiarButtonListener(e -> limpiar());
        view.addSeleccionListener(e -> {
            if (!e.getValueIsAdjusting()) seleccionarFila();
        });

        actualizarListado();
    }

    private void actualizarListado() {
        model.setCategorias(service.obtenerCategorias());
    }

    private void guardar() {
        String id = view.getIdCategoria();
        String descripcion = view.getDescripcionCategoria();

        if (id.isBlank() || descripcion.isBlank()) {
            view.mostrarError("El ID y la descripción son obligatorios.");
            return;
        }

        CategoriaRecurso categoria = new CategoriaRecurso(id, descripcion);
        service.guardarCategoria(categoria);
        limpiar();
        actualizarListado();

        if (alGuardarCategoria != null) {
            alGuardarCategoria.run();
        }
    }

    private void limpiar() {
        model.setCurrent(new CategoriaRecurso());
        view.limpiarCampos();
    }

    private void seleccionarFila() {
        int fila = view.getFilaSeleccionada();
        if (fila >= 0 && fila < model.getCategorias().size()) {
            model.setCurrent(model.getCategorias().get(fila));
        }
    }
}
