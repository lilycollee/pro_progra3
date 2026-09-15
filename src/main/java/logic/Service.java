package logic;

import data.Data;
import data.XmlPersister;
import logic.entities.*;
import logic.entities.enums.EstadoReserva;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import logic.entities.ia.ReservaExtraccion;
import logic.entities.ia.ReservaExtractorService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.*;
import java.util.stream.Collectors;

public class Service {
    private Data data;

    public Service(Data data) {
        this.data = data;
    }

    public Service() {
        data = XmlPersister.instance().load();

    }
    protected void guardarCambios(){
        XmlPersister.instance().store(data);
    }
    public Usuario autenticar(String id, String clave) {
        for (Administrador administrador : data.getAdministradores()) {
            if (administrador.getId().equals(id)) {
                if (administrador.getClave().equals(clave)) {
                    return administrador;
                }
            }
        }
        for (Funcionario funcionario : data.getFuncionarios()) {
            if (funcionario.getId().equals(id)) {
                if (funcionario.getClave().equals(clave)) {
                    return funcionario;
                }
            }
        }
        return null;
    }

    //-------- Funcionarios --------
    public Funcionario guardarFuncionario(Funcionario funcionario) {
        eliminarFuncionario(funcionario.getId());
        data.getFuncionarios().add(funcionario);
        guardarCambios();
        return funcionario;
    }
    public void eliminarFuncionario(String id) {
        data.getFuncionarios().removeIf(funcionario -> funcionario.getId().equals(id));
        guardarCambios();
    }
    public Optional<Funcionario> obtenerFuncionario(String id) {
        return data.getFuncionarios().stream().filter(f -> f.getId().equals(id)).findFirst();
    }
    public List<Funcionario> obtenerFuncionarios() {
        return data.getFuncionarios();
    }
    public List<Reserva> obtenerReservasPorFuncionario(String idFuncionario) {
        return data.getReservas().stream().filter(r -> r.getIdFuncionario().equals(idFuncionario)).toList();
    }

    //-------- Administradores --------
    public Administrador guardarAdministrador(Administrador administrador) {
        eliminarAdministrador(administrador.getId());
        data.getAdministradores().add(administrador);
        guardarCambios();
        return administrador;
    }
    public void eliminarAdministrador(String id) {
        data.getAdministradores().removeIf(a -> a.getId().equals(id));
        guardarCambios();
    }
    public Optional<Administrador> obtenerAdministrador(String id) {
        return data.getAdministradores().stream().filter(a -> a.getId().equals(id)).findFirst();
    }
    public List<Administrador> obtenerAdministradores() {
        return data.getAdministradores();
    }

    // ---------- Recursos ----------

    public Recurso guardarRecurso(Recurso recurso) {
        eliminarRecursoInterno(recurso.getId());
        data.getRecursos().add(recurso);
        guardarCambios();
        return recurso;
    }

    private void eliminarRecursoInterno(String id) {
        data.getRecursos().removeIf(recurso -> recurso.getId().equals(id));
    }

    public boolean recursoEnReservaActiva(String idRecurso) {
        return data.getReservas().stream()
                .filter(r -> EstadoReserva.ACTIVA.equals(r.getEstado()))
                .flatMap(r -> r.getDetalles().stream())
                .anyMatch(d -> idRecurso.equals(d.getIdRecursoAsignado()));
    }

    public boolean eliminarRecurso(String id) {
        if (recursoEnReservaActiva(id)) {
            return false;
        }
        boolean eliminado = data.getRecursos().removeIf(recurso -> recurso.getId().equals(id));
        if (eliminado) {
            guardarCambios();
        }
        return eliminado;
    }
    public Optional<Recurso> obtenerRecurso(String id) {
        return data.getRecursos().stream().filter(r -> r.getId().equals(id)).findFirst();
    }
    public List<Recurso> obtenerRecursos() {
        List<Recurso> recursos = data.getRecursos();
        for (Recurso recurso : recursos) {
            if (recurso.getIdCategoria() != null && recurso.getCategoriaRecurso() == null) {
                obtenerCategoria(recurso.getIdCategoria())
                        .ifPresent(recurso::setCategoriaRecurso);
            }
        }
        return recursos;
    }
    public List<Recurso> obtenerRecursosPorCategoria(String idCategoria) {
        return obtenerRecursos().stream().filter(r -> r.getCategoriaRecurso().getId().equals(idCategoria)).toList();
    }
    public String obtenerRecursosAsignadosTexto(Reserva reserva) {
        StringBuilder sb = new StringBuilder();
        for (DetalleReserva detalle : reserva.getDetalles()) {
            if (detalle.getIdRecursoAsignado() != null) {
                obtenerRecurso(detalle.getIdRecursoAsignado()).ifPresent(r -> {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(r.getDescripcion());
                });
            }
        }
        return sb.toString();
    }
    // ---------- Categorías ----------

