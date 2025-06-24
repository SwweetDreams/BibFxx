package pe.edu.upeu.biblfx.model;

public enum RolUsuario {
    ADMINISTRADOR("Administrador"),
    BIBLIOTECARIO("Bibliotecario"),
    LECTOR("Lector");
    
    private final String descripcion;
    
    RolUsuario(String descripcion) {
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