/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import com.tienda.grpc.CarritoRequest;
import com.tienda.grpc.CarritoResponse;
import com.tienda.grpc.CarritoServiceGrpc;
import com.tienda.grpc.Producto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 *
 * @author Jack Murrieta
 */
public class CarritoGRPCclient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        CarritoServiceGrpc.CarritoServiceBlockingStub stub
                = CarritoServiceGrpc.newBlockingStub(channel);

        //crear productos con builder
        Producto p1 = Producto.newBuilder()
                .setId("PROD-001").setNombre("Laptop gamer")
                .setPrecio(1200.00)
                .setCantidad(1).build();

        Producto p2 = Producto.newBuilder()
                .setId("PROD-002").setNombre("Mouse ptico")
                .setPrecio(25.500)
                .setCantidad(2).build();

        CarritoRequest request = CarritoRequest.newBuilder()
                .setUsuarioId("USER-123")
                .addItems(p1)
                .addItems(p2)
                .build();

        // 5. Llamada RPC al servidor
        System.out.println("Enviando carrito al servidor...");
        CarritoResponse response = stub.procesarCarrito(request);

        // 6. Imprimir resultados
        System.out.println("--- Factura Generada ---");
        System.out.println("ID Transacción: " + response.getTransaccionId());
        System.out.println("Subtotal: $" + response.getTotalNeto());
        System.out.println("Impuestos (16%): $" + response.getImpuestos());
        System.out.println("TOTAL A PAGAR: $" + response.getTotalPagar());

        // 7. Cerrar conexión
        channel.shutdown();
    }

}
