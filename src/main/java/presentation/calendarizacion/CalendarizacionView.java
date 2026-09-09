package presentation.calendarizacion;

import com.github.lgooddatepicker.components.DatePicker;
import logic.entities.CategoriaRecurso;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarizacionView extends JPanel implements PropertyChangeListener {
    private JPanel panel;
    private DatePicker fecha;
    private JComboBox<CategoriaRecurso> categoriaCombo;
    private JButton consultarButton;
    private JTable tablaCalendario;
    private JButton imprimirButton;

    private Controller controller;
    private Model model;
    private CalendarizacionTableModel tableModel;

    public CalendarizacionView() {
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        tableModel = new CalendarizacionTableModel(new ArrayList<>(), new ArrayList<>());
        tablaCalendario.setModel(tableModel);

        // Mismo renderer que usa RecursosView para mostrar la descripción, no el objeto crudo
        categoriaCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof CategoriaRecurso categoria) {
                    setText(categoria.getDescripcion());
                } else {
                    setText("");
                }
                return this;
            }
        });
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
    }

    public CalendarizacionTableModel getTableModel() {
        return tableModel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Model.CATEGORIAS -> cargarCategorias(model.getCategorias());
            case Model.CALENDARIO -> tableModel.setDatos(model.getRecursos(), model.getReservas());
        }
    }

    private void cargarCategorias(List<CategoriaRecurso> categorias) {
        categoriaCombo.removeAllItems();
        for (CategoriaRecurso categoria : categorias) {
            categoriaCombo.addItem(categoria);
        }
        categoriaCombo.setSelectedItem(null);
    }

    public void addConsultarListener(ActionListener listener) {
        consultarButton.addActionListener(listener);
    }

    public LocalDate getFecha() {
        return fecha.getDate();
    }

    public CategoriaRecurso getCategoriaSeleccionada() {
        return (CategoriaRecurso) categoriaCombo.getSelectedItem();
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}