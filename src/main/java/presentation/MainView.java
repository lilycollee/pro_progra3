package presentation;

import logic.Service;
import logic.entities.Usuario;
import logic.entities.Funcionario;
import logic.entities.enums.Rol;
import java.awt.Dimension;
import presentation.estadisticas.EstadisticasView;
import presentation.funcionarios.FuncionarioView;
import presentation.funcionarios.Controller;
import presentation.funcionarios.Model;

import presentation.recursos.RecursosView;
import presentation.reservas.ReservasView;

import javax.swing.*;

public class MainView extends JFrame {
    private JTabbedPane pestañas;
    private Service service;

    public MainView(Usuario usuarioActual) {
        super("Sistema de Reserva de Recursos");
        service = new Service();
        pestañas = new JTabbedPane();
        ImageIcon iconoCalendarizacion = new ImageIcon(getClass().getResource("/iconos/calendarizacion.png"));
        ImageIcon iconoReservas = new ImageIcon(getClass().getResource("/iconos/reservas.png"));
        ImageIcon iconoEstadisticas = new ImageIcon(getClass().getResource("/iconos/statistics.png"));
        ImageIcon iconoFuncionarios = new ImageIcon(getClass().getResource("/iconos/funcionarios.png"));
        ImageIcon iconoCategorias = new ImageIcon(getClass().getResource("/iconos/categorias.png"));
        ImageIcon iconoRecursos = new ImageIcon(getClass().getResource("/iconos/recursos.png"));
        ImageIcon iconoAgenda = new ImageIcon(getClass().getResource("/iconos/actividades.png"));

        if (usuarioActual.getRole().equals(Rol.ADMINISTRADOR)) {
            //-Funcionario pestaña
            FuncionarioView funcionarioView = new FuncionarioView();
            new presentation.funcionarios.Controller(funcionarioView, new presentation.funcionarios.Model(), service);
            pestañas.addTab("Funcionarios", iconoFuncionarios, funcionarioView);

            //-Estadisticas pestaña
            EstadisticasView estadisticasView = new EstadisticasView();
            new presentation.estadisticas.Controller(estadisticasView, new presentation.estadisticas.Model(), service);
            pestañas.addTab("Estadísticas", iconoEstadisticas, estadisticasView);

            //-Recursos pestaña
            presentation.recursos.RecursosView recursosView = new presentation.recursos.RecursosView();
            presentation.recursos.Model recursosModel = new presentation.recursos.Model();
            presentation.recursos.Controller recursosController = new presentation.recursos.Controller(recursosView, recursosModel, service);
            pestañas.addTab("Recursos", iconoRecursos, recursosView);

            //-Categorías pestaña
            presentation.categorias.CategoriasView categoriasView = new presentation.categorias.CategoriasView();
            presentation.categorias.Model categoriasModel = new presentation.categorias.Model();
            new presentation.categorias.Controller(
                    categoriasView,
                    categoriasModel,
                    service,
                    recursosController::actualizarCategorias
            );
            pestañas.addTab("Categorías", iconoCategorias, categoriasView);

            //-Calendarización pestaña
            presentation.calendarizacion.CalendarizacionView calendarizacionView = new presentation.calendarizacion.CalendarizacionView();
            new presentation.calendarizacion.Controller(calendarizacionView, new presentation.calendarizacion.Model(), service);
            pestañas.addTab("Calendarización", iconoCalendarizacion, calendarizacionView);

            //-Agenda pestaña
            presentation.agenda.AgendaView agendaView = new presentation.agenda.AgendaView();
            presentation.agenda.Model agendaModel = new presentation.agenda.Model();
            new presentation.agenda.Controller(agendaView, agendaModel, service);
            pestañas.addTab("Actividades", iconoAgenda, agendaView);

        } else if (usuarioActual.getRole().equals(Rol.FUNCIONARIO)) {
            //-Reservas pestaña
            ReservasView reservasView = new ReservasView();
            new presentation.reservas.Controller(reservasView, new presentation.reservas.Model(), service, (Funcionario) usuarioActual);
            pestañas.addTab("Reservas", iconoReservas ,reservasView);

            //-Estadisticas pestaña
            EstadisticasView estadisticasView = new EstadisticasView();
            new presentation.estadisticas.Controller(estadisticasView, new presentation.estadisticas.Model(), service);
            pestañas.addTab("Estadísticas", iconoEstadisticas, estadisticasView);

            //-Calendarización pestaña
            presentation.calendarizacion.CalendarizacionView calendarizacionViewFuncionario = new presentation.calendarizacion.CalendarizacionView();
            new presentation.calendarizacion.Controller(calendarizacionViewFuncionario, new presentation.calendarizacion.Model(), service);
            pestañas.addTab("Calendarización", iconoCalendarizacion, calendarizacionViewFuncionario);

            //-Agenda pestaña
            presentation.agenda.AgendaView agendaView = new presentation.agenda.AgendaView();
            presentation.agenda.Model agendaModel = new presentation.agenda.Model();
            new presentation.agenda.Controller(agendaView, agendaModel, service);
            pestañas.addTab("Agenda semanal", iconoAgenda, agendaView);
        }

        JButton cerrarSesionButton = new JButton("Cerrar sesión");
        cerrarSesionButton.addActionListener(e -> cerrarSesion());
        JPanel panelSuperior = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        panelSuperior.add(cerrarSesionButton);

        this.add(panelSuperior, java.awt.BorderLayout.NORTH);
        this.add(pestañas, java.awt.BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(1200, 700));
        this.setSize(1200, 700);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    private void cerrarSesion() {
        this.dispose();
        presentation.login.LoginView loginView = new presentation.login.LoginView();
        presentation.login.Model loginModel = new presentation.login.Model();
        new presentation.login.Controller(loginView, loginModel, new Service());
        loginView.setVisible(true);
    }
}
