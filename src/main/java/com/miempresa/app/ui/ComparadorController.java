package com.miempresa.app.ui;

import com.miempresa.app.model.ProductoFinanciero;
import com.miempresa.app.service.FinancieroService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.List;

public class ComparadorController {
    @FXML private ComboBox<ProductoFinanciero> comboA;
    @FXML private ComboBox<ProductoFinanciero> comboB;
    @FXML private BarChart<String, Number> comparisonChart;
    @FXML private CategoryAxis xAxis;  // inyectamos el eje para configurarlo si es necesario

    @FXML
    public void initialize() {
        List<ProductoFinanciero> todos =
                FinancieroService.getInstance().getAllProductos();
        comboA.setItems(FXCollections.observableList(todos));
        comboB.setItems(FXCollections.observableList(todos));

        // Aseguramos la rotación de etiquetas (también está en FXML)
        xAxis.setTickLabelRotation(90);
    }

    @FXML
    private void onCompare() {
        ProductoFinanciero a = comboA.getValue();
        ProductoFinanciero b = comboB.getValue();
        if (a == null || b == null) return;

        comparisonChart.getData().clear();
        XYChart.Series<String, Number> serieA = new XYChart.Series<>();
        serieA.setName("A: " + a.getNombre());
        XYChart.Series<String, Number> serieB = new XYChart.Series<>();
        serieB.setName("B: " + b.getNombre());

        // Agregamos solo dos categorías bien separadas
        serieA.getData().add(new XYChart.Data<>("Costo", a.calcularCosto()));
        serieA.getData().add(new XYChart.Data<>("Principal", a.getMontoPrincipal()));
        serieB.getData().add(new XYChart.Data<>("Costo", b.calcularCosto()));
        serieB.getData().add(new XYChart.Data<>("Principal", b.getMontoPrincipal()));

        comparisonChart.getData().addAll(serieA, serieB);
    }

    @FXML
    private void onBackToMenu() throws Exception {
        Parent menu = FXMLLoader.load(getClass().getResource(
                "/com/miempresa/app/ui/MainMenu.fxml"));
        Stage stage = (Stage) comboA.getScene().getWindow();
        stage.getScene().setRoot(menu);
    }
}
