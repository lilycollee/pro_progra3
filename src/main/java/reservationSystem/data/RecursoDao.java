package reservationSystem.data;

import reservationSystem.logic.entities.CategoriaRecurso;
import reservationSystem.logic.entities.Recurso;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecursoDao {
    private final Database db = Database.instance();

    private static final String SELECT_CON_CATEGORIA =
            "select r.id, r.descripcion, c.id as idCat, c.descripcion as descCat "
                    + "from Recurso r inner join CategoriaRecurso c on r.idCategoria = c.id ";

    public void save(Recurso r) {
        String sql = "insert into Recurso (id, descripcion, idCategoria) values (?, ?, ?) "
                + "on duplicate key update descripcion = values(descripcion), "
                + "idCategoria = values(idCategoria)";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, r.getId());
            stm.setString(2, r.getDescripcion());
            stm.setString(3, r.getIdCategoria());
            stm.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Optional<Recurso> read(String id) {
        try (PreparedStatement stm = db.prepareStatement(SELECT_CON_CATEGORIA + "where r.id = ?")) {
            stm.setString(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() ? Optional.of(from(rs)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Recurso> findAll() {
        List<Recurso> lista = new ArrayList<>();
        try (PreparedStatement stm = db.prepareStatement(SELECT_CON_CATEGORIA + "order by r.id");
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) lista.add(from(rs));
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return lista;
    }

    public List<Recurso> findByCategoria(String idCategoria) {
        List<Recurso> lista = new ArrayList<>();
        try (PreparedStatement stm = db.prepareStatement(SELECT_CON_CATEGORIA + "where c.id = ? order by r.id")) {
            stm.setString(1, idCategoria);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) lista.add(from(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return lista;
    }

    public boolean delete(String id) {
        try (PreparedStatement stm = db.prepareStatement("delete from Recurso where id = ?")) {
            stm.setString(1, id);
            return stm.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int maxNumeroId() {
        String sql = "select max(cast(substring(id, 4) as unsigned)) from Recurso where id regexp '^REC[0-9]+$'";
        try (PreparedStatement stm = db.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Recurso from(ResultSet rs) throws SQLException {
        CategoriaRecurso categoria = new CategoriaRecurso(rs.getString("idCat"), rs.getString("descCat"));
        return new Recurso(rs.getString("id"), rs.getString("descripcion"), categoria);
    }
}