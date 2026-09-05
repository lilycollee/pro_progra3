package presentation.funcionarios;

import logic.entities.Funcionario;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ArrayList;

public class FuncionarioView extends JPanel implements PropertyChangeListener {
    private JPanel panel;
    Controller controller;
    Model model;

    //Busqueda
    private JTextField idB;
    private JTextField nombreB;
    private JButton imprimirButton;
    private JButton buscarButton;

    //Funcionario
    private JTextField idF;
    private JTextField nombreF;
    private JTextField telefonoF;
    private JButton guardarButton;
    private JButton borrarButton;
    private JButton limpiarButton;

    //Listado
    private JTable tablaDeListado;
    private FuncionariosTableModel tableModel;

    public FuncionarioView(){
        setLayout(new BorderLayout());
        add(panel,BorderLayout.CENTER);

        tableModel = new FuncionariosTableModel(new ArrayList<>());
        tablaDeListado.setModel(tableModel);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
    }

    public void mostrarListado(List<Funcionario> funcionarios){
        tableModel.setDatos(funcionarios);
        tableModel.fireTableDataChanged();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()){
            case Model.LIST -> tableModel.setDatos(model.getListaFuncionarios());
            case Model.CURRENT -> mostrarCurrent();
        }
    }

    public void mostrarCurrent(){
        Funcionario funcionario =  model.getCurrent();
        idF.setText(funcionario.getId() == null ? "" : funcionario.getId());
        nombreF.setText(funcionario.getNombre()  == null ? "" : funcionario.getNombre());
        telefonoF.setText(funcionario.getTelefono() == null ? "" : funcionario.getTelefono());
    }

    //--------- Listeners ---------
    public void addGuardarButtonListener(ActionListener actionListener){
        guardarButton.addActionListener(actionListener);
    }
    public void addBorrarButtonListener(ActionListener actionListener){
        borrarButton.addActionListener(actionListener);
    }
    public void addLimpiarButtonListener(ActionListener actionListener){
        limpiarButton.addActionListener(actionListener);
    }
    public void addImprimirButtonListener(ActionListener actionListener){
        imprimirButton.addActionListener(actionListener);
    }
    public void addBuscarButtonListener(ActionListener actionListener){
        buscarButton.addActionListener(actionListener);
    }
    public void addSeleccionListener(ListSelectionListener actionListener) {
        tablaDeListado.getSelectionModel().addListSelectionListener(actionListener);
    }

    //--------- Getters ---------
    public String getIdF() {
        return idF.getText().trim();
    }
    public String getNombreF() {
        return nombreF.getText().trim();
    }
    public String getTelefonoF() {
        return telefonoF.getText().trim();
    }
    public String getIdB() {
        return idB.getText().trim();
    }
    public String getNombreB() {
        return nombreB.getText().trim();
    }
    public int getFilaSeleccionada() {
        return tablaDeListado.getSelectedRow();
    }

    public void mostrarError(String msj){
        JOptionPane.showMessageDialog(this, msj, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
