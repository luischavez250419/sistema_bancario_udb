# Política de registro de eventos - Sistema Bancario UDB

Todos los módulos deben utilizar Apache Log4j 2. No se deben utilizar `System.out.println`
ni crear archivos de log manualmente para registrar eventos de la aplicación.

- **INFO:** inicio/cierre de la aplicación y operaciones completadas (sin datos sensibles).
- **WARN:** validaciones rechazadas, intentos de operación con cuentas inactivas o saldo insuficiente.
- **ERROR:** fallos de acceso a datos, errores inesperados y transacciones fallidas. Incluir la excepción.
- **DEBUG:** solo durante desarrollo; desactivado por defecto.

Cada clase obtiene su logger con `LogManager.getLogger(NombreDeLaClase.class)`. La configuración
central `src/log4j2.xml` envía los eventos a consola y a `logs/sistema-bancario.log`, con rotación
por fecha y tamaño. No registrar contraseñas, números completos de tarjetas ni otros datos sensibles.
