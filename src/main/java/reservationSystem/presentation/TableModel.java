package reservationSystem.presentation;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public abstract class TableModel<T> extends AbstractTableModel {
    protected List<T> datos;
    protected int[] columnas;
    protected String[] columnasNombre;

    public TableModel(int[] columnas, List<T> datos) {
        this.columnas = columnas;
        this.datos = datos;
        initColumnasNombre();
    }

    public void setDatos(List<T> datos) {
        this.datos = datos;
        fireTableDataChanged(); // sin esto, la tabla visual no se refresca sola
    }
    public T getFila(int fila) {
        return datos.get(fila);
    }

    @Override
    public int getRowCount() {
        return datos == null ? 0 : datos.size();
    }
    @Override
    public int getColumnCount() {
        return columnas.length;
    }
    @Override
    public String getColumnName(int column) {
        return columnasNombre[columnas[column]];
    }
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    @Override
    public abstract Object getValueAt(int row, int column);

    protected abstract void initColumnasNombre();
}
