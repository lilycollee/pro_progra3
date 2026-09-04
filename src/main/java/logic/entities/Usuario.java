package logic.entities;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Usuario {
    private String id;
    private String clave;
    private String role;

    public Usuario(){}
    public Usuario(String id, String clave, String role) {
        this.id = id;
        this.clave = clave;
        this.role = role;
    }

    public boolean validarClave(String claveIngresada){
        return claveIngresada.equals(this.clave);
    }
    public void cambiarClave(String clave) {
        this.clave = clave;
    }

    //Metodos basicos
    public String getId() {
        return id;
    }
    public String getClave() {
        return clave;
    }
    public String getRole() {
        return role;
    }

}
