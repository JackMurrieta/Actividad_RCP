/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Inventario;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Jack Murrieta
 */
public class Inventario {

    private static Inventario instancia;

    private Map<String, ProductoInventario> productos;

    // Constructor privado (Singleton)
    private Inventario() {
        productos = new ConcurrentHashMap<>();
        inicializarProductos();
    }

    // Método para obtener la única instancia
    public static synchronized Inventario getInstance() {
        if (instancia == null) {
            instancia = new Inventario();
        }
        return instancia;
    }

    // Inicialización automática
    private void inicializarProductos() {

        productos.put("1", new ProductoInventario("1", "Laptop Gamer", 25000, 5));
        productos.put("2", new ProductoInventario("2", "Monitor 27\"", 4500, 8));
        productos.put("3", new ProductoInventario("3", "Teclado Mecánico", 1200, 10));
        productos.put("4", new ProductoInventario("4", "Mouse Gamer", 800, 15));
        productos.put("5", new ProductoInventario("5", "SSD 1TB", 1800, 7));

        System.out.println("Inventario inicializado correctamente.");
    }

    // Métodos DAO
    public ProductoInventario obtenerProductoPorId(String id) {
        return productos.get(id);
    }

    public Collection<ProductoInventario> listarProductos() {
        return productos.values();
    }

    // Método seguro para descontar stock
    public synchronized boolean descontarStock(String id, int cantidad) {

        ProductoInventario producto = productos.get(id);

        if (producto == null) {
            return false;
        }

        if (producto.getStock() < cantidad) {
            return false;
        }

        producto.setStock(producto.getStock() - cantidad);

        return true;
    }
}
