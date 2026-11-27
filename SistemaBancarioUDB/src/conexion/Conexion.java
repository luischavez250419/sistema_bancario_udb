
package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Conexion {

    private static final Properties propiedades = new Properties();

    static {

        String archivoConfig = System.getProperty(
                "db.config",
                "config_database/database.properties"
        );

        String url = System.getenv("DB_URL");
        String usuario = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        try {

            Path ruta = Path.of(archivoConfig);

            if (Files.exists(ruta)) {

                try (FileInputStream archivo =
                        new FileInputStream(ruta.toFile())) {

                    propiedades.load(archivo);
                }

            } else {

                // Si no existe el archivo, utilizar variables de entorno.
                if (url == null || usuario == null || password == null) {
                    throw new IOException(
                        "No se encontró la configuración de la base de datos."
                    );
                }

                propiedades.setProperty("db.url", url);
                propiedades.setProperty("db.user", usuario);
                propiedades.setProperty("db.password", password);
            }

        } catch (IOException e) {

            throw new ExceptionInInitializerError(
                    "No se pudo cargar la configuración: "
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
                    "Faltan propiedades de conexión."
            );
        }
        
         try {
        Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
        throw new SQLException(
                "No se encontró el controlador JDBC de MySQL.", e
        );
    }
        return DriverManager.getConnection(url, usuario, password);
    }
}
