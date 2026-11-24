# Sistema Bancario UDB - Fase 2 (base de integración)

## Contenido integrado

- Base de Fase 1: entidades, DAO y ventanas originales.
- Daniel: entidad Movimiento, excepción compartida, configuración Log4J y esquema SQL de Fase 2.
- Luis: DAO y servicio de depósitos/retiros.
- Samuel: DAO y servicio de transferencias tomados de su rama `Samuel`.
- Crystel: ventanas Swing y formularios de depósitos/retiros, transferencias, movimientos, reportes y menú.
- Cristian (Integrante 5): `ReportesDao.java` y `ReportesServicio.java`; además se conectó la ventana `VentReportes` con el servicio y se habilitó la carga del historial en `VentMovimientos`.

## Configuración local

El archivo `config_database/database.properties` está preparado para el entorno local observado durante la configuración:

- MySQL: `localhost`
- Puerto: `3306`
- Base de datos: `sistema_bancario_udb`
- Usuario: `root`
- Contraseña: vacía

Si tu instalación de WampServer tiene otra contraseña, cambia `db.password`.
No compartas ni subas este archivo a GitHub. Está incluido en `.gitignore`.

## Base de datos

1. Haz un respaldo de la base `sistema_bancario_udb` desde phpMyAdmin.
2. Selecciona la base `sistema_bancario_udb` en phpMyAdmin.
3. Abre la pestaña **Importar**.
4. Selecciona `database/sistema_bancario.sql` y ejecuta la importación.
5. Verifica que aparezcan `clientes`, `cuentas_bancarias` y `movimientos`.

El script utiliza `CREATE TABLE IF NOT EXISTS`; no elimina ni vacía los registros existentes de clientes y cuentas. La tabla `movimientos` se crea si aún no existe. Si ya existe una tabla con estructura distinta, no se altera automáticamente: debe revisarse antes de migrarla.

## Abrir en NetBeans

1. Extrae el ZIP completo.
2. En NetBeans selecciona **File > Open Project**.
3. Abre la carpeta `SistemaBancarioUDB_Fase2_Integracion`.
4. Ejecuta `sistema.Main`.

El proyecto está configurado para compilar con Java 21 o superior. Se comprobó la compilación con Ant (`ant clean jar`) usando JDK 21.

## Alcance de la verificación

- Compilación Java/Ant: correcta.
- Integración de las clases de reportes y ventanas: compilada.
- Ejecución real contra MySQL/WampServer: pendiente de realizar en el equipo local.
- Pruebas funcionales de depósitos, retiros, transferencias y reportes: pendientes de ejecutar con la base importada.

## Observación de integración

La ventana de transferencia y la ventana de depósitos/retiros se conservaron desde la entrega de Crystel. La integración visual no sustituye la comprobación funcional de sus botones. Deben probarse contra los servicios correspondientes.