    public List<CategoriaRecurso> obtenerCategorias() {
        return data.getCategorias();
    }
    public CategoriaRecurso guardarCategoria(CategoriaRecurso categoria) {
        eliminarCategoriaInterno(categoria.getId());
        data.getCategorias().add(categoria);
        guardarCambios();
        return categoria;
    }

    private void eliminarCategoriaInterno(String id) {
        data.getCategorias().removeIf(categoria -> categoria.getId().equals(id));
    }

    public boolean categoriaTieneRecursoEnReservaActiva(String idCategoria) {
        return data.getRecursos().stream()
                .filter(r -> idCategoria.equals(r.getIdCategoria()))
                .anyMatch(r -> recursoEnReservaActiva(r.getId()));
    }

    public boolean eliminarCategoria(String id) {
        if (categoriaTieneRecursoEnReservaActiva(id)) {
            return false;
        }

        data.getRecursos().removeIf(recurso -> id.equals(recurso.getIdCategoria()));
        boolean eliminada = data.getCategorias().removeIf(categoria -> categoria.getId().equals(id));

        if (eliminada) {
            guardarCambios();
        }
        return eliminada;
    }

    public Optional<CategoriaRecurso> obtenerCategoria(String id) {
        return data.getCategorias().stream().filter(c -> c.getId().equals(id)).findFirst();
    }


    //---------- Reservas ----------

    public List<Reserva> obtenerReservas() {
        return data.getReservas();
    }

    public List<Reserva> obtenerReservasSemana(LocalDate fechaDeLaSemana) {
        LocalDate lunes = fechaDeLaSemana.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingo = lunes.plusDays(6);

        return data.getReservas().stream()
                .filter(r -> r.getEstado() != null && r.getEstado().equals(EstadoReserva.ACTIVA))
                .filter(r -> r.getFecha() != null)
                .filter(r -> !r.getFecha().isBefore(lunes) && !r.getFecha().isAfter(domingo))
                .toList();
    }

