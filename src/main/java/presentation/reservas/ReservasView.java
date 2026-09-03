package presentation.reservas;

import javax.swing.*;

public class ReservasView extends JPanel{
    private JPanel panelReservas;
    Controller controller;
    Model model;

    //Nueva Reserva
    private JPanel panel;
    private JTextField fraseField;
    private JButton extraerButton;
    private JTextField actividadField;

    //Fecha
    private JTextField fechaField;
    private JButton fechasButton;

    //Horas
    private JTextField horaInicioField;
    private JComboBox comboBox1;
    private JTextField horaFinalField;
    private JComboBox comboBox2;

    //Reservas
    private JButton reservarButton;
    private JButton limpiarButton;
    private JButton cancelarReservaSelecionadaButton;
    private JTable tablaReservas;
    private JButton imprimirButton;
    private JTable tablaCategorias;
}
