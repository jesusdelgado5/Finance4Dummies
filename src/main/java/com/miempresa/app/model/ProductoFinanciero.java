package com.miempresa.app.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Clase base para todos los productos financieros.
 */
public abstract class ProductoFinanciero {
    protected String nombre;
    protected double montoPrincipal;
    protected LocalDate fechaInicio;

    public ProductoFinanciero(String nombre, double montoPrincipal, LocalDate fechaInicio) {
        this.nombre = nombre;
        this.montoPrincipal = montoPrincipal;
        this.fechaInicio = fechaInicio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getMontoPrincipal() {
        return montoPrincipal;
    }

    public void setMontoPrincipal(double montoPrincipal) {
        this.montoPrincipal = montoPrincipal;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /** Calcula el costo o interés dependiendo del tipo de producto. */
    public abstract double calcularCosto();

    /** Genera un cronograma (cuotas, proyección, etc.) representado como una lista. */
    public abstract List<?> generarCronograma();

    /**
     * Esto hace que, cuando pongas un ProductoFinanciero en un ComboBox,
     * se muestre su nombre en lugar de algo como "com.miempresa.app.model.TarjetaCredito@4fbf..."
     */
    @Override
    public String toString() {
        return nombre;
    }
}