    public ResultadoReserva crearReserva(Reserva reservaSolicitada) {
        List<CategoriaRecurso> noDisponibles = new ArrayList<>();
        List<DetalleReserva> detallesAsignados = new ArrayList<>();

        for (DetalleReserva detalle : reservaSolicitada.getDetalles()) {
            Recurso disponible = buscarRecursoDisponible(detalle.getIdCategoriaSolicitada(), reservaSolicitada);
            if (disponible == null) {
                obtenerCategoria(detalle.getIdCategoriaSolicitada()).ifPresent(noDisponibles::add);
            } else {
                detalle.setIdRecursoAsignado(disponible.getId());
                detallesAsignados.add(detalle);
            }
        }
        if (!noDisponibles.isEmpty()) {
            return ResultadoReserva.fracaso(noDisponibles);
        }
        String id = "R" + (data.getReservas().size() + 1);
        Reserva reserva = new Reserva(
                id,
                reservaSolicitada.getIdFuncionario(),
                reservaSolicitada.getActividad(),
                reservaSolicitada.getFecha(),
                reservaSolicitada.getHoraInicio(),
                reservaSolicitada.getHoraFin()
        );
        for (DetalleReserva detalle : detallesAsignados) {
            reserva.agregarDetalleReserva(detalle);
        }
        data.getReservas().add(reserva);
        guardarCambios();
        return ResultadoReserva.exito();
    }
    public void cancelarReserva(String id) {
        data.getReservas().stream().filter(r -> r.getId().equals(id)).findFirst().ifPresent(Reserva::cancelarReserva);
        guardarCambios();
    }
    private Recurso buscarRecursoDisponible(String idCategoria, Reserva nuevaReserva) {
        List<Recurso> recursosDeCategoria = obtenerRecursos().stream().filter(r -> r.getCategoriaRecurso().getId().equals(idCategoria)).toList();
        for (Recurso recurso : recursosDeCategoria) {
            if (!estaOcupado(recurso, nuevaReserva)) {
                return recurso;
            }
        }
        return null;
    }
    private boolean estaOcupado(Recurso recurso, Reserva nuevaReserva) {
        for (Reserva existente : data.getReservas()) {
            if (existente.getEstado() == null || !existente.getEstado().equals(EstadoReserva.ACTIVA)) continue;
            if (!existente.getFecha().equals(nuevaReserva.getFecha())) continue;
            boolean seSolapan = nuevaReserva.getHoraInicio().isBefore(existente.getHoraFin()) && existente.getHoraInicio().isBefore(nuevaReserva.getHoraFin());
            if (!seSolapan) continue;
            for (DetalleReserva detalle : existente.getDetalles()) {
                if (detalle.getIdRecursoAsignado() != null && detalle.getIdRecursoAsignado().equals(recurso.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Reserva> obtenerReservasPorFechaYCategoria(LocalDate fecha, String idCategoria) {
        obtenerRecursos(); // fuerza a reconectar categoriaRecurso en todos los recursos antes de usarla abajo
        return data.getReservas().stream()
                .filter(r -> r.getEstado().equals(EstadoReserva.ACTIVA))
                .filter(r -> r.getFecha().equals(fecha))
                .filter(r -> r.getDetalles().stream().anyMatch(d ->
                        d.getIdRecursoAsignado() != null && obtenerRecurso(d.getIdRecursoAsignado())
                                .map(rec -> rec.getCategoriaRecurso().getId().equals(idCategoria))
                                .orElse(false))).toList();
    }

    // ---------- Cambiar clave ----------

    public boolean cambiarClave(String id, String claveActual, String claveNueva) {
        Usuario usuario = null;
        for (Administrador administrador : data.getAdministradores()) {
            if (administrador.getId().equals(id)) {
                usuario = administrador;
                break;
            }
        }
        if (usuario == null) {
            for (Funcionario funcionario : data.getFuncionarios()) {
                if (funcionario.getId().equals(id)) {
                    usuario = funcionario;
                    break;
                }
            }
        }
        if (usuario == null || !usuario.validarClave(claveActual)) {
            return false;
        }
        usuario.cambiarClave(claveNueva);
        guardarCambios();
        return true;
    }

    //---------- Estadísticas ----------
    public List<EstadisticaItem> obtenerEstadisticasRecurso(LocalDate desde, LocalDate hasta) {
        Map<String, Long> conteo = new TreeMap<>();
        for (Reserva reserva : data.getReservas()) {
            if (!reserva.getEstado().equals(EstadoReserva.ACTIVA)) continue;
            if (reserva.getFecha().isBefore(desde) || reserva.getFecha().isAfter(hasta)) continue;
            for (DetalleReserva detalle : reserva.getDetalles()) {
                String categoria;
                if (detalle.getIdRecursoAsignado() != null) {
                    categoria = obtenerRecurso(detalle.getIdRecursoAsignado()).map(r -> r.getCategoriaRecurso().getDescripcion()).orElse("N/A");
                } else {
                    categoria = obtenerCategoria(detalle.getIdCategoriaSolicitada()).map(CategoriaRecurso::getDescripcion).orElse("N/A");
                }
                conteo.merge(categoria, 1L, Long::sum);
            }
        }
        List<EstadisticaItem> resultado = new ArrayList<>();
        for (Map.Entry<String, Long> entry : conteo.entrySet()) {
            resultado.add(new EstadisticaItem(entry.getKey(), entry.getValue()));
        }
        return resultado;
    }
    public List<EstadisticaItem> obtenerEstadisticasActividades(LocalDate desde, LocalDate hasta) {
        Map<LocalDate, Long> conteo = new TreeMap<>();
        for (Reserva reserva : data.getReservas()) {
            if(!reserva.getEstado().equals(EstadoReserva.ACTIVA)) continue;
            if (reserva.getFecha().isBefore(desde) || reserva.getFecha().isAfter(hasta)) continue;
            LocalDate inicioSemana = reserva.getFecha().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            conteo.merge(inicioSemana, 1L, Long::sum);
        }
        List<EstadisticaItem> resultado = new ArrayList<>();
        for (Map.Entry<LocalDate, Long> entry : conteo.entrySet()) {
            resultado.add(new EstadisticaItem(entry.getKey().toString(), entry.getValue()));
        }
        return resultado;
    }

    //---------- IA ----------
    public ReservaExtraccion extraerReserva(String frase) {
        OpenAiChatModel aiModel = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();

        ReservaExtractorService aiService = AiServices.create(ReservaExtractorService.class, aiModel);

        String listaCategorias = formatearCategorias(data.getCategorias().stream()
                .map(CategoriaRecurso::getDescripcion).collect(Collectors.toList()));

        return aiService.extraer(frase, listaCategorias, LocalDate.now().toString());
    }
    private String formatearCategorias(List<String> descripciones) {
        return String.join(", ", descripciones);
    }

}
