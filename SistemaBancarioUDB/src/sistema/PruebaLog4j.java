
package sistema;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PruebaLog4j {

    private static final Logger logger =
            LogManager.getLogger(PruebaLog4j.class);

    public static void main(String[] args) {

        logger.info("Prueba de registro INFO: sistema bancario iniciado.");

        logger.warn("Prueba de registro WARN: operación de prueba.");

        logger.error("Prueba de registro ERROR: error simulado.");

        System.out.println("Prueba de Log4J finalizada.");
    }
}
