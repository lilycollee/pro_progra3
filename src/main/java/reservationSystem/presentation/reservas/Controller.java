package reservationSystem.presentation.reservas;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import reservationSystem.logic.Service;
import reservationSystem.logic.entities.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

//-------- Pdf import's --------
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import reservationSystem.logic.entities.ia.ReservaExtraccion;

public class Controller {
    private ReservasView view;
    private Model model;
    private Service service;
    private Funcionario funcionarioActual;

    public Controller(ReservasView view, Model model, Service service, Funcionario funcionarioActual) {
        this.view = view;
        this.model = model;
        this.funcionarioActual = funcionarioActual;
        this.service = service;

        view.setController(this);
        view.setModel(model);
        view.getReservasTableModel().setService(service);

        view.addReservarListener(e-> reservar());
        view.addLimpiarListener(e-> limpiar());
        view.addCancelarReservaListener(e-> cancelarReserva());
        view.addImprimirListener(e -> {
            try {
                imprimir();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        view.addSeleccionReservaListener(e-> {if (!e.getValueIsAdjusting()) seleccionarFila();});
        view.addExtraerIAListener(e -> extraerConIA());

        model.setCategorias(service.obtenerCategorias());
        actualizarListado();
    }
    private void actualizarListado() {
        model.setReservas(service.obtenerReservasPorFuncionario(funcionarioActual.getId()));
    }
    private void reservar() {
        String actividad = view.getActividad();
        LocalDate fecha = view.getFecha();
        LocalTime horaInicio = view.getHoraInicio();
        LocalTime horaFinal = view.getHoraFinal();
        var categorias = view.getCategoriasSeleccionadas();
        if (actividad.isBlank()){
            view.mostrarError("Ingrese la actividad de la reserva.");
        }
        if (fecha == null) {
            view.mostrarError("La fecha no tiene un formato válido (dd/MM/yyyy).");
            return;
        }
        if (horaInicio == null || horaFinal == null) {
            view.mostrarError("Seleccione hora de inicio y hora de fin.");
            return;
        }
        if (!horaInicio.isBefore(horaFinal)) {
            view.mostrarError("La hora de inicio debe ser antes que la hora de fin.");
            return;
        }
        if (categorias.isEmpty()) {
            view.mostrarError("Seleccione al menos una categoría de recurso.");
            return;
        }

        Reserva reserva = new Reserva(null, funcionarioActual.getId(), actividad, fecha, horaInicio, horaFinal);
        for (CategoriaRecurso categoriaRecurso : categorias){
            reserva.agregarDetalleReserva(new DetalleReserva(categoriaRecurso.getId()));
        }
        ResultadoReserva resultadoReserva = service.crearReserva(reserva);
        if (resultadoReserva.esExitosa()) {
            limpiar();
            actualizarListado();
        } else {
            StringBuilder mgs = new StringBuilder("No hay recursos disponibles para:");
            for (CategoriaRecurso c : resultadoReserva.getCategoriasNoDisponibles()){
                mgs.append(c.getDescripcion()).append(" ");
            }
            view.mostrarError(mgs.toString());
        }
    }
    private void cancelarReserva() {
        Reserva seleccionada = model.getCurrent();
        if (seleccionada == null || seleccionada.getId() == null) {
            view.mostrarError("Seleccione una reserva de la tabla para cancelar.");
            return;
        }
        service.cancelarReserva(seleccionada.getId());
        limpiar();
        actualizarListado();
    }
    private void imprimir() {
        try {
            List<Reserva> reservas = service.obtenerReservasPorFuncionario(funcionarioActual.getId());
            String dest = funcionarioActual.getId() + "_reservas.pdf";
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);

            Document document = new Document(pdf);
            document.setMargins(20, 20, 20, 20);

            Table header = new Table(1);
            header.setWidth(400);
            header.setHorizontalAlignment(HorizontalAlignment.CENTER);
            header.addCell(getCell(new Paragraph("Listado reservas - " + funcionarioActual.getId()).setFont(font).setBold().setFontSize(16), HorizontalAlignment.CENTER, false));
            document.add(header);

            Table tabla = new Table(new float[]{1, 2, 1, 2, 3, 1});
            tabla.setWidth(UnitValue.createPercentValue(100));
            tabla.addHeaderCell(getCell(new Paragraph("ID").setFont(font).setBold(), HorizontalAlignment.LEFT, true));
            tabla.addHeaderCell(getCell(new Paragraph("Actividad").setFont(font).setBold(), HorizontalAlignment.LEFT, true));
            tabla.addHeaderCell(getCell(new Paragraph("Fecha").setFont(font).setBold(), HorizontalAlignment.LEFT, true));
            tabla.addHeaderCell(getCell(new Paragraph("Horario").setFont(font).setBold(), HorizontalAlignment.LEFT, true));
            tabla.addHeaderCell(getCell(new Paragraph("Recursos").setFont(font).setBold(), HorizontalAlignment.LEFT, true));
            tabla.addHeaderCell(getCell(new Paragraph("Estado").setFont(font).setBold(), HorizontalAlignment.LEFT, true));

            for (Reserva reserva : reservas) {
                String horario = reserva.getHoraInicio() + " - " + reserva.getHoraFin();
                StringBuilder recursosTexto = new StringBuilder();
                for (DetalleReserva detalle : reserva.getDetalles()) {
                    if (detalle.getIdRecursoAsignado() != null) {
                        service.obtenerRecurso(detalle.getIdRecursoAsignado()).ifPresent(r -> recursosTexto.append(r.getDescripcion()));
                    } else {
                        service.obtenerCategoria(detalle.getIdCategoriaSolicitada()).ifPresent(c -> recursosTexto.append(c.getDescripcion()).append(" (pendiente)"));
                    }
                    recursosTexto.append(", ");
                }
                if (recursosTexto.length() > 0) {
                    recursosTexto.setLength(recursosTexto.length() - 2);
                }

                tabla.addCell(getCell(new Paragraph(reserva.getId()).setFont(font), HorizontalAlignment.LEFT, true));
                tabla.addCell(getCell(new Paragraph(reserva.getActividad()).setFont(font), HorizontalAlignment.LEFT, true));
                tabla.addCell(getCell(new Paragraph(reserva.getFecha().toString()).setFont(font), HorizontalAlignment.LEFT, true));
                tabla.addCell(getCell(new Paragraph(horario).setFont(font), HorizontalAlignment.LEFT, true));
                tabla.addCell(getCell(new Paragraph(recursosTexto.toString()).setFont(font), HorizontalAlignment.LEFT, true));
                tabla.addCell(getCell(new Paragraph(reserva.getEstado()).setFont(font), HorizontalAlignment.LEFT, true));
            }

            document.add(tabla);
            document.close();
            view.mostrarMensaje("PDF generado: " + dest);
        } catch (Exception e) {
            view.mostrarError("Error al generar el PDF: " + e.getMessage());
        }
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
    private void limpiar() {
        model.setCurrent(new  Reserva());
        view.limpiarCampos();
    }
    private void seleccionarFila() {
        int fila = view.getFilasReservaSeleccionada();
        if (fila >= 0) {
            model.setCurrent(model.getReservas().get(fila));
        }
    }

    //-IA
    private void extraerConIA() {
        String frase = view.getFrase();
        if (frase == null || frase.isBlank()) {
            view.mostrarError("Escriba una descripción de la reserva primero.");
            return;
        }

        try {
            ReservaExtraccion extraccion = service.extraerReserva(frase);
            aplicarExtraccion(extraccion);
        } catch (Exception e) {
            view.mostrarError("No se pudo procesar la frase con IA: " + e.getMessage());
        }
    }
    private void aplicarExtraccion(ReservaExtraccion extraccion) {
        if (extraccion.getActividad() != null) {
            view.setActividad(extraccion.getActividad());
        }
        if (extraccion.getFecha() != null) {
            view.setFecha(LocalDate.parse(extraccion.getFecha()));
        }
        if (extraccion.getHoraInicio() != null) {
            view.setHoraInicio(LocalTime.parse(extraccion.getHoraInicio()));
        }
        if (extraccion.getHoraFinal() != null) {
            view.setHoraFinal(LocalTime.parse(extraccion.getHoraFinal()));
        }
        if (extraccion.getCategoriasRecurso() != null) {
            List<CategoriaRecurso> categoriasEncontradas = model.getCategorias().stream().filter(c -> extraccion.getCategoriasRecurso().contains(c.getDescripcion())).toList();
            view.seleccionarCategorias(categoriasEncontradas);
        }
    }
}
