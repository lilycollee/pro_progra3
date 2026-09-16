package reservationSystem.presentation.agenda;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import reservationSystem.logic.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.format.DateTimeFormatter;

public class Controller {
    private final AgendaView view;
    private final Model model;
    private final Service service;

    public Controller(AgendaView view, Model model, Service service) {
        this.view = view;
        this.model = model;
        this.service = service;

        view.setController(this);
        view.setModel(model);
        view.getTableModel().setService(service);

        view.addMostrarSemanaListener(e -> mostrarSemanaSeleccionada());
        view.addSemanaAnteriorListener(e -> cambiarSemana(-1));
        view.addSemanaSiguienteListener(e -> cambiarSemana(1));
        view.addImprimirListener(e -> imprimir());

        cargarSemana(LocalDate.now());
    }

    private void mostrarSemanaSeleccionada() {
        LocalDate fecha = view.getFechaSeleccionada();
        if (fecha == null) {
            view.mostrarError("Seleccione una fecha para mostrar su semana.");
            return;
        }
        cargarSemana(fecha);
    }

    private void cambiarSemana(int cantidadSemanas) {
        LocalDate inicioActual = model.getInicioSemana();
        if (inicioActual == null) inicioActual = LocalDate.now();
        cargarSemana(inicioActual.plusWeeks(cantidadSemanas));
    }

    private void imprimir() {
        if (model.getInicioSemana() == null) {
            view.mostrarError("No hay una semana cargada para imprimir.");
            return;
        }

        DateTimeFormatter formatoArchivo = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dest = "agenda_" + model.getInicioSemana().format(formatoArchivo) + ".pdf";

        try {
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setDefaultPageSize(PageSize.A4.rotate());

            Document document = new Document(pdf);
            document.setMargins(18, 18, 18, 18);

            LocalDate finSemana = model.getInicioSemana().plusDays(6);
            Table header = new Table(1);
            header.setWidth(400);
            header.setHorizontalAlignment(HorizontalAlignment.CENTER);
            Paragraph titulo = new Paragraph("Agenda semanal")
                    .setFont(font)
                    .setBold()
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER);
            header.addCell(new Cell().add(titulo).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            Image logo = new Image(ImageDataFactory.create(getClass().getResource("/iconos/actividades.png")));
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            header.addCell(new Cell().add(logo).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            document.add(header);

            Paragraph rango = new Paragraph("Semana: "
                    + model.getInicioSemana().format(formatoFecha)
                    + " - " + finSemana.format(formatoFecha))
                    .setFont(font)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(rango);

            AgendaTableModel agenda = view.getTableModel();
            Table tabla = new Table(new float[]{0.7f, 2f, 2f, 2f, 2f, 2f, 2f, 2f});
            tabla.setWidth(UnitValue.createPercentValue(100));
            tabla.setFontSize(6.5f);

            for (int columna = 0; columna < agenda.getColumnCount(); columna++) {
                tabla.addHeaderCell(crearCelda(agenda.getColumnName(columna), font, true));
            }

            for (int fila = 0; fila < agenda.getRowCount(); fila++) {
                for (int columna = 0; columna < agenda.getColumnCount(); columna++) {
                    Object valor = agenda.getValueAt(fila, columna);
                    tabla.addCell(crearCelda(valor == null ? "" : valor.toString(), font, columna == 0));
                }
            }

            document.add(tabla);
            document.close();
            view.mostrarMensaje("PDF generado: " + dest);
        } catch (Exception e) {
            view.mostrarError("Error al generar el PDF: " + e.getMessage());
        }
    }

    private Cell crearCelda(String texto, PdfFont font, boolean negrita) {
        Paragraph paragraph = new Paragraph(texto == null ? "" : texto)
                .setFont(font)
                .setFontSize(6.5f);
        if (negrita) {
            paragraph.setBold();
        }
        Cell cell = new Cell().add(paragraph);
        cell.setPadding(2);
        cell.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
        cell.setTextAlignment(TextAlignment.LEFT);
        return cell;
    }

    private void cargarSemana(LocalDate fecha) {
        LocalDate lunes = fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        model.setInicioSemana(lunes);
        model.setReservas(service.obtenerReservasSemana(lunes));
        view.setFechaSeleccionada(lunes);
    }
}
