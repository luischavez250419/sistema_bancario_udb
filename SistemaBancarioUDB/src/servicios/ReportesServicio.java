package servicios;

import dao.ReportesDao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Capa de servicio para validar parámetros y coordinar los reportes.
 * No ejecuta SQL directamente; delega las consultas a ReportesDao.
 */
public class ReportesServicio {

    private static final Logger logger = LogManager.getLogger(ReportesServicio.class);

    private final ReportesDao dao;

    public ReportesServicio() {
        this.dao = new ReportesDao();
    }

    // Constructor útil para pruebas y para sustituir el DAO si se requiere.
    public ReportesServicio(ReportesDao dao) {
        if (dao == null) {
            throw new IllegalArgumentException("El DAO de reportes no puede ser nulo.");
        }
        this.dao = dao;
    }

    public List<Map<String, Object>> obtenerCuentasActivas() throws SQLException {
        return ejecutarReporte("cuentas activas", () -> dao.obtenerCuentasActivas());
    }

    public List<Map<String, Object>> obtenerCuentasBloqueadas() throws SQLException {
        return ejecutarReporte("cuentas bloqueadas", () -> dao.obtenerCuentasBloqueadas());
    }

    public List<Map<String, Object>> obtenerClientesRegistrados() throws SQLException {
        return ejecutarReporte("clientes registrados", () -> dao.obtenerClientesRegistrados());
    }

    public List<Map<String, Object>> obtenerHistorialTransacciones() throws SQLException {
        return obtenerHistorialTransacciones(null, null);
    }

    public List<Map<String, Object>> obtenerHistorialTransacciones(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) throws SQLException {
        validarRangoFechas(fechaInicio, fechaFin);
        return ejecutarReporte("historial de transacciones",
                () -> dao.obtenerHistorialTransacciones(fechaInicio, fechaFin));
    }

    public List<Map<String, Object>> obtenerTransaccionesPorCuenta(String numeroCuenta)
            throws SQLException {
        return obtenerTransaccionesPorCuenta(numeroCuenta, null, null);
    }

    public List<Map<String, Object>> obtenerTransaccionesPorCuenta(
            String numeroCuenta,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) throws SQLException {

        String cuentaValidada = validarNumeroCuenta(numeroCuenta);
        validarRangoFechas(fechaInicio, fechaFin);

        boolean existe = ejecutarReporte("validación de cuenta para consulta de movimientos",
                () -> dao.existeCuenta(cuentaValidada));
        if (!existe) {
            logger.warn("Se solicitó consultar movimientos de una cuenta inexistente: {}", cuentaValidada);
            throw new IllegalArgumentException("La cuenta " + cuentaValidada + " no existe.");
        }

        return ejecutarReporte("transacciones de la cuenta " + cuentaValidada,
                () -> dao.obtenerTransaccionesPorCuenta(cuentaValidada, fechaInicio, fechaFin));
    }

    public BigDecimal obtenerTotalDepositado() throws SQLException {
        return obtenerTotalDepositado(null, null);
    }

    public BigDecimal obtenerTotalDepositado(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) throws SQLException {
        validarRangoFechas(fechaInicio, fechaFin);
        return ejecutarReporte("total depositado",
                () -> dao.obtenerTotalDepositado(fechaInicio, fechaFin));
    }

    public BigDecimal obtenerTotalRetirado() throws SQLException {
        return obtenerTotalRetirado(null, null);
    }

    public BigDecimal obtenerTotalRetirado(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) throws SQLException {
        validarRangoFechas(fechaInicio, fechaFin);
        return ejecutarReporte("total retirado",
                () -> dao.obtenerTotalRetirado(fechaInicio, fechaFin));
    }

    public BigDecimal obtenerTotalTransferido() throws SQLException {
        return obtenerTotalTransferido(null, null);
    }

    public BigDecimal obtenerTotalTransferido(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) throws SQLException {
        validarRangoFechas(fechaInicio, fechaFin);
        return ejecutarReporte("total transferido",
                () -> dao.obtenerTotalTransferido(fechaInicio, fechaFin));
    }

    private String validarNumeroCuenta(String numeroCuenta) {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("Debes ingresar un número de cuenta.");
        }
        return numeroCuenta.trim();
    }

    private void validarRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser posterior a la fecha final.");
        }
    }

    private <T> T ejecutarReporte(String nombre, Consulta<T> consulta) throws SQLException {
        try {
            T resultado = consulta.ejecutar();
            logger.info("Reporte generado correctamente: {}", nombre);
            return resultado;
        } catch (SQLException e) {
            logger.error("Error de base de datos al generar el reporte: {}", nombre, e);
            throw e;
        }
    }

    @FunctionalInterface
    private interface Consulta<T> {
        T ejecutar() throws SQLException;
    }
}
