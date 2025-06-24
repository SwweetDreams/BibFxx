package pe.edu.upeu.biblfx.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EncriptacionService {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    public EncriptacionService() {
        this.passwordEncoder = new BCryptPasswordEncoder(12); // Factor de trabajo 12
    }
    
    /**
     * Encripta una contraseña usando BCrypt
     */
    public String encriptarPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }
        return passwordEncoder.encode(password);
    }
    
    /**
     * Verifica si una contraseña coincide con su versión encriptada
     */
    public boolean verificarPassword(String password, String passwordEncriptado) {
        if (password == null || passwordEncriptado == null) {
            return false;
        }
        return passwordEncoder.matches(password, passwordEncriptado);
    }
    
    /**
     * Genera una contraseña aleatoria segura
     */
    public String generarPasswordAleatorio() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        
        // Asegurar al menos una mayúscula, una minúscula, un número y un carácter especial
        password.append(generarCaracterAleatorio("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        password.append(generarCaracterAleatorio("abcdefghijklmnopqrstuvwxyz"));
        password.append(generarCaracterAleatorio("0123456789"));
        password.append(generarCaracterAleatorio("!@#$%^&*"));
        
        // Completar hasta 12 caracteres
        for (int i = 4; i < 12; i++) {
            password.append(generarCaracterAleatorio(caracteres));
        }
        
        // Mezclar los caracteres
        return mezclarString(password.toString());
    }
    
    private char generarCaracterAleatorio(String caracteres) {
        int indice = (int) (Math.random() * caracteres.length());
        return caracteres.charAt(indice);
    }
    
    private String mezclarString(String input) {
        char[] caracteres = input.toCharArray();
        for (int i = caracteres.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            char temp = caracteres[i];
            caracteres[i] = caracteres[j];
            caracteres[j] = temp;
        }
        return new String(caracteres);
    }
    
    /**
     * Valida la fortaleza de una contraseña
     */
    public boolean validarFortalezaPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean tieneMayuscula = password.matches(".*[A-Z].*");
        boolean tieneMinuscula = password.matches(".*[a-z].*");
        boolean tieneNumero = password.matches(".*\\d.*");
        boolean tieneCaracterEspecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        
        return tieneMayuscula && tieneMinuscula && tieneNumero && tieneCaracterEspecial;
    }
    
    /**
     * Obtiene el factor de trabajo actual del encoder
     */
    public int getFactorTrabajo() {
        return 12;
    }
} 