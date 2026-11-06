package conexion;

import java.sql.Connection;
import java.sql.SQLException;

public class PruebaConexion {
    
    public static void main(String[] args) {

        try (Connection conexion = Conexion.obtenerConexion()) {

            if (conexion != null && conexion.isValid(5)) {

                System.out.println(
                    "CONEXION EXITOSA CON MYSQL"
                );

                System.out.println(
                    "Base de datos: "
                    + conexion.getCatalog()
                );
            }

        } catch (SQLException e) {

            System.err.println(
                "ERROR DE CONEXION: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }
    
}
