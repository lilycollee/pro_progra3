package reservationSystem.logic;

import reservationSystem.logic.entities.*;
import reservationSystem.logic.entities.ia.ReservaExtraccion;
import reservationSystem.protocol.Config;
import reservationSystem.protocol.Request;
import reservationSystem.protocol.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ServiceProxy implements IService {
    private static ServiceProxy instance;

    public static synchronized ServiceProxy instance() {
        if (instance == null) {
            instance = new ServiceProxy();
        }
        return instance;
    }

    private String host = Config.HOST;
    private int puerto = Config.PUERTO;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String sid;   // id de sesión: lo usa el socket asíncrono para identificarse
    private volatile boolean conectado;

    private ServiceProxy() {
        leerConfiguracion();
        conectar();
    }

    private void leerConfiguracion() {
        try (InputStream is = ServiceProxy.class.getResourceAsStream("/frontend.properties")) {
            if (is != null) {
                Properties p = new Properties();
                p.load(is);
                host = p.getProperty("backend_host", host);
                puerto = Integer.parseInt(p.getProperty("backend_port", String.valueOf(puerto)));
            }
        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException("Error al leer frontend.properties: " + e.getMessage());
        }
    }

    private void conectar() {
        try {
            socket = new Socket(host, puerto);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            out.writeInt(Config.SYNC);
            out.flush();
            sid = (String) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("No se pudo conectar con el servidor " + host + ":" + puerto);
        }
        conectado = true;
    }

    public String getSid() {
        return sid;
    }

    public String getHost() {
        return host;
    }

    public int getPuerto() {
        return puerto;
    }

    public void cerrar() {
        conectado = false;
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {
        }
    }

    private synchronized <T> T invocar(String operacion, Object... argumentos) {
        if (!conectado) {
            throw new RuntimeException("No hay conexión con el servidor");
        }
        try {
            out.reset();
            out.writeObject(new Request(operacion, argumentos));
            out.flush();
            Response respuesta = (Response) in.readObject();
            if (respuesta.esError()) {
                throw new RuntimeException(respuesta.getError());
            }
            @SuppressWarnings("unchecked")
            T valor = (T) respuesta.getValor();
            return valor;
        } catch (IOException e) {
            conectado = false;
            throw new RuntimeException("Error de comunicación con el servidor: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Respuesta inválida del servidor");
        }
    }

    // ---------- Autenticación ----------

    @Override
    public Usuario autenticar(String id, String clave) {
        return invocar("autenticar", id, clave);
    }

    @Override
    public boolean cambiarClave(String id, String claveActual, String claveNueva) {
        return this.<Boolean>invocar("cambiarClave", id, claveActual, claveNueva);
    }

    // ---------- Funcionarios ----------

    @Override
    public Funcionario guardarFuncionario(Funcionario funcionario) {
        return invocar("guardarFuncionario", funcionario);
    }

    @Override
    public boolean funcionarioTieneReservas(String id) {
        return this.<Boolean>invocar("funcionarioTieneReservas", id);
    }

    @Override
    public boolean eliminarFuncionario(String id) {
        return this.<Boolean>invocar("eliminarFuncionario", id);
    }

    @Override
    public Optional<Funcionario> obtenerFuncionario(String id) {
        return Optional.ofNullable(this.<Funcionario>invocar("obtenerFuncionario", id));
    }

    @Override
    public List<Funcionario> obtenerFuncionarios() {
        return invocar("obtenerFuncionarios");
    }

    @Override
    public List<Reserva> obtenerReservasPorFuncionario(String idFuncionario) {
        return invocar("obtenerReservasPorFuncionario", idFuncionario);
    }

    // ---------- Administradores ----------

    @Override
    public Administrador guardarAdministrador(Administrador administrador) {
        return invocar("guardarAdministrador", administrador);
    }

    @Override
    public void eliminarAdministrador(String id) {
        invocar("eliminarAdministrador", id);
    }

    @Override
    public Optional<Administrador> obtenerAdministrador(String id) {
        return Optional.ofNullable(this.<Administrador>invocar("obtenerAdministrador", id));
    }

    @Override
    public List<Administrador> obtenerAdministradores() {
        return invocar("obtenerAdministradores");
    }

    // ---------- Recursos ----------

    @Override
    public Recurso guardarRecurso(Recurso recurso) {
        return invocar("guardarRecurso", recurso);
    }

    @Override
    public boolean recursoEnReservaActiva(String idRecurso) {
        return this.<Boolean>invocar("recursoEnReservaActiva", idRecurso);
    }

    @Override
    public boolean eliminarRecurso(String id) {
        return this.<Boolean>invocar("eliminarRecurso", id);
    }

    @Override
    public Optional<Recurso> obtenerRecurso(String id) {
        return Optional.ofNullable(this.<Recurso>invocar("obtenerRecurso", id));
    }

    @Override
    public List<Recurso> obtenerRecursos() {
        return invocar("obtenerRecursos");
    }

    @Override
    public List<Recurso> obtenerRecursosPorCategoria(String idCategoria) {
        return invocar("obtenerRecursosPorCategoria", idCategoria);
    }

    @Override
    public String obtenerRecursosAsignadosTexto(Reserva reserva) {
        return invocar("obtenerRecursosAsignadosTexto", reserva);
    }

    @Override
    public String generarIdRecurso() {
        return invocar("generarIdRecurso");
    }

    // ---------- Categorías ----------

    @Override
    public List<CategoriaRecurso> obtenerCategorias() {
        return invocar("obtenerCategorias");
    }

    @Override
    public CategoriaRecurso guardarCategoria(CategoriaRecurso categoria) {
        return invocar("guardarCategoria", categoria);
    }

    @Override
    public boolean categoriaTieneRecursoEnReservaActiva(String idCategoria) {
        return this.<Boolean>invocar("categoriaTieneRecursoEnReservaActiva", idCategoria);
    }

    @Override
    public boolean eliminarCategoria(String id) {
        return this.<Boolean>invocar("eliminarCategoria", id);
    }

    @Override
    public Optional<CategoriaRecurso> obtenerCategoria(String id) {
        return Optional.ofNullable(this.<CategoriaRecurso>invocar("obtenerCategoria", id));
    }

    @Override
    public String generarIdCategoria() {
        return invocar("generarIdCategoria");
    }

    // ---------- Reservas ----------

    @Override
    public List<Reserva> obtenerReservas() {
        return invocar("obtenerReservas");
    }

    @Override
    public List<Reserva> obtenerReservasSemana(LocalDate fechaDeLaSemana) {
        return invocar("obtenerReservasSemana", fechaDeLaSemana);
    }

    @Override
    public ResultadoReserva crearReserva(Reserva reservaSolicitada) {
        return invocar("crearReserva", reservaSolicitada);
    }

    @Override
    public void cancelarReserva(String id) {
        invocar("cancelarReserva", id);
    }

    @Override
    public List<Reserva> obtenerReservasPorFechaYCategoria(LocalDate fecha, String idCategoria) {
        return invocar("obtenerReservasPorFechaYCategoria", fecha, idCategoria);
    }

    // ---------- Estadísticas ----------

    @Override
    public List<EstadisticaItem> obtenerEstadisticasRecurso(LocalDate desde, LocalDate hasta) {
        return invocar("obtenerEstadisticasRecurso", desde, hasta);
    }

    @Override
    public List<EstadisticaItem> obtenerEstadisticasActividades(LocalDate desde, LocalDate hasta) {
        return invocar("obtenerEstadisticasActividades", desde, hasta);
    }

    // ---------- IA ----------

    @Override
    public ReservaExtraccion extraerReserva(String frase) {
        return invocar("extraerReserva", frase);
    }
}