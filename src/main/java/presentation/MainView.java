package presentation;

import logic.Service;
import logic.entities.Usuario;
import logic.entities.Funcionario;
import logic.entities.enums.Rol;

import presentation.estadisticas.EstadisticasView;
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
            //-Funcionario pestaña
            FuncionarioView funcionarioView = new FuncionarioView();
            new presentation.funcionarios.Controller(funcionarioView, new presentation.funcionarios.Model(), service);
            pestañas.addTab("Funcionarios", funcionarioView);

            //-Estadisticas pestaña
            EstadisticasView estadisticasView = new EstadisticasView();
            new presentation.estadisticas.Controller(estadisticasView, new presentation.estadisticas.Model(), service);
            pestañas.addTab("Estadísticas", estadisticasView);

        } else if (usuarioActual.getRole().equals(Rol.FUNCIONARIO)) {
            //-Reservas pestaña
            ReservasView reservasView = new ReservasView();
            new presentation.reservas.Controller(reservasView, new presentation.reservas.Model(), service, (Funcionario) usuarioActual);
            pestañas.addTab("Reservas", reservasView);

            //-Estadisticas pestaña
            EstadisticasView estadisticasView = new EstadisticasView();
            new presentation.estadisticas.Controller(estadisticasView, new presentation.estadisticas.Model(), service);
            pestañas.addTab("Estadísticas", estadisticasView);
        }

        this.add(pestañas);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 600);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
