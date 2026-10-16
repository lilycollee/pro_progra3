package reservationSystem.data;

import reservationSystem.logic.entities.DetalleReserva;
import reservationSystem.logic.entities.Reserva;
import reservationSystem.logic.entities.enums.EstadoReserva;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;

public class ReservaDao {
    private final Database db = Database.instance();

    private static final String ORDEN = " order by cast(substring(id, 2) as unsigned)";

    public void create(Reserva r) {
        Connection cnx = db.getCnx();
        String sqlReserva = "insert into Reserva (id, actividad, idFuncionario, fecha, horaInicio, horaFin, estado) "
                + "values (?, ?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "insert into DetalleReserva (idReserva, idCategoriaSolicitada, idRecursoAsignado) "
                + "values (?, ?, ?)";
        try {
            cnx.setAutoCommit(false);
            try (PreparedStatement stm = cnx.prepareStatement(sqlReserva)) {
                stm.setString(1, r.getId());
                stm.setString(2, r.getActividad());
                stm.setString(3, r.getIdFuncionario());
                stm.setDate(4, Date.valueOf(r.getFecha()));
                stm.setTime(5, Time.valueOf(r.getHoraInicio()));
                stm.setTime(6, Time.valueOf(r.getHoraFin()));
                stm.setString(7, r.getEstado());
                stm.executeUpdate();
            }
            try (PreparedStatement stm = cnx.prepareStatement(sqlDetalle)) {
                for (DetalleReserva d : r.getDetalles()) {
                    stm.setString(1, r.getId());
                    stm.setString(2, d.getIdCategoriaSolicitada());
                    stm.setString(3, d.getIdRecursoAsignado());
                    stm.addBatch();
                }
                stm.executeBatch();
            }
            cnx.commit();
        } catch (SQLException ex) {
            try { cnx.rollback(); } catch (SQLException ignored) { }
            throw new RuntimeException(ex);
        } finally {
            try { cnx.setAutoCommit(true); } catch (SQLException ignored) { }
        }
    }

    public boolean updateEstado(String id, String estado) {
        try (PreparedStatement stm = db.prepareStatement("update Reserva set estado = ? where id = ?")) {
            stm.setString(1, estado);
            stm.setString(2, id);
            return stm.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Optional<Reserva> read(String id) {
        List<Reserva> lista = cargar("where id = ?", id);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    public List<Reserva> findAll() {
        return cargar("", null);
    }

    public List<Reserva> findByFuncionario(String idFuncionario) {
        return cargar("where idFuncionario = ?", idFuncionario);
    }

    public boolean recursoEnReservaActiva(String idRecurso) {
        String sql = "select 1 from DetalleReserva d inner join Reserva r on d.idReserva = r.id "
                + "where r.estado = ? and d.idRecursoAsignado = ? limit 1";
        return existe(sql, EstadoReserva.ACTIVA, idRecurso);
    }

    public boolean categoriaConReservaActiva(String idCategoria) {
        String sql = "select 1 from DetalleReserva d inner join Reserva r on d.idReserva = r.id "
                + "inner join Recurso rc on d.idRecursoAsignado = rc.id "
                + "where r.estado = ? and rc.idCategoria = ? limit 1";
        return existe(sql, EstadoReserva.ACTIVA, idCategoria);
    }

    public boolean funcionarioTieneReservas(String idFuncionario) {
        return existe("select 1 from Reserva where idFuncionario = ? limit 1", idFuncionario);
    }

    public int maxNumeroId() {
        String sql = "select max(cast(substring(id, 2) as unsigned)) from Reserva where id regexp '^R[0-9]+$'";
        try (PreparedStatement stm = db.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    // ---------- privados ----------

    private boolean existe(String sql, String... params) {
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) stm.setString(i + 1, params[i]);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<Reserva> cargar(String where, String param) {
        Map<String, List<DetalleReserva>> detalles = detallesPorReserva();
        List<Reserva> lista = new ArrayList<>();
        try (PreparedStatement stm = db.prepareStatement("select * from Reserva " + where + ORDEN)) {
            if (param != null) stm.setString(1, param);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) lista.add(from(rs, detalles));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return lista;
    }

    private Map<String, List<DetalleReserva>> detallesPorReserva() {
        Map<String, List<DetalleReserva>> mapa = new HashMap<>();
        try (PreparedStatement stm = db.prepareStatement("select * from DetalleReserva order by id");
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                DetalleReserva d = new DetalleReserva(rs.getString("idCategoriaSolicitada"));
                d.setIdRecursoAsignado(rs.getString("idRecursoAsignado"));
                mapa.computeIfAbsent(rs.getString("idReserva"), k -> new ArrayList<>()).add(d);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return mapa;
    }

    private Reserva from(ResultSet rs, Map<String, List<DetalleReserva>> detalles) throws SQLException {
        String id = rs.getString("id");
        Reserva r = new Reserva(
                id,
                rs.getString("idFuncionario"),
                rs.getString("actividad"),
                rs.getDate("fecha").toLocalDate(),
                rs.getTime("horaInicio").toLocalTime(),
                rs.getTime("horaFin").toLocalTime());
        r.setEstado(rs.getString("estado"));
        for (DetalleReserva d : detalles.getOrDefault(id, List.of())) {
            r.agregarDetalleReserva(d);
        }
        return r;
    }
}