# Fase 2 – Avance del Integrante 1

## Archivos preparados
- `../database/sistema_bancario.sql`: conserva `clientes` y `cuentas_bancarias` y agrega `movimientos` con dos claves foráneas.
- `../src/sistema/Movimiento.java`: entidad Java con `BigDecimal`, `LocalDateTime`, `Integer` nullable y enum de tipos.
- `../src/excepciones/TransaccionException.java`: excepción personalizada.
- `../src/log4j2.xml`: configuración central de Log4j 2.
- `../src/config/PruebaLog4j.java`: clase para generar eventos INFO, WARN y ERROR.
- `../nbproject/project.properties`: referencias de Log4j y nivel de compilación Java 24.
- `Modelo_Relacional_Fase_2.pdf`: modelo relacional actualizado.
- `POLITICA_LOGS.md`: política común de registro.

## Pendiente antes de declarar la fase probada
1. Descargar `log4j-api-2.26.1.jar` y `log4j-core-2.26.1.jar` desde los enlaces indicados en `lib/LEEME_LOG4J.txt` y guardarlos en `lib/`.
2. Ejecutar el script SQL en un servidor MySQL/MariaDB y comprobar la tabla `movimientos`. No se ejecutó aquí porque el servicio MariaDB del equipo está pendiente de recuperación.
3. Ejecutar `config.PruebaLog4j` para generar `logs/sistema-bancario.log` real.
4. Confirmar con los compañeros que usarán los nombres de atributos y getters definidos en `Movimiento.java`.

No se eliminaron las tablas ni los datos de Fase 1 en el script actualizado.
