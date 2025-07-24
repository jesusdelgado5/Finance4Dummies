package com.miempresa.app.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una tarjeta de crédito con cálculo de interés basado en días de ciclo.
 */
public class TarjetaCredito extends ProductoFinanciero {
    private double tasaInteres; // anual en porcentaje
    private LocalDate fechaCorte;
    private LocalDate fechaPago;

    public TarjetaCredito(String nombre, double montoPrincipal, LocalDate fechaInicio,
                          double tasaInteres, LocalDate fechaCorte, LocalDate fechaPago) {
        super(nombre, montoPrincipal, fechaInicio);
        this.tasaInteres = tasaInteres;
        this.fechaCorte = fechaCorte;
        this.fechaPago = fechaPago;
    }

    @Override
    public double calcularCosto() {
        // Validar fechas
        if (fechaPago.isBefore(fechaCorte)) {
            return 0;
        }
        // Calcular días de facturación
        long dias = ChronoUnit.DAYS.between(fechaCorte, fechaPago);
        // Tasa diaria: tasa anual / días del año
        double tasaDiaria = (tasaInteres / 100.0) / 365.0;
        // Interés sobre el principal
        return montoPrincipal * tasaDiaria * dias;
    }

    @Override
    public List<String> generarCronograma() {
        List<String> cronograma = new ArrayList<>();
        double interes = calcularCosto();
        // Pago mínimo: 5% del principal o interés si mayor
        double pagoMinimo = Math.max(montoPrincipal * 0.05, interes);
        double pagoTotal = montoPrincipal + interes;
        cronograma.add(String.format("Días de ciclo: %d", ChronoUnit.DAYS.between(fechaCorte, fechaPago)));
        cronograma.add(String.format("Interés devengado: %.2f", interes));
        cronograma.add(String.format("Pago mínimo (5%% o interés): %.2f", pagoMinimo));
        cronograma.add(String.format("Pago total (principal + interés): %.2f", pagoTotal));
        return cronograma;
    }
}
