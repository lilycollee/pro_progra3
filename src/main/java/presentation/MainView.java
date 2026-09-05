package presentation;

import logic.Service;
import logic.entities.enums.Rol;

import presentation.funcionarios.FuncionarioView;
import presentation.funcionarios.Controller;
import presentation.funcionarios.Model;

import presentation.reservas.ReservasView;

import javax.swing.*;

public class MainView extends JFrame {
    private JTabbedPane pestañas;
    private Service service;

    public MainView(String rol) {
        super("Sistema de Reserva de Recursos");
        service = new Service();
        pestañas = new JTabbedPane();

        if (rol.equals(Rol.ADMINISTRADOR)){
            FuncionarioView funcionarioView = new FuncionarioView();
            Model funcionarioModel = new Model();
            new Controller(funcionarioView, funcionarioModel, service);
            pestañas.addTab("Funcionarios", funcionarioView);

        } else if (rol.equals(Rol.FUNCIONARIO)){
            pestañas.addTab("Reservas", new ReservasView());
        }
        this.add(pestañas);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900,600);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
