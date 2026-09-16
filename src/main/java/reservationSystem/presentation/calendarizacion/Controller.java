package reservationSystem.presentation.calendarizacion;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javax.swing.JOptionPane;
import reservationSystem.logic.Service;
import reservationSystem.logic.entities.CategoriaRecurso;
import reservationSystem.logic.entities.Recurso;
import reservationSystem.logic.entities.Reserva;
import java.time.LocalDate;
import java.util.List;

public class Controller {
    private final CalendarizacionView view;
    private final Model model;
    private final Service service;

    public Controller(CalendarizacionView view, Model model, Service service) {
        this.view = view;
        this.model = model;
        this.service = service;

        view.setController(this);
        view.setModel(model);
        view.getTableModel().setService(service);

        view.addConsultarListener(e -> consultar());
        model.setCategorias(service.obtenerCategorias());
    }

    private void consultar() {
        LocalDate fecha = view.getFecha();
        CategoriaRecurso categoria = view.getCategoriaSeleccionada();

        if (fecha == null) {
            view.mostrarError("Seleccione una fecha");
            return;
        }
        if (categoria == null) {
            view.mostrarError("Seleccione una categoría de recurso");
            return;
        }

        List<Recurso> recursos = service.obtenerRecursosPorCategoria(categoria.getId());
        List<Reserva> reservas = service.obtenerReservasPorFechaYCategoria(fecha, categoria.getId());
        model.setCalendario(recursos, reservas);
    }

    public void imprimir() {
        try {
            generarPdf();
        } catch (Exception e) {
            view.mostrarError("Error al generar el PDF: " + e.getMessage());
        }
    }

    private void generarPdf() throws Exception {
        LocalDate fecha = view.getFecha();
        CategoriaRecurso categoria = view.getCategoriaSeleccionada();

        if (fecha == null) {
            view.mostrarError("Seleccione una fecha");
            return;
        }
        if (categoria == null) {
            view.mostrarError("Seleccione una categoría de recurso");
            return;
        }

        CalendarizacionTableModel tableModel = view.getTableModel();

        String dest = "calendarizacion.pdf";
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        Table header = new Table(1);
        header.setWidth(400);
        header.setHorizontalAlignment(HorizontalAlignment.CENTER);
        header.addCell(getCell(new Paragraph("Calendarización de Recursos - " + categoria.getDescripcion() + " (" + fecha + ")")
                .setFont(font).setBold().setFontSize(16), HorizontalAlignment.CENTER, false));
        header.addCell(getCell(new Image(ImageDataFactory.create(getClass().getResource("/iconos/calendarizacion.png"))),
                HorizontalAlignment.CENTER, false));
        document.add(header);

        int columnas = tableModel.getColumnCount();
        float[] anchos = new float[columnas];
        for (int i = 0; i < columnas; i++) anchos[i] = 1;

        Table tabla = new Table(anchos);
        tabla.setWidth(UnitValue.createPercentValue(100));

        for (int col = 0; col < columnas; col++) {
            tabla.addHeaderCell(getCell(new Paragraph(tableModel.getColumnName(col)).setFont(font).setBold(), HorizontalAlignment.LEFT, true));
        }

        for (int fila = 0; fila < tableModel.getRowCount(); fila++) {
            for (int col = 0; col < columnas; col++) {
                Object valor = tableModel.getValueAt(fila, col);
                tabla.addCell(getCell(new Paragraph(valor == null ? "" : valor.toString()).setFont(font), HorizontalAlignment.LEFT, true));
            }
        }

        document.add(tabla);
        document.close();

        JOptionPane.showMessageDialog(view, "PDF generado: " + dest);
    }

    private Cell getCell(Paragraph texto, HorizontalAlignment aligment, boolean hasBorder) {
        Cell cell = new Cell().add(texto);
        texto.setHorizontalAlignment(aligment);
        cell.setPadding(0);
        if (!hasBorder) {
            cell.setBorder(Border.NO_BORDER);
        }
        return cell;
    }

    private Cell getCell(Image image, HorizontalAlignment aligment, boolean hasBorder) {
        Cell cell = new Cell().add(image);
        image.setHorizontalAlignment(aligment);
        cell.setPadding(0);
        if (!hasBorder) {
            cell.setBorder(Border.NO_BORDER);
        }
        return cell;
    }
}