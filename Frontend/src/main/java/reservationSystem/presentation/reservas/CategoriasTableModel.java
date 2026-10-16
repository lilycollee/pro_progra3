package reservationSystem.presentation.reservas;

import reservationSystem.logic.entities.CategoriaRecurso;
import reservationSystem.presentation.TableModel;

import java.util.List;

public class CategoriasTableModel extends TableModel<CategoriaRecurso> {
    public CategoriasTableModel(List<CategoriaRecurso> datos) {
        super(new int[]{0}, datos);
    }

    @Override
    protected void initColumnasNombre() {
        columnasNombre = new String[]{""};
    }

    @Override
    public Object getValueAt(int row, int col) {
        CategoriaRecurso categoriaRecurso = datos.get(row);
        return switch(col) {
            case 0 -> categoriaRecurso.getDescripcion();
            default -> null;
        };
    }
}
