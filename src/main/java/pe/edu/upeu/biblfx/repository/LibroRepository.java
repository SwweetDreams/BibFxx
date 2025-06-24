package pe.edu.upeu.biblfx.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.biblfx.model.CategoriaLibro;
import pe.edu.upeu.biblfx.model.Libro;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    
    /**
     * Busca un libro por su ISBN
     */
    Optional<Libro> findByIsbn(String isbn);
    
    /**
     * Verifica si existe un libro con el ISBN dado
     */
    boolean existsByIsbn(String isbn);
    
    /**
     * Busca libros por título (búsqueda parcial)
     */
    List<Libro> findByTituloContainingIgnoreCase(String titulo);
    
    /**
     * Busca libros por autor (búsqueda parcial)
     */
    List<Libro> findByAutorContainingIgnoreCase(String autor);
    
    /**
     * Busca libros por editorial (búsqueda parcial)
     */
    List<Libro> findByEditorialContainingIgnoreCase(String editorial);
    
    /**
     * Busca libros por categoría
     */
    List<Libro> findByCategoria(CategoriaLibro categoria);
    
    /**
     * Busca libros activos
     */
    List<Libro> findByActivoTrue();
    
    /**
     * Busca libros disponibles (cantidad disponible > 0)
     */
    List<Libro> findByCantidadDisponibleGreaterThanAndActivoTrue(int cantidad);
    
    /**
     * Busca libros por año de publicación
     */
    List<Libro> findByAnioPublicacion(Integer anioPublicacion);
    
    /**
     * Busca libros con paginación y filtros
     */
    @Query("SELECT l FROM Libro l WHERE " +
           "(:titulo IS NULL OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))) AND " +
           "(:autor IS NULL OR LOWER(l.autor) LIKE LOWER(CONCAT('%', :autor, '%'))) AND " +
           "(:isbn IS NULL OR LOWER(l.isbn) LIKE LOWER(CONCAT('%', :isbn, '%'))) AND " +
           "(:editorial IS NULL OR LOWER(l.editorial) LIKE LOWER(CONCAT('%', :editorial, '%'))) AND " +
           "(:categoria IS NULL OR l.categoria = :categoria) AND " +
           "(:anioPublicacion IS NULL OR l.anioPublicacion = :anioPublicacion) AND " +
           "(:soloDisponibles IS NULL OR (l.cantidadDisponible > 0 AND l.activo = true)) AND " +
           "(:activo IS NULL OR l.activo = :activo)")
    Page<Libro> findLibrosWithFilters(
            @Param("titulo") String titulo,
            @Param("autor") String autor,
            @Param("isbn") String isbn,
            @Param("editorial") String editorial,
            @Param("categoria") CategoriaLibro categoria,
            @Param("anioPublicacion") Integer anioPublicacion,
            @Param("soloDisponibles") Boolean soloDisponibles,
            @Param("activo") Boolean activo,
            Pageable pageable);
    
    /**
     * Cuenta libros por categoría
     */
    long countByCategoria(CategoriaLibro categoria);
    
    /**
     * Cuenta libros activos
     */
    long countByActivoTrue();
    
    /**
     * Cuenta libros disponibles
     */
    long countByCantidadDisponibleGreaterThanAndActivoTrue(int cantidad);
    
    /**
     * Busca libros con stock bajo (cantidad disponible <= 2)
     */
    @Query("SELECT l FROM Libro l WHERE l.cantidadDisponible <= 2 AND l.activo = true")
    List<Libro> findLibrosConStockBajo();
    
    /**
     * Busca libros más populares (ordenados por cantidad total - cantidad disponible)
     */
    @Query("SELECT l FROM Libro l WHERE l.activo = true ORDER BY (l.cantidadTotal - l.cantidadDisponible) DESC")
    Page<Libro> findLibrosMasPopulares(Pageable pageable);
} 