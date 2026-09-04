package logic;

import logic.entities.Administrador;
import logic.entities.Funcionario;
import logic.entities.Usuario;
import java.util.ArrayList;
import java.util.List;

public class Service {
    /*

    De momento usaremos estos datos para probrar el funcionamiento del login, una vez tengamos los datos del XML,
    se cambiara todo correctamente

     */

    private List<Usuario> usuarios;

    public Service() {
        usuarios = new ArrayList<>();
        usuarios.add(new Administrador("admin"));
        usuarios.add(new Funcionario("f001", "Juan Perez", "8888-8888"));
    }

    public Usuario autenticar(String id, String clave) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id) && usuario.validarClave(clave)) {
                return usuario;
            }
        }
        return null;
    }
}
