package pe.edu.upeu.biblfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.edu.upeu.biblfx.model.RolUsuario;
import pe.edu.upeu.biblfx.model.Usuario;
import pe.edu.upeu.biblfx.service.UsuarioService;

import java.util.Arrays;
import java.util.function.Consumer;

@Component
public class UsuarioCrudController {
    
    @FXML private Label lblTitulo;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtNombreCompleto;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<RolUsuario> cmbRol;
    @FXML private CheckBox chkActivo;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    
    @Autowired
    private UsuarioService usuarioService;
    
    private Usuario usuario;
    private Consumer<Void> onSuccess;
    
    @FXML
    public void initialize() {
        configurarComboBox();
        configurarValidaciones();
    }
    
    private void configurarComboBox() {
        cmbRol.setItems(javafx.collections.FXCollections.observableArrayList(Arrays.asList(RolUsuario.values())));
        cmbRol.setCellFactory(param -> new ListCell<RolUsuario>() {
            @Override
            protected void updateItem(RolUsuario item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDescripcion());
                }
            }
        });
        cmbRol.setButtonCell(cmbRol.getCellFactory().call(null));
    }
    
    private void configurarValidaciones() {
        // Validación de username
        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 50) {
                txtUsername.setText(oldValue);
            }
        });
        
        // Validación de nombre completo
        txtNombreCompleto.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 100) {
                txtNombreCompleto.setText(oldValue);
            }
        });
        
        // Validación de email
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 100) {
                txtEmail.setText(oldValue);
            }
        });
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            // Modo edición
            lblTitulo.setText("Editar Usuario");
            txtUsername.setText(usuario.getUsername());
            txtUsername.setDisable(true); // No permitir cambiar username en edición
            txtPassword.setPromptText("Dejar vacío para mantener contraseña actual");
            txtNombreCompleto.setText(usuario.getNombreCompleto());
            txtEmail.setText(usuario.getEmail());
            cmbRol.setValue(usuario.getRol());
            chkActivo.setSelected(usuario.isActivo());
        } else {
            // Modo creación
            lblTitulo.setText("Nuevo Usuario");
            txtUsername.setDisable(false);
            txtPassword.setPromptText("Ingrese contraseña");
            chkActivo.setSelected(true);
        }
    }
    
    public void setOnSuccess(Consumer<Void> onSuccess) {
        this.onSuccess = onSuccess;
    }
    
    @FXML
    private void handleGuardar() {
        if (!validarFormulario()) {
            return;
        }
        
        try {
            if (usuario == null) {
                // Crear nuevo usuario
                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.setUsername(txtUsername.getText().trim());
                nuevoUsuario.setPassword(txtPassword.getText());
                nuevoUsuario.setNombreCompleto(txtNombreCompleto.getText().trim());
                nuevoUsuario.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
                nuevoUsuario.setRol(cmbRol.getValue());
                nuevoUsuario.setActivo(chkActivo.isSelected());
                
                usuarioService.guardarUsuario(nuevoUsuario);
                mostrarInformacion("Éxito", "Usuario creado correctamente");
            } else {
                // Actualizar usuario existente
                usuario.setNombreCompleto(txtNombreCompleto.getText().trim());
                usuario.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
                usuario.setRol(cmbRol.getValue());
                usuario.setActivo(chkActivo.isSelected());
                
                // Actualizar contraseña solo si se ingresó una nueva
                if (!txtPassword.getText().isEmpty()) {
                    usuario.setPassword(txtPassword.getText());
                }
                
                usuarioService.actualizarUsuario(usuario);
                mostrarInformacion("Éxito", "Usuario actualizado correctamente");
            }
            
            if (onSuccess != null) {
                onSuccess.accept(null);
            }
            
            cerrarVentana();
            
        } catch (Exception e) {
            mostrarError("Error", "Error al guardar usuario: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }
    
    private boolean validarFormulario() {
        // Validar username
        if (txtUsername.getText().trim().isEmpty()) {
            mostrarError("Error", "El nombre de usuario es obligatorio");
            txtUsername.requestFocus();
            return false;
        }
        
        if (usuario == null && txtPassword.getText().isEmpty()) {
            mostrarError("Error", "La contraseña es obligatoria para nuevos usuarios");
            txtPassword.requestFocus();
            return false;
        }
        
        // Validar nombre completo
        if (txtNombreCompleto.getText().trim().isEmpty()) {
            mostrarError("Error", "El nombre completo es obligatorio");
            txtNombreCompleto.requestFocus();
            return false;
        }
        
        // Validar rol
        if (cmbRol.getValue() == null) {
            mostrarError("Error", "Debe seleccionar un rol");
            cmbRol.requestFocus();
            return false;
        }
        
        // Validar email si se ingresó
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarError("Error", "El formato del email no es válido");
            txtEmail.requestFocus();
            return false;
        }
        
        return true;
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
    
    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }
} 