package com.miempresa.app.ui;

import com.miempresa.app.model.*;
import com.miempresa.app.service.FinancieroService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador mejorado para comparar dos productos financieros del mismo tipo,
 * mostrando atributos textuales y una gráfica de valor en el tiempo.
 */
public class ComparadorController {
    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<ProductoFinanciero> comboA;
    @FXML private ComboBox<ProductoFinanciero> comboB;
    @FXML private TextArea detailsArea;
    @FXML private LineChart<Number, Number> lineChart;
    @FXML private Button clearButton;

    @FXML
    public void initialize() {
        // 1. Poblamos el selector de tipo
        typeCombo.setItems(FXCollections.observableArrayList(
                "Préstamo", "Tarjeta de Crédito", "Inversión"
        ));

        // 2. Cuando el usuario elige un tipo, filtramos los productos
        typeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldType, newType) -> {
            filterProductsByType(newType);
            onClear();
        });

        // Inicializamos zona de detalles y gráfico vacíos
        detailsArea.clear();
        lineChart.getData().clear();
    }

    /** Llena comboA y comboB sólo con productos del tipo dado. */
    private void filterProductsByType(String tipo) {
        List<ProductoFinanciero> all = FinancieroService.getInstance().getAllProductos();
        List<ProductoFinanciero> filtered = all.stream()
                .filter(p -> {
                    switch (tipo) {
                        case "Préstamo": return p instanceof Prestamo;
                        case "Tarjeta de Crédito": return p instanceof TarjetaCredito;
                        case "Inversión": return p instanceof Inversion;
                        default: return false;
                    }
                })
                .collect(Collectors.toList());
        comboA.setItems(FXCollections.observableList(filtered));
        comboB.setItems(FXCollections.observableList(filtered));
    }

    /** Compara los dos productos seleccionados y actualiza texto y gráfica. */
    @FXML
    private void onCompare() {
        ProductoFinanciero a = comboA.getValue();
        ProductoFinanciero b = comboB.getValue();
        if (a == null || b == null) {
            showError("Selección incompleta", "Por favor selecciona ambos productos.");
            return;
        }

        // Construir detalles textuales
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Nombre: %s vs %s%n", a.getNombre(), b.getNombre()));
        sb.append(String.format("Tipo: %s vs %s%n",
                a.esActivo() ? "Activo" : "Pasivo",
                b.esActivo() ? "Activo" : "Pasivo"));
        sb.append(String.format("Plazo (meses): %d vs %d%n",
                a.getPlazo(), b.getPlazo()));
        sb.append(String.format("Tasa anual: %.2f%% vs %.2f%%%n",
                a.getTasaAnual(), b.getTasaAnual()));
        sb.append(String.format("Monto inicial: %.2f vs %.2f%n",
                a.getMontoPrincipal(), b.getMontoPrincipal()));
        detailsArea.setText(sb.toString());

        // Generar series de evolución mensual
        List<Number> seriesA = extractTimeSeries(a);
        List<Number> seriesB = extractTimeSeries(b);

        XYChart.Series<Number, Number> lineA = new XYChart.Series<>();
        lineA.setName("A: " + a.getNombre());
        XYChart.Series<Number, Number> lineB = new XYChart.Series<>();
        lineB.setName("B: " + b.getNombre());

        int max = Math.max(seriesA.size(), seriesB.size());
        for (int i = 0; i < max; i++) {
            if (i < seriesA.size()) {
                lineA.getData().add(new XYChart.Data<>(i + 1, seriesA.get(i)));
            }
            if (i < seriesB.size()) {
                lineB.getData().add(new XYChart.Data<>(i + 1, seriesB.get(i)));
            }
        }

        lineChart.getData().setAll(lineA, lineB);
    }

    /** Limpia las selecciones, el texto y la gráfica. */
    @FXML
    private void onClear() {
        comboA.getSelectionModel().clearSelection();
        comboB.getSelectionModel().clearSelection();
        detailsArea.clear();
        lineChart.getData().clear();
    }

    /** Vuelve al menú principal manteniendo el mismo tamaño de ventana. */
    @FXML
    private void onBackToMenu() throws IOException {
        Parent menu = FXMLLoader.load(
                getClass().getResource("/com/miempresa/app/ui/MainMenu.fxml")
        );
        Stage stage = (Stage) typeCombo.getScene().getWindow();
        stage.getScene().setRoot(menu);
    }

    /**
     * Extrae una serie de valores mensuales (saldo o valor de inversión) de un producto.
     */
    private List<Number> extractTimeSeries(ProductoFinanciero p) {
        if (p instanceof TarjetaCredito) {
            // Las entradas vienen como "fecha -> valor"
            return ((TarjetaCredito) p).generarCronograma().stream()
                    .map(entry -> {
                        String[] parts = entry.split("->");
                        return Double.parseDouble(parts[1].trim());
                    }).collect(Collectors.toList());

        } else if (p instanceof Prestamo) {
            return ((Prestamo) p).generarCronograma().stream()
                    .map(Cuota::getSaldo)
                    .collect(Collectors.toList());

        } else /* Inversion */ {
            return ((Inversion) p).generarCronograma().stream()
                    .map(entry -> {
                        String[] parts = entry.split("->");
                        return Double.parseDouble(parts[1].trim());
                    }).collect(Collectors.toList());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
