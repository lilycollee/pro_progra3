package reservationSystem.server;

import reservationSystem.logic.IService;
import reservationSystem.protocol.Config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final IService service;
    private final List<Worker> workers = new ArrayList<>();
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
                // El saludo se hace en otro hilo para que un cliente lento no bloquee el accept
                new Thread(() -> atender(socket), "saludo").start();
            } catch (IOException e) {
                if (activo) {
                    System.err.println("Error al aceptar un cliente: " + e);
                }
            }
        }
    }

    private void atender(Socket socket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            int tipo = in.readInt();
            switch (tipo) {
                case Config.SYNC -> {
                    String sid = String.valueOf(socket.getRemoteSocketAddress());
                    Worker worker = new Worker(this, socket, out, in, sid, service);
                    agregar(worker);
                    out.writeObject(sid);   // el cliente usará este sid para abrir su socket ASYNC
                    out.flush();
                    new Thread(worker, "worker-" + sid).start();
                }
                case Config.ASYNC -> {
                    String sid = (String) in.readObject();
                    unirAsincrono(socket, out, sid);
                }
                default -> socket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Conexión descartada: " + e);
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void agregar(Worker worker) {
        synchronized (workers) {
            workers.add(worker);
            System.out.println("Cliente conectado: " + worker.getSid() + " (activos: " + workers.size() + ")");
        }
    }

    public void quitar(Worker worker) {
        synchronized (workers) {
            workers.remove(worker);
            System.out.println("Cliente desconectado: " + worker.getSid() + " (activos: " + workers.size() + ")");
        }
    }

    private void unirAsincrono(Socket socket, ObjectOutputStream out, String sid) throws IOException {
        Worker destino = null;
        synchronized (workers) {
            for (Worker w : workers) {
                if (w.getSid().equals(sid)) {
                    destino = w;
                    break;
                }
            }
        }
        if (destino == null) {
            socket.close();
            return;
        }
        destino.setAsync(socket, out);
        System.out.println("Canal asíncrono asociado: " + sid);
    }

    public void notificarAOtros(Worker origen, Object notificacion) {
        List<Worker> copia;
        synchronized (workers) {
            copia = new ArrayList<>(workers);
        }
        for (Worker w : copia) {
            if (w != origen) {
                w.notificar(notificacion);
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