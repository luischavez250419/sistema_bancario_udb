package config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Ejecutar esta clase para comprobar que Log4j escribe en el archivo de logs. */
public class PruebaLog4j {

    private static final Logger logger = LogManager.getLogger(PruebaLog4j.class);

    public static void main(String[] args) {
        logger.info("Prueba de Log4j iniciada correctamente.");
        logger.warn("Este es un evento de advertencia de prueba.");
        logger.error("Este es un evento de error de prueba.");
        logger.info("Prueba de Log4j finalizada.");
    }
}
