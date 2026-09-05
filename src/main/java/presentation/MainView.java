package presentation;

import logic.Service;
import logic.entities.Usuario;
import logic.entities.Funcionario;
import logic.entities.enums.Rol;

import presentation.funcionarios.FuncionarioView;
import presentation.funcionarios.Controller;
import presentation.funcionarios.Model;

import presentation.reservas.ReservasView;

import javax.swing.*;

public class MainView extends JFrame {
    private JTabbedPane pestañas;
    private Service service;

    public MainView(Usuario usuarioActual) {
        super("Sistema de Reserva de Recursos");
        service = new Service();
        pestañas = new JTabbedPane();

        if (usuarioActual.getRole().equals(Rol.ADMINISTRADOR)) {
            FuncionarioView funcionarioView = new FuncionarioView();
            Model funcionarioModel = new Model();
            new Controller(funcionarioView, funcionarioModel, service);
            pestañas.addTab("Funcionarios", funcionarioView);

        } else if (usuarioActual.getRole().equals(Rol.FUNCIONARIO)) {
            presentation.reservas.ReservasView reservasView = new ReservasView();
            presentation.reservas.Model reservasModel = new presentation.reservas.Model();
            new presentation.reservas.Controller(reservasView, reservasModel, service, (Funcionario) usuarioActual);
            pestañas.addTab("Reservas", reservasView);
        }

        this.add(pestañas);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 600);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
