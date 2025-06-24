package pe.edu.upeu.biblfx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.biblfx.repository.LibroRepository;
import pe.edu.upeu.biblfx.repository.PrestamoRepository;
import pe.edu.upeu.biblfx.repository.UsuarioRepository;

/**
 * Servicio para resetear la base de datos
 * Útil para desarrollo y pruebas
 */
@Service
public class DatabaseResetService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private PrestamoRepository prestamoRepository;

    /**
     * Limpia todos los datos de la base de datos
     * ATENCIÓN: Este método elimina TODOS los datos
     */
    @Transactional
    public void resetDatabase() {
        System.out.println("🗑️ Limpiando base de datos...");
        
        // Eliminar en orden para respetar las restricciones de clave foránea
        prestamoRepository.deleteAll();
        libroRepository.deleteAll();
        usuarioRepository.deleteAll();
        
        System.out.println("✅ Base de datos limpiada exitosamente");
        System.out.println("🔄 Reinicia la aplicación para que se inicialice con datos de prueba");
    }

    /**
     * Verifica si la base de datos está vacía
     */
    public boolean isDatabaseEmpty() {
        return usuarioRepository.count() == 0 && 
               libroRepository.count() == 0 && 
               prestamoRepository.count() == 0;
    }

    /**
     * Muestra estadísticas de la base de datos
     */
    public void showDatabaseStats() {
        System.out.println("📊 Estadísticas de la base de datos:");
        System.out.println("   👥 Usuarios: " + usuarioRepository.count());
        System.out.println("   📚 Libros: " + libroRepository.count());
        System.out.println("   📖 Préstamos: " + prestamoRepository.count());
    }
} 