package presentation.recursos;

import logic.entities.Recurso;
import presentation.TableModel;

import java.util.List;

public class RecursosTableModel extends TableModel<Recurso> {
    public RecursosTableModel(List<Recurso> datos) {
        super(new int[]{0, 1, 2}, datos);
    }

    @Override
    protected void initColumnasNombre() {
        columnasNombre = new String[]{"ID", "Descripción", "Categoría"};
    }

    @Override
    public Object getValueAt(int row, int col) {
        Recurso recurso = datos.get(row);
        return switch (col) {
            case 0 -> recurso.getId();
            case 1 -> recurso.getDescripcion();
            case 2 -> recurso.getCategoriaRecurso() == null ? "" : recurso.getCategoriaRecurso().getDescripcion();
            default -> null;
        };
    }
}
