package pe.edu.upeu.biblfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.edu.upeu.biblfx.model.CategoriaLibro;
import pe.edu.upeu.biblfx.model.Libro;
import pe.edu.upeu.biblfx.service.LibroService;

import java.util.Arrays;
import java.util.function.Consumer;

@Component
public class LibroCrudController {
    
    @FXML private Label lblTitulo;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtIsbn;
    @FXML private TextField txtEditorial;
    @FXML private TextField txtAnioPublicacion;
    @FXML private ComboBox<CategoriaLibro> cmbCategoria;
    @FXML private Spinner<Integer> spnCantidadTotal;
    @FXML private Spinner<Integer> spnCantidadDisponible;
    @FXML private TextArea txtDescripcion;
    @FXML private CheckBox chkActivo;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    
    @Autowired
    private LibroService libroService;
    
    private Libro libro;
    private Consumer<Void> onSuccess;
    
    @FXML
    public void initialize() {
        configurarComboBox();
        configurarSpinners();
        configurarValidaciones();
    }
    
    private void configurarComboBox() {
        cmbCategoria.setItems(javafx.collections.FXCollections.observableArrayList(Arrays.asList(CategoriaLibro.values())));
        cmbCategoria.setCellFactory(param -> new ListCell<CategoriaLibro>() {
            @Override
            protected void updateItem(CategoriaLibro item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDescripcion());
                }
            }
        });
        cmbCategoria.setButtonCell(cmbCategoria.getCellFactory().call(null));
    }
    
    private void configurarSpinners() {
        // Configurar spinner de cantidad total
        SpinnerValueFactory<Integer> totalFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1);
        spnCantidadTotal.setValueFactory(totalFactory);
        
        // Configurar spinner de cantidad disponible
        SpinnerValueFactory<Integer> disponibleFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 1);
        spnCantidadDisponible.setValueFactory(disponibleFactory);
        
        // Sincronizar spinners
        spnCantidadTotal.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && spnCantidadDisponible.getValue() > newVal) {
                spnCantidadDisponible.getValueFactory().setValue(newVal);
            }
            SpinnerValueFactory<Integer> newFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, newVal, spnCantidadDisponible.getValue());
            spnCantidadDisponible.setValueFactory(newFactory);
        });
    }
    
    private void configurarValidaciones() {
        // Validación de título
        txtTitulo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 200) {
                txtTitulo.setText(oldValue);
            }
        });
        
        // Validación de autor
        txtAutor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 100) {
                txtAutor.setText(oldValue);
            }
        });
        
        // Validación de ISBN
        txtIsbn.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 20) {
                txtIsbn.setText(oldValue);
            }
        });
        
        // Validación de editorial
        txtEditorial.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 100) {
                txtEditorial.setText(oldValue);
            }
        });
        
        // Validación de año
        txtAnioPublicacion.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                txtAnioPublicacion.setText(oldValue);
            }
        });
    }
    
    public void setLibro(Libro libro) {
        this.libro = libro;
        if (libro != null) {
            // Modo edición
            lblTitulo.setText("Editar Libro");
            txtTitulo.setText(libro.getTitulo());
            txtAutor.setText(libro.getAutor());
            txtIsbn.setText(libro.getIsbn());
            txtEditorial.setText(libro.getEditorial());
            txtAnioPublicacion.setText(libro.getAnioPublicacion() != null ? libro.getAnioPublicacion().toString() : "");
            cmbCategoria.setValue(libro.getCategoria());
            spnCantidadTotal.getValueFactory().setValue(libro.getCantidadTotal());
            spnCantidadDisponible.getValueFactory().setValue(libro.getCantidadDisponible());
            txtDescripcion.setText(libro.getDescripcion());
            chkActivo.setSelected(libro.isActivo());
        } else {
            // Modo creación
            lblTitulo.setText("Nuevo Libro");
            spnCantidadTotal.getValueFactory().setValue(1);
            spnCantidadDisponible.getValueFactory().setValue(1);
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
            if (libro == null) {
                // Crear nuevo libro
                Libro nuevoLibro = new Libro();
                nuevoLibro.setTitulo(txtTitulo.getText().trim());
                nuevoLibro.setAutor(txtAutor.getText().trim());
                nuevoLibro.setIsbn(txtIsbn.getText().trim().isEmpty() ? null : txtIsbn.getText().trim());
                nuevoLibro.setEditorial(txtEditorial.getText().trim().isEmpty() ? null : txtEditorial.getText().trim());
                
                String anioStr = txtAnioPublicacion.getText().trim();
                if (!anioStr.isEmpty()) {
                    try {
                        nuevoLibro.setAnioPublicacion(Integer.parseInt(anioStr));
                    } catch (NumberFormatException e) {
                        // Ignorar si no es un número válido
                    }
                }
                
                nuevoLibro.setCategoria(cmbCategoria.getValue());
                nuevoLibro.setCantidadTotal(spnCantidadTotal.getValue());
                nuevoLibro.setCantidadDisponible(spnCantidadDisponible.getValue());
                nuevoLibro.setDescripcion(txtDescripcion.getText().trim().isEmpty() ? null : txtDescripcion.getText().trim());
                nuevoLibro.setActivo(chkActivo.isSelected());
                
                libroService.guardarLibro(nuevoLibro);
                mostrarInformacion("Éxito", "Libro creado correctamente");
            } else {
                // Actualizar libro existente
                libro.setTitulo(txtTitulo.getText().trim());
                libro.setAutor(txtAutor.getText().trim());
                libro.setIsbn(txtIsbn.getText().trim().isEmpty() ? null : txtIsbn.getText().trim());
                libro.setEditorial(txtEditorial.getText().trim().isEmpty() ? null : txtEditorial.getText().trim());
                
                String anioStr = txtAnioPublicacion.getText().trim();
                if (!anioStr.isEmpty()) {
                    try {
                        libro.setAnioPublicacion(Integer.parseInt(anioStr));
                    } catch (NumberFormatException e) {
                        libro.setAnioPublicacion(null);
                    }
                } else {
                    libro.setAnioPublicacion(null);
                }
                
                libro.setCategoria(cmbCategoria.getValue());
                libro.setCantidadTotal(spnCantidadTotal.getValue());
                libro.setCantidadDisponible(spnCantidadDisponible.getValue());
                libro.setDescripcion(txtDescripcion.getText().trim().isEmpty() ? null : txtDescripcion.getText().trim());
                libro.setActivo(chkActivo.isSelected());
                
                libroService.actualizarLibro(libro);
                mostrarInformacion("Éxito", "Libro actualizado correctamente");
            }
            
            if (onSuccess != null) {
                onSuccess.accept(null);
            }
            
            cerrarVentana();
            
        } catch (Exception e) {
            mostrarError("Error", "Error al guardar libro: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }
    
    private boolean validarFormulario() {
        // Validar título
        if (txtTitulo.getText().trim().isEmpty()) {
            mostrarError("Error", "El título es obligatorio");
            txtTitulo.requestFocus();
            return false;
        }
        
        // Validar autor
        if (txtAutor.getText().trim().isEmpty()) {
            mostrarError("Error", "El autor es obligatorio");
            txtAutor.requestFocus();
            return false;
        }
        
        // Validar categoría
        if (cmbCategoria.getValue() == null) {
            mostrarError("Error", "Debe seleccionar una categoría");
            cmbCategoria.requestFocus();
            return false;
        }
        
        // Validar cantidades
        if (spnCantidadTotal.getValue() <= 0) {
            mostrarError("Error", "La cantidad total debe ser mayor a 0");
            spnCantidadTotal.requestFocus();
            return false;
        }
        
        if (spnCantidadDisponible.getValue() > spnCantidadTotal.getValue()) {
            mostrarError("Error", "La cantidad disponible no puede ser mayor a la cantidad total");
            spnCantidadDisponible.requestFocus();
            return false;
        }
        
        // Validar año si se ingresó
        String anioStr = txtAnioPublicacion.getText().trim();
        if (!anioStr.isEmpty()) {
            try {
                int anio = Integer.parseInt(anioStr);
                if (anio < 1000 || anio > 2100) {
                    mostrarError("Error", "El año debe estar entre 1000 y 2100");
                    txtAnioPublicacion.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                mostrarError("Error", "El año debe ser un número válido");
                txtAnioPublicacion.requestFocus();
                return false;
            }
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