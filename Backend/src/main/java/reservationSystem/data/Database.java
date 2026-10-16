package reservationSystem.data;

import java.sql.*;
import java.util.Properties;

public class Database {
    private static Database theInstance;
    public static Database instance() {
        if (theInstance == null) theInstance = new Database();
        return theInstance;
    }

    public static final String PROPERTIES_FILE_NAME = "/database.properties";
    private Connection cnx;

    private Database() {
        cnx = getConnection();
    }

    private Connection getConnection() {
        try {
            Properties prop = new Properties();
            prop.load(getClass().getResourceAsStream(PROPERTIES_FILE_NAME));
            String url = "jdbc:mysql://" + prop.getProperty("database_server") + ":"
                    + prop.getProperty("database_port") + "/" + prop.getProperty("database_name")
                    + "?user=" + prop.getProperty("database_user")
                    + "&password=" + prop.getProperty("database_password")
                    + "&serverTimezone=UTC";
            Class.forName(prop.getProperty("database_driver"));
            return DriverManager.getConnection(url);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        return null;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return cnx.prepareStatement(sql);
    }

    public Connection getCnx() { return cnx; }   // para transacciones (crear reserva + detalles)

    public void close() throws Exception {
        if (cnx != null && !cnx.isClosed()) cnx.close();
    }
}