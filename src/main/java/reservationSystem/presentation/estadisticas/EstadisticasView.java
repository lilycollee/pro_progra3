package reservationSystem.presentation.estadisticas;

import com.github.lgooddatepicker.components.DatePicker;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.ArrayList;

public class EstadisticasView extends JPanel implements PropertyChangeListener {
    private JPanel panelPrincipal;
    Controller controller;
    Model model;

    //------------------------------------------------
    private DatePicker fechaDesdeRecursos;
    private DatePicker fechaHastaRecursos;
    private JButton cargarRecursosButton;
    private JTable tablaEstadisticasRecursos;
    private JTable tablaEstadisticasActividades;
    private DatePicker fechaHastaActividades;
    private DatePicker fechaDesdeActividades;
    private JButton cargarActividadesButton;
    private JPanel panelGraficoRecursos;
    private JPanel panelGraficoActividades;

    //-Listado
    private ActividadesTableModel actividadesTableModel;
    private RecursosTableModel recursosTableModel;

    public EstadisticasView() {
        setLayout(new BorderLayout());
        add(panelPrincipal,BorderLayout.CENTER);

        actividadesTableModel = new ActividadesTableModel(new ArrayList<>());
        tablaEstadisticasActividades.setModel(actividadesTableModel);
        recursosTableModel = new RecursosTableModel(new ArrayList<>());
        tablaEstadisticasRecursos.setModel(recursosTableModel);
    }
    public void setController(Controller controller) {
        this.controller = controller;
    }
    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Model.LISTA_RECURSOS -> recursosTableModel.setDatos(model.getListaRecursos());
            case Model.LISTA_ACTIVIDADES ->  actividadesTableModel.setDatos(model.getListaActividades());
        }
    }

    //--------- Listeners ---------
    public void addCargarActividadesButtonListener(ActionListener actionListener){
        cargarActividadesButton.addActionListener(actionListener);
    }
    public void addCargarRecursosButtonListener(ActionListener actionListener){
        cargarRecursosButton.addActionListener(actionListener);
    }

    //--------- Getters ---------
    public LocalDate getFechaDesdeRecursos() {
        return fechaDesdeRecursos.getDate();
    }
    public LocalDate getFechaHastaRecursos() {
        return fechaHastaRecursos.getDate();
    }
    public LocalDate getFechaDesdeActividades() {
        return fechaDesdeActividades.getDate();
    }
    public LocalDate getFechaHastaActividades() {
        return fechaHastaActividades.getDate();
    }

    //-Graficos
    public void mostrarGraficoRecursos(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        panelGraficoRecursos.removeAll();
        panelGraficoRecursos.setLayout(new BorderLayout());
        panelGraficoRecursos.add(chartPanel, BorderLayout.CENTER);
        panelGraficoRecursos.revalidate();
        panelGraficoRecursos.repaint();
    }

    public void mostrarGraficoActividades(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        panelGraficoActividades.removeAll();
        panelGraficoActividades.setLayout(new BorderLayout());
        panelGraficoActividades.add(chartPanel, BorderLayout.CENTER);
        panelGraficoActividades.revalidate();
        panelGraficoActividades.repaint();
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
