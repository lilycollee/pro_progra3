package reservationSystem.logic.entities;

public class Recurso {
    private String id;
    private String descripcion;
    private String idCategoria;
    private transient CategoriaRecurso categoriaRecurso;

    public Recurso() {}

    public Recurso(String id, String descripcion, CategoriaRecurso categoriaRecurso) {
        this.id = id;
        this.descripcion = descripcion;
        setCategoriaRecurso(categoriaRecurso);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getIdCategoria() { return idCategoria; }
    public void setIdCategoria(String idCategoria) { this.idCategoria = idCategoria; }

    public CategoriaRecurso getCategoriaRecurso() { return categoriaRecurso; }

    public void setCategoriaRecurso(CategoriaRecurso categoriaRecurso) {
        this.categoriaRecurso = categoriaRecurso;
        this.idCategoria = categoriaRecurso != null ? categoriaRecurso.getId() : null;
    }
}