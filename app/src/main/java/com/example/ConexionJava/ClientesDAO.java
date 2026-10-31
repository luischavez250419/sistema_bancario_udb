package com.example.ConexionJava;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientesDAO {

    // **CREATE** - Insertar un nuevo cliente usando PreparedStatement
    public boolean insertar(String nombre, String correo, String telefono) {
        String sql = "INSERT INTO clientes (nombre, correo, telefono) VALUES (?, ?, ?)";
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            pstmt.setString(2, correo);
            pstmt.setString(3, telefono);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
            return false;
        }
    }

    // **READ** - Listar todos los clientes registrados
    public void listarClientes() {
        String sql = "SELECT id, nombre, correo, telefono FROM clientes";
        try (Connection conn = Conexion.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n--- LISTA DE CLIENTES ---");
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String correo = rs.getString("correo");
                String telefono = rs.getString("telefono");
                
                System.out.println("ID: " + id + " | Nombre: " + nombre + " | Correo: " + correo + " | Tel: " + telefono);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
        }
    }

    // **UPDATE** - Actualizar los datos de un cliente mediante su ID
    public boolean actualizar(int id, String nombre, String correo, String telefono) {
        String sql = "UPDATE clientes SET nombre = ?, correo = ?, telefono = ? WHERE id = ?";
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            pstmt.setString(2, correo);
            pstmt.setString(3, telefono);
            pstmt.setInt(4, id);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    // **DELETE** - Eliminar un cliente por su ID
    public boolean eliminar(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }
}