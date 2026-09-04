package presentation;

import logic.entities.enums.Rol;
import presentation.funcionarios.FuncionarioView;
import presentation.reservas.ReservasView;

import javax.swing.*;

public class MainView extends JFrame {
    private JTabbedPane pestnnias;

    public MainView(String rol) {
        super("Sistema de Reserva de Recursos");
        pestnnias = new JTabbedPane();

        if (rol.equals(Rol.ADMINISTRADOR)){
            pestnnias.addTab("Funcionarios", new FuncionarioView());
        } else if (rol.equals(Rol.FUNCIONARIO)){
            pestnnias.addTab("Reservas", new ReservasView());
        }
        this.add(pestnnias);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900,600);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
