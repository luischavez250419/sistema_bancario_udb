package dao;

import conexion.Conexion;
import sistema.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClientesDao {
    
    // CONSULTAR TODOS LOS CLIENTES
    public List<Cliente> listarTodos() throws SQLException {

        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT id_cliente, dui, nombre_completo, "
                   + "telefono, correo, direccion, estado "
                   + "FROM clientes ORDER BY id_cliente";

        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Cliente cliente = new Cliente(
                    String.valueOf(rs.getInt("id_cliente")),
                    rs.getString("dui"),
                    rs.getString("estado"),
                    rs.getString("nombre_completo"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("direccion")
                );

                clientes.add(cliente);
            }
        }

        return clientes;
    }

    // REGISTRAR CLIENTE
    public int insertar(Cliente cliente) throws SQLException {

        String sql = "INSERT INTO clientes "
                   + "(dui, nombre_completo, telefono, correo, "
                   + "direccion, estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                 sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getDui());
            ps.setString(2, cliente.getNombreCompleto());
            ps.setString(3, cliente.getTelefono());

            if (cliente.getEmail() == null ||
                cliente.getEmail().trim().isEmpty()) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, cliente.getEmail());
            }

            ps.setString(5, cliente.getDireccion());
            ps.setString(6, cliente.getEstado());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    // ACTUALIZAR CLIENTE
    public boolean actualizar(Cliente cliente) throws SQLException {

        String sql = "UPDATE clientes SET "
                   + "dui = ?, nombre_completo = ?, telefono = ?, "
                   + "correo = ?, direccion = ?, estado = ? "
                   + "WHERE id_cliente = ?";

        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getDui());
            ps.setString(2, cliente.getNombreCompleto());
            ps.setString(3, cliente.getTelefono());

            if (cliente.getEmail() == null ||
                cliente.getEmail().trim().isEmpty()) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, cliente.getEmail());
            }

            ps.setString(5, cliente.getDireccion());
            ps.setString(6, cliente.getEstado());
            ps.setInt(7, Integer.parseInt(cliente.getCodigoCliente()));

            return ps.executeUpdate() > 0;
        }
    }

    // ELIMINAR CLIENTE
    public boolean eliminar(int idCliente) throws SQLException {

        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            return ps.executeUpdate() > 0;
        }
    }
}
