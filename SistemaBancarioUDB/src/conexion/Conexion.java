package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Conexion {

    private static final String ARCHIVO_CONFIG =
            "config_database/database.properties";

    private static final Properties propiedades = new Properties();

    static {
        try (FileInputStream archivo =
                new FileInputStream(ARCHIVO_CONFIG)) {

            propiedades.load(archivo);

        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "No se pudo cargar database.properties: "
                    + e.getMessage()
            );
        }
    }

    public static Connection obtenerConexion() throws SQLException {

        String url = propiedades.getProperty("db.url");
        String usuario = propiedades.getProperty("db.user");
        String password = propiedades.getProperty("db.password");

        if (url == null || usuario == null || password == null) {
            throw new SQLException(
                    "Faltan propiedades en database.properties."
            );
        }

        return DriverManager.getConnection(url, usuario, password);
    }
}