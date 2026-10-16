package reservationSystem.logic;

import reservationSystem.logic.entities.*;
import reservationSystem.logic.entities.ia.ReservaExtraccion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IService {

    // ---------- Autenticación ----------
    Usuario autenticar(String id, String clave);
    boolean cambiarClave(String id, String claveActual, String claveNueva);

    // ---------- Funcionarios ----------
    Funcionario guardarFuncionario(Funcionario funcionario);
    boolean funcionarioTieneReservas(String id);
    boolean eliminarFuncionario(String id);
    Optional<Funcionario> obtenerFuncionario(String id);
    List<Funcionario> obtenerFuncionarios();
    List<Reserva> obtenerReservasPorFuncionario(String idFuncionario);

    // ---------- Administradores ----------
    Administrador guardarAdministrador(Administrador administrador);
    void eliminarAdministrador(String id);
    Optional<Administrador> obtenerAdministrador(String id);
    List<Administrador> obtenerAdministradores();

    // ---------- Recursos ----------
    Recurso guardarRecurso(Recurso recurso);
    boolean recursoEnReservaActiva(String idRecurso);
    boolean eliminarRecurso(String id);
    Optional<Recurso> obtenerRecurso(String id);
    List<Recurso> obtenerRecursos();
    List<Recurso> obtenerRecursosPorCategoria(String idCategoria);
    String obtenerRecursosAsignadosTexto(Reserva reserva);
    String generarIdRecurso();

    // ---------- Categorías ----------
    List<CategoriaRecurso> obtenerCategorias();
    CategoriaRecurso guardarCategoria(CategoriaRecurso categoria);
    boolean categoriaTieneRecursoEnReservaActiva(String idCategoria);
    boolean eliminarCategoria(String id);
    Optional<CategoriaRecurso> obtenerCategoria(String id);
    String generarIdCategoria();

    // ---------- Reservas ----------
    List<Reserva> obtenerReservas();
    List<Reserva> obtenerReservasSemana(LocalDate fechaDeLaSemana);
    ResultadoReserva crearReserva(Reserva reservaSolicitada);
    void cancelarReserva(String id);
    List<Reserva> obtenerReservasPorFechaYCategoria(LocalDate fecha, String idCategoria);

    // ---------- Estadísticas ----------
    List<EstadisticaItem> obtenerEstadisticasRecurso(LocalDate desde, LocalDate hasta);
    List<EstadisticaItem> obtenerEstadisticasActividades(LocalDate desde, LocalDate hasta);

    // ---------- IA ----------
    ReservaExtraccion extraerReserva(String frase);
}