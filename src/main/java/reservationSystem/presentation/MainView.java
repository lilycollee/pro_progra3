package reservationSystem.presentation;

import reservationSystem.logic.Service;
import reservationSystem.logic.entities.Usuario;
import reservationSystem.logic.entities.Funcionario;
import reservationSystem.logic.entities.enums.Rol;
import java.awt.Dimension;
import reservationSystem.presentation.estadisticas.EstadisticasView;
import reservationSystem.presentation.funcionarios.FuncionarioView;

import reservationSystem.presentation.reservas.ReservasView;

import javax.swing.*;

public class MainView extends JFrame {
    private JTabbedPane pestañas;
    private Service service;

    public MainView(Usuario usuarioActual, Service service) {
        super("Sistema de Reserva de Recursos");
        this.service = service;
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
            new reservationSystem.presentation.funcionarios.Controller(funcionarioView, new reservationSystem.presentation.funcionarios.Model(), service);
            pestañas.addTab("Funcionarios", iconoFuncionarios, funcionarioView);

            //-Estadisticas pestaña
            EstadisticasView estadisticasView = new EstadisticasView();
            new reservationSystem.presentation.estadisticas.Controller(estadisticasView, new reservationSystem.presentation.estadisticas.Model(), service);
            pestañas.addTab("Estadísticas", iconoEstadisticas, estadisticasView);

            //-Recursos pestaña
            reservationSystem.presentation.recursos.RecursosView recursosView = new reservationSystem.presentation.recursos.RecursosView();
            reservationSystem.presentation.recursos.Model recursosModel = new reservationSystem.presentation.recursos.Model();
            reservationSystem.presentation.recursos.Controller recursosController = new reservationSystem.presentation.recursos.Controller(recursosView, recursosModel, service);
            pestañas.addTab("Recursos", iconoRecursos, recursosView);

            //-Categorías pestaña
            reservationSystem.presentation.categorias.CategoriasView categoriasView = new reservationSystem.presentation.categorias.CategoriasView();
            reservationSystem.presentation.categorias.Model categoriasModel = new reservationSystem.presentation.categorias.Model();
            new reservationSystem.presentation.categorias.Controller(
                    categoriasView,
                    categoriasModel,
                    service,
                    recursosController::actualizarCategorias
            );
            pestañas.addTab("Categorías", iconoCategorias, categoriasView);

            //-Calendarización pestaña
            reservationSystem.presentation.calendarizacion.CalendarizacionView calendarizacionView = new reservationSystem.presentation.calendarizacion.CalendarizacionView();
            new reservationSystem.presentation.calendarizacion.Controller(calendarizacionView, new reservationSystem.presentation.calendarizacion.Model(), service);
            pestañas.addTab("Calendarización", iconoCalendarizacion, calendarizacionView);

            //-Agenda pestaña
            reservationSystem.presentation.agenda.AgendaView agendaView = new reservationSystem.presentation.agenda.AgendaView();
            reservationSystem.presentation.agenda.Model agendaModel = new reservationSystem.presentation.agenda.Model();
            new reservationSystem.presentation.agenda.Controller(agendaView, agendaModel, service);
            pestañas.addTab("Actividades", iconoAgenda, agendaView);

        } else if (usuarioActual.getRole().equals(Rol.FUNCIONARIO)) {
            //-Reservas pestaña
            ReservasView reservasView = new ReservasView();
            new reservationSystem.presentation.reservas.Controller(reservasView, new reservationSystem.presentation.reservas.Model(), service, (Funcionario) usuarioActual);
            pestañas.addTab("Reservas", iconoReservas ,reservasView);

            //-Estadisticas pestaña
            EstadisticasView estadisticasView = new EstadisticasView();
            new reservationSystem.presentation.estadisticas.Controller(estadisticasView, new reservationSystem.presentation.estadisticas.Model(), service);
            pestañas.addTab("Estadísticas", iconoEstadisticas,estadisticasView);

            //-Calendarización pestaña
            reservationSystem.presentation.calendarizacion.CalendarizacionView calendarizacionViewFuncionario = new reservationSystem.presentation.calendarizacion.CalendarizacionView();
            new reservationSystem.presentation.calendarizacion.Controller(calendarizacionViewFuncionario, new reservationSystem.presentation.calendarizacion.Model(), service);
            pestañas.addTab("Calendarización", iconoCalendarizacion, calendarizacionViewFuncionario);

            //-Agenda pestaña
            reservationSystem.presentation.agenda.AgendaView agendaView = new reservationSystem.presentation.agenda.AgendaView();
            reservationSystem.presentation.agenda.Model agendaModel = new reservationSystem.presentation.agenda.Model();
            new reservationSystem.presentation.agenda.Controller(agendaView, agendaModel, service);
            pestañas.addTab("Actividades", iconoAgenda, agendaView);
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
        reservationSystem.presentation.login.LoginView loginView = new reservationSystem.presentation.login.LoginView();
        reservationSystem.presentation.login.Model loginModel = new reservationSystem.presentation.login.Model();
        new reservationSystem.presentation.login.Controller(loginView, loginModel, new Service());
        loginView.setVisible(true);
    }
}
