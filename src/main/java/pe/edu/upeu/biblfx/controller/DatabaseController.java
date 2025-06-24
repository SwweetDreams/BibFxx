package pe.edu.upeu.biblfx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.biblfx.service.DatabaseResetService;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para gestionar la base de datos durante desarrollo
 * ATENCIÓN: Solo usar en entorno de desarrollo
 */
@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    @Autowired
    private DatabaseResetService databaseResetService;

    /**
     * Resetea la base de datos
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetDatabase() {
        try {
            databaseResetService.resetDatabase();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Base de datos reseteada exitosamente");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error al resetear la base de datos: " + e.getMessage());
            response.put("status", "error");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Obtiene estadísticas de la base de datos
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDatabaseStats() {
        try {
            databaseResetService.showDatabaseStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("isEmpty", databaseResetService.isDatabaseEmpty());
            response.put("message", "Estadísticas obtenidas");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al obtener estadísticas: " + e.getMessage());
            response.put("status", "error");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 