package logic.entities;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Recurso {
    private String id;
    private String descripcion;
    private CategoriaRecurso categoriaRecurso;

    public  Recurso() {}
    public Recurso(String id, String descripcion, CategoriaRecurso categoriaRecurso) {
        this.id = id;
        this.descripcion = descripcion;
        this.categoriaRecurso = categoriaRecurso;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public CategoriaRecurso getCategoriaRecurso() {
        return categoriaRecurso;
    }
    public void setCategoriaRecurso(CategoriaRecurso categoriaRecurso) {
        this.categoriaRecurso = categoriaRecurso;
    }
}
