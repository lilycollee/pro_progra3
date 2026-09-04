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
        Usuario usuario = service.autenticar(id, clave);
        if (usuario != null) {
            model.setMensajeError("");
            view.dispose();
            new MainView(usuario.getRole());
        } else {
            model.setMensajeError("Identificador o clave incorrectos");
        }
    }
}