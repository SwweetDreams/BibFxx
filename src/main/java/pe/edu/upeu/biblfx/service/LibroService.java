package pe.edu.upeu.biblfx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.biblfx.model.CategoriaLibro;
import pe.edu.upeu.biblfx.model.Libro;
import pe.edu.upeu.biblfx.repository.LibroRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LibroService {
    
    @Autowired
    private LibroRepository libroRepository;
    
    /**
     * Guarda un nuevo libro
     */
    public Libro guardarLibro(Libro libro) {
        // Validar que el ISBN no exista si se proporciona
        if (libro.getIsbn() != null && !libro.getIsbn().trim().isEmpty()) {
            if (libroRepository.existsByIsbn(libro.getIsbn())) {
                throw new IllegalArgumentException("El ISBN ya está registrado");
            }
        }
        
        // Validar que la cantidad disponible no sea mayor que la total
        if (libro.getCantidadDisponible() > libro.getCantidadTotal()) {
            throw new IllegalArgumentException("La cantidad disponible no puede ser mayor que la cantidad total");
        }
        
        // Establecer valores por defecto
        if (libro.getCantidadTotal() == null) {
            libro.setCantidadTotal(1);
        }
        if (libro.getCantidadDisponible() == null) {
            libro.setCantidadDisponible(libro.getCantidadTotal());
        }
        if (libro.getCategoria() == null) {
            libro.setCategoria(CategoriaLibro.GENERAL);
        }
        
        libro.setActivo(true);
        
        return libroRepository.save(libro);
    }
    
    /**
     * Actualiza un libro existente
     */
    public Libro actualizarLibro(Libro libro) {
        Libro libroExistente = libroRepository.findById(libro.getId())
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        
        // Verificar si el ISBN cambió y si ya existe
        if (libro.getIsbn() != null && !libro.getIsbn().trim().isEmpty()) {
            if (!libroExistente.getIsbn().equals(libro.getIsbn()) &&
                libroRepository.existsByIsbn(libro.getIsbn())) {
                throw new IllegalArgumentException("El ISBN ya está registrado");
            }
        }
        
        // Validar que la cantidad disponible no sea mayor que la total
        if (libro.getCantidadDisponible() > libro.getCantidadTotal()) {
            throw new IllegalArgumentException("La cantidad disponible no puede ser mayor que la cantidad total");
        }
        
        // Actualizar campos
        libroExistente.setTitulo(libro.getTitulo());
        libroExistente.setAutor(libro.getAutor());
        libroExistente.setIsbn(libro.getIsbn());
        libroExistente.setEditorial(libro.getEditorial());
        libroExistente.setAnioPublicacion(libro.getAnioPublicacion());
        libroExistente.setDescripcion(libro.getDescripcion());
        libroExistente.setCategoria(libro.getCategoria());
        libroExistente.setCantidadTotal(libro.getCantidadTotal());
        libroExistente.setCantidadDisponible(libro.getCantidadDisponible());
        libroExistente.setActivo(libro.isActivo());
        
        return libroRepository.save(libroExistente);
    }
    
    /**
     * Busca un libro por ID
     */
    @Transactional(readOnly = true)
    public Optional<Libro> buscarPorId(Long id) {
        return libroRepository.findById(id);
    }
    
    /**
     * Busca un libro por ISBN
     */
    @Transactional(readOnly = true)
    public Optional<Libro> buscarPorIsbn(String isbn) {
        return libroRepository.findByIsbn(isbn);
    }
    
    /**
     * Lista todos los libros con paginación
     */
    @Transactional(readOnly = true)
    public Page<Libro> listarLibros(int pagina, int tamanio, String ordenarPor) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by(ordenarPor));
        return libroRepository.findAll(pageable);
    }
    
    /**
     * Busca libros con filtros y paginación
     */
    @Transactional(readOnly = true)
    public Page<Libro> buscarLibrosConFiltros(
            String titulo, String autor, String isbn, String editorial,
            CategoriaLibro categoria, Integer anioPublicacion, 
            Boolean soloDisponibles, Boolean activo, int pagina, int tamanio) {
        
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("titulo"));
        return libroRepository.findLibrosWithFilters(
                titulo, autor, isbn, editorial, categoria, anioPublicacion, 
                soloDisponibles, activo, pageable);
    }
    
    /**
     * Busca libros por título
     */
    @Transactional(readOnly = true)
    public List<Libro> buscarPorTitulo(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo);
    }
    
    /**
     * Busca libros por autor
     */
    @Transactional(readOnly = true)
    public List<Libro> buscarPorAutor(String autor) {
        return libroRepository.findByAutorContainingIgnoreCase(autor);
    }
    
    /**
     * Busca libros por editorial
     */
    @Transactional(readOnly = true)
    public List<Libro> buscarPorEditorial(String editorial) {
        return libroRepository.findByEditorialContainingIgnoreCase(editorial);
    }
    
    /**
     * Lista libros por categoría
     */
    @Transactional(readOnly = true)
    public List<Libro> listarPorCategoria(CategoriaLibro categoria) {
        return libroRepository.findByCategoria(categoria);
    }
    
    /**
     * Lista libros activos
     */
    @Transactional(readOnly = true)
    public List<Libro> listarActivos() {
        return libroRepository.findByActivoTrue();
    }
    
    /**
     * Lista libros disponibles
     */
    @Transactional(readOnly = true)
    public List<Libro> listarDisponibles() {
        return libroRepository.findByCantidadDisponibleGreaterThanAndActivoTrue(0);
    }
    
    /**
     * Lista libros por año de publicación
     */
    @Transactional(readOnly = true)
    public List<Libro> listarPorAnio(Integer anio) {
        return libroRepository.findByAnioPublicacion(anio);
    }
    
    /**
     * Desactiva un libro
     */
    public void desactivarLibro(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        libro.setActivo(false);
        libroRepository.save(libro);
    }
    
    /**
     * Activa un libro
     */
    public void activarLibro(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        libro.setActivo(true);
        libroRepository.save(libro);
    }
    
    /**
     * Incrementa el stock de un libro
     */
    public void incrementarStock(Long id, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        
        libro.setCantidadTotal(libro.getCantidadTotal() + cantidad);
        libro.setCantidadDisponible(libro.getCantidadDisponible() + cantidad);
        
        libroRepository.save(libro);
    }
    
    /**
     * Decrementa el stock disponible de un libro (para préstamos)
     */
    public void decrementarStockDisponible(Long id, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        
        if (libro.getCantidadDisponible() < cantidad) {
            throw new IllegalArgumentException("No hay suficientes ejemplares disponibles");
        }
        
        libro.setCantidadDisponible(libro.getCantidadDisponible() - cantidad);
        libroRepository.save(libro);
    }
    
    /**
     * Incrementa el stock disponible de un libro (para devoluciones)
     */
    public void incrementarStockDisponible(Long id, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        
        if (libro.getCantidadDisponible() + cantidad > libro.getCantidadTotal()) {
            throw new IllegalArgumentException("La cantidad disponible no puede exceder la cantidad total");
        }
        
        libro.setCantidadDisponible(libro.getCantidadDisponible() + cantidad);
        libroRepository.save(libro);
    }
    
    /**
     * Cuenta libros por categoría
     */
    @Transactional(readOnly = true)
    public long contarPorCategoria(CategoriaLibro categoria) {
        return libroRepository.countByCategoria(categoria);
    }
    
    /**
     * Cuenta libros activos
     */
    @Transactional(readOnly = true)
    public long contarActivos() {
        return libroRepository.countByActivoTrue();
    }
    
    /**
     * Cuenta libros disponibles
     */
    @Transactional(readOnly = true)
    public long contarDisponibles() {
        return libroRepository.countByCantidadDisponibleGreaterThanAndActivoTrue(0);
    }
    
    /**
     * Lista libros con stock bajo
     */
    @Transactional(readOnly = true)
    public List<Libro> listarConStockBajo() {
        return libroRepository.findLibrosConStockBajo();
    }
    
    /**
     * Lista libros más populares
     */
    @Transactional(readOnly = true)
    public Page<Libro> listarMasPopulares(int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        return libroRepository.findLibrosMasPopulares(pageable);
    }
} 