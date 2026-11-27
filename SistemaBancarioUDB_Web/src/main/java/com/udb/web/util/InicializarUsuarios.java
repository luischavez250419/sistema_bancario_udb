package com.udb.web.util;

import com.udb.web.seguridad.PasswordUtil;
import conexion.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InicializarUsuarios {

    public static void main(String[] args) {
        
        System.setProperty(
        "db.config",
        "/home/luis/Documentos/sistema_bancario_udb/SistemaBancarioUDB/config_database/database.properties"
);

        String sql = "INSERT INTO usuarios "
                + "(nombre_usuario, password_hash, password_salt, rol, id_cliente) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            //crearUsuario(ps, "admin", "Admin123*", "Administrador", null);
            //crearUsuario(ps, "cajero", "Cajero123*", "Cajero", null);
            crearUsuario(ps, "cliente", "Cliente123*", "Cliente", 10);

            System.out.println("Usuarios de prueba creados correctamente.");

        } catch (Exception e) {
            System.out.println("Error al crear usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void crearUsuario(
            PreparedStatement ps,
            String nombreUsuario,
            String password,
            String rol,
            Integer idCliente) throws Exception {

        String salt = PasswordUtil.generarSalt();
        String hash = PasswordUtil.generarHash(password, salt);

        ps.setString(1, nombreUsuario);
        ps.setString(2, hash);
        ps.setString(3, salt);
        ps.setString(4, rol);

        if (idCliente == null) {
            ps.setNull(5, java.sql.Types.INTEGER);
        } else {
            ps.setInt(5, idCliente);
        }

        ps.executeUpdate();

        System.out.println("Usuario creado: " + nombreUsuario);
    }
}