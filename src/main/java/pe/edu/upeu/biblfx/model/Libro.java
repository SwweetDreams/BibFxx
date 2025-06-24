package pe.edu.upeu.biblfx.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "libros")
public class Libro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    @Column(nullable = false)
    private String titulo;
    
    @NotBlank(message = "El autor es obligatorio")
    @Size(max = 100, message = "El autor no puede exceder 100 caracteres")
    @Column(nullable = false)
    private String autor;
    
    @Size(max = 50, message = "El ISBN no puede exceder 50 caracteres")
    @Column(unique = true)
    private String isbn;
    
    @Size(max = 100, message = "La editorial no puede exceder 100 caracteres")
    private String editorial;
    
    @Positive(message = "El año de publicación debe ser positivo")
    @Column(name = "anio_publicacion")
    private Integer anioPublicacion;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(length = 500)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaLibro categoria = CategoriaLibro.GENERAL;
    
    @NotNull(message = "La cantidad total es obligatoria")
    @Positive(message = "La cantidad total debe ser positiva")
    @Column(name = "cantidad_total", nullable = false)
    private Integer cantidadTotal = 1;
    
    @NotNull(message = "La cantidad disponible es obligatoria")
    @Positive(message = "La cantidad disponible debe ser positiva")
    @Column(name = "cantidad_disponible", nullable = false)
    private Integer cantidadDisponible = 1;
    
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    
    @Column(nullable = false)
    private boolean activo = true;
    
    // Constructores
    public Libro() {}
    
    public Libro(String titulo, String autor, String isbn) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getAutor() {
        return autor;
    }
    
    public void setAutor(String autor) {
        this.autor = autor;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public String getEditorial() {
        return editorial;
    }
    
    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }
    
    public Integer getAnioPublicacion() {
        return anioPublicacion;
    }
    
    public void setAnioPublicacion(Integer anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public CategoriaLibro getCategoria() {
        return categoria;
    }
    
    public void setCategoria(CategoriaLibro categoria) {
        this.categoria = categoria;
    }
    
    public Integer getCantidadTotal() {
        return cantidadTotal;
    }
    
    public void setCantidadTotal(Integer cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }
    
    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }
    
    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", isbn='" + isbn + '\'' +
                ", editorial='" + editorial + '\'' +
                ", anioPublicacion=" + anioPublicacion +
                ", categoria=" + categoria +
                ", cantidadDisponible=" + cantidadDisponible +
                ", activo=" + activo +
                '}';
    }
} 