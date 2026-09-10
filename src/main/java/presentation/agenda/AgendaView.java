package presentation.agenda;

import com.github.lgooddatepicker.components.DatePicker;
import presentation.TableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AgendaView extends JPanel implements PropertyChangeListener {
    private JPanel panelAgenda;
    private DatePicker fechaSemana;
    private JButton mostrarSemanaButton;
    private JButton semanaAnteriorButton;
    private JButton semanaSiguienteButton;
    private JButton imprimirButton;
    private JLabel rangoSemanaLabel;
    private JTable tablaAgenda;

    private Controller controller;
    private Model model;
    private AgendaTableModel tableModel;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AgendaView() {
        setLayout(new BorderLayout());
        add(panelAgenda, BorderLayout.CENTER);

        tableModel = new AgendaTableModel();
        tablaAgenda.setModel(tableModel);
        tablaAgenda.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablaAgenda.setRowHeight(52);
        tablaAgenda.setDefaultRenderer(Object.class, new AgendaCellRenderer());
        ajustarColumnas();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
    }

    public AgendaTableModel getTableModel() {
        return tableModel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Model.SEMANA, Model.RESERVAS -> actualizarAgenda();
        }
    }

    private void actualizarAgenda() {
        if (model == null || model.getInicioSemana() == null) return;

        tableModel.setDatos(model.getInicioSemana(), model.getReservas());
        LocalDate finSemana = model.getInicioSemana().plusDays(6);
        rangoSemanaLabel.setText("Semana: " + model.getInicioSemana().format(FORMATO_FECHA)
                + " - " + finSemana.format(FORMATO_FECHA));
        ajustarColumnas();
    }

    private void ajustarColumnas() {
        if (tablaAgenda.getColumnModel().getColumnCount() < 8) return;
        tablaAgenda.getColumnModel().getColumn(0).setPreferredWidth(65);
        tablaAgenda.getColumnModel().getColumn(0).setMinWidth(65);
        tablaAgenda.getColumnModel().getColumn(0).setMaxWidth(65);
        for (int i = 1; i < 8; i++) {
            tablaAgenda.getColumnModel().getColumn(i).setPreferredWidth(180);
        }
    }

    public void addMostrarSemanaListener(ActionListener listener) {
        mostrarSemanaButton.addActionListener(listener);
    }

    public void addSemanaAnteriorListener(ActionListener listener) {
        semanaAnteriorButton.addActionListener(listener);
    }

    public void addSemanaSiguienteListener(ActionListener listener) {
        semanaSiguienteButton.addActionListener(listener);
    }

    public void addImprimirListener(ActionListener listener) {
        imprimirButton.addActionListener(listener);
    }

    public LocalDate getFechaSeleccionada() {
        return fechaSemana.getDate();
    }

    public void setFechaSeleccionada(LocalDate fecha) {
        fechaSemana.setDate(fecha);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    private static class AgendaCellRenderer extends JTextArea implements TableCellRenderer {
        private final DefaultTableCellRenderer rendererBase = new DefaultTableCellRenderer();

        public AgendaCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component base = rendererBase.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setText(value == null ? "" : value.toString());
            setForeground(base.getForeground());
            setBackground(base.getBackground());
            setFont(base.getFont());
            if (column == 0) {
                setLineWrap(false);
                setFont(getFont().deriveFont(Font.BOLD));
            } else {
                setLineWrap(true);
            }
            return this;
        }
    }
}
