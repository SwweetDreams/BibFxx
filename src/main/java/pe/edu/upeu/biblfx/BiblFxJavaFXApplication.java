package pe.edu.upeu.biblfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Clase principal de la aplicación JavaFX que se liga a BiblFxApplication
 * para arrancar el sistema de gestión de biblioteca.
 */
public class BiblFxJavaFXApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private Parent rootNode;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        // Inicializar el contexto de Spring
        springContext = SpringApplication.run(BiblFxApplication.class);
        
        // Cargar la vista de login
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        rootNode = fxmlLoader.load();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Configurar la escena
            Scene scene = new Scene(rootNode);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            // Configurar la ventana principal
            primaryStage.setTitle("BiblFx - Sistema de Gestión de Biblioteca");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            
            // Mostrar la ventana
            primaryStage.show();
            
        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void stop() throws Exception {
        // Cerrar el contexto de Spring al salir
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
    }
} 