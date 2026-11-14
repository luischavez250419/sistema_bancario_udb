package servicios;

import dao.DepositosRetirosDao;
import sistema.Cuenta_bancaria;
import excepciones.TransaccionExcepcion;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DepositosRetirosServicio {
    
    private static final Logger logger = LogManager.getLogger(DepositosRetirosServicio.class);
    
    private static final BigDecimal SALDO_MAXIMO = new BigDecimal("9999999999.99");
    
    private final DepositosRetirosDao dao;
    
    public DepositosRetirosServicio(){
        this.dao = new DepositosRetirosDao();
    }
    
    // VALIDAR CUENTA BANCARIA
    //===========================
    
    private Cuenta_bancaria validarCuenta(String numeroCuenta)
            throws TransaccionExcepcion, SQLException {
        
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new TransaccionExcepcion ("Debes ingresar un número de cuenta existente. ");
        }
        
       Cuenta_bancaria cuenta = dao.buscarCuenta(numeroCuenta.trim());
        
        if (cuenta == null) {
            throw new TransaccionExcepcion("La cuenta bancaria no existe");
        }
        
        if (!"Activa".equalsIgnoreCase(cuenta.getEstado())){
            throw new TransaccionExcepcion("La cuenta no está activa. No se permiten operaciones.");
        }
        
        return cuenta;
        
        } 
    
    // VALIDAR MONTO DE OPERACION
    //==============================
    
    private void validarMonto(BigDecimal monto) throws TransaccionExcepcion{
        
        if (monto == null) {
            throw new TransaccionExcepcion("Debes ingresar un monto mayor que cero.");
        }
        
        if (monto.compareTo(BigDecimal.ZERO)<= 0) {
            throw new TransaccionExcepcion("El monto debe ser mayor que cero.");
        }
        
        if (monto.stripTrailingZeros().scale() > 2){
            throw new TransaccionExcepcion("El monto no puede tener más de dos decimales.");
        }
        
        BigDecimal montoMaximo = new BigDecimal("9999999999.99");
        
        if (monto.compareTo(montoMaximo) > 0){
            throw new TransaccionExcepcion("El monto indicado supera el límite permitido para operaciones.");
        }
    }
    
    
    // VALIDAR SALDO RESULTANTE
    //==============================

    private void validarSaldo(BigDecimal saldo) throws TransaccionExcepcion {

        if (saldo == null) {
            throw new TransaccionExcepcion("No fue posible calcular el saldo de la cuenta.");
        }

        if (saldo.compareTo(SALDO_MAXIMO) > 0) {
            throw new TransaccionExcepcion(
                "La operación supera el saldo máximo permitido para la cuenta."
            );
        }
    }

    // REALIZAR DEPOSITO
    // ====================
    
    public void realizarDeposito(String numeroCuenta, BigDecimal monto)
            throws Exception{
        
        logger.info("Solicitud de depósito recibida.");
        
        try{
        
        // 1. Validar cuenta
        Cuenta_bancaria cuenta = validarCuenta(numeroCuenta);
        
        // 2. Validar monto
        validarMonto(monto);
        
        // 3. Calcular el nuevo saldo
        BigDecimal nuevoSaldo = cuenta.getSaldo().add(monto);
        
        //3.5. Validar el nuevo saldo
        validarSaldo(nuevoSaldo);
        
        logger.info("Validaciones de depósito superadas correctamente para la cuenta {}. "
        + "Monto: {}. Saldo calculado: {}", numeroCuenta, monto, nuevoSaldo
        );
        
        // 4. La operacion SQL se hace en el DAO correspondiente
        // Pendiente: actualizar saldo y registrar movimiento
    } catch (TransaccionExcepcion e){
        
        // Rechazo por reglas de negocio
        logger.warn(
        "Depósito rechazado para la cuenta {}. Motivo: {}", numeroCuenta, e.getMessage());
        
        throw e;
    
    } catch (SQLException e){
        // Error de database
        logger.error("Error de base de datos al procesar el depósito para la cuenta {}. ", numeroCuenta, e);
        
        throw new Exception("No fue posible procesar el depósito por un error de la base de datos.", e);
    
    } catch (Exception e) {
        // Error inesperado
        logger.error("Error inesperado al procesar el depósito para la cuenta {}.", numeroCuenta, e);
        
        throw e;
    } 
}
    
    // REALIZAR RETIRO
    //====================
    
    public void realizarRetiro(String numeroCuenta, BigDecimal monto)
            throws Exception{
        
        logger.info("Solicitud de retiro recibida.");
        
        try {
        
        // 1. Validar cuenta
        Cuenta_bancaria cuenta = validarCuenta(numeroCuenta);
        
        // 2. Validar monto
        validarMonto(monto);
        
        // 3. Comprobacion de saldo suficiente
        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new TransaccionExcepcion("La cuenta no tiene saldo suficiente para realizar el retiro solicitado.");
        }
        
        // 4. Calcular el nuevo saldo
        BigDecimal nuevoSaldo = cuenta.getSaldo().subtract(monto);
        
        logger.info(
        "Validaciones de retiro superadas para la cuenta {}. "
        + "Monto: {}. Saldo calculado: {}", numeroCuenta, monto, nuevoSaldo);
        
        // 5. La operacion SQL se hace en el DAO correspondiente
        // Pendiente actualizar saldo y registrar movimiento
    } catch (TransaccionExcepcion e) {
        
        // Rechazo por reglas de negocio
        logger.warn(
        "Retiro rechazado para la cuenta {}. Motivo: {}", numeroCuenta, e.getMessage());
        
        throw e;
        
    } catch (SQLException e) {
        
        //Error de base de datos
        logger.error(
        "Error de base de datos al procesar el retiro para la cuenta {}.", numeroCuenta, e);
        
        throw new Exception("No fue posible procesar el retiro por un error de base de datos.", e);
    
    } catch (Exception e) {
        
        //Error inesperado
        logger.error("Error inesperado al procesar el retiro para la cuenta {}.", numeroCuenta,e);
        
        throw e;
        }
    }
}

