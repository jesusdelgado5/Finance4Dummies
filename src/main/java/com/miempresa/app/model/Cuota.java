package com.miempresa.app.model;

import java.time.LocalDate;

/**
 * Representa una cuota de amortización.
 */
public class Cuota {
    private LocalDate fecha;
    private double capital;
    private double interes;
    private double saldo;

    public Cuota(LocalDate fecha, double capital, double interes, double saldo) {
        this.fecha = fecha;
        this.capital = capital;
        this.interes = interes;
        this.saldo = saldo;
    }

    public LocalDate getFecha() { return fecha; }
    public double getCapital() { return capital; }
    public double getInteres() { return interes; }
    public double getSaldo() { return saldo; }

    @Override
    public String toString() {
        return String.format("%s - Cap: %.2f, Int: %.2f, Saldo: %.2f",
                fecha, capital, interes, saldo);
    }
}
