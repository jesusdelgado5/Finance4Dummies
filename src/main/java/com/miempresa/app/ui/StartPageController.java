package com.miempresa.app.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class StartPageController {

    @FXML private ImageView logoUni;
    @FXML private ImageView logoFac;
    @FXML private Button startButton;

    @FXML
    private void initialize() {
        // Las imágenes se han declarado y cargado directamente en el FXML.
        // No cargamos nada programáticamente aquí para evitar NullPointerExceptions.
    }

    /**
     * Avanza al menú principal.
     */
    @FXML
    private void onStart() {
        try {
            Parent menuRoot = FXMLLoader.load(
                    getClass().getResource("/com/miempresa/app/ui/MainMenu.fxml")
            );
            Stage stage = (Stage) startButton.getScene().getWindow();
            stage.getScene().setRoot(menuRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
