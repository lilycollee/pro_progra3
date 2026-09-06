package presentation.categorias;

import logic.entities.CategoriaRecurso;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class CategoriasView extends JPanel implements PropertyChangeListener {
    private JPanel panel;
    private JTextField idCategoria;
    private JTextField descripcionCategoria;
    private JButton guardarButton;
    private JButton limpiarButton;
    private JTable tablaCategorias;

    private Controller controller;
    private Model model;
    private CategoriasTableModel tableModel;

    public CategoriasView() {
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        tableModel = new CategoriasTableModel(new ArrayList<>());
        tablaCategorias.setModel(tableModel);
        tablaCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
            case Model.CATEGORIAS -> {
                tableModel.setDatos(model.getCategorias());
                tableModel.fireTableDataChanged();
            }
            case Model.CURRENT -> mostrarCurrent();
        }
    }

    private void mostrarCurrent() {
        CategoriaRecurso categoria = model.getCurrent();
        idCategoria.setText(categoria.getId() == null ? "" : categoria.getId());
        descripcionCategoria.setText(categoria.getDescripcion() == null ? "" : categoria.getDescripcion());
    }

    public void addGuardarButtonListener(ActionListener listener) {
        guardarButton.addActionListener(listener);
    }

    public void addLimpiarButtonListener(ActionListener listener) {
        limpiarButton.addActionListener(listener);
    }

    public void addSeleccionListener(ListSelectionListener listener) {
        tablaCategorias.getSelectionModel().addListSelectionListener(listener);
    }

    public String getIdCategoria() {
        return idCategoria.getText().trim();
    }

    public String getDescripcionCategoria() {
        return descripcionCategoria.getText().trim();
    }

    public int getFilaSeleccionada() {
        return tablaCategorias.getSelectedRow();
    }

    public void limpiarCampos() {
        idCategoria.setText("");
        descripcionCategoria.setText("");
        tablaCategorias.clearSelection();
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
