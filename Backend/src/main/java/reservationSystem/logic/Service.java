package reservationSystem.logic;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import reservationSystem.data.AdministradorDao;
import reservationSystem.data.CategoriaDao;
import reservationSystem.data.FuncionarioDao;
import reservationSystem.data.RecursoDao;
import reservationSystem.data.ReservaDao;
import reservationSystem.logic.entities.*;
import reservationSystem.logic.entities.enums.EstadoReserva;
import reservationSystem.logic.entities.ia.ReservaExtraccion;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

public class Service implements IService {
    private final CategoriaDao categoriaDao = new CategoriaDao();
    private final RecursoDao recursoDao = new RecursoDao();
    private final FuncionarioDao funcionarioDao = new FuncionarioDao();
    private final AdministradorDao administradorDao = new AdministradorDao();
    private final ReservaDao reservaDao = new ReservaDao();
    private static Service instance;

    public static synchronized Service instance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    private Service() {
    }

    // ---------- Autenticación ----------

    public Usuario autenticar(String id, String clave) {
        Optional<Administrador> admin = administradorDao.read(id);
        if (admin.isPresent() && admin.get().getClave().equals(clave)) {
            return admin.get();
        }
        Optional<Funcionario> funcionario = funcionarioDao.read(id);
        if (funcionario.isPresent() && funcionario.get().getClave().equals(clave)) {
            return funcionario.get();
        }
        return null;
    }

    public boolean cambiarClave(String id, String claveActual, String claveNueva) {
        Optional<Administrador> admin = administradorDao.read(id);
        if (admin.isPresent()) {
            if (!admin.get().validarClave(claveActual)) return false;
            admin.get().cambiarClave(claveNueva);
            administradorDao.save(admin.get());
            return true;
        }
        Optional<Funcionario> funcionario = funcionarioDao.read(id);
        if (funcionario.isPresent()) {
            if (!funcionario.get().validarClave(claveActual)) return false;
            funcionario.get().cambiarClave(claveNueva);
            funcionarioDao.save(funcionario.get());
            return true;
        }
        return false;
    }

    // ---------- Funcionarios ----------

    public Funcionario guardarFuncionario(Funcionario funcionario) {
        funcionarioDao.save(funcionario);
        return funcionario;
    }

    public boolean funcionarioTieneReservas(String id) {
        return reservaDao.funcionarioTieneReservas(id);
    }

    /** @return false si el funcionario tiene reservas (la FK no permite borrarlo) o no existe. */
    public boolean eliminarFuncionario(String id) {
        if (reservaDao.funcionarioTieneReservas(id)) {
            return false;
        }
        return funcionarioDao.delete(id);
    }

    public Optional<Funcionario> obtenerFuncionario(String id) {
        return funcionarioDao.read(id);
    }

    public List<Funcionario> obtenerFuncionarios() {
        return funcionarioDao.findAll();
    }

    public List<Reserva> obtenerReservasPorFuncionario(String idFuncionario) {
        return reservaDao.findByFuncionario(idFuncionario);
    }

    // ---------- Administradores ----------

    public Administrador guardarAdministrador(Administrador administrador) {
        administradorDao.save(administrador);
        return administrador;
    }

    public void eliminarAdministrador(String id) {
        administradorDao.delete(id);
    }

    public Optional<Administrador> obtenerAdministrador(String id) {
        return administradorDao.read(id);
    }

    public List<Administrador> obtenerAdministradores() {
        return administradorDao.findAll();
    }

    // ---------- Recursos ----------

    public Recurso guardarRecurso(Recurso recurso) {
        recursoDao.save(recurso);
        return recurso;
    }

    public boolean recursoEnReservaActiva(String idRecurso) {
        return reservaDao.recursoEnReservaActiva(idRecurso);
    }

    public boolean eliminarRecurso(String id) {
        if (reservaDao.recursoEnReservaActiva(id)) {
            return false;
        }
        return recursoDao.delete(id);
    }

    public Optional<Recurso> obtenerRecurso(String id) {
        return recursoDao.read(id);
    }

    public List<Recurso> obtenerRecursos() {
        return recursoDao.findAll();
    }

    public List<Recurso> obtenerRecursosPorCategoria(String idCategoria) {
        return recursoDao.findByCategoria(idCategoria);
    }

    public String obtenerRecursosAsignadosTexto(Reserva reserva) {
        StringBuilder sb = new StringBuilder();
        for (DetalleReserva detalle : reserva.getDetalles()) {
            if (detalle.getIdRecursoAsignado() != null) {
                recursoDao.read(detalle.getIdRecursoAsignado()).ifPresent(r -> {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(r.getDescripcion());
                });
            }
        }
        return sb.toString();
    }

    public String generarIdRecurso() {
        return String.format("REC%03d", recursoDao.maxNumeroId() + 1);
    }

    // ---------- Categorías ----------

    public List<CategoriaRecurso> obtenerCategorias() {
        return categoriaDao.findAll();
    }

    public CategoriaRecurso guardarCategoria(CategoriaRecurso categoria) {
        categoriaDao.save(categoria);
        return categoria;
    }

    public boolean categoriaTieneRecursoEnReservaActiva(String idCategoria) {
        return reservaDao.categoriaConReservaActiva(idCategoria);
    }

    /** Los recursos de la categoría se borran por cascada en la base de datos. */
    public boolean eliminarCategoria(String id) {
        if (reservaDao.categoriaConReservaActiva(id)) {
            return false;
        }
        return categoriaDao.delete(id);
    }

