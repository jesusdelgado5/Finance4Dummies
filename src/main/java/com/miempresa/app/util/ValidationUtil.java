package com.miempresa.app.util;

import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;

public class ValidationUtil {
    private static final double MAX_CREDIT_LIMIT    = 100_000_000;   // Límite para tarjetas de crédito
    private static final double MAX_LOAN_PRINCIPAL  = 100_000_000;  // Límite para préstamos y montos de inversión
    private static final double MAX_ANNUAL_RATE     = 100.0;        // 100%

    /** Valida que el campo no esté vacío. */
    public static boolean requireNonEmpty(TextField field, Label errorLabel, String msg) {
        String text = field.getText();
        if (text == null || text.trim().isEmpty()) {
            setError(field, errorLabel, msg);
            return false;
        }
        clearError(field, errorLabel);
        return true;
    }

    /**
     * Valida formato numérico, signo y rango según el nombre del campo.
     * fieldName: "Límite de crédito", "Monto principal", "Monto de inversión", "Tasa anual"
     */
    public static boolean requireValidAmount(TextField field, Label errorLabel, String fieldName) {
        String text = field.getText();
        if (text == null || text.trim().isEmpty()) {
            setError(field, errorLabel, fieldName + " es requerido.");
            return false;
        }

        // Normalizar coma a punto
        String normalized = text.trim().replace(",", ".");
        double value;
        try {
            value = Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            setError(field, errorLabel,
                    fieldName + " debe ser un número válido (solo dígitos, . o ,).");
            return false;
        }

        if (value < 0) {
            setError(field, errorLabel,
                    fieldName + " no puede ser negativo.");
            return false;
        }

        String lower = fieldName.toLowerCase();
        double max;
        if (lower.contains("límite")) {
            max = MAX_CREDIT_LIMIT;
        } else if (lower.contains("principal") || lower.contains("monto")) {
            max = MAX_LOAN_PRINCIPAL;
        } else {
            max = MAX_ANNUAL_RATE;
        }
        if (value > max) {
            setError(field, errorLabel,
                    String.format("%s demasiado alto (máx %.0f).", fieldName, max));
            return false;
        }

        clearError(field, errorLabel);
        return true;
    }

    /**
     * Valida que ambas fechas estén presentes y que fecha2 > fecha1.
     */
    public static boolean requireValidDates(
            DatePicker startDatePicker, Label startError,
            DatePicker endDatePicker, Label endError) {

        boolean ok = true;
        LocalDate start = startDatePicker.getValue();
        LocalDate end   = endDatePicker.getValue();

        if (start == null) {
            setError(startDatePicker, startError, "Seleccione fecha de inicio.");
            ok = false;
        } else {
            clearError(startDatePicker, startError);
        }

        if (end == null) {
            setError(endDatePicker, endError, "Seleccione fecha final.");
            ok = false;
        } else {
            clearError(endDatePicker, endError);
        }

        if (ok && !end.isAfter(start)) {
            setError(startDatePicker, startError,
                    "Fecha de inicio debe ser anterior a la fecha final.");
            setError(endDatePicker, endError,
                    "Fecha final debe ser posterior a la fecha de inicio.");
            ok = false;
        }
        return ok;
    }

    // --- Manejo de estilos y mensajes ---
    private static void setError(TextField field, Label errorLabel, String msg) {
        if (!field.getStyleClass().contains("error")) {
            field.getStyleClass().add("error");
        }
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
    private static void clearError(TextField field, Label errorLabel) {
        field.getStyleClass().removeAll("error");
        errorLabel.setVisible(false);
    }
    private static void setError(DatePicker picker, Label errorLabel, String msg) {
        if (!picker.getStyleClass().contains("error")) {
            picker.getStyleClass().add("error");
        }
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
    private static void clearError(DatePicker picker, Label errorLabel) {
        picker.getStyleClass().removeAll("error");
        errorLabel.setVisible(false);
    }
}
