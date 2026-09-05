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

        // ---- Datos quemados para pruebas ----

        // Login: admin/admin y f001/f001 (la clave = el id, ver Usuario/Funcionario)
        administradores.add(new Administrador("admin"));
        funcionarios.add(new Funcionario("f001", "Juan Pérez", "8888-8888"));
        funcionarios.add(new Funcionario("f002", "María Rodríguez", "8888-9999"));

        // Categorías de recursos
        CategoriaRecurso catProyectores = new CategoriaRecurso("CAT001", "Proyectores");
        CategoriaRecurso catSalas = new CategoriaRecurso("CAT002", "Salas de reunión");
        categorias.add(catProyectores);
        categorias.add(catSalas);

        // Recursos (deben apuntar a una categoría ya creada arriba)
        recursos.add(new Recurso("REC001", "Proyector Epson 1", catProyectores));
        recursos.add(new Recurso("REC002", "Proyector Epson 2", catProyectores));
        recursos.add(new Recurso("REC003", "Sala A", catSalas));
        recursos.add(new Recurso("REC004", "Sala B", catSalas));
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

