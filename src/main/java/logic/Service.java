package logic;

import data.Data;
import data.XmlPersister;
import logic.entities.*;
import logic.entities.enums.EstadoReserva;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.*;

public class Service {
    private Data data;

    public Service() {
        data = XmlPersister.instance().load();

    }
    private void guardarCambios(){
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
        return data.getReservas().stream().filter(r -> r.getFuncionario().getId().equals(idFuncionario)).toList();
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

    // ---------- Categorías ----------

    public List<CategoriaRecurso> obtenerCategorias() {
        return data.getCategorias();
    }

    //---------- Reservas ----------

    public List<Reserva> obtenerReservas() {
        return data.getReservas();
    }

    public ResultadoReserva crearReserva(Reserva reservaSolicitada) {
        List<CategoriaRecurso> noDisponibles = new ArrayList<>();
        List<DetalleReserva> detallesAsignados = new ArrayList<>();
        for (DetalleReserva detalle : reservaSolicitada.getDetalles()) {
            Recurso disponible = buscarRecursoDisponible(detalle.getCategoriaSolicitada(), reservaSolicitada);
            if (disponible == null) {
                noDisponibles.add(detalle.getCategoriaSolicitada());
            } else {
                detalle.setRecursoAsignado(disponible);
                detallesAsignados.add(detalle);
            }
        }
        if (!noDisponibles.isEmpty()) {
            return ResultadoReserva.fracaso(noDisponibles);
        }
        String id = "R" + (data.getReservas().size() + 1);
        Reserva reserva = new Reserva(id, reservaSolicitada.getFuncionario(), reservaSolicitada.getActividad(), reservaSolicitada.getFecha(), reservaSolicitada.getHoraInicio(), reservaSolicitada.getHoraFin());
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
    private Recurso buscarRecursoDisponible(CategoriaRecurso categoria, Reserva nuevaReserva) {
        List<Recurso> recursosDeCategoria = data.getRecursos().stream().filter(r -> r.getCategoriaRecurso().getId().equals(categoria.getId())).toList();
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
                if (detalle.getRecursoAsignado() != null && detalle.getRecursoAsignado().getId().equals(recurso.getId())) {
                    return true;
                }
            }
        }
        return false;
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
            if(!reserva.getEstado().equals(EstadoReserva.ACTIVA)) continue;
            if (reserva.getFecha().isBefore(desde) || reserva.getFecha().isAfter(hasta)) continue;
            for(DetalleReserva detalle : reserva.getDetalles()) {
                String categoria = detalle.getRecursoAsignado() != null ? detalle.getRecursoAsignado().getCategoriaRecurso().getDescripcion() : detalle.getCategoriaSolicitada().getDescripcion();
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

}
