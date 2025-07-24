package com.miempresa.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
/**
 * Clase principal de la aplicación JavaFX.
 */
public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(
                "/com/miempresa/app/ui/MainMenu.fxml"
        ));
        Scene scene = new Scene(root, 800, 600);
        // Cargar CSS global
        scene.getStylesheets().add(getClass().getResource(
                "/com/miempresa/app/ui/style.css").toExternalForm());
        stage.setTitle("Gestión Financiera — Proyecto Final");
        stage.getIcons().add(new Image(
                getClass().getResourceAsStream(
                        "/com/miempresa/app/resources/logo_utp.png")));
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
