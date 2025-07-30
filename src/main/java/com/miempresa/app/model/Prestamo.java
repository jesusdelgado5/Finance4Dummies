package com.miempresa.app.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un préstamo bancario usando método de amortización francesa con redondeo a centavos.
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
        BigDecimal cuota = BigDecimal.valueOf(calcularCuotaMensual());
        BigDecimal total = cuota.multiply(BigDecimal.valueOf(plazoMeses));
        BigDecimal costo = total.subtract(BigDecimal.valueOf(montoPrincipal));
        return costo.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Calcula la cuota mensual con fórmula de amortización francesa, redondeada a centavos.
     */
    public double calcularCuotaMensual() {
        BigDecimal p = BigDecimal.valueOf(montoPrincipal);
        BigDecimal i = BigDecimal.valueOf(tasaAnual)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
        BigDecimal onePlusI = BigDecimal.ONE.add(i, mc);
        BigDecimal factor = onePlusI.pow(plazoMeses, mc); // (1 + i)^n

        BigDecimal numerator = p.multiply(i, mc);
        BigDecimal denominator = BigDecimal.ONE.subtract(
                BigDecimal.ONE.divide(factor, 10, RoundingMode.HALF_UP), mc
        );

        BigDecimal cuota = numerator.divide(denominator, mc);
        return cuota.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public List<Cuota> generarCronograma() {
        List<Cuota> cronograma = new ArrayList<>();
        MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
        BigDecimal saldo = BigDecimal.valueOf(montoPrincipal);
        BigDecimal cuotaMensual = BigDecimal.valueOf(calcularCuotaMensual());
        BigDecimal mensualRate = BigDecimal.valueOf(tasaAnual)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        for (int n = 1; n <= plazoMeses; n++) {
            BigDecimal interes = saldo.multiply(mensualRate, mc)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal capital = cuotaMensual.subtract(interes, mc)
                    .setScale(2, RoundingMode.HALF_UP);
            saldo = saldo.subtract(capital, mc)
                    .setScale(2, RoundingMode.HALF_UP);
            LocalDate fecha = fechaInicio.plusMonths(n);
            cronograma.add(new Cuota(
                    fecha,
                    capital.doubleValue(),
                    interes.doubleValue(),
                    saldo.max(BigDecimal.ZERO).doubleValue()
            ));
        }
        return cronograma;
    }

    @Override
    public boolean esActivo() {
        return false;
    }

    @Override
    public int getPlazo() {
        return plazoMeses;
    }

    @Override
    public double getTasaAnual() {
        return tasaAnual;
    }
}
