package pe.edu.upeu.biblfx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.biblfx.model.RolUsuario;
import pe.edu.upeu.biblfx.model.Usuario;
import pe.edu.upeu.biblfx.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private EncriptacionService encriptacionService;
    
    /**
     * Guarda un nuevo usuario
     */
    public Usuario guardarUsuario(Usuario usuario) {
        // Validar que el username no exista
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        
        // Validar que el email no exista si se proporciona
        if (usuario.getEmail() != null && !usuario.getEmail().trim().isEmpty()) {
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
        }
        
        // Encriptar la contraseña
        usuario.setPassword(encriptacionService.encriptarPassword(usuario.getPassword()));
        
        // Establecer fecha de creación
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setActivo(true);
        
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Actualiza un usuario existente
     */
    public Usuario actualizarUsuario(Usuario usuario) {
        Usuario usuarioExistente = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        // Verificar si el username cambió y si ya existe
        if (!usuarioExistente.getUsername().equals(usuario.getUsername()) &&
            usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        
        // Verificar si el email cambió y si ya existe
        if (usuario.getEmail() != null && !usuario.getEmail().trim().isEmpty()) {
            if (!usuarioExistente.getEmail().equals(usuario.getEmail()) &&
                usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
        }
        
        // Actualizar campos
        usuarioExistente.setUsername(usuario.getUsername());
        usuarioExistente.setNombreCompleto(usuario.getNombreCompleto());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setRol(usuario.getRol());
        usuarioExistente.setActivo(usuario.isActivo());
        
        // Solo actualizar contraseña si se proporciona una nueva
        if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
            usuarioExistente.setPassword(encriptacionService.encriptarPassword(usuario.getPassword()));
        }
        
        return usuarioRepository.save(usuarioExistente);
    }
    
    /**
     * Busca un usuario por ID
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    /**
     * Busca un usuario por username
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
    
    /**
     * Busca un usuario por email
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    /**
     * Autentica un usuario
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> autenticarUsuario(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.isActivo() && encriptacionService.verificarPassword(password, usuario.getPassword())) {
                // Actualizar último acceso
                usuario.setUltimoAcceso(LocalDateTime.now());
                usuarioRepository.save(usuario);
                return Optional.of(usuario);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Lista todos los usuarios con paginación
     */
    @Transactional(readOnly = true)
    public Page<Usuario> listarUsuarios(int pagina, int tamanio, String ordenarPor) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by(ordenarPor));
        return usuarioRepository.findAll(pageable);
    }
    
    /**
     * Busca usuarios con filtros y paginación
     */
    @Transactional(readOnly = true)
    public Page<Usuario> buscarUsuariosConFiltros(
            String username, String nombreCompleto, String email, 
            RolUsuario rol, Boolean activo, int pagina, int tamanio) {
        
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("username"));
        return usuarioRepository.findUsuariosWithFilters(username, nombreCompleto, email, rol, activo, pageable);
    }
    
    /**
     * Lista usuarios por rol
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol);
    }
    
    /**
     * Lista usuarios activos
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }
    
    /**
     * Desactiva un usuario
     */
    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
    
    /**
     * Activa un usuario
     */
    public void activarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }
    
    /**
     * Cambia la contraseña de un usuario
     */
    public void cambiarPassword(Long id, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        if (!encriptacionService.validarFortalezaPassword(nuevaPassword)) {
            throw new IllegalArgumentException("La contraseña no cumple con los requisitos de seguridad");
        }
        
        usuario.setPassword(encriptacionService.encriptarPassword(nuevaPassword));
        usuarioRepository.save(usuario);
    }
    
    /**
     * Genera una nueva contraseña para un usuario
     */
    public String generarNuevaPassword(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        String nuevaPassword = encriptacionService.generarPasswordAleatorio();
        usuario.setPassword(encriptacionService.encriptarPassword(nuevaPassword));
        usuarioRepository.save(usuario);
        
        return nuevaPassword;
    }
    
    /**
     * Cuenta usuarios por rol
     */
    @Transactional(readOnly = true)
    public long contarPorRol(RolUsuario rol) {
        return usuarioRepository.countByRol(rol);
    }
    
    /**
     * Cuenta usuarios activos
     */
    @Transactional(readOnly = true)
    public long contarActivos() {
        return usuarioRepository.countByActivoTrue();
    }
    
    /**
     * Busca usuarios inactivos
     */
    @Transactional(readOnly = true)
    public List<Usuario> buscarInactivos(LocalDateTime fechaLimite) {
        return usuarioRepository.findUsuariosInactivos(fechaLimite);
    }
} 