package presentation.reservas;

import logic.entities.Reserva;
import presentation.TableModel;

import java.util.List;

public class ReservasTableModel extends TableModel<Reserva> {
    public ReservasTableModel(List<Reserva> datos) {
        super(new int[]{0, 1, 2, 3, 4, 5}, datos);
    }

    @Override
    protected void initColumnasNombre() {
        columnasNombre = new String[]{"Id", "Actividad", "Fecha", "Horario", "Recursos", "Estado"};
    }

    @Override
    public Object getValueAt(int row, int col) {
        Reserva reserva = datos.get(row);
        return switch(col) {
            case 0 -> reserva.getId();
            case 1 -> reserva.getActividad();
            case 2 -> reserva.getFecha();
            case 3 -> reserva.getHoraInicio() + "-"  + reserva.getHoraFin();
            case 4 -> reserva.getRecursosAsignado();
            case 5 -> reserva.getEstado();
            default -> null;
        };
    }
}
