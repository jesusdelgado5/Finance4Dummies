package com.miempresa.app.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una inversión con interés simple o compuesto.
 */
public class Inversion extends ProductoFinanciero {
    private double tasaRendimiento; // anual en porcentaje
    private boolean compuesta;
    private int plazoMeses;

    public Inversion(String nombre, double montoPrincipal, LocalDate fechaInicio,
                     double tasaRendimiento, boolean compuesta, int plazoMeses) {
        super(nombre, montoPrincipal, fechaInicio);
        this.tasaRendimiento = tasaRendimiento;
        this.compuesta = compuesta;
        this.plazoMeses = plazoMeses;
    }

    @Override
    public double calcularCosto() {
        double i = tasaRendimiento / 100.0;
        if (compuesta) {
            // Compuesto mensual
            double factor = Math.pow(1 + i/12, plazoMeses);
            return montoPrincipal * factor - montoPrincipal;
        } else {
            // Simple
            return montoPrincipal * i * (plazoMeses/12.0);
        }
    }

    @Override
    public List<String> generarCronograma() {
        List<String> cronograma = new ArrayList<>();
        double saldo = montoPrincipal;
        for (int m = 1; m <= plazoMeses; m++) {
            if (compuesta) {
                saldo *= 1 + (tasaRendimiento/100.0)/12;
            } else {
                saldo += montoPrincipal * (tasaRendimiento/100.0)/12;
            }
            LocalDate fecha = fechaInicio.plusMonths(m);
            cronograma.add(String.format("%s -> %.2f", fecha, saldo));
        }
        return cronograma;
    }
}
