package logic.entities;

import logic.entities.enums.Rol;

public class Administrador extends Usuario {
    public Administrador() {}
    public Administrador(String id) {
        super(id,id, Rol.ADMINISTRADOR);
    }
}
