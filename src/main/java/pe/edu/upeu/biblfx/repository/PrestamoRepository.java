package pe.edu.upeu.biblfx.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.biblfx.model.EstadoPrestamo;
import pe.edu.upeu.biblfx.model.Prestamo;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    
    /**
     * Busca préstamos por usuario
     */
    List<Prestamo> findByUsuarioId(Long usuarioId);
    
    /**
     * Busca préstamos por libro
     */
    List<Prestamo> findByLibroId(Long libroId);
    
    /**
     * Busca préstamos por estado
     */
    List<Prestamo> findByEstado(EstadoPrestamo estado);
    
    /**
     * Busca préstamos vencidos
     */
    @Query("SELECT p FROM Prestamo p WHERE p.estado = 'ACTIVO' AND p.fechaDevolucionEsperada < :fechaActual")
    List<Prestamo> findPrestamosVencidos(@Param("fechaActual") LocalDateTime fechaActual);
    
    /**
     * Busca préstamos por usuario y estado
     */
    List<Prestamo> findByUsuarioIdAndEstado(Long usuarioId, EstadoPrestamo estado);
    
    /**
     * Busca préstamos por libro y estado
     */
    List<Prestamo> findByLibroIdAndEstado(Long libroId, EstadoPrestamo estado);
    
    /**
     * Busca préstamos en un rango de fechas
     */
    @Query("SELECT p FROM Prestamo p WHERE p.fechaPrestamo BETWEEN :fechaInicio AND :fechaFin")
    List<Prestamo> findPrestamosEnRangoFechas(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Busca préstamos con paginación y filtros
     */
    @Query("SELECT p FROM Prestamo p WHERE " +
           "(:usuarioId IS NULL OR p.usuario.id = :usuarioId) AND " +
           "(:libroId IS NULL OR p.libro.id = :libroId) AND " +
           "(:estado IS NULL OR p.estado = :estado) AND " +
           "(:fechaInicio IS NULL OR p.fechaPrestamo >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR p.fechaPrestamo <= :fechaFin)")
    Page<Prestamo> findPrestamosWithFilters(
            @Param("usuarioId") Long usuarioId,
            @Param("libroId") Long libroId,
            @Param("estado") EstadoPrestamo estado,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable);
    
    /**
     * Cuenta préstamos por estado
     */
    long countByEstado(EstadoPrestamo estado);
    
    /**
     * Cuenta préstamos activos por usuario
     */
    long countByUsuarioIdAndEstado(Long usuarioId, EstadoPrestamo estado);
    
    /**
     * Cuenta préstamos activos por libro
     */
    long countByLibroIdAndEstado(Long libroId, EstadoPrestamo estado);
    
    /**
     * Busca préstamos que vencen pronto (en los próximos 3 días)
     */
    @Query("SELECT p FROM Prestamo p WHERE p.estado = 'ACTIVO' AND " +
           "p.fechaDevolucionEsperada BETWEEN :fechaActual AND :fechaVencimiento")
    List<Prestamo> findPrestamosQueVencenPronto(
            @Param("fechaActual") LocalDateTime fechaActual,
            @Param("fechaVencimiento") LocalDateTime fechaVencimiento);
    
    /**
     * Busca préstamos recientes (últimos 30 días)
     */
    @Query("SELECT p FROM Prestamo p WHERE p.fechaPrestamo >= :fechaReciente")
    List<Prestamo> findPrestamosRecientes(@Param("fechaReciente") LocalDateTime fechaReciente);
    
    /**
     * Busca préstamos por usuario con paginación
     */
    Page<Prestamo> findByUsuarioId(Long usuarioId, Pageable pageable);
    
    /**
     * Busca préstamos por libro con paginación
     */
    Page<Prestamo> findByLibroId(Long libroId, Pageable pageable);
} 