package com.miempresa.app.ui;

import com.miempresa.app.model.Inversion;
import com.miempresa.app.service.FinancieroService;
import com.miempresa.app.util.ValidationUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class InversionController {

    @FXML private TextField nameField;
    @FXML private Label nameErrorLabel;

    @FXML private TextField amountField;
    @FXML private Label amountErrorLabel;

    @FXML private TextField rateField2;
    @FXML private Label rateErrorLabel;

    @FXML private TextField termField2;
    @FXML private Label termErrorLabel;

    @FXML private CheckBox compoundCheckBox;

    @FXML private DatePicker startDatePicker2;
    @FXML private Label startDateErrorLabel;

    @FXML private Button calculateButton;
    @FXML private Button projectButton;
    @FXML private Button clearButton;
    @FXML private Button backButton;

    @FXML private TextArea resultsArea;

    @FXML
    private void initialize() {
        // Ocultar mensajes de error al iniciar
        nameErrorLabel.setVisible(false);
        amountErrorLabel.setVisible(false);
        rateErrorLabel.setVisible(false);
        termErrorLabel.setVisible(false);
        startDateErrorLabel.setVisible(false);

        // Configurar área de resultados
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
    }

    @FXML
    private void onCalculateReturn() {
        resultsArea.clear();
        Inversion inv = validateAndReadInversion();
        if (inv == null) return;

        FinancieroService.getInstance().addProducto(inv);
        double totalGain = inv.calcularCosto();
        List<String> breakdown = inv.generarCronograma();

        StringBuilder sb = new StringBuilder();
        sb.append("Ganancia por mes:\n");
        for (int i = 0; i < breakdown.size(); i++) {
            sb.append(String.format("Mes %d: %s\n", i+1, breakdown.get(i).split(" -> ")[1]));
        }
        sb.append(String.format("\nGanancia total: %.2f", totalGain));
        resultsArea.setText(sb.toString());
    }

    @FXML
    private void onGenerateProjection() {
        Inversion inv = validateAndReadInversion();
        if (inv == null) return;

        FinancieroService.getInstance().addProducto(inv);
        List<String> proj = inv.generarCronograma();

        // Crear popup con gráfico
        NumberAxis xAxis = new NumberAxis(1, proj.size(), 1);
        xAxis.setLabel("Mes");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Valor Inversión");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Proyección de Valor de Inversión");
        chart.setAnimated(false);
        chart.setCreateSymbols(true);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Valor");
        for (int i = 0; i < proj.size(); i++) {
            double val = Double.parseDouble(proj.get(i).split(" -> ")[1]);
            series.getData().add(new XYChart.Data<>(i+1, val));
        }
        chart.getData().add(series);

        // Tooltips y hover
        Platform.runLater(() -> {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                Tooltip.install(data.getNode(),
                        new Tooltip(String.format("Mes %d: %.2f", data.getXValue().intValue(), data.getYValue().doubleValue())));
                data.getNode().setOnMouseEntered(e -> data.getNode().setStyle("-fx-scale-x:1.2; -fx-scale-y:1.2;"));
                data.getNode().setOnMouseExited(e -> data.getNode().setStyle(""));
            }
        });

        // Mostrar en nueva ventana
        Stage popup = new Stage();
        popup.setTitle("Gráfico de Proyección");
        popup.setScene(new Scene(chart, 800, 600));
        popup.show();
    }

    @FXML
    private void onClear() {
        nameField.clear();
        amountField.clear();
        rateField2.clear();
        termField2.clear();
        startDatePicker2.setValue(null);
        compoundCheckBox.setSelected(false);
        nameErrorLabel.setVisible(false);
        amountErrorLabel.setVisible(false);
        rateErrorLabel.setVisible(false);
        termErrorLabel.setVisible(false);
        startDateErrorLabel.setVisible(false);
        resultsArea.clear();
    }

    @FXML
    private void onBackToMenu2() throws IOException {
        Parent menu = FXMLLoader.load(getClass().getResource("/com/miempresa/app/ui/MainMenu.fxml"));
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.getScene().setRoot(menu);
    }

    private Inversion validateAndReadInversion() {
        boolean valid = true;
        valid &= ValidationUtil.requireNonEmpty(nameField, nameErrorLabel, "Nombre de inversión requerido.");
        valid &= ValidationUtil.requireValidAmount(amountField, amountErrorLabel, "Monto de inversión");
        valid &= ValidationUtil.requireValidAmount(rateField2, rateErrorLabel, "Tasa anual");
        if (!ValidationUtil.requireNonEmpty(termField2, termErrorLabel, "Plazo requerido.")) valid = false; else {
            try {
                int term = Integer.parseInt(termField2.getText().trim());
                if (term <= 0) throw new NumberFormatException();
                termErrorLabel.setVisible(false);
            } catch (NumberFormatException e) {
                termErrorLabel.setText("Plazo debe ser entero > 0."); termErrorLabel.setVisible(true); valid = false;
            }
        }
        if (startDatePicker2.getValue() == null) {
            startDateErrorLabel.setText("Seleccione fecha de inicio."); startDateErrorLabel.setVisible(true); valid = false;
        } else startDateErrorLabel.setVisible(false);
        return valid ? new Inversion(
                nameField.getText().trim(),
                Double.parseDouble(amountField.getText().trim().replace(",",".")),
                startDatePicker2.getValue(),
                Double.parseDouble(rateField2.getText().trim().replace(",",".")),
                compoundCheckBox.isSelected(),
                Integer.parseInt(termField2.getText().trim())
        ) : null;
    }
}
