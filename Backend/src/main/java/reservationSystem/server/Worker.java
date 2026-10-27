package reservationSystem.server;

import reservationSystem.logic.IService;
import reservationSystem.protocol.Request;
import reservationSystem.protocol.Response;

import java.io.EOFException;
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

    private final Socket socket;
    private final IService service;

    public Worker(Socket socket, IService service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        String cliente = String.valueOf(socket.getRemoteSocketAddress());
        System.out.println("Cliente conectado: " + cliente);
        try (Socket s = socket;
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream())) {
            out.flush();
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            while (true) {
                Request request = (Request) in.readObject();
                Response response = ejecutar(request);
                out.reset();
                out.writeObject(response);
                out.flush();
            }
        } catch (EOFException | SocketException e) {
        } catch (Exception e) {
            System.err.println("Error con el cliente " + cliente + ": " + e);
        } finally {
            System.out.println("Cliente desconectado: " + cliente);
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