package reservationSystem.presentation.reservas;

import com.github.lgooddatepicker.components.DatePicker;
import reservationSystem.logic.entities.CategoriaRecurso;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservasView extends JPanel implements PropertyChangeListener {
    private JPanel panelReservas;
    Controller controller;
    Model model;

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private JTextField fraseField;
    private JButton extraerButton;
    private JTextField actividadField;

    //Fecha
    private DatePicker fecha;

    //Horas
    private JTextField horaInicioField;
    private JComboBox horasInicio;
    private JTextField horaFinalField;
    private JComboBox horasFinal;

    //Reservas
    private JButton reservarButton;
    private JButton limpiarButton;
    private JButton cancelarReservaSelecionadaButton;
    private JTable tablaReservas;
    private JButton imprimirButton;
    private JTable tablaCategorias;

    private ReservasTableModel reservasTableModel;
    private CategoriasTableModel categoriasTableModel;

    public ReservasView() {
        setLayout(new java.awt.BorderLayout());
        add(panelReservas, java.awt.BorderLayout.CENTER);
        reservasTableModel = new ReservasTableModel(new ArrayList<>());
        categoriasTableModel = new CategoriasTableModel(new ArrayList<>());

        tablaReservas.setModel(reservasTableModel);
        tablaCategorias.setModel(categoriasTableModel);
        tablaCategorias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // El horasInicio/horasFinal llena el campo de texto correspondiente al elegir una hora
        horasInicio.addActionListener(e-> horaInicioField.setText(String.valueOf(horasInicio.getSelectedItem())));
        horasFinal.addActionListener(e-> horaFinalField.setText(String.valueOf(horasFinal.getSelectedItem())));
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt){
        switch (evt.getPropertyName()){
            case Model.RESERVAS -> reservasTableModel.setDatos(model.getReservas());
            case Model.CATEGORIAS -> categoriasTableModel.setDatos(model.getCategorias());
            case Model.CURRENT -> mostrarCurrent();
        }
    }
    public void mostrarCurrent(){

    }

    public void addReservarListener(ActionListener actionListener){
        reservarButton.addActionListener(actionListener);
    }
    public void addLimpiarListener(ActionListener actionListener){
        limpiarButton.addActionListener(actionListener);
    }
    public void addCancelarReservaListener(ActionListener actionListener){
        cancelarReservaSelecionadaButton.addActionListener(actionListener);
    }
    public void addImprimirListener(ActionListener actionListener){
        imprimirButton.addActionListener(actionListener);
    }
    public void addSeleccionReservaListener(javax.swing.event.ListSelectionListener actionListener){
        tablaReservas.getSelectionModel().addListSelectionListener(actionListener);
    }
    public void addExtraerIAListener(ActionListener actionListener){
        extraerButton.addActionListener(actionListener);
    }

    public ReservasTableModel getReservasTableModel() {
        return reservasTableModel;
    }
    public String getActividad() {
        return actividadField.getText();
    }
    public LocalDate getFecha() {
       return fecha.getDate();
    }
    public LocalTime getHoraInicio() {
        try {
            return LocalTime.parse(horaInicioField.getText().trim(), FORMATO_HORA);
        } catch (Exception e) {
            return null;
        }
    }
    public LocalTime getHoraFinal() {
        try {
            return LocalTime.parse(horaFinalField.getText().trim(), FORMATO_HORA);
        } catch (Exception e) {
            return null;
        }
    }
    public List<CategoriaRecurso> getCategoriasSeleccionadas() {
        List<CategoriaRecurso> categorias = new ArrayList<>();
        for (int fila : tablaCategorias.getSelectedRows()) {
            categorias.add(categoriasTableModel.getFila(fila));
        }
        return categorias;
    }
    public int getFilasReservaSeleccionada() {
        return tablaReservas.getSelectedRow();
    }

    public void limpiarCampos(){
        fraseField.setText("");
        actividadField.setText("");
        fecha.clear();
        horaInicioField.setText("");
        horaFinalField.setText("");
        tablaCategorias.clearSelection();
    }
    public void mostrarError(String mgs){
        JOptionPane.showMessageDialog(this, mgs, "Error", JOptionPane.ERROR_MESSAGE);
    }
    public void mostrarMensaje(String mgs){
        JOptionPane.showMessageDialog(this, mgs, "Mensaje", JOptionPane.INFORMATION_MESSAGE);
    }

    //-IA
    public String getFrase() {
        return fraseField.getText();
    }
    public void addExtraerListener(ActionListener actionListener) {
        extraerButton.addActionListener(actionListener);
    }
    public void setActividad(String actividad) {
        actividadField.setText(actividad);
    }
    public void setFecha(LocalDate fechaValor) {
        fecha.setDate(fechaValor);
    }
    public void setHoraInicio(LocalTime hora) {
        horaInicioField.setText(hora.format(FORMATO_HORA));
    }
    public void setHoraFinal(LocalTime hora) {
        horaFinalField.setText(hora.format(FORMATO_HORA));
    }
    public void seleccionarCategorias(List<CategoriaRecurso> categorias) {
        tablaCategorias.clearSelection();
        for (int fila = 0; fila < categoriasTableModel.getRowCount(); fila++) {
            CategoriaRecurso categoriaFila = categoriasTableModel.getFila(fila);
            boolean coincide = categorias.stream().anyMatch(c -> c.getId().equals(categoriaFila.getId()));
            if (coincide) {
                tablaCategorias.addRowSelectionInterval(fila, fila);
            }
        }
    }
}