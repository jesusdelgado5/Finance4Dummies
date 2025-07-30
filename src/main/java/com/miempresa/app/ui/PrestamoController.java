package com.miempresa.app.ui;

import com.miempresa.app.model.Cuota;
import com.miempresa.app.model.Prestamo;
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
import java.time.LocalDate;
import java.util.List;

public class PrestamoController {

    @FXML private TextField nameField;
    @FXML private Label nameErrorLabel;

    @FXML private TextField principalField;
    @FXML private Label principalErrorLabel;

    @FXML private TextField termField;
    @FXML private Label termErrorLabel;

    @FXML private TextField rateField;
    @FXML private Label rateErrorLabel;

    @FXML private DatePicker startDatePicker;
    @FXML private Label startDateErrorLabel;

    @FXML private Button calculateButton;
    @FXML private Button scheduleButton;
    @FXML private Button clearButton;
    @FXML private Button backButton;

    @FXML private TextArea resultsArea;

    @FXML
    private void initialize() {
        // Ocultar mensajes de error
        nameErrorLabel.setVisible(false);
        principalErrorLabel.setVisible(false);
        termErrorLabel.setVisible(false);
        rateErrorLabel.setVisible(false);
        startDateErrorLabel.setVisible(false);

        // Configurar área de resultados
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
    }

    /**
     * Muestra interés, saldo restante y pago mensual mes a mes, y total pagado al final.
     */
    @FXML
    private void onCalculatePayment() {
        resultsArea.clear();
        Prestamo prestamo = validateAndReadPrestamo();
        if (prestamo == null) return;

        // Pago mensual calculado por el préstamo
        double cuotaMensual = prestamo.calcularCuotaMensual();
        List<Cuota> cronograma = prestamo.generarCronograma();

        // Calcular total pagado (suma de cada cuota)
        double totalPagado = cronograma.stream()
                .mapToDouble(c -> c.getCapital() + c.getInteres())
                .sum();

        // Construir texto de resultados
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Pago mensual fijo: %.2f\n", cuotaMensual));
        sb.append(String.format("Total pagado: %.2f", totalPagado));

        resultsArea.setText(sb.toString());
    }

    /**
     * Muestra un popup con la proyección del saldo en gráfico.
     */
    @FXML
    private void onGenerateSchedule() {
        Prestamo prestamo = validateAndReadPrestamo();
        if (prestamo == null) return;

        List<Cuota> cronograma = prestamo.generarCronograma();
        int meses = cronograma.size();

        // Ejes
        NumberAxis xAxis = new NumberAxis(1, meses, 1);
        xAxis.setLabel("Mes");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Saldo Restante");

        // Gráfico
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Cronograma de Saldo");
        chart.setAnimated(false);
        chart.setCreateSymbols(true);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Saldo");
        for (int i = 0; i < meses; i++) {
            Cuota c = cronograma.get(i);
            series.getData().add(new XYChart.Data<>(i + 1, c.getSaldo()));
        }
        chart.getData().add(series);

        // Tooltips y hover
        Platform.runLater(() -> {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                Tooltip.install(data.getNode(), new Tooltip(
                        String.format("Mes %d\nSaldo: %.2f",
                                data.getXValue().intValue(), data.getYValue().doubleValue())
                ));
                data.getNode().setOnMouseEntered(e -> data.getNode()
                        .setStyle("-fx-scale-x:1.2; -fx-scale-y:1.2;"));
                data.getNode().setOnMouseExited(e -> data.getNode().setStyle(""));
            }
        });

        // Mostrar popup
        Stage popup = new Stage();
        popup.setTitle("Proyección de Préstamo");
        popup.setScene(new Scene(chart, 800, 600));
        popup.show();
    }

    @FXML
    private void onClear() {
        nameField.clear();
        principalField.clear();
        termField.clear();
        rateField.clear();
        startDatePicker.setValue(null);
        resultsArea.clear();
        nameErrorLabel.setVisible(false);
        principalErrorLabel.setVisible(false);
        termErrorLabel.setVisible(false);
        rateErrorLabel.setVisible(false);
        startDateErrorLabel.setVisible(false);
    }

    @FXML
    private void onBackToMenu() throws IOException {
        Parent menu = FXMLLoader.load(getClass().getResource(
                "/com/miempresa/app/ui/MainMenu.fxml"));
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.getScene().setRoot(menu);
    }

    /**
     * Valida campos y retorna Prestamo o null.
     */
    private Prestamo validateAndReadPrestamo() {
        boolean valid = true;
        valid &= ValidationUtil.requireNonEmpty(nameField, nameErrorLabel,
                "Nombre del préstamo requerido.");
        valid &= ValidationUtil.requireValidAmount(principalField,
                principalErrorLabel, "Monto principal");

        if (!ValidationUtil.requireNonEmpty(termField, termErrorLabel,
                "Plazo requerido.")) {
            valid = false;
        } else {
            try {
                int t = Integer.parseInt(termField.getText().trim());
                if (t <= 0) throw new NumberFormatException();
                termErrorLabel.setVisible(false);
            } catch (NumberFormatException ex) {
                termErrorLabel.setText("Plazo debe ser entero >0.");
                termErrorLabel.setVisible(true);
                valid = false;
            }
        }

        valid &= ValidationUtil.requireValidAmount(rateField,
                rateErrorLabel, "Tasa anual");

        if (startDatePicker.getValue() == null) {
            startDateErrorLabel.setText("Seleccione fecha inicio.");
            startDateErrorLabel.setVisible(true);
            valid = false;
        } else {
            startDateErrorLabel.setVisible(false);
        }

        if (!valid) return null;

        String name = nameField.getText().trim();
        double principal = Double.parseDouble(
                principalField.getText().trim().replace(",", ".")
        );
        int term = Integer.parseInt(termField.getText().trim());
        double rate = Double.parseDouble(
                rateField.getText().trim().replace(",", ".")
        );
        LocalDate start = startDatePicker.getValue();

        return new Prestamo(name, principal, start, term, rate);
    }
}
