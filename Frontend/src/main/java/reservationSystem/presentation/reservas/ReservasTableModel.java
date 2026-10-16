package reservationSystem.presentation.reservas;

import reservationSystem.logic.IService;
import reservationSystem.logic.entities.Reserva;
import reservationSystem.presentation.TableModel;

import java.util.List;

public class ReservasTableModel extends TableModel<Reserva> {
    private IService service;
    public ReservasTableModel(List<Reserva> datos) {
        super(new int[]{0, 1, 2, 3, 4, 5}, datos);
    }
    public void setService(IService service) {
        this.service = service;
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
            case 4 -> service != null ? service.obtenerRecursosAsignadosTexto(reserva) : "";
            case 5 -> reserva.getEstado();
            default -> null;
        };
    }
}