    public Optional<CategoriaRecurso> obtenerCategoria(String id) {
        return categoriaDao.read(id);
    }

    public String generarIdCategoria() {
        return String.format("CAT%03d", categoriaDao.maxNumeroId() + 1);
    }

    // ---------- Reservas ----------

    public List<Reserva> obtenerReservas() {
        return reservaDao.findAll();
    }

    public List<Reserva> obtenerReservasSemana(LocalDate fechaDeLaSemana) {
        LocalDate lunes = fechaDeLaSemana.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingo = lunes.plusDays(6);

        return reservaDao.findAll().stream()
                .filter(r -> EstadoReserva.ACTIVA.equals(r.getEstado()))
                .filter(r -> r.getFecha() != null)
                .filter(r -> !r.getFecha().isBefore(lunes) && !r.getFecha().isAfter(domingo))
                .toList();
    }

    // synchronized: la revisión de disponibilidad y el insert deben ser una sola operación
    public synchronized ResultadoReserva crearReserva(Reserva reservaSolicitada) {
        List<Reserva> existentes = reservaDao.findAll();
        List<CategoriaRecurso> noDisponibles = new ArrayList<>();
        List<DetalleReserva> detallesAsignados = new ArrayList<>();

        for (DetalleReserva detalle : reservaSolicitada.getDetalles()) {
            Recurso disponible = buscarRecursoDisponible(detalle.getIdCategoriaSolicitada(), reservaSolicitada, existentes);
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
        String id = "R" + (reservaDao.maxNumeroId() + 1);
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
        reservaDao.create(reserva);
        return ResultadoReserva.exito();
    }

    public void cancelarReserva(String id) {
        reservaDao.updateEstado(id, EstadoReserva.CANCELADA);
    }

    private Recurso buscarRecursoDisponible(String idCategoria, Reserva nuevaReserva, List<Reserva> existentes) {
        for (Recurso recurso : recursoDao.findByCategoria(idCategoria)) {
            if (!estaOcupado(recurso, nuevaReserva, existentes)) {
                return recurso;
            }
        }
        return null;
    }

    private boolean estaOcupado(Recurso recurso, Reserva nuevaReserva, List<Reserva> existentes) {
        for (Reserva existente : existentes) {
            if (!EstadoReserva.ACTIVA.equals(existente.getEstado())) continue;
            if (!existente.getFecha().equals(nuevaReserva.getFecha())) continue;
            boolean seSolapan = nuevaReserva.getHoraInicio().isBefore(existente.getHoraFin())
                    && existente.getHoraInicio().isBefore(nuevaReserva.getHoraFin());
            if (!seSolapan) continue;
            for (DetalleReserva detalle : existente.getDetalles()) {
                if (recurso.getId().equals(detalle.getIdRecursoAsignado())) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Reserva> obtenerReservasPorFechaYCategoria(LocalDate fecha, String idCategoria) {
        Set<String> idsRecursos = recursoDao.findByCategoria(idCategoria).stream()
                .map(Recurso::getId)
                .collect(Collectors.toSet());
        return reservaDao.findAll().stream()
                .filter(r -> EstadoReserva.ACTIVA.equals(r.getEstado()))
                .filter(r -> r.getFecha().equals(fecha))
                .filter(r -> r.getDetalles().stream().anyMatch(d ->
                        d.getIdRecursoAsignado() != null && idsRecursos.contains(d.getIdRecursoAsignado())))
                .toList();
    }

    // ---------- Estadísticas ----------

    public List<EstadisticaItem> obtenerEstadisticasRecurso(LocalDate desde, LocalDate hasta) {
        Map<String, String> categoriaPorRecurso = new HashMap<>();
        for (Recurso r : recursoDao.findAll()) {
            if (r.getCategoriaRecurso() != null) {
                categoriaPorRecurso.put(r.getId(), r.getCategoriaRecurso().getDescripcion());
            }
        }
        Map<String, String> descripcionCategoria = categoriaDao.findAll().stream()
                .collect(Collectors.toMap(CategoriaRecurso::getId, CategoriaRecurso::getDescripcion));

        Map<String, Long> conteo = new TreeMap<>();
        for (Reserva reserva : reservaDao.findAll()) {
            if (!EstadoReserva.ACTIVA.equals(reserva.getEstado())) continue;
            if (reserva.getFecha().isBefore(desde) || reserva.getFecha().isAfter(hasta)) continue;
            for (DetalleReserva detalle : reserva.getDetalles()) {
                String categoria;
                if (detalle.getIdRecursoAsignado() != null) {
                    categoria = categoriaPorRecurso.getOrDefault(detalle.getIdRecursoAsignado(), "N/A");
                } else {
                    categoria = descripcionCategoria.getOrDefault(detalle.getIdCategoriaSolicitada(), "N/A");
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
        for (Reserva reserva : reservaDao.findAll()) {
            if (!EstadoReserva.ACTIVA.equals(reserva.getEstado())) continue;
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

    // ---------- IA ----------

    public ReservaExtraccion extraerReserva(String frase) {
        OpenAiChatModel aiModel = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();

        ReservaExtractorService aiService = AiServices.create(ReservaExtractorService.class, aiModel);

        String listaCategorias = categoriaDao.findAll().stream()
                .map(CategoriaRecurso::getDescripcion)
                .collect(Collectors.joining(", "));

        return aiService.extraer(frase, listaCategorias, LocalDate.now().toString());
    }
}