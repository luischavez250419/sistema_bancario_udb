package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Conexion {
    private static final String URL =
        "jdbc:mysql://localhost:3306/sistema_bancario_udb"
        + "?useSSL=false"
        + "&serverTimezone=UTC-6"
        + "&allowPublicKeyRetrieval=true";

    private static final String USUARIO = "root";

    // Sustituir por la contraseña real de MySQL.
    private static final String PASSWORD = "250419";

    public static Connection obtenerConexion() throws SQLException {

        return DriverManager.getConnection(
            URL,
            USUARIO,
            PASSWORD
        );
    }
}
