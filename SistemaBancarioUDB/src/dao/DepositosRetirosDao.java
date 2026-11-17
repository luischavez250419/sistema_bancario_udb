package dao;

import conexion.Conexion;
import sistema.Cliente;
import sistema.Cuenta_bancaria;
import excepciones.TransaccionExcepcion;
import sistema.Movimiento;

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

    String sql = "SELECT id_cuenta, saldo, estado "
               + "FROM cuentas_bancarias "
               + "WHERE numero_cuenta = ? "
               + "FOR UPDATE";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, numeroCuenta);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new CuentaBloqueada(
                    rs.getInt("id_cuenta"),
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
    
    // REGISTRAR MOVIMIENTO BANCARIO
    // ===============================
    
    private void registrarMovimiento (
        Connection conn,
        String tipoMovimiento,
        Integer idCuentaOrigen,
        Integer idCuentaDestino,
        BigDecimal monto,
        String descripcion
    ) throws SQLException {
        
        String sql = "INSERT INTO movimientos "
                    + "(tipo_movimiento, id_cuenta_origen, "
                    + "id_cuenta_destino, monto, descripcion) "
                    + "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tipoMovimiento);
            
            if (idCuentaOrigen == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, idCuentaOrigen);
            }
            
            if (idCuentaDestino == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, idCuentaDestino);
            }
            
            ps.setBigDecimal(4, monto);
            ps.setString(5, descripcion);
            
            if (ps.executeUpdate() != 1) {
                throw new SQLException("No fue posible registrar el movimiento bancario.");
            }
        }
    }
    
    
    
        // REVERTIR TRANSACCION EN CASO DE ERRORES
    //=============================================

    private void revertirTransaccion(Connection conn, Throwable error) {

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
            throws SQLException, TransaccionExcepcion {
        
        try (Connection conn = Conexion.obtenerConexion()) {
            iniciarTransaccion(conn);
            
            try {
                
                //1. Bloquear y consultar la cuenta
                CuentaBloqueada cuenta = obtenerCuentaConBloqueo(conn, numeroCuenta);
                
                if (cuenta == null) {
                    throw new TransaccionExcepcion("La cuenta bancaria no existe.");
                }
                
                //2. Verificar que la cuenta este activa
                if (!"Activa".equals(cuenta.getEstado())) {
                    throw new TransaccionExcepcion("No se pueden realizar depósitos en una cuenta que no esta activa.");
                }
                
                //3. Validar el monto de la transacción
                if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new TransaccionExcepcion("El monto del depósito debe ser mayor que cero.");
                }
                
                //4. Calcular el nuevo saldo
                BigDecimal nuevoSaldo = cuenta.getSaldo().add(monto);
                BigDecimal saldoMaximo = new BigDecimal("9999999999.99");
                
                if (nuevoSaldo.compareTo(saldoMaximo) > 0) {
                    throw new TransaccionExcepcion("El depósito supera el saldo máximo permitido para una cuenta bancaria.");
                }
                
                //5. Actualizar el saldo
                actualizarSaldo(conn, numeroCuenta, nuevoSaldo);
                
                //6. Registrar el movimiento
                registrarMovimiento(conn, "Deposito", null, cuenta.getIdCuenta(), monto, "Depósito bancario");
                
                //7. Confirmar la transacción
                confirmarTransaccion(conn);
                
            } catch (SQLException | TransaccionExcepcion | RuntimeException e) {
                
                revertirTransaccion(conn, e);
                throw e;
                }
            }
        }
    
    //REALIZAR RETIRO
    //======================
    
    public void realizarRetiro(String numeroCuenta, BigDecimal monto)
            throws SQLException, TransaccionExcepcion {
       
        try (Connection conn = Conexion.obtenerConexion()) {
            
            iniciarTransaccion(conn);
            
            try {
                
                //1. Bloquear y consultar la cuenta
                CuentaBloqueada cuenta = obtenerCuentaConBloqueo(conn, numeroCuenta);
                
                if (cuenta == null) {
                    throw new TransaccionExcepcion("La cuenta bancaria no existe.");
                }
                
                //2. Verificar que la cuenta está activa
                if (!"Activa".equals(cuenta.getEstado())) {
                    throw new TransaccionExcepcion("No se pueden realizar retiros en una cuenta que no está activa.");
                }
                
                //3. Validar el monto
                if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new TransaccionExcepcion("El monto del retiro debe ser mayor que cero.");
                }
                
                //4. Verificar que existan fondos suficientes para el retiro
                if (cuenta.getSaldo().compareTo(monto) < 0) {
                    throw new TransaccionExcepcion("La cuenta no tiene fondos suficientes para realizar el retiro.");
                }
                
                //5. Calcular el nuevo saldo
                BigDecimal nuevoSaldo = cuenta.getSaldo().subtract(monto);
                
                //6. Actualizar el saldo
                actualizarSaldo(conn, numeroCuenta, nuevoSaldo);
                
                //7. Registrar el movimiento
                registrarMovimiento(conn, "Retiro", cuenta.getIdCuenta(), null, monto, "Retiro bancario");
            
                 //8. Confirmar la transacción
                 confirmarTransaccion(conn);
                 
            } catch (SQLException | TransaccionExcepcion | RuntimeException e) {
                
                revertirTransaccion(conn, e);
                throw e;
            }
    }
    
    }
    
    private static class CuentaBloqueada {

    private final int idCuenta;
    private final BigDecimal saldo;
    private final String estado;

    public CuentaBloqueada(int idCuenta, BigDecimal saldo, String estado) {
        this.idCuenta = idCuenta;
        this.saldo = saldo;
        this.estado = estado;
    }
    
    public int getIdCuenta() {
        return idCuenta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public String getEstado() {
        return estado;
    }
}

}
