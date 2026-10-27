package reservationSystem.server;

import reservationSystem.logic.IService;
import reservationSystem.protocol.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final IService service;
    private ServerSocket serverSocket;
    private volatile boolean activo;

    public Server(IService service) {
        this.service = service;
    }

    public void iniciar() throws IOException {
        serverSocket = new ServerSocket(Config.PUERTO);
        activo = true;
        Thread aceptador = new Thread(this::aceptarClientes, "server-aceptador");
        aceptador.start();
        System.out.println("Servidor escuchando en el puerto " + Config.PUERTO);
    }

    private void aceptarClientes() {
        while (activo) {
            try {
                Socket socket = serverSocket.accept();
                new Thread(new Worker(socket, service), "worker").start();
            } catch (IOException e) {
                if (activo) {
                    System.err.println("Error al aceptar un cliente: " + e);
                }
            }
        }
    }

    public void detener() {
        activo = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {
        }
    }
}