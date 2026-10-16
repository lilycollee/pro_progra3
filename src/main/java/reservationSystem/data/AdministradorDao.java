package reservationSystem.data;

import reservationSystem.logic.entities.Administrador;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdministradorDao {
    private final Database db = Database.instance();

    public void save(Administrador a) {
        String sql = "insert into Administrador (id, clave) values (?, ?) "
                + "on duplicate key update clave = values(clave)";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, a.getId());
            stm.setString(2, a.getClave());
            stm.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Optional<Administrador> read(String id) {
        try (PreparedStatement stm = db.prepareStatement("select * from Administrador where id = ?")) {
            stm.setString(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() ? Optional.of(from(rs)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Administrador> findAll() {
        List<Administrador> lista = new ArrayList<>();
        try (PreparedStatement stm = db.prepareStatement("select * from Administrador order by id");
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) lista.add(from(rs));
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return lista;
    }

    public boolean delete(String id) {
        try (PreparedStatement stm = db.prepareStatement("delete from Administrador where id = ?")) {
            stm.setString(1, id);
            return stm.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Administrador from(ResultSet rs) throws SQLException {
        Administrador a = new Administrador(rs.getString("id"));
        a.cambiarClave(rs.getString("clave"));
        return a;
    }
}