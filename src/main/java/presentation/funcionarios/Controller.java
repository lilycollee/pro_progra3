package presentation.funcionarios;

//-------- Pdf import's --------
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;

import logic.Service;
import logic.entities.Funcionario;

import javax.swing.*;

public class Controller {
    private FuncionarioView view;
    private Model model;
    private Service service;

    public Controller(FuncionarioView view, Model model, Service service) {
        this.view = view;
        this.model = model;
        this.service = service;
        view.setController(this);
        view.setModel(model);

        view.addGuardarButtonListener(e ->guardar());
        view.addBorrarButtonListener(e-> borrar());
        view.addLimpiarButtonListener(e-> limpiar());
        view.addBuscarButtonListener(e-> buscar());
        view.addImprimirButtonListener(e-> {
            try {
                imprimir();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        view.addSeleccionListener(e -> {if (!e.getValueIsAdjusting()) seleccionarFila();});

        actualizarListado();
    }

    private void actualizarListado(){
        model.setListaFuncionarios(service.obtenerFuncionarios());
    }

    private void guardar(){
        String id =  view.getIdF();
        String nombre = view.getNombreF();
        String telefono = view.getTelefonoF();
        if (id.isBlank() || nombre.isBlank()){
            view.mostrarError("El ID y el Nombre son obligatorios.");
            return;
        }
        service.guardarFuncionario(new Funcionario(id,nombre,telefono));
        limpiar();
        actualizarListado();
    }
    private void borrar(){
        String id = view.getIdF();
        if (id.isBlank()){
            view.mostrarError("Seleccione o escriba el ID.");
            return;
        }
        service.eliminarFuncionario(id);
        limpiar();
        actualizarListado();
    }
    private void limpiar(){
        model.setCurrent(new Funcionario());
    }
    private void buscar(){
        String id = view.getIdB();
        String nombre = view.getNombreB();
        var resultado = service.obtenerFuncionarios().stream().filter(f -> (id.isBlank() || f.getId().contains(id)) && (nombre.isBlank() || f.getNombre().toLowerCase().contains(nombre.toLowerCase()))).toList();
        model.setListaFuncionarios(resultado);
    }
    private void imprimir() throws Exception {
        String dest = "funcionarios.pdf";
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        Table header = new Table(1);
        header.setWidth(400);
        header.setHorizontalAlignment(HorizontalAlignment.CENTER);
        header.addCell(getCell(new Paragraph("Listado de Funcionarios").setFont(font).setBold().setFontSize(16), HorizontalAlignment.CENTER, false));
        //header.addCell(getCell(new Image(ImageDataFactory.create(Application.class.getResource("/logo.png"))), HorizontalAlignment.CENTER, false));
        document.add(header);

        Table tabla = new Table(new float[]{1, 2, 3});
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.addHeaderCell(getCell(new Paragraph("ID").setFont(font).setBold(), HorizontalAlignment.LEFT, true));
        tabla.addHeaderCell(getCell(new Paragraph("Nombre").setFont(font).setBold(), HorizontalAlignment.LEFT, true));
        tabla.addHeaderCell(getCell(new Paragraph("Telefono").setFont(font).setBold(), HorizontalAlignment.LEFT, true));

        for (Funcionario funcionario : model.getListaFuncionarios()) {
            tabla.addCell(getCell(new Paragraph(funcionario.getId()).setFont(font), HorizontalAlignment.LEFT, true));
            tabla.addCell(getCell(new Paragraph(funcionario.getNombre()).setFont(font), HorizontalAlignment.LEFT, true));
            tabla.addCell(getCell(new Paragraph(funcionario.getTelefono()).setFont(font), HorizontalAlignment.LEFT, true));
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

    private void seleccionarFila(){
        int fila = view.getFilaSeleccionada();
        if (fila >= 0){
            model.setCurrent(model.getListaFuncionarios().get(fila));
        }
    }
}
