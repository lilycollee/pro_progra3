package presentation.funcionarios;

import logic.Service;
import logic.entities.Funcionario;

public class Controller {
    private FuncionarioView view;
    private Model model;
    private Service service;

    public Controller(FuncionarioView view, Model model, Service service) {
        this.view = view;
        this.model = model;
        this.service = service;
        view.setController(this);
        view.setModel(model);

        view.addGuardarButtonListener(e ->guardar());
        view.addBorrarButtonListener(e-> borrar());
        view.addLimpiarButtonListener(e-> limpiar());
        view.addBuscarButtonListener(e-> buscar());
        view.addImprimirButtonListener(e-> imprimir());
        view.addSeleccionListener(e -> {if (!e.getValueIsAdjusting()) seleccionarFila();});

        actualizarListado();
    }

    private void actualizarListado(){
        model.setListaFuncionarios(service.obtenerFuncionarios());
    }

    private void guardar(){
        String id =  view.getIdF();
        String nombre = view.getNombreF();
        String telefono = view.getTelefonoF();
        if (id.isBlank() || nombre.isBlank()){
            view.mostrarError("El ID y el Nombre son obligatorios.");
            return;
        }
        service.guardarFuncionario(new Funcionario(id,nombre,telefono));
        limpiar();
        actualizarListado();
    }
    private void borrar(){
        String id = view.getIdF();
        if (id.isBlank()){
            view.mostrarError("Seleccione o escriba el ID.");
            return;
        }
        service.eliminarFuncionario(id);
        limpiar();
        actualizarListado();
    }
    private void limpiar(){
        model.setCurrent(new Funcionario());
    }
    private void buscar(){
        String id = view.getIdB();
        String nombre = view.getNombreB();
        var resultado = service.obtenerFuncionarios().stream().filter(f -> (id.isBlank() || f.getId().contains(id)) && (nombre.isBlank() || f.getNombre().toLowerCase().contains(nombre.toLowerCase()))).toList();
        model.setListaFuncionarios(resultado);
    }
    private void imprimir(){
        //pendiente
    }
    private void seleccionarFila(){
        int fila = view.getFilaSeleccionada();
        if (fila >= 0){
            model.setCurrent(model.getListaFuncionarios().get(fila));
        }
    }
}
