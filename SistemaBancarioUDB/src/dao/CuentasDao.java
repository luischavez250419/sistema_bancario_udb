package dao;

import conexion.Conexion;
import sistema.Cliente;
import sistema.Cuenta_bancaria;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CuentasDao {
    
     private final SecureRandom random = new SecureRandom();

    // =========================================
    // GENERAR NUMERO DE CUENTA
    // =========================================

    private String generarNumeroCuenta() {

        StringBuilder numero = new StringBuilder();

        // Primer dígito entre 1 y 9
        numero.append(random.nextInt(9) + 1);

        // Los otros nueve dígitos
        for (int i = 0; i < 9; i++) {
            numero.append(random.nextInt(10));
        }

        return numero.toString();
    }

    private boolean numeroExiste(
            Connection conn, String numero) throws SQLException {

        String sql = "SELECT 1 FROM cuentas_bancarias "
                   + "WHERE numero_cuenta = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numero);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // =========================================
    // BUSCAR CLIENTE POR DUI
    // =========================================

    public Cliente buscarClientePorDui(String dui)
            throws SQLException {

        String sql = "SELECT id_cliente, dui, estado, "
                   + "nombre_completo, telefono, correo, direccion "
                   + "FROM clientes WHERE dui = ?";

        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dui);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Cliente(
                        String.valueOf(rs.getInt("id_cliente")),
                        rs.getString("dui"),
                        rs.getString("estado"),
                        rs.getString("nombre_completo"),
                        rs.getString("telefono"),
                        rs.getString("correo"),
                        rs.getString("direccion")
                    );
                }
            }
        }

        return null;
    }

    // =========================================
    // CONSULTAR TODAS LAS CUENTAS
    // =========================================

    public List<Cuenta_bancaria> listarTodas()
            throws SQLException {

        List<Cuenta_bancaria> cuentas = new ArrayList<>();

        String sql = "SELECT cb.numero_cuenta, cb.saldo, "
                   + "cb.tipo_cuenta, cb.fechaHoraApertura, "
                   + "cb.estado, c.id_cliente, c.dui, "
                   + "c.estado AS estado_cliente, "
                   + "c.nombre_completo, c.telefono, "
                   + "c.correo, c.direccion "
                   + "FROM cuentas_bancarias cb "
                   + "INNER JOIN clientes c "
                   + "ON cb.id_cliente = c.id_cliente "
                   + "ORDER BY cb.id_cuenta";

        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Cliente cliente = new Cliente(
                    String.valueOf(rs.getInt("id_cliente")),
                    rs.getString("dui"),
                    rs.getString("estado_cliente"),
                    rs.getString("nombre_completo"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("direccion")
                );

                Timestamp fecha = rs.getTimestamp(
                    "fechaHoraApertura"
                );

                LocalDateTime fechaApertura =
                    fecha != null ? fecha.toLocalDateTime() : null;

                Cuenta_bancaria cuenta = new Cuenta_bancaria(
                    rs.getString("numero_cuenta"),
                    rs.getBigDecimal("saldo"),
                    rs.getString("tipo_cuenta"),
                    fechaApertura,
                    rs.getString("estado"),
                    cliente
                );

                cuentas.add(cuenta);
            }
        }

        return cuentas;
    }

    // =========================================
    // REGISTRAR CUENTA
    // =========================================

    public String insertar(Cuenta_bancaria cuenta)
            throws SQLException {

        if (cuenta.getCliente() == null
                || cuenta.getCliente().getCodigoCliente() == null) {

            throw new IllegalArgumentException(
                "La cuenta debe tener un cliente asociado."
            );
        }

        int idCliente = Integer.parseInt(
            cuenta.getCliente().getCodigoCliente()
        );

        String sql = "INSERT INTO cuentas_bancarias "
                   + "(id_cliente, numero_cuenta, tipo_cuenta, "
                   + "saldo, estado) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.obtenerConexion()) {

            // Intentar generar un número disponible
            for (int intento = 0; intento < 10; intento++) {

                String numero = generarNumeroCuenta();

                if (numeroExiste(conn, numero)) {
                    continue;
                }

                try (PreparedStatement ps =
                        conn.prepareStatement(sql)) {

                    ps.setInt(1, idCliente);
                    ps.setString(2, numero);
                    ps.setString(3, cuenta.getTipoCuenta());
                    ps.setBigDecimal(4, cuenta.getSaldo());
                    ps.setString(5, cuenta.getEstado());

                    ps.executeUpdate();

                    cuenta.setNumeroCuenta(numero);

                    return numero;
                }
            }
        }

        throw new SQLException(
            "No fue posible generar un número de cuenta disponible."
        );
    }

    // =========================================
    // ACTUALIZAR CUENTA
    // =========================================

public boolean actualizar(Cuenta_bancaria cuenta)
        throws SQLException {

    String sql = "UPDATE cuentas_bancarias SET "
               + "tipo_cuenta = ?, saldo = ?, estado = ? "
               + "WHERE numero_cuenta = ?";

    try (Connection conn = Conexion.obtenerConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, cuenta.getTipoCuenta());
        ps.setBigDecimal(2, cuenta.getSaldo());
        ps.setString(3, cuenta.getEstado());
        ps.setString(4, cuenta.getNumeroCuenta());

        return ps.executeUpdate() > 0;
    }
}

    // =========================================
    // ELIMINAR CUENTA
    // =========================================

    public boolean eliminar(String numeroCuenta)
            throws SQLException {

        String sql = "DELETE FROM cuentas_bancarias "
                   + "WHERE numero_cuenta = ?";

        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numeroCuenta);

            return ps.executeUpdate() > 0;
        }
    }
    
    //Obtener fecha y hora de apertura de la cuenta
    public LocalDateTime obtenerFechaApertura(String numeroCuenta)
        throws SQLException {

    String sql = "SELECT fechaHoraApertura "
               + "FROM cuentas_bancarias "
               + "WHERE numero_cuenta = ?";

    try (Connection conn = Conexion.obtenerConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, numeroCuenta);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                Timestamp fecha = rs.getTimestamp("fechaHoraApertura");
                return fecha.toLocalDateTime();
            }
        }
    }

    return null;
}
}
  
