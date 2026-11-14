package excepciones;

public class TransaccionExcepcion extends Exception {
    
    public TransaccionExcepcion(String mensaje) {
        super(mensaje);
    }

    public TransaccionExcepcion(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
