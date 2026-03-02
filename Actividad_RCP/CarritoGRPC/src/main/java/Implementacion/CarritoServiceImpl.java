/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Implementacion;

import com.tienda.grpc.CarritoRequest;
import com.tienda.grpc.CarritoResponse;
import com.tienda.grpc.CarritoServiceGrpc.CarritoServiceImplBase;
import com.tienda.grpc.Producto;
import io.grpc.stub.StreamObserver;
import java.util.UUID;

/**
 *
 * @author Jack Murrieta
 */
public class CarritoServiceImpl extends CarritoServiceImplBase {

    @Override
    public void procesarCarrito(CarritoRequest request, StreamObserver<CarritoResponse> responseObserver) {
        System.out.println("Procesando Carrito para el usuario:  " + request.getUsuarioId());
        double subtotal = 0;
        //iteracion de prductos de la lista proto
        for (Producto p : request.getItemsList()) {
            subtotal += p.getPrecio() * p.getCantidad();

        }
        //iva del 16%
        double impuesto = subtotal * .16;
        double total = subtotal + impuesto;

        //construccion de respuesta con builder
        //generado por protoBuf
        CarritoResponse response = crearRespuestaCarrito(subtotal, impuesto, total, "EXITOSO");
//        CarritoResponse response = CarritoResponse.newBuilder()
//                .setTransaccionId(UUID.randomUUID().toString())
//                .setTotalNeto(subtotal)
//                .setImpuestos(impuesto)
//                .setTotalPagar(total)
//                .setEstado("EXITOSO")
//                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    private CarritoResponse crearRespuestaCarrito(double subtotal, double impuesto, double total, String estado) {
        //construccion de respuesta con builder
        //generado por protoBuf
        CarritoResponse response = CarritoResponse.newBuilder()
                .setTransaccionId(UUID.randomUUID().toString())
                .setTotalNeto(subtotal)
                .setImpuestos(impuesto)
                .setTotalPagar(total)
                .setEstado(estado)
                .build();

        return response;

    }

}
