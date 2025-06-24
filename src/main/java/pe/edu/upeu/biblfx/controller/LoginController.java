package pe.edu.upeu.biblfx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.edu.upeu.biblfx.model.Usuario;
import pe.edu.upeu.biblfx.service.UsuarioService;

import java.io.IOException;
import java.util.Optional;

@Component
public class LoginController {
    
    @FXML
    private TextField txtUsername;
    
    @FXML
    private PasswordField txtPassword;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @FXML
    public void initialize() {
        // Configurar validación en tiempo real
        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 50) {
                txtUsername.setText(oldValue);
            }
        });
        
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 100) {
                txtPassword.setText(oldValue);
            }
        });
    }
    
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        
        // Validar campos
        if (username.isEmpty()) {
            mostrarError("Error de Validación", "El nombre de usuario es obligatorio");
            txtUsername.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            mostrarError("Error de Validación", "La contraseña es obligatoria");
            txtPassword.requestFocus();
            return;
        }
        
        try {
            // Intentar autenticar
            Optional<Usuario> usuarioOpt = usuarioService.autenticarUsuario(username, password);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                mostrarInformacion("Login Exitoso", "Bienvenido " + usuario.getNombreCompleto());
                
                // Abrir ventana principal
                abrirVentanaPrincipal(usuario);
                
                // Cerrar ventana de login
                cerrarVentanaLogin();
            } else {
                mostrarError("Error de Autenticación", "Usuario o contraseña incorrectos");
                txtPassword.clear();
                txtPassword.requestFocus();
            }
            
        } catch (Exception e) {
            mostrarError("Error del Sistema", "Error al intentar iniciar sesión: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClear() {
        txtUsername.clear();
        txtPassword.clear();
        txtUsername.requestFocus();
    }
    
    @FXML
    private void handleForgotPassword() {
        String username = txtUsername.getText().trim();
        
        if (username.isEmpty()) {
            mostrarError("Error", "Por favor ingrese su nombre de usuario primero");
            txtUsername.requestFocus();
            return;
        }
        
        try {
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorUsername(username);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                String nuevaPassword = usuarioService.generarNuevaPassword(usuario.getId());
                
                mostrarInformacion("Contraseña Generada", 
                    "Se ha generado una nueva contraseña para el usuario: " + username + 
                    "\n\nNueva contraseña: " + nuevaPassword + 
                    "\n\nPor favor, cambie esta contraseña después de iniciar sesión.");
                
                txtPassword.clear();
            } else {
                mostrarError("Usuario no encontrado", "No existe un usuario con ese nombre de usuario");
            }
            
        } catch (Exception e) {
            mostrarError("Error del Sistema", "Error al generar nueva contraseña: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRegister() {
        mostrarInformacion("Registro de Usuario", 
            "Para registrar un nuevo usuario, contacte al administrador del sistema.");
    }
    
    private void abrirVentanaPrincipal(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenuView.fxml"));
            Parent root = loader.load();
            
            MainMenuController controller = loader.getController();
            controller.setUsuarioLogueado(usuario);
            controller.inicializarDatos();
            
            Stage stage = new Stage();
            stage.setTitle("BiblFx - Sistema de Gestión de Biblioteca");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
            
        } catch (IOException e) {
            mostrarError("Error del Sistema", "Error al abrir la ventana principal: " + e.getMessage());
        }
    }
    
    private void cerrarVentanaLogin() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
} 