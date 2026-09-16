package reservationSystem.presentation.categorias;

import reservationSystem.logic.entities.CategoriaRecurso;
import reservationSystem.presentation.TableModel;

import java.util.List;

public class CategoriasTableModel extends TableModel<CategoriaRecurso> {
    public CategoriasTableModel(List<CategoriaRecurso> datos) {
        super(new int[]{0, 1}, datos);
    }

    @Override
    protected void initColumnasNombre() {
        columnasNombre = new String[]{"ID", "Descripción"};
    }

    @Override
    public Object getValueAt(int row, int col) {
        CategoriaRecurso categoria = datos.get(row);
        return switch (col) {
            case 0 -> categoria.getId();
            case 1 -> categoria.getDescripcion();
            default -> null;
        };
    }
}
