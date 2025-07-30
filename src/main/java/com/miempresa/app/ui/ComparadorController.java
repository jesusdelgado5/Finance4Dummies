package com.miempresa.app.ui;

import com.miempresa.app.model.Inversion;
import com.miempresa.app.service.FinancieroService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import java.util.stream.Collectors;

public class ComparadorController {
    @FXML private ComboBox<Inversion> comboA;
    @FXML private ComboBox<Inversion> comboB;
    @FXML private TextArea detailsArea;
    @FXML private Button compareButton;
    @FXML private Button clearButton;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        // Solo inversiones
        List<Inversion> inversiones = FinancieroService.getInstance()
                .getAllProductos().stream()
                .filter(p -> p instanceof Inversion)
                .map(p -> (Inversion)p)
                .collect(Collectors.toList());
        comboA.setItems(FXCollections.observableList(inversiones));
        comboB.setItems(FXCollections.observableList(inversiones));
        detailsArea.clear();
    }

    @FXML
    private void onCompare() {
        Inversion a = comboA.getValue();
        Inversion b = comboB.getValue();
        if (a == null || b == null) {
            showError("Selección incompleta", "Por favor selecciona ambas inversiones.");
            return;
        }
        // Texto comparativo
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Nombre: %s vs %s%n", a.getNombre(), b.getNombre()));
        sb.append(String.format("Monto inicial: %.2f vs %.2f%n", a.getMontoPrincipal(), b.getMontoPrincipal()));
        sb.append(String.format("Tasa anual: %.2f%% vs %.2f%%%n", a.getTasaAnual(), b.getTasaAnual()));
        sb.append(String.format("Plazo (meses): %d vs %d%n", a.getPlazo(), b.getPlazo()));

        sb.append(String.format("Ganancia total: %.2f vs %.2f%n", a.calcularCosto(), b.calcularCosto()));
        detailsArea.setText(sb.toString());

        // Series de valores
        List<Number> seriesA = a.generarCronograma().stream()
                .map(entry -> Double.parseDouble(entry.split("->")[1].trim()))
                .collect(Collectors.toList());
        List<Number> seriesB = b.generarCronograma().stream()
                .map(entry -> Double.parseDouble(entry.split("->")[1].trim()))
                .collect(Collectors.toList());

        XYChart.Series<Number, Number> lineA = new XYChart.Series<>();
        lineA.setName("A: " + a.getNombre());
        XYChart.Series<Number, Number> lineB = new XYChart.Series<>();
        lineB.setName("B: " + b.getNombre());
        int max = Math.max(seriesA.size(), seriesB.size());
        for (int i = 0; i < max; i++) {
            if (i < seriesA.size()) lineA.getData().add(new XYChart.Data<>(i+1, seriesA.get(i)));
            if (i < seriesB.size()) lineB.getData().add(new XYChart.Data<>(i+1, seriesB.get(i)));
        }

        // Crear popup de gráfico
        NumberAxis xAxis = new NumberAxis(1, max, 1);
        xAxis.setLabel("Mes");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Valor Inversión");
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Comparación de Inversiones");
        chart.setAnimated(false);
        chart.setCreateSymbols(true);
        chart.getData().setAll(lineA, lineB);

        Platform.runLater(() -> {
            for (XYChart.Series<Number, Number> series : List.of(lineA, lineB)) {
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    Tooltip.install(data.getNode(),
                            new Tooltip(String.format("Mes %d: %.2f",
                                    data.getXValue().intValue(), data.getYValue().doubleValue())));
                    data.getNode().setOnMouseEntered(e -> data.getNode()
                            .setStyle("-fx-scale-x:1.2; -fx-scale-y:1.2;"));
                    data.getNode().setOnMouseExited(e -> data.getNode().setStyle(""));
                }
            }
        });

        Stage popup = new Stage();
        popup.setTitle("Gráfico Comparativo de Inversiones");
        popup.setScene(new Scene(chart, 800, 600));
        popup.show();
    }

    @FXML
    private void onClear() {
        comboA.getSelectionModel().clearSelection();
        comboB.getSelectionModel().clearSelection();
        detailsArea.clear();
    }

    @FXML
    private void onBackToMenu() throws IOException {
        Parent menu = FXMLLoader.load(getClass().getResource(
                "/com/miempresa/app/ui/MainMenu.fxml"));
        Stage stage = (Stage) comboA.getScene().getWindow();
        stage.getScene().setRoot(menu);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
