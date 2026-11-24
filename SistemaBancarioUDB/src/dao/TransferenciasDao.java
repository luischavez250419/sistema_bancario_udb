package dao;

import conexion.Conexion;
import excepciones.TransaccionExcepcion;
import sistema.Cliente;
import sistema.Cuenta_bancaria;
import sistema.Movimiento;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TransferenciasDao {

    private static final BigDecimal SALDO_MAXIMO = new BigDecimal("9999999999.99");

  
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
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numeroCuenta);

            try (ResultSet rs = ps.executeQuery()) {

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
                    LocalDateTime fechaApertura = fecha != null ? fecha.toLocalDateTime() : null;

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

   
    private CuentaBloqueada obtenerCuentaConBloqueo(
            Connection conn, String numeroCuenta) throws SQLException {

        String sql = "SELECT id_cuenta, numero_cuenta, saldo, estado "
                   + "FROM cuentas_bancarias "
                   + "WHERE numero_cuenta = ? "
                   + "FOR UPDATE";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numeroCuenta);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new CuentaBloqueada(
                            rs.getInt("id_cuenta"),
                            rs.getString("numero_cuenta"),
                            rs.getBigDecimal("saldo"),
                            rs.getString("estado")
                    );
                }
            }
        }

        return null;
    }

   
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
                        "No fue posible actualizar el saldo de la cuenta: " + numeroCuenta
                );
            }
        }
    }

    // REGISTRAR MOVIMIENTO DE TRANSFERENCIA
   // ======================================

   private Movimiento registrarMovimiento(
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

       try (PreparedStatement ps = conn.prepareStatement(
               sql, Statement.RETURN_GENERATED_KEYS)) {

           ps.setString(1, tipoMovimiento);
           ps.setInt(2, idCuentaOrigen);
           ps.setInt(3, idCuentaDestino);
           ps.setBigDecimal(4, monto);
           ps.setString(5, descripcion);

           if (ps.executeUpdate() != 1) {
               throw new SQLException(
                       "No fue posible registrar el movimiento de la transferencia."
               );
           }

           try (ResultSet rs = ps.getGeneratedKeys()) {

               if (!rs.next()) {
                   throw new SQLException(
                           "No se pudo recuperar el código de la transferencia."
                   );
               }

               int idMovimiento = rs.getInt(1);

               String sqlFecha = "SELECT fecha_hora "
                       + "FROM movimientos "
                       + "WHERE id_movimiento = ?";

               try (PreparedStatement psFecha = conn.prepareStatement(sqlFecha)) {

                   psFecha.setInt(1, idMovimiento);

                   try (ResultSet rsFecha = psFecha.executeQuery()) {

                       if (!rsFecha.next()) {
                           throw new SQLException(
                                   "No se pudo recuperar la fecha de la transferencia."
                           );
                       }

                       Timestamp timestamp = rsFecha.getTimestamp("fecha_hora");

                       LocalDateTime fechaHora = timestamp != null
                               ? timestamp.toLocalDateTime()
                               : null;

                       return new Movimiento(
                               idMovimiento,
                               Movimiento.TipoMovimiento.Transferencia,
                               idCuentaOrigen,
                               idCuentaDestino,
                               monto,
                               fechaHora,
                               descripcion
                       );
                   }
               }
           }
       }
   }

    
    private void iniciarTransaccion(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
    }

    private void confirmarTransaccion(Connection conn) throws SQLException {
        conn.commit();
    }

    private void revertirTransaccion(Connection conn, Throwable error) {
        try {
            conn.rollback();
        } catch (SQLException rollbackError) {
            error.addSuppressed(rollbackError);
        }
    }

  
    public Movimiento realizarTransferencia(
            String numeroCuentaOrigen,
            String numeroCuentaDestino,
            BigDecimal monto,
            String descripcion) throws SQLException, TransaccionExcepcion {

        try (Connection conn = Conexion.obtenerConexion()) {

            iniciarTransaccion(conn);

            try {
              
                if (numeroCuentaOrigen == null || numeroCuentaDestino == null
                        || numeroCuentaOrigen.trim().equals(numeroCuentaDestino.trim())) {
                    throw new TransaccionExcepcion("La cuenta de origen y la cuenta de destino no pueden ser la misma.");
                }

           
                if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new TransaccionExcepcion("El monto de la transferencia debe ser mayor que cero.");
                }

                
                CuentaBloqueada origen;
                CuentaBloqueada destino;

                if (numeroCuentaOrigen.compareTo(numeroCuentaDestino) < 0) {
                    origen = obtenerCuentaConBloqueo(conn, numeroCuentaOrigen);
                    destino = obtenerCuentaConBloqueo(conn, numeroCuentaDestino);
                } else {
                    destino = obtenerCuentaConBloqueo(conn, numeroCuentaDestino);
                    origen = obtenerCuentaConBloqueo(conn, numeroCuentaOrigen);
                }

               
                if (origen == null) {
                    throw new TransaccionExcepcion("La cuenta de origen no existe.");
                }
                if (destino == null) {
                    throw new TransaccionExcepcion("La cuenta de destino no existe.");
                }

              
                if (!"Activa".equalsIgnoreCase(origen.getEstado())) {
                    throw new TransaccionExcepcion("La cuenta de origen no está activa. No se permiten transferencias.");
                }
                if (!"Activa".equalsIgnoreCase(destino.getEstado())) {
                    throw new TransaccionExcepcion("La cuenta de destino no está activa. No se permiten transferencias.");
                }

               
                if (origen.getSaldo().compareTo(monto) < 0) {
                    throw new TransaccionExcepcion("La cuenta de origen no tiene fondos suficientes para realizar la transferencia.");
                }

                BigDecimal nuevoSaldoOrigen = origen.getSaldo().subtract(monto);
                BigDecimal nuevoSaldoDestino = destino.getSaldo().add(monto);

                if (nuevoSaldoDestino.compareTo(SALDO_MAXIMO) > 0) {
                    throw new TransaccionExcepcion("La transferencia supera el saldo máximo permitido para la cuenta de destino.");
                }

           
                actualizarSaldo(conn, numeroCuentaOrigen, nuevoSaldoOrigen);
                actualizarSaldo(conn, numeroCuentaDestino, nuevoSaldoDestino);

        
                String detalle = (descripcion != null && !descripcion.trim().isEmpty())
                        ? descripcion.trim()
                        : "Transferencia entre cuentas";

                Movimiento movimiento = registrarMovimiento(
                        conn,
                        "Transferencia",
                        origen.getIdCuenta(),
                        destino.getIdCuenta(),
                        monto,
                        detalle
                );

              
                confirmarTransaccion(conn);
                
                return movimiento;

            } catch (SQLException | TransaccionExcepcion | RuntimeException e) {
                revertirTransaccion(conn, e);
                throw e;
            }
        }
        
        
    }
    

    private static class CuentaBloqueada {

        private final int idCuenta;
        private final String numeroCuenta;
        private final BigDecimal saldo;
        private final String estado;

        public CuentaBloqueada(int idCuenta, String numeroCuenta, BigDecimal saldo, String estado) {
            this.idCuenta = idCuenta;
            this.numeroCuenta = numeroCuenta;
            this.saldo = saldo;
            this.estado = estado;
        }

        public int getIdCuenta() {
            return idCuenta;
        }

        public String getNumeroCuenta() {
            return numeroCuenta;
        }

        public BigDecimal getSaldo() {
            return saldo;
        }

        public String getEstado() {
            return estado;
        }
    }
}