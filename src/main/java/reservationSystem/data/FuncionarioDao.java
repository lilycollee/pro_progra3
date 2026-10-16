package reservationSystem.data;

import reservationSystem.logic.entities.Funcionario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FuncionarioDao {
    private final Database db = Database.instance();

    public void save(Funcionario f) {
        String sql = "insert into Funcionario (id, clave, nombre, telefono) values (?, ?, ?, ?) "
                + "on duplicate key update clave = values(clave), nombre = values(nombre), "
                + "telefono = values(telefono)";
        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, f.getId());
            stm.setString(2, f.getClave());
            stm.setString(3, f.getNombre());
            stm.setString(4, f.getTelefono());
            stm.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Optional<Funcionario> read(String id) {
        try (PreparedStatement stm = db.prepareStatement("select * from Funcionario where id = ?")) {
            stm.setString(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() ? Optional.of(from(rs)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Funcionario> findAll() {
        List<Funcionario> lista = new ArrayList<>();
        try (PreparedStatement stm = db.prepareStatement("select * from Funcionario order by id");
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) lista.add(from(rs));
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return lista;
    }

    public boolean delete(String id) {
        try (PreparedStatement stm = db.prepareStatement("delete from Funcionario where id = ?")) {
            stm.setString(1, id);
            return stm.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Funcionario from(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario(rs.getString("id"), rs.getString("nombre"), rs.getString("telefono"));
        f.cambiarClave(rs.getString("clave"));
        return f;
    }
}