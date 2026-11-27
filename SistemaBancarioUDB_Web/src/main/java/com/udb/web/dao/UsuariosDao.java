package com.udb.web.dao;

import com.udb.web.beans.Usuario;
import com.udb.web.seguridad.PasswordUtil;
import conexion.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.NoSuchAlgorithmException;

public class UsuariosDao {

    // Consultar y autenticar un usuario.
    public Usuario autenticar(String nombreUsuario, String password)
            throws SQLException, NoSuchAlgorithmException {

        String sql = "SELECT id_usuario, nombre_usuario, password_hash, "
        + "password_salt, rol, id_cliente, estado "
        + "FROM usuarios "
        + "WHERE nombre_usuario = ?";

        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    String hash = rs.getString("password_hash");
                    String salt = rs.getString("password_salt");
                    String estado = rs.getString("estado");

                    // Comprobar que el usuario esté activo.
                    if (!"Activo".equals(estado)) {
                        return null;
                    }

                    // Verificar la contraseña.
                    boolean valido = PasswordUtil.verificarPassword(
                            password, salt, hash
                    );

                    if (!valido) {
                        return null;
                    }

                    // Construir el objeto Usuario.
                    Usuario usuario = new Usuario();

                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNombreUsuario(
                            rs.getString("nombre_usuario")
                    );
                    usuario.setRol(rs.getString("rol"));
                    usuario.setEstado(estado);

                    int idCliente = rs.getInt("id_cliente");

                    if (!rs.wasNull()) {
                        usuario.setIdCliente(idCliente);
                    }

                    return usuario;
                }
            }
        }

        return null;
    }
}
