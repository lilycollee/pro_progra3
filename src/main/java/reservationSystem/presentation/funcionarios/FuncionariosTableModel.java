package reservationSystem.presentation.funcionarios;

import reservationSystem.logic.entities.Funcionario;
import reservationSystem.presentation.TableModel;
import java.util.List;

public class FuncionariosTableModel extends TableModel<Funcionario> {
    public FuncionariosTableModel(List<Funcionario> datos) {
        super(new int[]{0, 1, 2}, datos);
    }

    @Override
    protected void initColumnasNombre() {
        columnasNombre = new String[]{"Id", "Nombre", "Telefono"};
    }

    @Override
    public Object getValueAt(int row, int col) {
        Funcionario funcionario = datos.get(row);
        return switch(col) {
            case 0 -> funcionario.getId();
            case 1 -> funcionario.getNombre();
            case 2 -> funcionario.getTelefono();
            default -> null;
        };
    }
}
