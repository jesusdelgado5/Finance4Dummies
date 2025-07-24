package com.miempresa.app.ui;

import com.miempresa.app.model.TarjetaCredito;
import com.miempresa.app.service.FinancieroService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class TarjetaController {
    @FXML private TextField nameField;
    @FXML private TextField limitField;
    @FXML private TextField rateField;
    @FXML private DatePicker cutDatePicker;
    @FXML private DatePicker paymentDatePicker;
    @FXML private TextArea outputArea;

    @FXML
    private void onCalculateInterest() {
        try {
            TarjetaCredito tarjeta = readTarjeta();
            // Registra en el servicio
            FinancieroService.getInstance().addProducto(tarjeta);

            double costo = tarjeta.calcularCosto();
            if (costo <= 0) {
                showError("Fechas inválidas", "La fecha de pago debe ser posterior a la fecha de corte.");
            } else {
                outputArea.setText(String.format("Interés calculado: %.2f", costo));
            }
        } catch (Exception e) {
            showError("Formato inválido", e.getMessage());
        }
    }

    @FXML
    private void onGenerateSchedule() {
        try {
            TarjetaCredito tarjeta = readTarjeta();
            FinancieroService.getInstance().addProducto(tarjeta);
            List<String> schedule = tarjeta.generarCronograma();
            outputArea.setText(String.join("\n", schedule));
        } catch (IllegalArgumentException e) {
            showError("Datos inválidos", e.getMessage());
        }
    }

    @FXML
    private void onBackToMenu() throws IOException {
        Parent menuRoot = FXMLLoader.load(
                getClass().getResource("/com/miempresa/app/ui/MainMenu.fxml")
        );
        Stage stage = (Stage) nameField.getScene().getWindow();
        // Reutilizamos la escena existente, cambiamos sólo el root:
        stage.getScene().setRoot(menuRoot);
    }

    private TarjetaCredito readTarjeta() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) throw new IllegalArgumentException("Nombre de tarjeta requerido.");

        double limit;
        try { limit = Double.parseDouble(limitField.getText().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Línea de crédito debe ser un número."); }
        if (limit <= 0) throw new IllegalArgumentException("Línea de crédito debe ser mayor que cero.");

        double rate;
        try { rate = Double.parseDouble(rateField.getText().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Tasa anual debe ser un número."); }
        if (rate < 0) throw new IllegalArgumentException("Tasa anual no puede ser negativa.");

        LocalDate cut = cutDatePicker.getValue();
        LocalDate payment = paymentDatePicker.getValue();
        if (cut == null || payment == null) throw new IllegalArgumentException("Seleccione fechas de corte y pago.");

        return new TarjetaCredito(name, limit, LocalDate.now(), rate, cut, payment);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
