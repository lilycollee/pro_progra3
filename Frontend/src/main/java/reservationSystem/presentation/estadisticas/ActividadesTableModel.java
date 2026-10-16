package reservationSystem.presentation.estadisticas;

import reservationSystem.logic.entities.EstadisticaItem;
import reservationSystem.presentation.TableModel;
import java.util.List;

public class ActividadesTableModel extends TableModel<EstadisticaItem> {
    public ActividadesTableModel(List<EstadisticaItem> datos) {
        super(new int[]{0, 1}, datos);
    }
    @Override
    protected void initColumnasNombre() {
        columnasNombre = new String[]{"Semana", "Cantidad"};
    }
    @Override
    public Object getValueAt(int row, int col) {
        EstadisticaItem item = datos.get(row);
        return switch (col) {
            case 0 -> item.getEtiqueta();
            case 1 -> item.getCantidad();
            default -> null;
        };
    }
}
