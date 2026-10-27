package reservationSystem;

import reservationSystem.logic.Service;
import reservationSystem.server.Server;

import java.io.IOException;

public class BackendApplication {
    public static void main(String[] args) throws IOException {
        Server server = new Server(Service.instance());
        server.iniciar();
    }
}