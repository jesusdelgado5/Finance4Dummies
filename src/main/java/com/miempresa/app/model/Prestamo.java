package com.miempresa.app.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un préstamo bancario usando método de amortización francesa.
 */
public class Prestamo extends ProductoFinanciero {
    private int plazoMeses;
    private double tasaAnual;

    public Prestamo(String nombre, double montoPrincipal, LocalDate fechaInicio,
                    int plazoMeses, double tasaAnual) {
        super(nombre, montoPrincipal, fechaInicio);
        this.plazoMeses = plazoMeses;
        this.tasaAnual = tasaAnual;
    }

    @Override
    public double calcularCosto() {
        // Costo total = (cuota mensual * plazo) - principal
        double cuota = calcularCuotaMensual();
        return cuota * plazoMeses - montoPrincipal;
    }

    /**
     * Calcula la cuota mensual con fórmula de amortización francesa.
     */
    public double calcularCuotaMensual() {
        double i = (tasaAnual / 100.0) / 12.0;
        return montoPrincipal * (i / (1 - Math.pow(1 + i, -plazoMeses)));
    }

    @Override
    public List<Cuota> generarCronograma() {
        List<Cuota> cronograma = new ArrayList<>();
        double saldo = montoPrincipal;
        double cuotaMensual = calcularCuotaMensual();
        for (int n = 1; n <= plazoMeses; n++) {
            double interes = saldo * (tasaAnual / 100.0) / 12.0;
            double capital = cuotaMensual - interes;
            saldo -= capital;
            LocalDate fecha = fechaInicio.plusMonths(n);
            cronograma.add(new Cuota(fecha, capital, interes, Math.max(saldo, 0)));
        }
        return cronograma;
    }
}
