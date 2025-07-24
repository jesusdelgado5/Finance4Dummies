package com.miempresa.app.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {
    @FXML private StackPane root;
    @FXML private ImageView bgView;

    @FXML
    public void initialize() {
        // El fondo siempre al 100%
        bgView.fitWidthProperty().bind(root.widthProperty());
        bgView.fitHeightProperty().bind(root.heightProperty());
    }

    @FXML private void goToTarjetas(ActionEvent e) throws IOException {
        loadModule("TarjetaForm.fxml");
    }
    @FXML private void goToPrestamos(ActionEvent e) throws IOException {
        loadModule("PrestamoForm.fxml");
    }
    @FXML private void goToInversiones(ActionEvent e) throws IOException {
        loadModule("InversionForm.fxml");
    }
    @FXML private void goToComparador(ActionEvent e) throws IOException {
        loadModule("Comparador.fxml");
    }

    private void loadModule(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(
                getClass().getResource("/com/miempresa/app/ui/" + fxml)
        );
        Stage stage = (Stage) root.getScene().getWindow();
        stage.getScene().setRoot(pane);
        stage.setMaximized(true);
        stage.setIconified(false);
    }
}
