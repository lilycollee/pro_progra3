package presentation.funcionarios;

import logic.entities.Funcionario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioView extends JPanel{
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

    public void setController(Controller controller) {
        this.controller = controller;
    }
    public void setModel(Model model) {
        this.model = model;
    }

    public FuncionarioView(){
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        tableModel = new FuncionariosTableModel(new ArrayList<>());
        tablaDeListado.setModel(tableModel);
    }

    public void mostrarListado(List<Funcionario> funcionarios){
        tableModel.setDatos(funcionarios);
        tableModel.fireTableDataChanged();
    }
}
