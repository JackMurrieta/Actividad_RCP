/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.carritogrpc;

import com.tienda.grpc.CarritoRequest;
import com.tienda.grpc.CarritoResponse;
import com.tienda.grpc.CarritoServiceGrpc.CarritoServiceImplBase;
import com.tienda.grpc.Producto;
import io.grpc.stub.StreamObserver;
import java.util.UUID;

public class CarritoServiceImpl extends CarritoServiceImplBase {

    @Override
    public void procesarCarrito(CarritoRequest request,
            StreamObserver<CarritoResponse> responseObserver) {
        System.out.println("Procesando carrito para el usuario: "
                + request.getUsuarioId());
        double subtotal = 0;
        //Iteramos sobre la lista repetida de productos 
        //definida en el archivo proto  
        for (Producto p : request.getItemsList()) {
            subtotal += p.getPrecio() * p.getCantidad();
        }
        double impuestos = subtotal * 0.16; // IVA del 16%
        double total = subtotal + impuestos;
        //Construimos la respuesta usando el Builder 
        //generado por Protobuf 
        CarritoResponse response = construirRespuesta(subtotal, impuestos, total, "EXITOSO");
        responseObserver.onNext(response); // Enviamos al cliente
        responseObserver.onCompleted();
    }

    public CarritoResponse construirRespuesta(double subtotal, double impuestos, double total, String estado) {
        CarritoResponse response = CarritoResponse.newBuilder()
                .setTransaccionId(UUID.randomUUID().toString())
                .setTotalNeto(subtotal)
                .setImpuestos(impuestos)
                .setTotalPagar(total)
                .setEstado(estado)
                .build();
        return response;

    }
}
