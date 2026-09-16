package reservationSystem.presentation.calendarizacion;

import reservationSystem.logic.Service;
import reservationSystem.logic.entities.DetalleReserva;
import reservationSystem.logic.entities.Recurso;
import reservationSystem.logic.entities.Reserva;
import javax.swing.table.AbstractTableModel;
import java.time.LocalTime;
import java.util.List;

public class CalendarizacionTableModel extends AbstractTableModel {
    private List<Recurso> recursos;
    private List<Reserva> reservas;
    private Service service;

    public CalendarizacionTableModel(List<Recurso> recursos, List<Reserva> reservas) {
        this.recursos = recursos;
        this.reservas = reservas;
    }
    public void setService(Service service) {
        this.service = service;
    }
    public void setDatos(List<Recurso> recursos, List<Reserva> reservas) {
        this.recursos = recursos;
        this.reservas = reservas;
        fireTableStructureChanged();
    }
    @Override
    public int getRowCount() {
        return 24;
    }
    @Override
    public int getColumnCount() {
        return recursos == null ? 1 : recursos.size() + 1;
    }
    @Override
    public String getColumnName(int column) {
        if (column == 0) return "Hora";
        return recursos.get(column - 1).getDescripcion();
    }
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return String.format("%02d:00", row);
        }
        Recurso recurso = recursos.get(column - 1);
        for (Reserva reserva : reservas) {
            if (!ocupaHora(reserva, row)) continue;
            for (DetalleReserva detalle : reserva.getDetalles()) {
                if (detalle.getIdRecursoAsignado() != null && detalle.getIdRecursoAsignado().equals(recurso.getId())) {
                    String nombreFuncionario = "Sin funcionario";
                    if (service != null) {
                        nombreFuncionario = service.obtenerFuncionario(reserva.getIdFuncionario()).map(f -> f.getNombre()).orElse("Sin funcionario");
                    }
                    return reserva.getActividad() + " - " + nombreFuncionario;
                }
            }
        }
        return "";
    }
    private boolean ocupaHora(Reserva reserva, int hora) {
        LocalTime inicioHora = LocalTime.of(hora, 0);
        LocalTime finHora = hora == 23 ? LocalTime.MAX : LocalTime.of(hora + 1, 0);
        return reserva.getHoraInicio().isBefore(finHora) && reserva.getHoraFin().isAfter(inicioHora);
    }
}