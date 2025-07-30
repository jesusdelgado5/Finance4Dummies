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

    /**
     * Calcula el costo o interés dependiendo del tipo de producto.
     */
    public abstract double calcularCosto();

    /**
     * Genera un cronograma (cuotas, proyección, etc.) representado como una lista.
     */
    public abstract List<?> generarCronograma();

    /**
     * Indica si el producto es un activo (true) o pasivo (false).
     */
    public abstract boolean esActivo();

    /**
     * Devuelve el plazo del producto en meses.
     */
    public abstract int getPlazo();

    /**
     * Devuelve la tasa anual aplicada al producto (en porcentaje).
     */
    public abstract double getTasaAnual();

    @Override
    public String toString() {
        return nombre;
    }
}
