package com.miempresa.app.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
        if (fechaPago.isBefore(fechaCorte)) {
            return 0;
        }
        long dias = ChronoUnit.DAYS.between(fechaCorte, fechaPago);
        double tasaDiaria = (tasaInteres / 100.0) / 365.0;
        return montoPrincipal * tasaDiaria * dias;
    }

    @Override
    public List<String> generarCronograma() {
        List<String> cronograma = new ArrayList<>();
        double interes = calcularCosto();
        double pagoMinimo = Math.max(montoPrincipal * 0.05, interes);
        double pagoTotal = montoPrincipal + interes;
        cronograma.add(String.format("Días de ciclo: %d", ChronoUnit.DAYS.between(fechaCorte, fechaPago)));
        cronograma.add(String.format("Interés devengado: %.2f", interes));
        cronograma.add(String.format("Pago mínimo (5%% o interés): %.2f", pagoMinimo));
        cronograma.add(String.format("Pago total (principal + interés): %.2f", pagoTotal));
        return cronograma;
    }

    /**
     * Simula mes a mes la deuda restante considerando interés compuesto.
     * @return List<Double> saldos pendientes al final de cada mes tras pago mínimo
     */
    public List<Double> simularBalanceMensual() {
        List<Double> balances = new ArrayList<>();
        double balance = montoPrincipal;
        double tasaMensual = (tasaInteres / 100.0) / 12.0;

        for (int mes = 1; mes <= 600 && balance > 0; mes++) {
            // Aplicar interés al saldo actual
            double interes = balance * tasaMensual;
            double saldoConInteres = balance + interes;

            // Calcular pago mínimo sobre el saldo con interés
            double pagoMinimo = Math.max(saldoConInteres * 0.05, interes);

            // Nuevo balance después de pago
            balance = saldoConInteres - pagoMinimo;

            // Guardar el balance restante
            balances.add(balance);
        }
        return balances;
    }

    @Override
    public boolean esActivo() { return false; }
    @Override
    public int getPlazo() { return (int) ChronoUnit.MONTHS.between(fechaCorte, fechaPago); }
    @Override
    public double getTasaAnual() { return tasaInteres; }
}
