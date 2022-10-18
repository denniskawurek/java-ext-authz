package de.dkwr.authzexample;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * External authorization server for Istio implemented in Java.
 * Uses grpc-java: https://github.com/grpc/grpc-java
 * This bases upon the grpc-java example 'RouteGuideServer' (Apache License, Version 2.0): https://github.com/grpc/grpc-java/tree/master/examples/src/main/java/io/grpc/examples/routeguide
 */
public class AuthorizationServer {
    private final int port;
    private final Server server;

    /** Create a AuthorizationServer using serverBuilder as a base and features as data. */
    public AuthorizationServer(ServerBuilder<?> serverBuilder, int port) {
        this.port = port;
        server = serverBuilder.addService(new AuthorizationServiceImpl())
                .addService(ProtoReflectionService.newInstance())
                .build();
    }

    /** Start serving requests. */
    public void start() throws IOException {
        server.start();
        System.out.println("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    AuthorizationServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        });
    }

    /** Stop serving requests and shutdown resources. */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        AuthorizationServer server = new AuthorizationServer(ServerBuilder.forPort(9000), 9000);
        server.start();
        server.blockUntilShutdown();
    }

}