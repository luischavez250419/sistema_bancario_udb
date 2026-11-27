package servicios;

import dao.TransferenciasDao;
import excepciones.TransaccionExcepcion;
import sistema.Cuenta_bancaria;
import sistema.Movimiento;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransferenciasServicio {

    private static final Logger logger = LogManager.getLogger(TransferenciasServicio.class);

    private static final BigDecimal SALDO_MAXIMO = new BigDecimal("9999999999.99");

    private final TransferenciasDao dao;

    public TransferenciasServicio() {
        this.dao = new TransferenciasDao();
    }

    // OBTENER NOMBRE DEL PROPIETARIO DE UNA CUENTA
// ==============================================

public String obtenerNombrePropietario(String numeroCuenta)
        throws SQLException {

    if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
        return null;
    }

    Cuenta_bancaria cuenta = dao.buscarCuenta(numeroCuenta.trim());

    if (cuenta == null || cuenta.getCliente() == null) {
        return null;
    }

    return cuenta.getCliente().getNombreCompleto();
}
    
    private Cuenta_bancaria validarCuenta(String numeroCuenta, String tipoCuenta)
            throws TransaccionExcepcion, SQLException {

        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new TransaccionExcepcion("Debes ingresar un número de cuenta de " + tipoCuenta + " válido.");
        }

        Cuenta_bancaria cuenta = dao.buscarCuenta(numeroCuenta.trim());

        if (cuenta == null) {
            throw new TransaccionExcepcion("La cuenta de " + tipoCuenta + " no existe.");
        }

        if (!"Activa".equalsIgnoreCase(cuenta.getEstado())) {
            throw new TransaccionExcepcion("La cuenta de " + tipoCuenta + " no está activa. No se permiten operaciones.");
        }

        return cuenta;
    }


    private void validarMonto(BigDecimal monto) throws TransaccionExcepcion {

        if (monto == null) {
            throw new TransaccionExcepcion("Debes ingresar un monto mayor que cero.");
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransaccionExcepcion("El monto debe ser mayor que cero.");
        }

        if (monto.stripTrailingZeros().scale() > 2) {
            throw new TransaccionExcepcion("El monto no puede tener más de dos decimales.");
        }

        if (monto.compareTo(SALDO_MAXIMO) > 0) {
            throw new TransaccionExcepcion("El monto indicado supera el límite permitido para operaciones.");
        }
    }

  
    private void validarSaldo(BigDecimal saldo) throws TransaccionExcepcion {

        if (saldo == null) {
            throw new TransaccionExcepcion("No fue posible calcular el saldo de la cuenta.");
        }

        if (saldo.compareTo(SALDO_MAXIMO) > 0) {
            throw new TransaccionExcepcion(
                    "La operación supera el saldo máximo permitido para la cuenta de destino."
            );
        }
    }

    public Movimiento realizarTransferencia(
            String numeroCuentaOrigen,
            String numeroCuentaDestino,
            BigDecimal monto,
            String descripcion) throws Exception {

        logger.info("Solicitud de transferencia recibida. Origen: {}, Destino: {}, Monto: {}",
                numeroCuentaOrigen, numeroCuentaDestino, monto);

        try {
  
            Cuenta_bancaria cuentaOrigen = validarCuenta(numeroCuentaOrigen, "origen");
            Cuenta_bancaria cuentaDestino = validarCuenta(numeroCuentaDestino, "destino");

         
            if (cuentaOrigen.getNumeroCuenta().trim().equals(cuentaDestino.getNumeroCuenta().trim())) {
                throw new TransaccionExcepcion("La cuenta de origen y la cuenta de destino no pueden ser la misma.");
            }

            
            validarMonto(monto);

            if (cuentaOrigen.getSaldo().compareTo(monto) < 0) {
                throw new TransaccionExcepcion("La cuenta de origen no tiene saldo suficiente para realizar la transferencia.");
            }

     
            BigDecimal nuevoSaldoOrigen = cuentaOrigen.getSaldo().subtract(monto);
            BigDecimal nuevoSaldoDestino = cuentaDestino.getSaldo().add(monto);
            validarSaldo(nuevoSaldoDestino);

           
            Movimiento movimiento = dao.realizarTransferencia(
                    cuentaOrigen.getNumeroCuenta().trim(),
                    cuentaDestino.getNumeroCuenta().trim(),
                    monto,
                    descripcion
            );

            logger.info("Transferencia realizada correctamente. Origen: {} (Nuevo saldo: {}), "
                    + "Destino: {} (Nuevo saldo: {}), Monto transferido: {}",
                    cuentaOrigen.getNumeroCuenta(), nuevoSaldoOrigen,
                    cuentaDestino.getNumeroCuenta(), nuevoSaldoDestino, monto);

            return movimiento;
            
        } catch (TransaccionExcepcion e) {
          
            logger.warn("Transferencia rechazada de {} hacia {}. Motivo: {}",
                    numeroCuentaOrigen, numeroCuentaDestino, e.getMessage());
            throw e;

        } catch (SQLException e) {
            
            logger.error("Error de base de datos al procesar la transferencia de {} hacia {}.",
                    numeroCuentaOrigen, numeroCuentaDestino, e);
            throw new Exception("No fue posible procesar la transferencia por un error de la base de datos.", e);

        } catch (Exception e) {
            // Error inesperado
            logger.error("Error inesperado al procesar la transferencia de {} hacia {}.",
                    numeroCuentaOrigen, numeroCuentaDestino, e);
            throw e;
        }
    }

    
    public Movimiento realizarTransferencia(
            String numeroCuentaOrigen,
            String numeroCuentaDestino,
            BigDecimal monto) throws Exception {
        
         return realizarTransferencia(
            numeroCuentaOrigen,
            numeroCuentaDestino,
            monto,
            "Transferencia entre cuentas");
    }
                 
        
}