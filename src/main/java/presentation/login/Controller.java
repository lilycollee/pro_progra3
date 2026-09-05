package presentation.login;

import logic.Service;
import logic.entities.Usuario;
import presentation.MainView;

public class Controller {
    private LoginView view;
    private Model model;
    private Service service;

    public Controller(LoginView view, Model model, Service service) {
        this.view = view;
        this.model = model;
        this.service = service;
        view.setController(this);
        view.setModel(model);
    }

    public void ingresar(String id, String clave) {
        boolean idVacio = id == null || id.isBlank();
        boolean claveVacia = clave == null || clave.isBlank();

        if (idVacio && claveVacia) {
            view.mostrarError("Debe ingresar el identificador y la clave");
            return;
        }
        if (idVacio) {
            view.mostrarError("Debe ingresar el identificador");
            return;
        }
        if (claveVacia) {
            view.mostrarError("Debe ingresar la clave");
            return;
        }

        Usuario usuario = service.autenticar(id, clave);
        if (usuario != null) {
            view.dispose();
            new MainView(usuario);
        } else {
            view.mostrarError("Identificador o clave incorrectos");
        }
    }
}