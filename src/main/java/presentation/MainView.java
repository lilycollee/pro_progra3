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

        if (usuarioActual.getRole().equals(Rol.ADMINISTRADOR)) {
            //-Funcionario pestaña
            FuncionarioView funcionarioView = new FuncionarioView();
            new presentation.funcionarios.Controller(funcionarioView, new presentation.funcionarios.Model(), service);
            pestañas.addTab("Funcionarios", funcionarioView);

            //-Estadisticas pestaña
            EstadisticasView estadisticasView = new EstadisticasView();
            new presentation.estadisticas.Controller(estadisticasView, new presentation.estadisticas.Model(), service);
            pestañas.addTab("Estadísticas", estadisticasView);

            //-Recursos pestaña
            presentation.recursos.RecursosView recursosView = new presentation.recursos.RecursosView();
            presentation.recursos.Model recursosModel = new presentation.recursos.Model();
            presentation.recursos.Controller recursosController =
                    new presentation.recursos.Controller(recursosView, recursosModel, service);
            pestañas.addTab("Recursos", recursosView);

            //-Categorías pestaña
            presentation.categorias.CategoriasView categoriasView = new presentation.categorias.CategoriasView();
            presentation.categorias.Model categoriasModel = new presentation.categorias.Model();
            new presentation.categorias.Controller(
                    categoriasView,
                    categoriasModel,
                    service,
                    recursosController::actualizarCategorias
            );
            pestañas.addTab("Categorías", categoriasView);

            //-Calendarización pestaña
            presentation.calendarizacion.CalendarizacionView calendarizacionView = new presentation.calendarizacion.CalendarizacionView();
            new presentation.calendarizacion.Controller(calendarizacionView, new presentation.calendarizacion.Model(), service);
            pestañas.addTab("Calendarización", calendarizacionView);

            //-Agenda pestaña
            presentation.agenda.AgendaView agendaView = new presentation.agenda.AgendaView();
            presentation.agenda.Model agendaModel = new presentation.agenda.Model();
            new presentation.agenda.Controller(agendaView, agendaModel, service);
            pestañas.addTab("Agenda semanal", agendaView);

        } else if (usuarioActual.getRole().equals(Rol.FUNCIONARIO)) {
            //-Reservas pestaña
            ReservasView reservasView = new ReservasView();
            new presentation.reservas.Controller(reservasView, new presentation.reservas.Model(), service, (Funcionario) usuarioActual);
            pestañas.addTab("Reservas", reservasView);

            //-Estadisticas pestaña
            EstadisticasView estadisticasView = new EstadisticasView();
            new presentation.estadisticas.Controller(estadisticasView, new presentation.estadisticas.Model(), service);
            pestañas.addTab("Estadísticas", estadisticasView);

            //-Calendarización pestaña
            presentation.calendarizacion.CalendarizacionView calendarizacionViewFuncionario = new presentation.calendarizacion.CalendarizacionView();
            new presentation.calendarizacion.Controller(calendarizacionViewFuncionario, new presentation.calendarizacion.Model(), service);
            pestañas.addTab("Calendarización", calendarizacionViewFuncionario);

            //-Agenda pestaña
            presentation.agenda.AgendaView agendaView = new presentation.agenda.AgendaView();
            presentation.agenda.Model agendaModel = new presentation.agenda.Model();
            new presentation.agenda.Controller(agendaView, agendaModel, service);
            pestañas.addTab("Agenda semanal", agendaView);
        }

        this.add(pestañas);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(1200, 700));
        this.setSize(1200, 700);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
