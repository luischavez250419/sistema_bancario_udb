package dao;

import conexion.Conexion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Acceso a datos para los ocho reportes administrativos de la Fase 2.
 * Todas las consultas parametrizadas utilizan PreparedStatement.
 *
 * Los resultados tabulares se devuelven como List<Map<String, Object>>,
 * usando como claves los alias definidos en cada consulta SQL.
 */
public class ReportesDao {

    private static final String SQL_HISTORIAL_BASE =
        "SELECT m.id_movimiento, "
        + "m.tipo_movimiento, "
        + "co.numero_cuenta AS cuenta_origen, "
        + "cd.numero_cuenta AS cuenta_destino, "
        + "cli_o.nombre_completo AS titular_origen, "
        + "cli_d.nombre_completo AS titular_destino, "
        + "m.monto, m.fecha_hora, m.descripcion "
        + "FROM movimientos m "
        + "LEFT JOIN cuentas_bancarias co "
        + "ON m.id_cuenta_origen = co.id_cuenta "
        + "LEFT JOIN clientes cli_o "
        + "ON co.id_cliente = cli_o.id_cliente "
        + "LEFT JOIN cuentas_bancarias cd "
        + "ON m.id_cuenta_destino = cd.id_cuenta "
        + "LEFT JOIN clientes cli_d "
        + "ON cd.id_cliente = cli_d.id_cliente ";

    public List<Map<String, Object>> obtenerCuentasActivas() throws SQLException {
        String sql = "SELECT cb.numero_cuenta, cb.tipo_cuenta, cb.saldo, "
                + "c.nombre_completo AS titular "
                + "FROM cuentas_bancarias cb "
                + "INNER JOIN clientes c ON cb.id_cliente = c.id_cliente "
                + "WHERE cb.estado = ? "
                + "ORDER BY cb.numero_cuenta";

        return ejecutarConsulta(sql, List.of("Activa"));
    }

    public List<Map<String, Object>> obtenerCuentasBloqueadas() throws SQLException {
        String sql = "SELECT cb.numero_cuenta, cb.tipo_cuenta, cb.saldo, "
                + "c.nombre_completo AS titular "
                + "FROM cuentas_bancarias cb "
                + "INNER JOIN clientes c ON cb.id_cliente = c.id_cliente "
                + "WHERE cb.estado = ? "
                + "ORDER BY cb.numero_cuenta";

        return ejecutarConsulta(sql, List.of("Bloqueada"));
    }

    public List<Map<String, Object>> obtenerClientesRegistrados() throws SQLException {
        String sql = "SELECT c.id_cliente AS codigo, c.dui, "
                + "c.nombre_completo AS nombre, c.telefono, c.estado "
                + "FROM clientes c "
                + "ORDER BY c.id_cliente";

        return ejecutarConsulta(sql, List.of());
    }

    public List<Map<String, Object>> obtenerHistorialTransacciones(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) throws SQLException {

        StringBuilder sql = new StringBuilder(SQL_HISTORIAL_BASE);
        sql.append("WHERE 1 = 1 ");

        List<Object> parametros = new ArrayList<>();
        agregarFiltroFechas(sql, parametros, "m.fecha_hora", fechaInicio, fechaFin);
        sql.append("ORDER BY m.fecha_hora DESC, m.id_movimiento DESC");

        return ejecutarConsulta(sql.toString(), parametros);
    }

    public List<Map<String, Object>> obtenerTransaccionesPorCuenta(
            String numeroCuenta,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) throws SQLException {

        StringBuilder sql = new StringBuilder(SQL_HISTORIAL_BASE);
        sql.append("WHERE (co.numero_cuenta = ? OR cd.numero_cuenta = ?) ");

        List<Object> parametros = new ArrayList<>();
        parametros.add(numeroCuenta);
        parametros.add(numeroCuenta);
        agregarFiltroFechas(sql, parametros, "m.fecha_hora", fechaInicio, fechaFin);
        sql.append("ORDER BY m.fecha_hora DESC, m.id_movimiento DESC");

        // El OR devuelve cada fila de movimiento una sola vez, incluso en transferencias.
        return ejecutarConsulta(sql.toString(), parametros);
    }

    public boolean existeCuenta(String numeroCuenta) throws SQLException {
        String sql = "SELECT 1 FROM cuentas_bancarias WHERE numero_cuenta = ? LIMIT 1";

        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numeroCuenta);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public BigDecimal obtenerTotalDepositado(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) throws SQLException {
        return obtenerTotalPorTipo("Deposito", fechaInicio, fechaFin);
    }

    public BigDecimal obtenerTotalRetirado(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) throws SQLException {
        return obtenerTotalPorTipo("Retiro", fechaInicio, fechaFin);
    }

    public BigDecimal obtenerTotalTransferido(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) throws SQLException {
        // Cada transferencia ocupa una sola fila: se suma una vez su monto.
        return obtenerTotalPorTipo("Transferencia", fechaInicio, fechaFin);
    }

    private BigDecimal obtenerTotalPorTipo(
            String tipoMovimiento,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT COALESCE(SUM(m.monto), 0.00) AS total "
                + "FROM movimientos m WHERE m.tipo_movimiento = ? ");

        List<Object> parametros = new ArrayList<>();
        parametros.add(tipoMovimiento);
        agregarFiltroFechas(sql, parametros, "m.fecha_hora", fechaInicio, fechaFin);

        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            asignarParametros(ps, parametros);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }

        return BigDecimal.ZERO;
    }

    private List<Map<String, Object>> ejecutarConsulta(
            String sql,
            List<?> parametros) throws SQLException {

        List<Map<String, Object>> resultados = new ArrayList<>();

        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            asignarParametros(ps, parametros);

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData metadata = rs.getMetaData();
                int columnas = metadata.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    for (int i = 1; i <= columnas; i++) {
                        fila.put(metadata.getColumnLabel(i), rs.getObject(i));
                    }
                    resultados.add(fila);
                }
            }
        }

        return resultados;
    }

    private void asignarParametros(PreparedStatement ps, List<?> parametros)
            throws SQLException {
        for (int i = 0; i < parametros.size(); i++) {
            Object parametro = parametros.get(i);
            int posicion = i + 1;

            if (parametro instanceof String) {
                ps.setString(posicion, (String) parametro);
            } else if (parametro instanceof Timestamp) {
                ps.setTimestamp(posicion, (Timestamp) parametro);
            } else if (parametro == null) {
                ps.setObject(posicion, null);
            } else {
                ps.setObject(posicion, parametro);
            }
        }
    }

    private void agregarFiltroFechas(
            StringBuilder sql,
            List<Object> parametros,
            String columnaFecha,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        if (fechaInicio != null) {
            sql.append("AND ").append(columnaFecha).append(" >= ? ");
            parametros.add(Timestamp.valueOf(fechaInicio));
        }

        if (fechaFin != null) {
            sql.append("AND ").append(columnaFecha).append(" <= ? ");
            parametros.add(Timestamp.valueOf(fechaFin));
        }
    }
}
