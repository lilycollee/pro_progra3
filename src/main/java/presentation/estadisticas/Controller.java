package presentation.estadisticas;

import logic.Service;
import logic.entities.EstadisticaItem;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import java.awt.Color;

import java.time.LocalDate;
import java.util.List;

public class Controller {
    private EstadisticasView view;
    private Model model;
    private Service service;
    public Controller(EstadisticasView view, Model model, Service service){
        this.view = view;
        this.model = model;
        this.service = service;

        view.setController(this);
        view.setModel(model);

        view.addCargarRecursosButtonListener(e-> cargarRecursos());
        view.addCargarActividadesButtonListener(e-> cargarActividades());
    }

    private void cargarRecursos(){
        LocalDate desde = view.getFechaDesdeRecursos();
        LocalDate hasta = view.getFechaHastaRecursos();
        if (desde == null || hasta == null){
            view.mostrarError("Seleccione ambas fechas (desde y hasta) para recursos.");
            return;
        }
        if (desde.isAfter(hasta)){
            view.mostrarError("La fecha 'desde' debe ser anterior o igual a la fecha 'hasta'.");
            return;
        }
        List<EstadisticaItem> datos = service.obtenerEstadisticasRecurso(desde, hasta);
        model.setListaRecursos(datos);
        DefaultCategoryDataset dataset = crearDataset(datos, "Recurso");
        JFreeChart chart = crearGraficoBarras("Recursos Usados", "Categoría", "Cantidad", dataset, Color.RED);
        view.mostrarGraficoRecursos(chart);
    }
    private void cargarActividades(){
        LocalDate desde = view.getFechaDesdeActividades();
        LocalDate hasta = view.getFechaHastaActividades();
        if (desde == null || hasta == null){
            view.mostrarError("Seleccione ambas fechas (desde y hasta) para actividades.");
            return;
        }
        if (desde.isAfter(hasta)){
            view.mostrarError("La fecha 'desde' debe ser anterior o igual a la fecha 'hasta'.");
            return;
        }
        List<EstadisticaItem> datos = service.obtenerEstadisticasActividades(desde, hasta);
        model.setListaActividades(datos);
        DefaultCategoryDataset dataset = crearDataset(datos, "Semana");
        JFreeChart chart = crearGraficoBarras("Actividades Realizadas", "Semana", "Cantidad", dataset, Color.BLUE);
        view.mostrarGraficoActividades(chart);
    }

    private DefaultCategoryDataset crearDataset(List<EstadisticaItem> datos, String tipo){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (EstadisticaItem item : datos) {
            dataset.addValue(item.getCantidad(), tipo, item.getEtiqueta());
        }
        return dataset;
    }
    private JFreeChart crearGraficoBarras(String titulo, String categoria, String valor, DefaultCategoryDataset dataset, Color colorBarras) {
        JFreeChart chart = ChartFactory.createBarChart(titulo, categoria, valor, dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, colorBarras);
        return chart;
    }
}
