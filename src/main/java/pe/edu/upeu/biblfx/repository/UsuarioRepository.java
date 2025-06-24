package pe.edu.upeu.biblfx.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.biblfx.model.RolUsuario;
import pe.edu.upeu.biblfx.model.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    /**
     * Busca un usuario por su nombre de usuario
     */
    Optional<Usuario> findByUsername(String username);
    
    /**
     * Busca un usuario por su email
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el nombre de usuario dado
     */
    boolean existsByUsername(String username);
    
    /**
     * Verifica si existe un usuario con el email dado
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca usuarios por rol
     */
    List<Usuario> findByRol(RolUsuario rol);
    
    /**
     * Busca usuarios activos
     */
    List<Usuario> findByActivoTrue();
    
    /**
     * Busca usuarios por rol y estado activo
     */
    List<Usuario> findByRolAndActivoTrue(RolUsuario rol);
    
    /**
     * Busca usuarios con paginación y filtros
     */
    @Query("SELECT u FROM Usuario u WHERE " +
           "(:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
           "(:nombreCompleto IS NULL OR LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :nombreCompleto, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:rol IS NULL OR u.rol = :rol) AND " +
           "(:activo IS NULL OR u.activo = :activo)")
    Page<Usuario> findUsuariosWithFilters(
            @Param("username") String username,
            @Param("nombreCompleto") String nombreCompleto,
            @Param("email") String email,
            @Param("rol") RolUsuario rol,
            @Param("activo") Boolean activo,
            Pageable pageable);
    
    /**
     * Cuenta usuarios por rol
     */
    long countByRol(RolUsuario rol);
    
    /**
     * Cuenta usuarios activos
     */
    long countByActivoTrue();
    
    /**
     * Busca usuarios que no han accedido recientemente
     */
    @Query("SELECT u FROM Usuario u WHERE u.ultimoAcceso IS NULL OR u.ultimoAcceso < :fecha")
    List<Usuario> findUsuariosInactivos(@Param("fecha") java.time.LocalDateTime fecha);
} 