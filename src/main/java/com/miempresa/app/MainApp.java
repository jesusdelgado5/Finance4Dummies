package com.miempresa.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * Clase principal de la aplicación JavaFX.
 */
public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // 1) Cargar StartPage.fxml
        URL fxmlUrl = getClass().getResource("/com/miempresa/app/ui/StartPage.fxml");
        Objects.requireNonNull(fxmlUrl, "No se encontró StartPage.fxml");
        Parent root = FXMLLoader.load(fxmlUrl);

        // 2) Crear escena y aplicarle CSS
        Scene scene = new Scene(root, 1200, 600);
        URL cssUrl = getClass().getResource("/com/miempresa/app/ui/style.css");
        Objects.requireNonNull(cssUrl, "No se encontró style.css");
        scene.getStylesheets().add(cssUrl.toExternalForm());

        // 3) Configurar la ventana
        stage.setTitle("Gestión Financiera — Proyecto Final");

        // 4) Icono de la aplicación (ruta corregida)
        InputStream iconStream = getClass().getResourceAsStream(
                "/com/miempresa/app/resources/logo_utp.png"
        );
        Objects.requireNonNull(iconStream, "No se encontró logo_utp.png");
        stage.getIcons().add(new Image(iconStream));

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
