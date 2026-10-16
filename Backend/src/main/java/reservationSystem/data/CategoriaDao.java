package reservationSystem.data;

import reservationSystem.logic.entities.CategoriaRecurso;
import java.sql.*;
import java.util.*;

public class CategoriaDao {
    private final Database db = Database.instance();

    public void save(CategoriaRecurso c) {
        String sql = "insert into CategoriaRecurso (id, descripcion) values (?, ?) "
                + "on duplicate key update descripcion = values(descripcion)";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, c.getId());
            stm.setString(2, c.getDescripcion());
            stm.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Optional<CategoriaRecurso> read(String id) {
        try (PreparedStatement stm = db.prepareStatement("select * from CategoriaRecurso where id = ?")) {
            stm.setString(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() ? Optional.of(from(rs)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<CategoriaRecurso> findAll() {
        List<CategoriaRecurso> lista = new ArrayList<>();
        try (PreparedStatement stm = db.prepareStatement("select * from CategoriaRecurso order by id");
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) lista.add(from(rs));
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return lista;
    }

    public boolean delete(String id) {
        try (PreparedStatement stm = db.prepareStatement("delete from CategoriaRecurso where id = ?")) {
            stm.setString(1, id);
            return stm.executeUpdate() > 0;     // los recursos se borran por ON DELETE CASCADE
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int maxNumeroId() {
        String sql = "select max(cast(substring(id, 4) as unsigned)) from CategoriaRecurso where id regexp '^CAT[0-9]+$'";
        try (PreparedStatement stm = db.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private CategoriaRecurso from(ResultSet rs) throws SQLException {
        return new CategoriaRecurso(rs.getString("id"), rs.getString("descripcion"));
    }
}