package reservationSystem.presentation.categorias;

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
import reservationSystem.logic.IService;
import reservationSystem.logic.entities.CategoriaRecurso;

import javax.swing.*;

public class Controller {
    private final CategoriasView view;
    private final Model model;
    private final IService service;
    private final Runnable alGuardarCategoria;

    public Controller(CategoriasView view, Model model, IService service) {
        this(view, model, service, null);
    }

    public Controller(CategoriasView view, Model model, IService service, Runnable alGuardarCategoria) {
        this.view = view;
        this.model = model;
        this.service = service;
        this.alGuardarCategoria = alGuardarCategoria;

        view.setController(this);
        view.setModel(model);

        view.addBuscarButtonListener(e -> buscar());
        view.addGuardarButtonListener(e -> guardar());
        view.addLimpiarButtonListener(e -> limpiar());
        view.addEliminarButtonListener(e -> eliminar());
        view.addImprimirButtonListener(e -> {
            try {
                imprimir();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        view.addSeleccionListener(e -> {
            if (!e.getValueIsAdjusting()) seleccionarFila();
        });

        actualizarListado();
    }

    private void actualizarListado() {
        model.setCategorias(service.obtenerCategorias());
    }

    private void buscar() {
        String id = view.getIdBusqueda();
        String descripcion = view.getDescripcionBusqueda();

        var resultado = service.obtenerCategorias().stream()
                .filter(c -> (id.isBlank() || (c.getId() != null && c.getId().contains(id)))
                        && (descripcion.isBlank() || (c.getDescripcion() != null
                        && c.getDescripcion().toLowerCase().contains(descripcion.toLowerCase()))))
                .toList();

        model.setCategorias(resultado);
    }

    private void guardar() {
        String id = view.getIdCategoria();
        String descripcion = view.getDescripcionCategoria();

        if (descripcion.isBlank()) {
            view.mostrarError("La descripción es obligatoria.");
            return;
        }

        if (id.isBlank()) {
            id = service.generarIdCategoria();
        }

        CategoriaRecurso categoria = new CategoriaRecurso(id, descripcion);
        service.guardarCategoria(categoria);
        limpiar();
        actualizarListado();

        if (alGuardarCategoria != null) {
            alGuardarCategoria.run();
        }
    }

    private void eliminar() {
        CategoriaRecurso seleccionada = model.getCurrent();
        if (seleccionada == null || seleccionada.getId() == null || seleccionada.getId().isBlank()) {
            view.mostrarError("Seleccione una categoría de la tabla para eliminar.");
            return;
        }

        if (service.categoriaTieneRecursoEnReservaActiva(seleccionada.getId())) {
            view.mostrarError("No se puede eliminar la categoría porque uno de sus recursos está siendo usado por una reserva activa.");
            return;
        }

        if (!view.confirmarEliminacion(
                "¿Desea eliminar la categoría " + seleccionada.getDescripcion() +
                " y todos sus recursos?")) {
            return;
        }

        if (service.eliminarCategoria(seleccionada.getId())) {
            limpiar();
            actualizarListado();
            if (alGuardarCategoria != null) {
                alGuardarCategoria.run();
            }
            view.mostrarMensaje("Categoría y sus recursos eliminados correctamente.");
        } else {
            view.mostrarError("No se pudo eliminar la categoría.");
        }
    }

    private void limpiar() {
        model.setCurrent(new CategoriaRecurso());
        view.limpiarCampos();
    }

    private void imprimir() throws Exception {
        String dest = "categorias.pdf";
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        Table header = new Table(1);
        header.setWidth(400);
        header.setHorizontalAlignment(HorizontalAlignment.CENTER);
        header.addCell(getCell(
                new Paragraph("Listado de Categorías").setFont(font).setBold().setFontSize(16),
                HorizontalAlignment.CENTER,
                false));
        header.addCell(getCell(new Image(ImageDataFactory.create(getClass().getResource("/iconos/categorias.png"))), HorizontalAlignment.CENTER, false));
        document.add(header);

        Table tabla = new Table(new float[]{1, 3});
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.addHeaderCell(getCell(new Paragraph("ID").setFont(font).setBold(), HorizontalAlignment.LEFT, true));
        tabla.addHeaderCell(getCell(new Paragraph("Descripción").setFont(font).setBold(), HorizontalAlignment.LEFT, true));

        for (CategoriaRecurso categoria : model.getCategorias()) {
            tabla.addCell(getCell(new Paragraph(textoSeguro(categoria.getId())).setFont(font), HorizontalAlignment.LEFT, true));
            tabla.addCell(getCell(new Paragraph(textoSeguro(categoria.getDescripcion())).setFont(font), HorizontalAlignment.LEFT, true));
        }

        document.add(tabla);
        document.close();

        JOptionPane.showMessageDialog(view, "PDF generado: " + dest);
    }

    private String textoSeguro(String texto) {
        return texto == null ? "" : texto;
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
    private Cell getCell(Image image, HorizontalAlignment aligment, boolean hasBorder) {
        Cell cell = new Cell().add(image);
        image.setHorizontalAlignment(aligment);
        cell.setPadding(0);
        if (!hasBorder) {
            cell.setBorder(Border.NO_BORDER);
        }
        return cell;
    }

    private void seleccionarFila() {
        int fila = view.getFilaSeleccionada();
        if (fila >= 0 && fila < model.getCategorias().size()) {
            model.setCurrent(model.getCategorias().get(fila));
        }
    }
}
