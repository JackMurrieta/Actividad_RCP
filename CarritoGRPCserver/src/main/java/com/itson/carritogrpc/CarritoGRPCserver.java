/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.itson.carritogrpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

/**
 *
 * @author Jack Murrieta
 */
public class CarritoGRPCserver {
    public static void main(String[] args) throws InterruptedException {
        Server server = ServerBuilder.forPort(50051)
                .addService(new CarritoServiceImpl())
                .build();
        try {
            server.start();
            System.out.println
                ("Servidor de Carrito iniciado en el puerto 50051...");
            server.awaitTermination();
        } catch (IOException ex) {
            System.getLogger(CarritoGRPCserver.class.getName())
                .log(System.Logger.Level.ERROR, (String) null, ex);
        }       
    }
}


