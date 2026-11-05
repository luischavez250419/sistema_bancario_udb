package com.example.ConexionJava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
 
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_db?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "tu_contraseña";

    
    public static Connection obtenerConexion() {
        Connection conexion = null;
        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("¡Conexión exitosa a la base de datos!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el Driver JDBC de MySQL. " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
        return conexion;
    }
}