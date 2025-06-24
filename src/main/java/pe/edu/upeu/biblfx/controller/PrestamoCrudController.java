package pe.edu.upeu.biblfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.edu.upeu.biblfx.model.EstadoPrestamo;
import pe.edu.upeu.biblfx.model.Libro;
import pe.edu.upeu.biblfx.model.Prestamo;
import pe.edu.upeu.biblfx.model.Usuario;
import pe.edu.upeu.biblfx.service.LibroService;
import pe.edu.upeu.biblfx.service.PrestamoService;
import pe.edu.upeu.biblfx.service.UsuarioService;
import pe.edu.upeu.biblfx.repository.PrestamoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Component
public class PrestamoCrudController {
    
    @FXML private Label lblTitulo;
    @FXML private ComboBox<Usuario> cmbUsuario;
    @FXML private ComboBox<Libro> cmbLibro;
    @FXML private DatePicker dpFechaDevolucionEsperada;
    @FXML private ComboBox<EstadoPrestamo> cmbEstado;
    @FXML private TextArea txtObservaciones;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    
    @Autowired
    private PrestamoRepository prestamoRepository;
    
    @Autowired
    private PrestamoService prestamoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private LibroService libroService;
    
    private Prestamo prestamo;
    private Consumer<Void> onSuccess;
    
    @FXML
    public void initialize() {
        configurarComboBoxes();
        configurarValidaciones();
        cargarDatos();
    }
    
    private void configurarComboBoxes() {
        // Configurar ComboBox de usuarios
        cmbUsuario.setCellFactory(param -> new ListCell<Usuario>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getUsername() + " - " + item.getNombreCompleto());
                }
            }
        });
        cmbUsuario.setButtonCell(cmbUsuario.getCellFactory().call(null));
        
        // Configurar ComboBox de libros
        cmbLibro.setCellFactory(param -> new ListCell<Libro>() {
            @Override
            protected void updateItem(Libro item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitulo() + " - " + item.getAutor() + " (Disponible: " + item.getCantidadDisponible() + ")");
                }
            }
        });
        cmbLibro.setButtonCell(cmbLibro.getCellFactory().call(null));
        
        // Configurar ComboBox de estados
        cmbEstado.setItems(javafx.collections.FXCollections.observableArrayList(Arrays.asList(EstadoPrestamo.values())));
        cmbEstado.setCellFactory(param -> new ListCell<EstadoPrestamo>() {
            @Override
            protected void updateItem(EstadoPrestamo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDescripcion());
                }
            }
        });
        cmbEstado.setButtonCell(cmbEstado.getCellFactory().call(null));
    }
    
    private void configurarValidaciones() {
        // Validación de observaciones
        txtObservaciones.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 500) {
                txtObservaciones.setText(oldValue);
            }
        });
    }
    
    private void cargarDatos() {
        try {
            // Cargar usuarios activos
            List<Usuario> usuarios = usuarioService.listarActivos();
            cmbUsuario.setItems(javafx.collections.FXCollections.observableArrayList(usuarios));
            
            // Cargar libros disponibles
            List<Libro> libros = libroService.listarDisponibles();
            cmbLibro.setItems(javafx.collections.FXCollections.observableArrayList(libros));
            
            // Establecer fecha por defecto (7 días desde hoy)
            dpFechaDevolucionEsperada.setValue(LocalDate.now().plusDays(7));
            
            // Establecer estado por defecto
            cmbEstado.setValue(EstadoPrestamo.ACTIVO);
            
        } catch (Exception e) {
            mostrarError("Error", "Error al cargar datos: " + e.getMessage());
        }
    }
    
    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
        if (prestamo != null) {
            // Modo edición
            lblTitulo.setText("Editar Préstamo");
            cmbUsuario.setValue(prestamo.getUsuario());
            cmbLibro.setValue(prestamo.getLibro());
            dpFechaDevolucionEsperada.setValue(prestamo.getFechaDevolucionEsperada().toLocalDate());
            cmbEstado.setValue(prestamo.getEstado());
            txtObservaciones.setText(prestamo.getObservaciones());
            
            // Deshabilitar campos que no se pueden editar
            cmbUsuario.setDisable(true);
            cmbLibro.setDisable(true);
        } else {
            // Modo creación
            lblTitulo.setText("Nuevo Préstamo");
            cmbUsuario.setDisable(false);
            cmbLibro.setDisable(false);
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
            if (prestamo == null) {
                // Crear nuevo préstamo
                prestamoService.crearPrestamo(
                    cmbUsuario.getValue().getId(),
                    cmbLibro.getValue().getId(),
                    dpFechaDevolucionEsperada.getValue().atStartOfDay(),
                    txtObservaciones.getText().trim().isEmpty() ? null : txtObservaciones.getText().trim()
                );
                mostrarInformacion("Éxito", "Préstamo creado correctamente");
            } else {
                // Actualizar préstamo existente - solo fecha de devolución y observaciones
                prestamo.setFechaDevolucionEsperada(dpFechaDevolucionEsperada.getValue().atStartOfDay());
                prestamo.setObservaciones(txtObservaciones.getText().trim().isEmpty() ? null : txtObservaciones.getText().trim());
                
                // Guardar directamente en el repositorio
                prestamoRepository.save(prestamo);
                mostrarInformacion("Éxito", "Préstamo actualizado correctamente");
            }
            
            if (onSuccess != null) {
                onSuccess.accept(null);
            }
            
            cerrarVentana();
            
        } catch (Exception e) {
            mostrarError("Error", "Error al guardar préstamo: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }
    
    private boolean validarFormulario() {
        // Validar usuario
        if (cmbUsuario.getValue() == null) {
            mostrarError("Error", "Debe seleccionar un usuario");
            cmbUsuario.requestFocus();
            return false;
        }
        
        // Validar libro
        if (cmbLibro.getValue() == null) {
            mostrarError("Error", "Debe seleccionar un libro");
            cmbLibro.requestFocus();
            return false;
        }
        
        // Validar fecha de devolución
        if (dpFechaDevolucionEsperada.getValue() == null) {
            mostrarError("Error", "Debe seleccionar una fecha de devolución");
            dpFechaDevolucionEsperada.requestFocus();
            return false;
        }
        
        if (dpFechaDevolucionEsperada.getValue().isBefore(LocalDate.now())) {
            mostrarError("Error", "La fecha de devolución no puede ser anterior a hoy");
            dpFechaDevolucionEsperada.requestFocus();
            return false;
        }
        
        // Validar estado
        if (cmbEstado.getValue() == null) {
            mostrarError("Error", "Debe seleccionar un estado");
            cmbEstado.requestFocus();
            return false;
        }
        
        // Validar disponibilidad del libro (solo para nuevos préstamos)
        if (prestamo == null && cmbLibro.getValue().getCantidadDisponible() <= 0) {
            mostrarError("Error", "El libro seleccionado no tiene ejemplares disponibles");
            cmbLibro.requestFocus();
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