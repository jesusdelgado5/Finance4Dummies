package com.miempresa.app.ui;

import com.miempresa.app.model.Cuota;
import com.miempresa.app.model.Prestamo;
import com.miempresa.app.service.FinancieroService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class PrestamoController {
    @FXML private TextField nameField;
    @FXML private TextField principalField;
    @FXML private TextField termField;
    @FXML private TextField rateField;
    @FXML private DatePicker startDatePicker;
    @FXML private LineChart<Number, Number> scheduleChart;

    @FXML
    private void initialize() {
        // Desactivar animaciones para mantener tooltips
        scheduleChart.setAnimated(false);
        // Mostrar símbolos
        scheduleChart.setCreateSymbols(true);
    }

    @FXML
    private void onCalculatePayment() {
        try {
            Prestamo prestamo = readPrestamo();
            // Registra en el servicio
            FinancieroService.getInstance().addProducto(prestamo);

            double cuota = prestamo.calcularCuotaMensual();
            showInfo("Cuota mensual: " + String.format("%.2f", cuota));
        } catch (IllegalArgumentException e) {
            showError("Datos inválidos", e.getMessage());
        }
    }

    @FXML
    private void onGenerateSchedule() {
        try {
            Prestamo prestamo = readPrestamo();
            FinancieroService.getInstance().addProducto(prestamo);
            List<Cuota> cronograma = prestamo.generarCronograma();

            // Limpiar series previas
            scheduleChart.getData().clear();

            // Serie de saldo restante
            XYChart.Series<Number, Number> seriesSaldo = new XYChart.Series<>();
            seriesSaldo.setName("Saldo");

            for (int i = 0; i < cronograma.size(); i++) {
                Cuota c = cronograma.get(i);
                int mes = i + 1;
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(mes, c.getSaldo());
                seriesSaldo.getData().add(dataPoint);
            }

            scheduleChart.getData().add(seriesSaldo);

            // Agregar tooltips y hover effects a cada punto
            Platform.runLater(() -> {
                for (XYChart.Data<Number, Number> data : seriesSaldo.getData()) {
                    data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            String tooltipText = String.format("Mes: %d Saldo: %.2f",
                            data.getXValue().intValue(), data.getYValue().doubleValue());
                            Tooltip tooltip = new Tooltip(tooltipText);
                            Tooltip.install(newNode, tooltip);
                            newNode.setOnMouseEntered(e -> newNode.setStyle("-fx-scale-x:1.2; -fx-scale-y:1.2;"));
                            newNode.setOnMouseExited(e -> newNode.setStyle(""));
                        }
                    });
                }
            });
        } catch (IllegalArgumentException e) {
            showError("Datos inválidos", e.getMessage());
        }
    }

    @FXML
    private void onBackToMenu() throws Exception {
        Parent menu = FXMLLoader.load(getClass().getResource(
                "/com/miempresa/app/ui/MainMenu.fxml"));
        Stage stage = (Stage) principalField.getScene().getWindow();
        stage.getScene().setRoot(menu);
    }

    private Prestamo readPrestamo() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) throw new IllegalArgumentException("Nombre del préstamo requerido.");
        double principal;
        try { principal = Double.parseDouble(principalField.getText().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Monto principal debe ser numérico."); }
        int term;
        try { term = Integer.parseInt(termField.getText().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Plazo debe ser entero."); }
        double rate;
        try { rate = Double.parseDouble(rateField.getText().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Tasa anual debe ser numérico."); }
        LocalDate start = startDatePicker.getValue();
        if (start == null) throw new IllegalArgumentException("Seleccione la fecha de inicio.");
        return new Prestamo(name, principal, start, term, rate);
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
