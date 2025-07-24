package com.miempresa.app.service;

import com.miempresa.app.model.ProductoFinanciero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Servicio singleton que guarda todos los productos financieros
 * creados durante la ejecución de la aplicación.
 * Evita duplicados basados en el nombre del producto.
 */
public class FinancieroService {
    private static final FinancieroService INSTANCE = new FinancieroService();
    private final List<ProductoFinanciero> productos = new ArrayList<>();

    private FinancieroService() {}

    public static FinancieroService getInstance() {
        return INSTANCE;
    }

    /**
     * Registra un nuevo producto si no existe otro con el mismo nombre.
     */
    public void addProducto(ProductoFinanciero producto) {
        boolean existe = productos.stream()
                .anyMatch(p -> p.getNombre().equals(producto.getNombre()));
        if (!existe) {
            productos.add(producto);
        }
    }

    /**
     * Devuelve una lista inmutable de todos los productos registrados.
     */
    public List<ProductoFinanciero> getAllProductos() {
        return Collections.unmodifiableList(productos);
    }

    /**
     * Limpia todos los productos (útil para pruebas o reinicio de la app).
     */
    public void clear() {
        productos.clear();
    }
}
