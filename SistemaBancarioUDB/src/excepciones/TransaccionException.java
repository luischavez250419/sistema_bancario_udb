package excepciones;

/** Excepción para errores de validación o ejecución de transacciones bancarias. */
public class TransaccionException extends Exception {

    public TransaccionException(String mensaje) {
        super(mensaje);
    }

    public TransaccionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
