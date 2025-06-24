package pe.edu.upeu.biblfx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de seguridad para la aplicación BiblFx
 */
@Configuration
public class SecurityConfig {

    /**
     * Bean para el encoder de contraseñas usando BCrypt
     * Factor de trabajo 12 para mayor seguridad
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
} 