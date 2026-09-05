package data;
import javax.xml.bind.annotation.*;
import logic.entities.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class Data {
    @XmlElementWrapper(name = "administradores")
    @XmlElement(name = "administrador")
    private List<Administrador> administradores;

    @XmlElementWrapper(name = "funcionarios")
    @XmlElement(name = "funcionarios")
    private List<Funcionario> funcionarios;

    @XmlElementWrapper(name = "categorias")
    @XmlElement(name = "categorias")
    private List<CategoriaRecurso> categorias;

    @XmlElementWrapper(name = "recursos")
    @XmlElement(name = "recursos")
    private List<Recurso> recursos;

    @XmlElementWrapper(name = "reservas")
    @XmlElement(name = "reservas")
    private List<Reserva> reservas;

    public Data() {
        administradores = new ArrayList<>();
        funcionarios = new ArrayList<>();
        categorias = new ArrayList<>();
        recursos = new ArrayList<>();
        reservas = new ArrayList<>();

        administradores.add(new Administrador("administrador"));
    }
    public List<Administrador> getAdministradores() {
        return administradores;
    }
    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }
    public List<CategoriaRecurso> getCategorias() {
        return categorias;
    }
    public List<Recurso> getRecursos() {
        return recursos;
    }
    public List<Reserva> getReservas() {
        return reservas;
    }
}
