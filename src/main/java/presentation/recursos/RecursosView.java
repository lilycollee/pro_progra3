package presentation.recursos;

import logic.entities.CategoriaRecurso;
import logic.entities.Recurso;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class RecursosView extends JPanel implements PropertyChangeListener {
    private JPanel panel;
    private JTextField idField;
    private JTextField descripcionField;
    private JComboBox<CategoriaRecurso> categoriaCombo;
    private JButton guardarButton;
    private JButton limpiarButton;
    private JTable tablaRecursos;

    private Controller controller;
    private Model model;
    private RecursosTableModel tableModel;

    public RecursosView() {
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        tableModel = new RecursosTableModel(new ArrayList<>());
        tablaRecursos.setModel(tableModel);
        tablaRecursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // El combo conserva objetos CategoriaRecurso, pero muestra únicamente
        // el nombre/descripción de la categoría al usuario.
        categoriaCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Model.RECURSOS -> {
                tableModel.setDatos(model.getRecursos());
                tableModel.fireTableDataChanged();
            }
            case Model.CATEGORIAS -> cargarCategorias(model.getCategorias());
            case Model.CURRENT -> mostrarCurrent();
        }
    }

    private void cargarCategorias(List<CategoriaRecurso> categorias) {
        categoriaCombo.removeAllItems();
        for (CategoriaRecurso categoria : categorias) {
            categoriaCombo.addItem(categoria);
        }
        categoriaCombo.setSelectedItem(null);
    }

    private void mostrarCurrent() {
        Recurso recurso = model.getCurrent();
        idField.setText(recurso.getId() == null ? "" : recurso.getId());
        descripcionField.setText(recurso.getDescripcion() == null ? "" : recurso.getDescripcion());
        categoriaCombo.setSelectedItem(recurso.getCategoriaRecurso());
    }

    public void addGuardarListener(ActionListener listener) {
        guardarButton.addActionListener(listener);
    }

    public void addLimpiarListener(ActionListener listener) {
        limpiarButton.addActionListener(listener);
    }

    public void addSeleccionListener(ListSelectionListener listener) {
        tablaRecursos.getSelectionModel().addListSelectionListener(listener);
    }

    public String getId() {
        return idField.getText().trim();
    }

    public String getDescripcion() {
        return descripcionField.getText().trim();
    }

    public CategoriaRecurso getCategoriaSeleccionada() {
        return (CategoriaRecurso) categoriaCombo.getSelectedItem();
    }

    public int getFilaSeleccionada() {
        return tablaRecursos.getSelectedRow();
    }

    public void limpiarCampos() {
        idField.setText("");
        descripcionField.setText("");
        categoriaCombo.setSelectedItem(null);
        tablaRecursos.clearSelection();
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
