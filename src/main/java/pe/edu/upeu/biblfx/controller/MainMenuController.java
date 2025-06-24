package pe.edu.upeu.biblfx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import pe.edu.upeu.biblfx.model.*;
import pe.edu.upeu.biblfx.service.UsuarioService;
import pe.edu.upeu.biblfx.service.LibroService;
import pe.edu.upeu.biblfx.service.PrestamoService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MainMenuController {
    
    @FXML private Label lblUsuario;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblTotalLibros;
    @FXML private Label lblTotalPrestamos;
    @FXML private Label lblPrestamosActivos;
    @FXML private Label lblPrestamosVencidos;
    @FXML private Label lblLibrosDisponibles;
    @FXML private Label lblStockBajo;
    
    // Tabla Usuarios
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, Long> colIdUsuario;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colNombreCompleto;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, RolUsuario> colRol;
    @FXML private TableColumn<Usuario, Boolean> colActivo;
    @FXML private TableColumn<Usuario, String> colFechaCreacion;
    @FXML private TextField txtBuscarUsuario;
    @FXML private Label lblPaginaUsuario;
    
    // Tabla Libros
    @FXML private TableView<Libro> tblLibros;
    @FXML private TableColumn<Libro, Long> colIdLibro;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colIsbn;
    @FXML private TableColumn<Libro, String> colEditorial;
    @FXML private TableColumn<Libro, CategoriaLibro> colCategoria;
    @FXML private TableColumn<Libro, Integer> colDisponible;
    @FXML private TableColumn<Libro, Integer> colTotal;
    @FXML private TableColumn<Libro, Boolean> colActivoLibro;
    @FXML private TextField txtBuscarLibro;
    @FXML private Label lblPaginaLibro;
    
    // Tabla Préstamos
    @FXML private TableView<Prestamo> tblPrestamos;
    @FXML private TableColumn<Prestamo, Long> colIdPrestamo;
    @FXML private TableColumn<Prestamo, String> colUsuarioPrestamo;
    @FXML private TableColumn<Prestamo, String> colLibroPrestamo;
    @FXML private TableColumn<Prestamo, String> colFechaPrestamo;
    @FXML private TableColumn<Prestamo, String> colFechaDevolucion;
    @FXML private TableColumn<Prestamo, EstadoPrestamo> colEstado;
    @FXML private TableColumn<Prestamo, String> colObservaciones;
    @FXML private TextField txtBuscarPrestamo;
    @FXML private Label lblPaginaPrestamo;
    
    // Reportes
    @FXML private TextArea txtReporte;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private LibroService libroService;
    
    @Autowired
    private PrestamoService prestamoService;
    
    private Usuario usuarioLogueado;
    private int paginaActualUsuarios = 0;
    private int paginaActualLibros = 0;
    private int paginaActualPrestamos = 0;
    private static final int TAMANIO_PAGINA = 10;
    
    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
        lblUsuario.setText("Usuario: " + usuario.getNombreCompleto() + " (" + usuario.getRol().getDescripcion() + ")");
    }
    
    public void inicializarDatos() {
        configurarTablas();
        cargarEstadisticas();
        cargarUsuarios();
        cargarLibros();
        cargarPrestamos();
    }
    
    private void configurarTablas() {
        // Configurar tabla de usuarios
        colIdUsuario.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colNombreCompleto.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colFechaCreacion.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
        
        // Configurar tabla de libros
        colIdLibro.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colEditorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("cantidadDisponible"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("cantidadTotal"));
        colActivoLibro.setCellValueFactory(new PropertyValueFactory<>("activo"));
        
        // Configurar tabla de préstamos
        colIdPrestamo.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsuarioPrestamo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsuario().getUsername()));
        colLibroPrestamo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLibro().getTitulo()));
        colFechaPrestamo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFechaPrestamo().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        colFechaDevolucion.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFechaDevolucionEsperada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));
    }
    
    private void cargarEstadisticas() {
        try {
            lblTotalUsuarios.setText(String.valueOf(usuarioService.contarActivos()));
            lblTotalLibros.setText(String.valueOf(libroService.contarActivos()));
            lblTotalPrestamos.setText(String.valueOf(prestamoService.contarPorEstado(EstadoPrestamo.ACTIVO)));
            lblPrestamosActivos.setText(String.valueOf(prestamoService.contarPorEstado(EstadoPrestamo.ACTIVO)));
            lblPrestamosVencidos.setText(String.valueOf(prestamoService.listarVencidos().size()));
            lblLibrosDisponibles.setText(String.valueOf(libroService.contarDisponibles()));
            lblStockBajo.setText(String.valueOf(libroService.listarConStockBajo().size()));
        } catch (Exception e) {
            mostrarError("Error", "Error al cargar estadísticas: " + e.getMessage());
        }
    }
    
    private void cargarUsuarios() {
        try {
            Page<Usuario> pagina = usuarioService.listarUsuarios(paginaActualUsuarios, TAMANIO_PAGINA, "username");
            ObservableList<Usuario> usuarios = FXCollections.observableArrayList(pagina.getContent());
            tblUsuarios.setItems(usuarios);
            lblPaginaUsuario.setText("Página " + (paginaActualUsuarios + 1) + " de " + pagina.getTotalPages());
        } catch (Exception e) {
            mostrarError("Error", "Error al cargar usuarios: " + e.getMessage());
        }
    }
    
    private void cargarLibros() {
        try {
            Page<Libro> pagina = libroService.listarLibros(paginaActualLibros, TAMANIO_PAGINA, "titulo");
            ObservableList<Libro> libros = FXCollections.observableArrayList(pagina.getContent());
            tblLibros.setItems(libros);
            lblPaginaLibro.setText("Página " + (paginaActualLibros + 1) + " de " + pagina.getTotalPages());
        } catch (Exception e) {
            mostrarError("Error", "Error al cargar libros: " + e.getMessage());
        }
    }
    
    private void cargarPrestamos() {
        try {
            Page<Prestamo> pagina = prestamoService.listarPrestamos(paginaActualPrestamos, TAMANIO_PAGINA, "fechaPrestamo");
            ObservableList<Prestamo> prestamos = FXCollections.observableArrayList(pagina.getContent());
            tblPrestamos.setItems(prestamos);
            lblPaginaPrestamo.setText("Página " + (paginaActualPrestamos + 1) + " de " + pagina.getTotalPages());
        } catch (Exception e) {
            mostrarError("Error", "Error al cargar préstamos: " + e.getMessage());
        }
    }
    
    // Métodos del Dashboard
    @FXML
    private void handleGestionarUsuarios() {
        TabPane tabPane = (TabPane) lblUsuario.getScene().lookup("#tabPane");
        tabPane.getSelectionModel().select(1);
    }
    
    @FXML
    private void handleGestionarLibros() {
        TabPane tabPane = (TabPane) lblUsuario.getScene().lookup("#tabPane");
        tabPane.getSelectionModel().select(2);
    }
    
    @FXML
    private void handleGestionarPrestamos() {
        TabPane tabPane = (TabPane) lblUsuario.getScene().lookup("#tabPane");
        tabPane.getSelectionModel().select(3);
    }
    
    @FXML
    private void handleVerReportes() {
        TabPane tabPane = (TabPane) lblUsuario.getScene().lookup("#tabPane");
        tabPane.getSelectionModel().select(4);
    }
    
    // Métodos de Usuarios
    @FXML
    private void handleNuevoUsuario() {
        abrirFormularioUsuario(null);
    }
    
    @FXML
    private void handleEditarUsuario() {
        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Error", "Por favor seleccione un usuario");
            return;
        }
        abrirFormularioUsuario(seleccionado);
    }
    
    @FXML
    private void handleEliminarUsuario() {
        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Error", "Por favor seleccione un usuario");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro de que desea desactivar el usuario: " + seleccionado.getUsername() + "?");
        
        if (alert.showAndWait().orElse(null) == ButtonType.OK) {
            try {
                usuarioService.desactivarUsuario(seleccionado.getId());
                mostrarInformacion("Éxito", "Usuario desactivado correctamente");
                cargarUsuarios();
                cargarEstadisticas();
            } catch (Exception e) {
                mostrarError("Error", "Error al desactivar usuario: " + e.getMessage());
            }
        }
    }
    
    private void abrirFormularioUsuario(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UsuarioFormView.fxml"));
            Parent root = loader.load();
            
            UsuarioCrudController controller = loader.getController();
            controller.setUsuario(usuario);
            controller.setOnSuccess((Void v) -> {
                cargarUsuarios();
                cargarEstadisticas();
            });
            
            Stage stage = new Stage();
            stage.setTitle(usuario == null ? "Nuevo Usuario" : "Editar Usuario");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            
        } catch (Exception e) {
            mostrarError("Error", "Error al abrir formulario: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBuscarUsuario() {
        String busqueda = txtBuscarUsuario.getText().trim();
        if (busqueda.isEmpty()) {
            cargarUsuarios();
            return;
        }
        
        try {
            Page<Usuario> pagina = usuarioService.buscarUsuariosConFiltros(
                busqueda, busqueda, busqueda, null, null, paginaActualUsuarios, TAMANIO_PAGINA);
            ObservableList<Usuario> usuarios = FXCollections.observableArrayList(pagina.getContent());
            tblUsuarios.setItems(usuarios);
            lblPaginaUsuario.setText("Página " + (paginaActualUsuarios + 1) + " de " + pagina.getTotalPages());
        } catch (Exception e) {
            mostrarError("Error", "Error al buscar usuarios: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAnteriorUsuario() {
        if (paginaActualUsuarios > 0) {
            paginaActualUsuarios--;
            cargarUsuarios();
        }
    }
    
    @FXML
    private void handleSiguienteUsuario() {
        paginaActualUsuarios++;
        cargarUsuarios();
    }
    
    // Métodos de Libros
    @FXML
    private void handleNuevoLibro() {
        abrirFormularioLibro(null);
    }
    
    @FXML
    private void handleEditarLibro() {
        Libro seleccionado = tblLibros.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Error", "Por favor seleccione un libro");
            return;
        }
        abrirFormularioLibro(seleccionado);
    }
    
    @FXML
    private void handleEliminarLibro() {
        Libro seleccionado = tblLibros.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Error", "Por favor seleccione un libro");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro de que desea desactivar el libro: " + seleccionado.getTitulo() + "?");
        
        if (alert.showAndWait().orElse(null) == ButtonType.OK) {
            try {
                libroService.desactivarLibro(seleccionado.getId());
                mostrarInformacion("Éxito", "Libro desactivado correctamente");
                cargarLibros();
                cargarEstadisticas();
            } catch (Exception e) {
                mostrarError("Error", "Error al desactivar libro: " + e.getMessage());
            }
        }
    }
    
    private void abrirFormularioLibro(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LibroFormView.fxml"));
            Parent root = loader.load();
            
            LibroCrudController controller = loader.getController();
            controller.setLibro(libro);
            controller.setOnSuccess((Void v) -> {
                cargarLibros();
                cargarEstadisticas();
            });
            
            Stage stage = new Stage();
            stage.setTitle(libro == null ? "Nuevo Libro" : "Editar Libro");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            
        } catch (Exception e) {
            mostrarError("Error", "Error al abrir formulario: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBuscarLibro() {
        String busqueda = txtBuscarLibro.getText().trim();
        if (busqueda.isEmpty()) {
            cargarLibros();
            return;
        }
        
        try {
            Page<Libro> pagina = libroService.buscarLibrosConFiltros(
                busqueda, busqueda, busqueda, busqueda, null, null, null, null, 
                paginaActualLibros, TAMANIO_PAGINA);
            ObservableList<Libro> libros = FXCollections.observableArrayList(pagina.getContent());
            tblLibros.setItems(libros);
            lblPaginaLibro.setText("Página " + (paginaActualLibros + 1) + " de " + pagina.getTotalPages());
        } catch (Exception e) {
            mostrarError("Error", "Error al buscar libros: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAnteriorLibro() {
        if (paginaActualLibros > 0) {
            paginaActualLibros--;
            cargarLibros();
        }
    }
    
    @FXML
    private void handleSiguienteLibro() {
        paginaActualLibros++;
        cargarLibros();
    }
    
    // Métodos de Préstamos
    @FXML
    private void handleNuevoPrestamo() {
        abrirFormularioPrestamo(null);
    }
    
    @FXML
    private void handleDevolverLibro() {
        Prestamo seleccionado = tblPrestamos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Error", "Por favor seleccione un préstamo");
            return;
        }
        
        if (seleccionado.getEstado() != EstadoPrestamo.ACTIVO) {
            mostrarError("Error", "Solo se pueden devolver préstamos activos");
            return;
        }
        
        try {
            prestamoService.devolverLibro(seleccionado.getId(), "Devolución realizada desde la interfaz");
            mostrarInformacion("Éxito", "Libro devuelto correctamente");
            cargarPrestamos();
            cargarEstadisticas();
        } catch (Exception e) {
            mostrarError("Error", "Error al devolver libro: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancelarPrestamo() {
        Prestamo seleccionado = tblPrestamos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Error", "Por favor seleccione un préstamo");
            return;
        }
        
        if (seleccionado.getEstado() != EstadoPrestamo.ACTIVO) {
            mostrarError("Error", "Solo se pueden cancelar préstamos activos");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cancelación");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro de que desea cancelar el préstamo?");
        
        if (alert.showAndWait().orElse(null) == ButtonType.OK) {
            try {
                prestamoService.cancelarPrestamo(seleccionado.getId(), "Préstamo cancelado desde la interfaz");
                mostrarInformacion("Éxito", "Préstamo cancelado correctamente");
                cargarPrestamos();
                cargarEstadisticas();
            } catch (Exception e) {
                mostrarError("Error", "Error al cancelar préstamo: " + e.getMessage());
            }
        }
    }
    
    private void abrirFormularioPrestamo(Prestamo prestamo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PrestamoFormView.fxml"));
            Parent root = loader.load();
            
            PrestamoCrudController controller = loader.getController();
            controller.setPrestamo(prestamo);
            controller.setOnSuccess((Void v) -> {
                cargarPrestamos();
                cargarEstadisticas();
            });
            
            Stage stage = new Stage();
            stage.setTitle(prestamo == null ? "Nuevo Préstamo" : "Editar Préstamo");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            
        } catch (Exception e) {
            mostrarError("Error", "Error al abrir formulario: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBuscarPrestamo() {
        String busqueda = txtBuscarPrestamo.getText().trim();
        if (busqueda.isEmpty()) {
            cargarPrestamos();
            return;
        }
        
        // Buscar por ID de préstamo
        try {
            Long idPrestamo = Long.parseLong(busqueda);
            prestamoService.buscarPorId(idPrestamo).ifPresent(prestamo -> {
                ObservableList<Prestamo> prestamos = FXCollections.observableArrayList(prestamo);
                tblPrestamos.setItems(prestamos);
                lblPaginaPrestamo.setText("Resultado de búsqueda");
            });
        } catch (NumberFormatException e) {
            mostrarError("Error", "Ingrese un ID de préstamo válido");
        }
    }
    
    @FXML
    private void handleAnteriorPrestamo() {
        if (paginaActualPrestamos > 0) {
            paginaActualPrestamos--;
            cargarPrestamos();
        }
    }
    
    @FXML
    private void handleSiguientePrestamo() {
        paginaActualPrestamos++;
        cargarPrestamos();
    }
    
    // Métodos de Reportes
    @FXML
    private void handleReportePrestamos() {
        try {
            List<Prestamo> prestamosActivos = prestamoService.listarActivos();
            List<Prestamo> prestamosVencidos = prestamoService.listarVencidos();
            
            StringBuilder reporte = new StringBuilder();
            reporte.append("=== REPORTE DE PRÉSTAMOS ===\n\n");
            reporte.append("Préstamos Activos: ").append(prestamosActivos.size()).append("\n");
            reporte.append("Préstamos Vencidos: ").append(prestamosVencidos.size()).append("\n\n");
            
            if (!prestamosVencidos.isEmpty()) {
                reporte.append("PRÉSTAMOS VENCIDOS:\n");
                prestamosVencidos.forEach(p -> 
                    reporte.append("- ").append(p.getUsuario().getUsername())
                          .append(" - ").append(p.getLibro().getTitulo())
                          .append(" (Vence: ").append(p.getFechaDevolucionEsperada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                          .append(")\n"));
            }
            
            txtReporte.setText(reporte.toString());
        } catch (Exception e) {
            mostrarError("Error", "Error al generar reporte: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleReporteLibros() {
        try {
            List<Libro> librosDisponibles = libroService.listarDisponibles();
            List<Libro> librosStockBajo = libroService.listarConStockBajo();
            
            StringBuilder reporte = new StringBuilder();
            reporte.append("=== REPORTE DE LIBROS ===\n\n");
            reporte.append("Libros Disponibles: ").append(librosDisponibles.size()).append("\n");
            reporte.append("Libros con Stock Bajo: ").append(librosStockBajo.size()).append("\n\n");
            
            if (!librosStockBajo.isEmpty()) {
                reporte.append("LIBROS CON STOCK BAJO:\n");
                librosStockBajo.forEach(l -> 
                    reporte.append("- ").append(l.getTitulo())
                          .append(" (Disponible: ").append(l.getCantidadDisponible())
                          .append("/").append(l.getCantidadTotal()).append(")\n"));
            }
            
            txtReporte.setText(reporte.toString());
        } catch (Exception e) {
            mostrarError("Error", "Error al generar reporte: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleReporteUsuarios() {
        try {
            long totalUsuarios = usuarioService.contarActivos();
            long administradores = usuarioService.contarPorRol(RolUsuario.ADMINISTRADOR);
            long bibliotecarios = usuarioService.contarPorRol(RolUsuario.BIBLIOTECARIO);
            long lectores = usuarioService.contarPorRol(RolUsuario.LECTOR);
            
            StringBuilder reporte = new StringBuilder();
            reporte.append("=== REPORTE DE USUARIOS ===\n\n");
            reporte.append("Total de Usuarios Activos: ").append(totalUsuarios).append("\n");
            reporte.append("Administradores: ").append(administradores).append("\n");
            reporte.append("Bibliotecarios: ").append(bibliotecarios).append("\n");
            reporte.append("Lectores: ").append(lectores).append("\n");
            
            txtReporte.setText(reporte.toString());
        } catch (Exception e) {
            mostrarError("Error", "Error al generar reporte: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cierre de Sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro de que desea cerrar sesión?");
        
        if (alert.showAndWait().orElse(null) == ButtonType.OK) {
            Stage stage = (Stage) lblUsuario.getScene().getWindow();
            stage.close();
        }
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