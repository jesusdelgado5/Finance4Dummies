package com.miempresa.app.ui;

import com.miempresa.app.model.Inversion;
import com.miempresa.app.service.FinancieroService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class InversionController {
    @FXML private TextField nameField;
    @FXML private TextField amountField;
    @FXML private TextField rateField2;
    @FXML private TextField termField2;
    @FXML private CheckBox compoundCheckBox;
    @FXML private DatePicker startDatePicker2;
    @FXML private LineChart<Number, Number> projectionChart;

    @FXML
    private void initialize() {
        projectionChart.setAnimated(false);
        projectionChart.setCreateSymbols(true);
    }

    @FXML
    private void onCalculateReturn() {
        try {
            Inversion inv = readInversion();
            // Registra en el servicio
            FinancieroService.getInstance().addProducto(inv);

            double gain = inv.calcularCosto();
            showInfo("Ganancia: " + String.format("%.2f", gain));
        } catch (IllegalArgumentException e) {
            showError("Datos inválidos", e.getMessage());
        }
    }

    @FXML
    private void onGenerateProjection() {
        try {
            Inversion inv = readInversion();
            FinancieroService.getInstance().addProducto(inv);
            List<String> proj = inv.generarCronograma();
            projectionChart.getData().clear();
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Valor inversión");
            for (int i = 0; i < proj.size(); i++) {
                String[] parts = proj.get(i).split(" -> ");
                double val = Double.parseDouble(parts[1]);
                series.getData().add(new XYChart.Data<>(i+1, val));
            }
            projectionChart.getData().add(series);
            Platform.runLater(() -> {
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    Tooltip.install(data.getNode(), new Tooltip(
                            String.format("Mes %d: %.2f",
                                    data.getXValue().intValue(),
                                    data.getYValue().doubleValue())
                    ));
                }
            });
        } catch (IllegalArgumentException e) {
            showError("Datos inválidos", e.getMessage());
        }
    }

    @FXML
    private void onBackToMenu2() throws Exception {
        Parent menu = FXMLLoader.load(getClass().getResource(
                "/com/miempresa/app/ui/MainMenu.fxml"));
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.getScene().setRoot(menu);
    }

    private Inversion readInversion() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) throw new IllegalArgumentException("Nombre de inversión requerido.");
        double amount;
        try { amount = Double.parseDouble(amountField.getText().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Monto debe ser numérico."); }
        int term;
        try { term = Integer.parseInt(termField2.getText().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Plazo debe ser entero."); }
        double rate;
        try { rate = Double.parseDouble(rateField2.getText().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Tasa anual debe ser numérica."); }
        LocalDate start = startDatePicker2.getValue();
        if (start == null) throw new IllegalArgumentException("Seleccione fecha de inicio.");
        boolean comp = compoundCheckBox.isSelected();
        return new Inversion(name, amount, start, rate, comp, term);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
