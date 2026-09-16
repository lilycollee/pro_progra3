package reservationSystem.presentation.agenda;

import reservationSystem.logic.Service;
import reservationSystem.logic.entities.Reserva;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AgendaTableModel extends AbstractTableModel {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM");
    private LocalDate inicioSemana;
    private List<Reserva> reservas;
    private Service service;

    public AgendaTableModel() {
        inicioSemana = LocalDate.now();
        reservas = new ArrayList<>();
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setDatos(LocalDate inicioSemana, List<Reserva> reservas) {
        this.inicioSemana = inicioSemana;
        this.reservas = reservas == null ? new ArrayList<>() : reservas;
        fireTableStructureChanged();
    }

    @Override
    public int getRowCount() {
        return 24;
    }

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) return "Hora";

        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        LocalDate fecha = inicioSemana.plusDays(column - 1);
        return dias[column - 1] + " " + fecha.format(FORMATO_FECHA);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return String.format("%02d:00", rowIndex);
        }

        LocalDate fecha = inicioSemana.plusDays(columnIndex - 1);
        LocalTime inicioBloque = LocalTime.of(rowIndex, 0);
        LocalTime finBloque = rowIndex == 23 ? LocalTime.MAX : LocalTime.of(rowIndex + 1, 0);

        StringBuilder actividades = new StringBuilder();
        for (Reserva reserva : reservas) {
            if (reserva.getFecha() == null || !reserva.getFecha().equals(fecha)) continue;
            if (reserva.getHoraInicio() == null || reserva.getHoraFin() == null) continue;
            boolean seSolapa = reserva.getHoraInicio().isBefore(finBloque) && reserva.getHoraFin().isAfter(inicioBloque);
            if (seSolapa) {
                if (actividades.length() > 0) actividades.append("\n");
                String funcionario = "Sin funcionario";
                if (service != null && reserva.getIdFuncionario() != null) {
                    funcionario = service.obtenerFuncionario(reserva.getIdFuncionario()).map(f -> f.getNombre()).orElse("Sin funcionario");
                }
                actividades.append(reserva.getActividad()).append(" - ").append(funcionario);
            }
        }
        return actividades.toString();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
