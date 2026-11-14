package dao;

import conexion.Conexion;
import sistema.Cliente;
import sistema.Cuenta_bancaria;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DepositosRetirosDao {
    
    // BUSCAR CUENTA POR NUMERO
    //==========================
    public Cuenta_bancaria buscarCuenta(String numeroCuenta)
            throws SQLException {
        
        String sql = "SELECT cb.numero_cuenta, cb.saldo, "
                    + "cb.tipo_cuenta, cb.fechaHoraApertura, "
                    + "cb.estado, cb.id_cliente, c.dui, "
                    + "c.estado AS estado_cliente, "
                    + "c.nombre_completo, c.telefono, "
                    + "c.correo, c.direccion "
                    + "FROM cuentas_bancarias cb "
                    + "INNER JOIN clientes c "
                    + "ON cb.id_cliente = c.id_cliente "
                    + "WHERE cb.numero_cuenta = ?";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setString(1, numeroCuenta);
            
            try(ResultSet rs = ps.executeQuery()){
                
                if (rs.next()) {
                    Cliente cliente = new Cliente(
                    String.valueOf(rs.getInt("id_cliente")),
                    rs.getString("dui"),
                    rs.getString("estado_cliente"),
                    rs.getString("nombre_completo"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("direccion")
                    );
                    
                 Timestamp fecha = rs.getTimestamp("fechaHoraApertura");
                 LocalDateTime fechaApertura = fecha !=null ? fecha.toLocalDateTime(): null;
                 
                 return new Cuenta_bancaria(
                 rs.getString("numero_cuenta"),
                 rs.getBigDecimal("saldo"),
                 rs.getString("tipo_cuenta"),
                 fechaApertura,
                 rs.getString("estado"),
                 cliente
                 );
                }
            }
        }
        
        return null;
    }
    
    //CONSULTAR SALDO CON BLOQUEO DE FILA
    
    private CuentaBloqueada obtenerCuentaConBloqueo(
        Connection conn, String numeroCuenta) throws SQLException {

    String sql = "SELECT saldo, estado "
               + "FROM cuentas_bancarias "
               + "WHERE numero_cuenta = ? "
               + "FOR UPDATE";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, numeroCuenta);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new CuentaBloqueada(
                    rs.getBigDecimal("saldo"),
                    rs.getString("estado")
                );
            }
        }
    }

    return null;
    }
    
        
        // ACTUALIZAR SALDO DE LA CUENTA
    //================================

    private void actualizarSaldo(
            Connection conn,
            String numeroCuenta,
            BigDecimal nuevoSaldo) throws SQLException {

        String sql = "UPDATE cuentas_bancarias "
                   + "SET saldo = ? "
                   + "WHERE numero_cuenta = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, nuevoSaldo);
            ps.setString(2, numeroCuenta);

            int filasActualizadas = ps.executeUpdate();

            if (filasActualizadas != 1) {
                throw new SQLException(
                    "No fue posible actualizar el saldo de la cuenta."
                );
            }
        }
    }
    
        // REVERTIR TRANSACCION EN CASO DE ERRORES
    //=============================================

    private void revertirTransaccion(Connection conn, SQLException error) {

        try {
            conn.rollback();

        } catch (SQLException rollbackError) {

            error.addSuppressed(rollbackError);
        }
    }
    
        // INICIAR TRANSACCION
    //====================

    private void iniciarTransaccion(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
    }

    // CONFIRMAR TRANSACCION
    //======================

    private void confirmarTransaccion(Connection conn) throws SQLException {
        conn.commit();
    }
    
    //REALIZAR DEPOSITO
    //====================
    
    public void realizarDeposito(String numeroCuenta, BigDecimal monto)
            throws SQLException {
        
        // Pendiente de implementar:
    // 1. Iniciar transacción JDBC.
    // 2. Bloquear y consultar la cuenta.
    // 3. Actualizar saldo.
    // 4. Registrar movimiento.
    // 5. Confirmar con commit().
    // 6. Revertir con rollback() si ocurre un error.
    }
    
    //REALIZAR RETIRO
    //======================
    
    public void realizarRetiro(String numeroCuenta, BigDecimal monto)
            throws SQLException{
        
        // Pendiente de implementar:
    // 1. Iniciar transacción JDBC.
    // 2. Bloquear y consultar la cuenta.
    // 3. Verificar saldo suficiente.
    // 4. Actualizar saldo.
    // 5. Registrar movimiento.
    // 6. Confirmar con commit().
    // 7. Revertir con rollback() si ocurre un error.
    }
    
    private static class CuentaBloqueada {

    private final BigDecimal saldo;
    private final String estado;

    public CuentaBloqueada(BigDecimal saldo, String estado) {
        this.saldo = saldo;
        this.estado = estado;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public String getEstado() {
        return estado;
    }
}
}
