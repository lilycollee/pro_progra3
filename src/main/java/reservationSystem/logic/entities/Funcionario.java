package reservationSystem.logic.entities;

import reservationSystem.logic.entities.enums.Rol;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Funcionario extends Usuario {
    private String nombre;
    private String telefono;

    public Funcionario(){}
    public Funcionario(String id,String nombre, String telefono){
        super(id, id, Rol.FUNCIONARIO);
        this.nombre = nombre;
        this.telefono = telefono;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
