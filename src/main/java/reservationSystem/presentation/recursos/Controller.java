package reservationSystem.presentation.recursos;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import reservationSystem.logic.Service;
import reservationSystem.logic.entities.CategoriaRecurso;
import reservationSystem.logic.entities.Recurso;

import javax.swing.*;

public class Controller {
    private final RecursosView view;
    private final Model model;
    private final Service service;

    public Controller(RecursosView view, Model model, Service service) {
        this.view = view;
        this.model = model;
        this.service = service;

        view.setController(this);
        view.setModel(model);

        view.addBuscarListener(e -> buscar());
        view.addGuardarListener(e -> guardar());
        view.addLimpiarListener(e -> limpiar());
        view.addEliminarListener(e -> eliminar());
        view.addImprimirListener(e -> {
            try {
                imprimir();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        view.addSeleccionListener(e -> {
            if (!e.getValueIsAdjusting()) seleccionarFila();
        });

        actualizarCategorias();
        actualizarListado();
    }

    private void actualizarListado() {
        model.setRecursos(service.obtenerRecursos());
    }

    public void actualizarCategorias() {
        model.setCategorias(service.obtenerCategorias());
    }

    private void buscar() {
        String id = view.getIdBusqueda();
        String descripcion = view.getDescripcionBusqueda();

        var resultado = service.obtenerRecursos().stream()
                .filter(r -> (id.isBlank() || (r.getId() != null && r.getId().contains(id)))
                        && (descripcion.isBlank() || (r.getDescripcion() != null
                        && r.getDescripcion().toLowerCase().contains(descripcion.toLowerCase()))))
                .toList();

        model.setRecursos(resultado);
    }

    private void guardar() {
        String id = view.getId();
        String descripcion = view.getDescripcion();
        CategoriaRecurso categoria = view.getCategoriaSeleccionada();

        if (id.isBlank() || descripcion.isBlank()) {
            view.mostrarError("El ID y la descripción son obligatorios.");
            return;
        }

        if (categoria == null) {
            view.mostrarError("Seleccione una categoría existente.");
            return;
        }

        Recurso recurso = new Recurso(id, descripcion, categoria);
        service.guardarRecurso(recurso);

        limpiar();
        actualizarListado();
    }

    private void eliminar() {
        Recurso seleccionado = model.getCurrent();
        if (seleccionado == null || seleccionado.getId() == null || seleccionado.getId().isBlank()) {
            view.mostrarError("Seleccione un recurso de la tabla para eliminar.");
            return;
        }

        if (service.recursoEnReservaActiva(seleccionado.getId())) {
            view.mostrarError("No se puede eliminar el recurso porque está siendo usado por una reserva activa.");
            return;
        }

        if (!view.confirmarEliminacion("¿Desea eliminar el recurso " + seleccionado.getDescripcion() + "?")) {
            return;
        }

        if (service.eliminarRecurso(seleccionado.getId())) {
            limpiar();
            actualizarListado();
            view.mostrarMensaje("Recurso eliminado correctamente.");
        } else {
            view.mostrarError("No se pudo eliminar el recurso.");
        }
    }

    private void limpiar() {
        model.setCurrent(new Recurso());
        view.limpiarCampos();
    }

    private void imprimir() throws Exception {
        String dest = "recursos.pdf";
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        Table header = new Table(1);
        header.setWidth(400);
        header.setHorizontalAlignment(HorizontalAlignment.CENTER);
        header.addCell(getCell(
                new Paragraph("Listado de Recursos").setFont(font).setBold().setFontSize(16),
                HorizontalAlignment.CENTER,
                false));
        header.addCell(getCell(new Image(ImageDataFactory.create(getClass().getResource("/iconos/recursos.png"))), HorizontalAlignment.CENTER, false));
        document.add(header);

        Table tabla = new Table(new float[]{1, 3, 2});
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.addHeaderCell(getCell(new Paragraph("ID").setFont(font).setBold(), HorizontalAlignment.LEFT, true));
        tabla.addHeaderCell(getCell(new Paragraph("Descripción").setFont(font).setBold(), HorizontalAlignment.LEFT, true));
        tabla.addHeaderCell(getCell(new Paragraph("Categoría").setFont(font).setBold(), HorizontalAlignment.LEFT, true));

        for (Recurso recurso : model.getRecursos()) {
            String categoria = recurso.getCategoriaRecurso() == null
                    ? ""
                    : textoSeguro(recurso.getCategoriaRecurso().getDescripcion());

            tabla.addCell(getCell(new Paragraph(textoSeguro(recurso.getId())).setFont(font), HorizontalAlignment.LEFT, true));
            tabla.addCell(getCell(new Paragraph(textoSeguro(recurso.getDescripcion())).setFont(font), HorizontalAlignment.LEFT, true));
            tabla.addCell(getCell(new Paragraph(categoria).setFont(font), HorizontalAlignment.LEFT, true));
        }

        document.add(tabla);
        document.close();

        JOptionPane.showMessageDialog(view, "PDF generado: " + dest);
    }

    private String textoSeguro(String texto) {
        return texto == null ? "" : texto;
    }

    private Cell getCell(Image image, HorizontalAlignment alignment, boolean hasBorder) {
        Cell cell = new Cell().add(image);
        image.setHorizontalAlignment(alignment);
        cell.setPadding(0);
        if (!hasBorder) {
            cell.setBorder(Border.NO_BORDER);
        }
        return cell;
    }

    private Cell getCell(Paragraph texto, HorizontalAlignment alignment, boolean hasBorder) {
        Cell cell = new Cell().add(texto);
        texto.setHorizontalAlignment(alignment);
        cell.setPadding(0);
        if (!hasBorder) {
            cell.setBorder(Border.NO_BORDER);
        }
        return cell;
    }

    private void seleccionarFila() {
        int fila = view.getFilaSeleccionada();
        if (fila >= 0) {
            model.setCurrent(model.getRecursos().get(fila));
        }
    }
}
