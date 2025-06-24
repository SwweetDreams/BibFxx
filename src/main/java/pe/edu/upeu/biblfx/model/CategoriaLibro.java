package pe.edu.upeu.biblfx.model;

public enum CategoriaLibro {
    GENERAL("General"),
    LITERATURA("Literatura"),
    CIENCIA("Ciencia"),
    TECNOLOGIA("Tecnología"),
    HISTORIA("Historia"),
    FILOSOFIA("Filosofía"),
    ARTE("Arte"),
    DEPORTES("Deportes"),
    COCINA("Cocina"),
    INFANTIL("Infantil"),
    EDUCACION("Educación"),
    NEGOCIOS("Negocios"),
    SALUD("Salud"),
    VIAJES("Viajes"),
    OTROS("Otros");
    
    private final String descripcion;
    
    CategoriaLibro(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return descripcion;
    }
} 