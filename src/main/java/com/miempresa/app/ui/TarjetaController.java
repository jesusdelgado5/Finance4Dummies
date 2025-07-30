package com.miempresa.app.ui;

import com.miempresa.app.model.TarjetaCredito;
import com.miempresa.app.service.FinancieroService;
import com.miempresa.app.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class TarjetaController {
    @FXML private TextField nameField;
    @FXML private Label nameErrorLabel;
    @FXML private TextField limitField;
    @FXML private Label limitErrorLabel;
    @FXML private TextField rateField;
    @FXML private Label rateErrorLabel;
    @FXML private DatePicker cutDatePicker;
    @FXML private Label cutDateErrorLabel;
    @FXML private DatePicker paymentDatePicker;
    @FXML private Label paymentDateErrorLabel;
    @FXML private TextArea outputArea;
    @FXML private Button calculateButton;
    @FXML private Button scheduleButton;
    @FXML private Button compareButton; // Proyección Deuda
    @FXML private Button clearButton;
    @FXML private Button backButton;

    @FXML
    private void initialize() {
        nameErrorLabel.setVisible(false);
        limitErrorLabel.setVisible(false);
        rateErrorLabel.setVisible(false);
        cutDateErrorLabel.setVisible(false);
        paymentDateErrorLabel.setVisible(false);
    }

    @FXML
    private void onCalculateInterest() {
        outputArea.clear();
        TarjetaCredito tarjeta = validateAndReadTarjeta();
        if (tarjeta == null) return;
        FinancieroService.getInstance().addProducto(tarjeta);
        double costo = tarjeta.calcularCosto();
        if (costo <= 0) {
            showError("Fechas inválidas", "La fecha de pago debe ser posterior a la fecha de corte.");
        } else {
            outputArea.setText(String.format("Interés calculado: %.2f", costo));
        }
    }

    @FXML
    private void onGenerateSchedule() {
        outputArea.clear();
        TarjetaCredito tarjeta = validateAndReadTarjeta();
        if (tarjeta == null) return;
        FinancieroService.getInstance().addProducto(tarjeta);
        List<String> schedule = tarjeta.generarCronograma();
        outputArea.setText(String.join("\n", schedule));
    }

    /**
     * Grafica la proyección de la deuda restante si solo se paga el mínimo cada mes.
     */
    @FXML
    private void onCompareMinimumPayment() {
        outputArea.clear();
        TarjetaCredito tarjeta = validateAndReadTarjeta();
        if (tarjeta == null) return;

        List<Double> balances = tarjeta.simularBalanceMensual();
        int meses = Math.min(balances.size(), 60);
        List<Double> subset = balances.subList(0, meses);

        NumberAxis xAxis = new NumberAxis(1, meses, 1);
        xAxis.setLabel("Mes");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Deuda Restante");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Proyección Deuda Restante (hasta 60 meses)");

        XYChart.Series<Number, Number> serieDebt = new XYChart.Series<>();
        serieDebt.setName("Deuda Restante");
        for (int i = 0; i < subset.size(); i++) {
            serieDebt.getData().add(new XYChart.Data<>(i + 1, subset.get(i)));
        }
        chart.getData().add(serieDebt);

        Stage stage = new Stage();
        stage.setTitle("Gráfico Deuda Restante (60 meses)");
        stage.setScene(new Scene(chart, 800, 600));
        stage.show();
    }

    @FXML
    private void onClear() {
        nameField.clear(); limitField.clear(); rateField.clear();
        cutDatePicker.setValue(null); paymentDatePicker.setValue(null);
        nameErrorLabel.setVisible(false); limitErrorLabel.setVisible(false);
        rateErrorLabel.setVisible(false); cutDateErrorLabel.setVisible(false);
        paymentDateErrorLabel.setVisible(false); outputArea.clear();
    }

    @FXML
    private void onBackToMenu() throws IOException {
        Parent menuRoot = FXMLLoader.load(
                getClass().getResource("/com/miempresa/app/ui/MainMenu.fxml")
        );
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.getScene().setRoot(menuRoot);
    }

    private TarjetaCredito validateAndReadTarjeta() {
        boolean valid = true;
        valid &= ValidationUtil.requireNonEmpty(nameField, nameErrorLabel, "Nombre de tarjeta requerido.");
        valid &= ValidationUtil.requireValidAmount(limitField, limitErrorLabel, "Límite de crédito");
        valid &= ValidationUtil.requireValidAmount(rateField, rateErrorLabel, "Tasa anual");
        valid &= ValidationUtil.requireValidDates(cutDatePicker, cutDateErrorLabel,
                paymentDatePicker, paymentDateErrorLabel);
        if (!valid) return null;
        String name = nameField.getText().trim();
        double limit = Double.parseDouble(limitField.getText().trim().replace(",", "."));
        double rate = Double.parseDouble(rateField.getText().trim().replace(",", "."));
        LocalDate cut = cutDatePicker.getValue();
        LocalDate payment = paymentDatePicker.getValue();
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
