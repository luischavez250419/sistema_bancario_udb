package sistema;

import servicios.DepositosRetirosServicio;
import excepciones.TransaccionExcepcion;
import java.math.BigDecimal;

public class PruebaValidaciones {
 
    public static void main(String[] args) {

        DepositosRetirosServicio servicio =
                new DepositosRetirosServicio();

        String numeroCuenta = "7123681428";

        // PRUEBA 1: DEPÓSITO
        probarOperacion(
            "Depósito válido",
            () -> servicio.realizarDeposito(
                numeroCuenta,
                new BigDecimal("50.00")
            )
        );

        // PRUEBA 2: DEPÓSITO NEGATIVO
        probarOperacion(
            "Depósito negativo",
            () -> servicio.realizarDeposito(
                numeroCuenta,
                new BigDecimal("-50.00")
            )
        );

        // PRUEBA 3: RETIRO
        probarOperacion(
            "Retiro válido",
            () -> servicio.realizarRetiro(
                numeroCuenta,
                new BigDecimal("20.00")
            )
        );

        // PRUEBA 4: RETIRO CON MONTO EXCESIVO
        probarOperacion(
            "Retiro con saldo insuficiente",
            () -> servicio.realizarRetiro(
                numeroCuenta,
                new BigDecimal("999999.00")
            )
        );

        // PRUEBA 5: CUENTA INEXISTENTE
        probarOperacion(
            "Cuenta inexistente",
            () -> servicio.realizarDeposito(
                "9999999999",
                new BigDecimal("50.00")
            )
        );
        
        probarOperacion("Monto cero", () ->
            servicio.realizarDeposito(
                numeroCuenta, new BigDecimal("0.00")
            )
        );

        probarOperacion("Monto con tres decimales significativos", () ->
            servicio.realizarDeposito(
                numeroCuenta, new BigDecimal("10.555")
            )
        );

        probarOperacion("Monto con ceros decimales adicionales", () ->
            servicio.realizarDeposito(
                numeroCuenta, new BigDecimal("10.500")
            )
        );

        probarOperacion("Monto máximo permitido", () ->
            servicio.realizarDeposito(
                numeroCuenta, new BigDecimal("9999999999.99")
            )
        );

        probarOperacion("Monto superior al máximo permitido", () ->
            servicio.realizarDeposito(
                numeroCuenta, new BigDecimal("10000000000.00")
            )
        );
        
        probarOperacion("Depósito que supera el saldo máximo", () ->
    servicio.realizarDeposito(
        numeroCuenta,
        new BigDecimal("9999999999.99")
    )
    );
    }

    private static void probarOperacion(
        String nombre,
        Operacion operacion) {

    System.out.println("\nPRUEBA: " + nombre);

    try {

        operacion.ejecutar();

        System.out.println("Resultado: Validaciones superadas.");

    } catch (TransaccionExcepcion e) {

        System.out.println("Resultado: Operación rechazada por validación.");
        System.out.println("Motivo: " + e.getMessage());

    } catch (Exception e) {

        System.out.println("Resultado: Error técnico o inesperado.");
        System.out.println("Motivo: " + e.getMessage());
    }
}

    @FunctionalInterface
    interface Operacion {
        void ejecutar() throws Exception;
    }
}
