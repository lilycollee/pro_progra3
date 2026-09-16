package reservationSystem.logic.entities;

import reservationSystem.logic.entities.enums.Rol;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Administrador extends Usuario {
    public Administrador() {}
    public Administrador(String id) {
        super(id,id, Rol.ADMINISTRADOR);
    }
}
