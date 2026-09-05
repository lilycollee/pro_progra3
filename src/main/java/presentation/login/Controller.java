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

    public void abrirCambioClave(String idPrellenado) {
        CambiarClaveView dialog = new CambiarClaveView(view, idPrellenado);
        dialog.setController(this);
        dialog.setVisible(true);
    }

    public void cambiarClave(CambiarClaveView dialogView, String id, String claveActual, String claveNueva, String claveConfirmar) {
        boolean algunCampoVacio = id == null || id.isBlank() || claveActual == null || claveActual.isBlank() || claveNueva == null || claveNueva.isBlank()
                || claveConfirmar == null || claveConfirmar.isBlank();

        if (algunCampoVacio) {
            dialogView.mostrarError("Debe llenar todos los campos");
            return;
        }
        if (!claveNueva.equals(claveConfirmar)) {
            dialogView.mostrarError("La clave nueva y la confirmación no coinciden");
            return;
        }

        boolean exito = service.cambiarClave(id, claveActual, claveNueva);
        if (!exito) {
            dialogView.mostrarError("La clave actual es incorrecta");
            return;
        }

        dialogView.mostrarExito("Clave cambiada correctamente");
    }
}