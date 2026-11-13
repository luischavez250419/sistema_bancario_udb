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
        
        String sql = "SELECT cb.numero_cuenta, cb.saldo,"
                    +"cb.tipo_cuenta, cb.fechaHoraApertura,"
                    +"cb.estado, cb.id_cliente, c.dui,"
                    +"c.estado AS estado_cliente,"
                    +"c.nombre_completo, c.telefono,"
                    +"c.correo, c.direccion"
                    +"FROM cuentas_bancarias cb"
                    +"INNER JOIN clientes c"
                    +"ON cb.id_cliente = c.id_cliente"
                    +"WHERE cb.numero_cuenta =?";
        
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
}
