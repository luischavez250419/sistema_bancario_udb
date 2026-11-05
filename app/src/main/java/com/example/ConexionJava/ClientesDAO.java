package com.example.ConexionJava;

import java.sql.*;

public class ClientesDAO {

    public boolean insertarCliente(String dui, String nombreCompleto, String telefono, String correoElectronico, String direccion, String estado) {
        String sql = "INSERT INTO Clientes (dui, nombre_completo, telefono, correo_electronico, direccion, estado) VALUES (?, ?, ?, ?, ?, ?)";
       try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dui);
            pstmt.setString(2, nombreCompleto);
            pstmt.setString(3, telefono);
            pstmt.setString(4, correoElectronico);
            pstmt.setString(5, direccion);
            pstmt.setString(6, estado);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
            return false;
        }
    }

    public void listarClientes() {
        String sql = "SELECT codigo_cliente, dui, nombre_completo, telefono, correo_electronico, direccion, estado FROM Clientes";
        try (Connection conn = Conexion.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n--- LISTA DE CLIENTES ---");
            while (rs.next()) {
                int codigo = rs.getInt("codigo_cliente");
                String dui = rs.getString("dui");
                String nombre = rs.getString("nombre_completo");
                String tel = rs.getString("telefono");
                String correo = rs.getString("correo_electronico");
                String estado = rs.getString("estado");
                
                System.out.println("Código: " + codigo + " | DUI: " + dui + " | Nombre: " + nombre + " | Tel: " + tel + " | Correo: " + correo + " | Estado: " + estado);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
        }
    }

    public boolean actualizarCliente(int codigoCliente, String dui, String nombreCompleto, String telefono, String correoElectronico, String direccion, String estado) {
        String sql = "UPDATE Clientes SET dui = ?, nombre_completo = ?, telefono = ?, correo_electronico = ?, direccion = ?, estado = ? WHERE codigo_cliente = ?";
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dui);
            pstmt.setString(2, nombreCompleto);
            pstmt.setString(3, telefono);
            pstmt.setString(4, correoElectronico);
            pstmt.setString(5, direccion);
            pstmt.setString(6, estado);
            pstmt.setInt(7, codigoCliente);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

  
    public boolean eliminarCliente(int codigoCliente) {
        String sql = "DELETE FROM Clientes WHERE codigo_cliente = ?";
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, codigoCliente);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }
}