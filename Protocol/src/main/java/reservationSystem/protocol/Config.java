package reservationSystem.protocol;

public class Config {
    public static final String HOST = "localhost";
    public static final int PUERTO = 7000;


    public static final int SYNC = 10;   // peticiones y respuestas
    public static final int ASYNC = 11;  // notificaciones del servidor

    private Config() {
    }
}