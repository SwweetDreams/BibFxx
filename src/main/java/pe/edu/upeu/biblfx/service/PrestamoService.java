package pe.edu.upeu.biblfx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.biblfx.model.EstadoPrestamo;
import pe.edu.upeu.biblfx.model.Libro;
import pe.edu.upeu.biblfx.model.Prestamo;
import pe.edu.upeu.biblfx.model.Usuario;
import pe.edu.upeu.biblfx.repository.PrestamoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PrestamoService {
    
    @Autowired
    private PrestamoRepository prestamoRepository;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private LibroService libroService;
    
    /**
     * Crea un nuevo préstamo
     */
    public Prestamo crearPrestamo(Long usuarioId, Long libroId, LocalDateTime fechaDevolucionEsperada, String observaciones) {
        // Validar que el usuario existe y está activo
        Usuario usuario = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        if (!usuario.isActivo()) {
            throw new IllegalArgumentException("El usuario no está activo");
        }
        
        // Validar que el libro existe y está activo
        Libro libro = libroService.buscarPorId(libroId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        
        if (!libro.isActivo()) {
            throw new IllegalArgumentException("El libro no está activo");
        }
        
        // Validar que hay ejemplares disponibles
        if (libro.getCantidadDisponible() <= 0) {
            throw new IllegalArgumentException("No hay ejemplares disponibles del libro");
        }
        
        // Validar que la fecha de devolución esperada sea futura
        if (fechaDevolucionEsperada.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de devolución esperada debe ser futura");
        }
        
        // Crear el préstamo
        Prestamo prestamo = new Prestamo(usuario, libro, fechaDevolucionEsperada);
        prestamo.setObservaciones(observaciones);
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        
        // Decrementar el stock disponible del libro
        libroService.decrementarStockDisponible(libroId, 1);
        
        return prestamoRepository.save(prestamo);
    }
    
    /**
     * Devuelve un libro prestado
     */
    public Prestamo devolverLibro(Long prestamoId, String observaciones) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado"));
        
        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new IllegalArgumentException("El préstamo no está activo");
        }
        
        // Actualizar el préstamo
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucionReal(LocalDateTime.now());
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            prestamo.setObservaciones(observaciones);
        }
        
        // Incrementar el stock disponible del libro
        libroService.incrementarStockDisponible(prestamo.getLibro().getId(), 1);
        
        return prestamoRepository.save(prestamo);
    }
    
    /**
     * Busca un préstamo por ID
     */
    @Transactional(readOnly = true)
    public Optional<Prestamo> buscarPorId(Long id) {
        return prestamoRepository.findById(id);
    }
    
    /**
     * Lista todos los préstamos con paginación
     */
    @Transactional(readOnly = true)
    public Page<Prestamo> listarPrestamos(int pagina, int tamanio, String ordenarPor) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by(ordenarPor));
        return prestamoRepository.findAll(pageable);
    }
    
    /**
     * Busca préstamos con filtros y paginación
     */
    @Transactional(readOnly = true)
    public Page<Prestamo> buscarPrestamosConFiltros(
            Long usuarioId, Long libroId, EstadoPrestamo estado,
            LocalDateTime fechaInicio, LocalDateTime fechaFin, int pagina, int tamanio) {
        
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("fechaPrestamo").descending());
        return prestamoRepository.findPrestamosWithFilters(
                usuarioId, libroId, estado, fechaInicio, fechaFin, pageable);
    }
    
    /**
     * Lista préstamos por usuario
     */
    @Transactional(readOnly = true)
    public List<Prestamo> listarPorUsuario(Long usuarioId) {
        return prestamoRepository.findByUsuarioId(usuarioId);
    }
    
    /**
     * Lista préstamos por libro
     */
    @Transactional(readOnly = true)
    public List<Prestamo> listarPorLibro(Long libroId) {
        return prestamoRepository.findByLibroId(libroId);
    }
    
    /**
     * Lista préstamos por estado
     */
    @Transactional(readOnly = true)
    public List<Prestamo> listarPorEstado(EstadoPrestamo estado) {
        return prestamoRepository.findByEstado(estado);
    }
    
    /**
     * Lista préstamos activos
     */
    @Transactional(readOnly = true)
    public List<Prestamo> listarActivos() {
        return prestamoRepository.findByEstado(EstadoPrestamo.ACTIVO);
    }
    
    /**
     * Lista préstamos vencidos
     */
    @Transactional(readOnly = true)
    public List<Prestamo> listarVencidos() {
        return prestamoRepository.findPrestamosVencidos(LocalDateTime.now());
    }
    
    /**
     * Lista préstamos por usuario y estado
     */
    @Transactional(readOnly = true)
    public List<Prestamo> listarPorUsuarioYEstado(Long usuarioId, EstadoPrestamo estado) {
        return prestamoRepository.findByUsuarioIdAndEstado(usuarioId, estado);
    }
    
    /**
     * Lista préstamos por libro y estado
     */
    @Transactional(readOnly = true)
    public List<Prestamo> listarPorLibroYEstado(Long libroId, EstadoPrestamo estado) {
        return prestamoRepository.findByLibroIdAndEstado(libroId, estado);
    }
    
    /**
     * Lista préstamos en un rango de fechas
     */
    @Transactional(readOnly = true)
    public List<Prestamo> listarEnRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return prestamoRepository.findPrestamosEnRangoFechas(fechaInicio, fechaFin);
    }
    
    /**
     * Lista préstamos que vencen pronto (próximos 3 días)
     */
    @Transactional(readOnly = true)
    public List<Prestamo> listarQueVencenPronto() {
        LocalDateTime fechaActual = LocalDateTime.now();
        LocalDateTime fechaVencimiento = fechaActual.plusDays(3);
        return prestamoRepository.findPrestamosQueVencenPronto(fechaActual, fechaVencimiento);
    }
    
    /**
     * Lista préstamos recientes (últimos 30 días)
     */
    @Transactional(readOnly = true)
    public List<Prestamo> listarRecientes() {
        LocalDateTime fechaReciente = LocalDateTime.now().minusDays(30);
        return prestamoRepository.findPrestamosRecientes(fechaReciente);
    }
    
    /**
     * Lista préstamos por usuario con paginación
     */
    @Transactional(readOnly = true)
    public Page<Prestamo> listarPorUsuario(Long usuarioId, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("fechaPrestamo").descending());
        return prestamoRepository.findByUsuarioId(usuarioId, pageable);
    }
    
    /**
     * Lista préstamos por libro con paginación
     */
    @Transactional(readOnly = true)
    public Page<Prestamo> listarPorLibro(Long libroId, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("fechaPrestamo").descending());
        return prestamoRepository.findByLibroId(libroId, pageable);
    }
    
    /**
     * Cuenta préstamos por estado
     */
    @Transactional(readOnly = true)
    public long contarPorEstado(EstadoPrestamo estado) {
        return prestamoRepository.countByEstado(estado);
    }
    
    /**
     * Cuenta préstamos activos por usuario
     */
    @Transactional(readOnly = true)
    public long contarActivosPorUsuario(Long usuarioId) {
        return prestamoRepository.countByUsuarioIdAndEstado(usuarioId, EstadoPrestamo.ACTIVO);
    }
    
    /**
     * Cuenta préstamos activos por libro
     */
    @Transactional(readOnly = true)
    public long contarActivosPorLibro(Long libroId) {
        return prestamoRepository.countByLibroIdAndEstado(libroId, EstadoPrestamo.ACTIVO);
    }
    
    /**
     * Verifica si un usuario tiene préstamos vencidos
     */
    @Transactional(readOnly = true)
    public boolean tienePrestamosVencidos(Long usuarioId) {
        List<Prestamo> prestamosActivos = prestamoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoPrestamo.ACTIVO);
        return prestamosActivos.stream().anyMatch(Prestamo::isVencido);
    }
    
    /**
     * Verifica si un usuario puede tomar más préstamos (máximo 3 activos)
     */
    @Transactional(readOnly = true)
    public boolean puedeTomarPrestamo(Long usuarioId) {
        long prestamosActivos = prestamoRepository.countByUsuarioIdAndEstado(usuarioId, EstadoPrestamo.ACTIVO);
        return prestamosActivos < 3;
    }
    
    /**
     * Cancela un préstamo
     */
    public Prestamo cancelarPrestamo(Long prestamoId, String observaciones) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado"));
        
        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new IllegalArgumentException("El préstamo no está activo");
        }
        
        // Actualizar el préstamo
        prestamo.setEstado(EstadoPrestamo.CANCELADO);
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            prestamo.setObservaciones(observaciones);
        }
        
        // Incrementar el stock disponible del libro
        libroService.incrementarStockDisponible(prestamo.getLibro().getId(), 1);
        
        return prestamoRepository.save(prestamo);
    }
} 