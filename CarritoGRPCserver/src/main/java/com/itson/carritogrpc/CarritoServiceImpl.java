/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.carritogrpc;

import Inventario.Inventario;
import Inventario.ProductoInventario;
import com.google.protobuf.Empty;
import com.tienda.grpc.CarritoRequest;
import com.tienda.grpc.CarritoResponse;
import com.tienda.grpc.CarritoServiceGrpc.CarritoServiceImplBase;
import com.tienda.grpc.ItemCarrito;
import com.tienda.grpc.ListaProductos;
import com.tienda.grpc.Producto;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.UUID;

public class CarritoServiceImpl extends CarritoServiceImplBase {

    @Override
    public void procesarCarrito(CarritoRequest request,
            StreamObserver<CarritoResponse> responseObserver) {

        System.out.println("Procesando carrito para el usuario: "
                + request.getUsuarioId());

        Inventario inventario = Inventario.getInstance();

        // 🔹 VALIDACIONES
        String error = validarCarrito(request, inventario);

        if (error != null) {
            enviarError(error, responseObserver);
            return;
        }

        //DESCONTAR INVENTARIO
        descontarInventario(request.getItemsList(), inventario);

        // CALCULAR TOTALES
        double subtotal = calcularSubtotal(request.getItemsList(), inventario);
        double impuestos = subtotal * 0.16;
        double total = subtotal + impuestos;

        //Patron Observer
        //inventario.notificarCambio(); // actualizar GUI

        CarritoResponse response = construirRespuesta(
                subtotal,
                impuestos,
                total,
                "EXITOSO"
        );

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // VALIDACIONES AGRUPADAS
    private String validarCarrito(CarritoRequest request,
            Inventario inventario) {

        if (esCarritoVacio(request)) {
            return "CARRITO VACÍO";
        }

        if (!cantidadesValidas(request)) {
            return "EXISTEN CANTIDADES INVÁLIDAS";
        }

        if (!productosExisten(request, inventario)) {
            return "EXISTE UN PRODUCTO QUE NO EXISTE";
        }

        if (!inventarioSuficiente(request, inventario)) {
            return "INVENTARIO INSUFICIENTE";
        }

        return null; // todo correcto
    }

    private boolean esCarritoVacio(CarritoRequest request) {
        return request.getItemsCount() == 0;
    }

    private boolean cantidadesValidas(CarritoRequest request) {
        for (ItemCarrito item : request.getItemsList()) {
            if (item.getCantidad() <= 0) {
                return false;
            }
        }
        return true;
    }

    private boolean productosExisten(CarritoRequest request,
            Inventario inventario) {

        for (ItemCarrito item : request.getItemsList()) {
            if (inventario.obtenerProductoPorId(item.getProductoId()) == null) {
                return false;
            }
        }
        return true;
    }

    private boolean inventarioSuficiente(CarritoRequest request,
            Inventario inventario) {

        for (ItemCarrito item : request.getItemsList()) {

            ProductoInventario producto
                    = inventario.obtenerProductoPorId(item.getProductoId());

            if (producto.getStock() < item.getCantidad()) {
                return false;
            }
        }
        return true;
    }

    // ======================================================
    // DESCONTAR INVENTARIO
    // ======================================================
    private void descontarInventario(List<ItemCarrito> items,
            Inventario inventario) {

        for (ItemCarrito item : items) {

            ProductoInventario producto
                    = inventario.obtenerProductoPorId(item.getProductoId());

            producto.setStock(
                    producto.getStock() - item.getCantidad()
            );
        }
    }

    // CALCULAR SUBTOTAL
    private double calcularSubtotal(List<ItemCarrito> items,
            Inventario inventario) {

        double subtotal = 0;

        for (ItemCarrito item : items) {

            ProductoInventario producto
                    = inventario.obtenerProductoPorId(item.getProductoId());

            subtotal += producto.getPrecio() * item.getCantidad();
        }

        return subtotal;
    }

    // 
    // RESPUESTAS
    private void enviarError(String mensaje,
            StreamObserver<CarritoResponse> responseObserver) {

        CarritoResponse response = CarritoResponse.newBuilder()
                .setTransaccionId("N/A")
                .setTotalNeto(0)
                .setImpuestos(0)
                .setTotalPagar(0)
                .setEstado("CANCELADO: " + mensaje)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private CarritoResponse construirRespuesta(
            double subtotal,
            double impuestos,
            double total,
            String estado) {

        return CarritoResponse.newBuilder()
                .setTransaccionId(UUID.randomUUID().toString())
                .setTotalNeto(subtotal)
                .setImpuestos(impuestos)
                .setTotalPagar(total)
                .setEstado(estado)
                .build();
    }
}
