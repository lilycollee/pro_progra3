package presentation.categorias;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import logic.Service;
import logic.entities.CategoriaRecurso;

import javax.swing.*;

public class Controller {
    private final CategoriasView view;
    private final Model model;
    private final Service service;
    private final Runnable alGuardarCategoria;

    public Controller(CategoriasView view, Model model, Service service) {
        this(view, model, service, null);
    }

    public Controller(CategoriasView view, Model model, Service service, Runnable alGuardarCategoria) {
        this.view = view;
        this.model = model;
        this.service = service;
        this.alGuardarCategoria = alGuardarCategoria;

        view.setController(this);
        view.setModel(model);

        view.addGuardarButtonListener(e -> guardar());
        view.addLimpiarButtonListener(e -> limpiar());
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

    private void guardar() {
        String id = view.getIdCategoria();
        String descripcion = view.getDescripcionCategoria();

        if (id.isBlank() || descripcion.isBlank()) {
            view.mostrarError("El ID y la descripción son obligatorios.");
            return;
        }

        CategoriaRecurso categoria = new CategoriaRecurso(id, descripcion);
        service.guardarCategoria(categoria);
        limpiar();
        actualizarListado();

        if (alGuardarCategoria != null) {
            alGuardarCategoria.run();
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

    private void seleccionarFila() {
        int fila = view.getFilaSeleccionada();
        if (fila >= 0 && fila < model.getCategorias().size()) {
            model.setCurrent(model.getCategorias().get(fila));
        }
    }
}
