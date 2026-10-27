package reservationSystem.server;

import reservationSystem.logic.IService;
import reservationSystem.protocol.Request;
import reservationSystem.protocol.Response;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Worker implements Runnable {
    private static final Object LOCK = new Object();
    private static final Map<String, Method> METODOS = new HashMap<>();

    static {
        for (Method m : IService.class.getMethods()) {
            METODOS.put(m.getName(), m);
        }
    }

    private final Server server;
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final String sid;
    private final IService service;
    private final Object asyncLock = new Object();
    private Socket asyncSocket;
    private ObjectOutputStream asyncOut;

    public Worker(Server server, Socket socket, ObjectOutputStream out, ObjectInputStream in,
                  String sid, IService service) {
        this.server = server;
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.sid = sid;
        this.service = service;
    }

    public String getSid() {
        return sid;
    }

    public void setAsync(Socket asyncSocket, ObjectOutputStream asyncOut) {
        synchronized (asyncLock) {
            this.asyncSocket = asyncSocket;
            this.asyncOut = asyncOut;
        }
    }

    public void notificar(Object notificacion) {
        synchronized (asyncLock) {
            if (asyncOut == null) return;
            try {
                asyncOut.reset();
                asyncOut.writeObject(notificacion);
                asyncOut.flush();
            } catch (IOException e) {
                cerrarAsync();
            }
        }
    }

    private void cerrarAsync() {
        synchronized (asyncLock) {
            try {
                if (asyncSocket != null) asyncSocket.close();
            } catch (IOException ignored) {
            }
            asyncSocket = null;
            asyncOut = null;
        }
    }

    @Override
    public void run() {
        try (Socket s = socket) {
            while (true) {
                Request request = (Request) in.readObject();
                Response response = ejecutar(request);
                out.reset();
                out.writeObject(response);
                out.flush();
            }
        } catch (EOFException | SocketException e) {
        } catch (Exception e) {
            System.err.println("Error con el cliente " + sid + ": " + e);
        } finally {
            server.quitar(this);
            cerrarAsync();
        }
    }

    private Response ejecutar(Request request) {
        Method metodo = METODOS.get(request.getOperacion());
        if (metodo == null) {
            return Response.error("Operación desconocida: " + request.getOperacion());
        }
        try {
            Object resultado;
            synchronized (LOCK) {
                resultado = metodo.invoke(service, request.getArgumentos());
            }
            if (resultado instanceof Optional<?> opt) {
                resultado = opt.orElse(null);
            }
            return Response.ok(resultado);
        } catch (InvocationTargetException e) {
            Throwable causa = e.getCause();
            return Response.error(causa.getMessage() != null ? causa.getMessage() : causa.toString());
        } catch (Exception e) {
            return Response.error("Error al ejecutar " + request.getOperacion() + ": " + e);
        }
    }
}