-- =============================================================
-- SISTEMA BANCARIO UDB - SCRIPT MAESTRO UNIFICADO
-- Motor: MySQL 8.4 LTS
--
-- Integra:
--   1. Estructura de Fase 1: clientes y cuentas_bancarias.
--   2. Estructura de Fase 2: movimientos y sus restricciones.
--   3. Datos existentes exportados de las tres tablas.
--
-- IMPORTANTE:
--   Este script está preparado para una BASE DE DATOS NUEVA/VACÍA.
--   No elimina tablas ni datos existentes.
--   Si las tablas ya existen, CREATE TABLE IF NOT EXISTS no modifica
--   su estructura y los INSERT pueden producir claves duplicadas.
--   Para una base existente, realiza primero un respaldo y restaura
--   los datos mediante un procedimiento controlado.
-- =============================================================

CREATE DATABASE IF NOT EXISTS sistema_bancario_udb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE sistema_bancario_udb;

-- =============================================================
-- FASE 1: TABLA CLIENTES
-- =============================================================

CREATE TABLE IF NOT EXISTS clientes (
    id_cliente INT NOT NULL AUTO_INCREMENT,
    dui VARCHAR(10) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    correo VARCHAR(100) DEFAULT NULL,
    direccion VARCHAR(200) NOT NULL,
    estado ENUM('Activo', 'Inactivo', 'Bloqueado')
        NOT NULL DEFAULT 'Activo',

    PRIMARY KEY (id_cliente),
    UNIQUE KEY uq_clientes_dui (dui),
    UNIQUE KEY uq_clientes_correo (correo)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- =============================================================
-- FASE 1: TABLA CUENTAS_BANCARIAS
-- =============================================================

CREATE TABLE IF NOT EXISTS cuentas_bancarias (
    id_cuenta INT NOT NULL AUTO_INCREMENT,
    id_cliente INT NOT NULL,
    numero_cuenta VARCHAR(10) NOT NULL,
    tipo_cuenta ENUM('Ahorro', 'Corriente') NOT NULL,
    saldo DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    estado ENUM('Activa', 'Inactiva', 'Bloqueada')
        NOT NULL DEFAULT 'Activa',
    fechaHoraApertura DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_cuenta),
    UNIQUE KEY uq_cuentas_numero (numero_cuenta),
    KEY idx_cuentas_cliente (id_cliente),

    CONSTRAINT fk_cuenta_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- =============================================================
-- FASE 2: TABLA MOVIMIENTOS
--
-- Depósito:      origen NULL, destino obligatorio.
-- Retiro:        origen obligatorio, destino NULL.
-- Transferencia: origen y destino obligatorios y diferentes.
-- =============================================================

CREATE TABLE IF NOT EXISTS movimientos (
    id_movimiento INT NOT NULL AUTO_INCREMENT,
    tipo_movimiento ENUM('Deposito', 'Retiro', 'Transferencia') NOT NULL,
    id_cuenta_origen INT DEFAULT NULL,
    id_cuenta_destino INT DEFAULT NULL,
    monto DECIMAL(12,2) NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion VARCHAR(255) NOT NULL,

    PRIMARY KEY (id_movimiento),
    KEY idx_movimientos_cuenta_origen (id_cuenta_origen),
    KEY idx_movimientos_cuenta_destino (id_cuenta_destino),
    KEY idx_movimientos_fecha (fecha_hora),

    CONSTRAINT fk_movimiento_cuenta_origen
        FOREIGN KEY (id_cuenta_origen)
        REFERENCES cuentas_bancarias(id_cuenta)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT fk_movimiento_cuenta_destino
        FOREIGN KEY (id_cuenta_destino)
        REFERENCES cuentas_bancarias(id_cuenta)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,

    CONSTRAINT chk_movimiento_monto_positivo
        CHECK (monto > 0),

    CONSTRAINT chk_movimiento_cuentas
        CHECK (
            (
                tipo_movimiento = 'Deposito'
                AND id_cuenta_origen IS NULL
                AND id_cuenta_destino IS NOT NULL
            )
            OR
            (
                tipo_movimiento = 'Retiro'
                AND id_cuenta_origen IS NOT NULL
                AND id_cuenta_destino IS NULL
            )
            OR
            (
                tipo_movimiento = 'Transferencia'
                AND id_cuenta_origen IS NOT NULL
                AND id_cuenta_destino IS NOT NULL
                AND id_cuenta_destino <> id_cuenta_origen
            )
        )
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- =============================================================
-- DATOS INICIALES
-- Orden de inserción: clientes, cuentas_bancarias, movimientos.
-- Se conservan los identificadores y valores del respaldo.
-- =============================================================

START TRANSACTION;

-- -------------------------------------------------------------
-- Datos de CLIENTES (6 registros)
-- -------------------------------------------------------------

INSERT INTO clientes
    (id_cliente, dui, nombre_completo, telefono, correo, direccion, estado)
VALUES
    (1, '12345678-9', 'Carlos Alberto Pérez', '71234567',
     'carlos.perez.prueba@example.com', 'Santa Tecla', 'Activo'),
    (3, '06566014-0', 'alexander diaz', '61126862',
     'alexander1@ejemplo.com', 'Mejicanos', 'Activo'),
    (8, '98765432-1', 'LaurA Villa', '61111234',
     'lautara1@gmail.com', 'San Martin', 'Activo'),
    (10, '25252525-2', 'Cliente Prueba', '71234567',
     'clientel@ejemplo.com', 'San Salvador', 'Inactivo'),
    (11, '23232323-3', 'jeferson diaz', '71552959',
     NULL, 'San Salvador', 'Bloqueado'),
    (12, '10101010-1', 'gaston edul', '22222222',
     NULL, 'argentina', 'Activo');

-- -------------------------------------------------------------
-- Datos de CUENTAS_BANCARIAS (7 registros)
-- -------------------------------------------------------------

INSERT INTO cuentas_bancarias
    (id_cuenta, id_cliente, numero_cuenta, tipo_cuenta, saldo, estado, fechaHoraApertura)
VALUES
    (1, 1, '7123681428', 'Ahorro', 100.00, 'Activa', '2026-11-06 17:56:45'),
    (3, 8, '1170743022', 'Ahorro', 20.00, 'Inactiva', '2026-11-06 18:34:43'),
    (4, 8, '2135868682', 'Corriente', 200.00, 'Activa', '2026-11-06 18:37:35'),
    (5, 1, '7138915324', 'Corriente', 3500.00, 'Activa', '2026-11-06 18:40:53'),
    (6, 3, '6330860292', 'Ahorro', 1000.00, 'Activa', '2026-11-06 18:44:38'),
    (7, 3, '1609250370', 'Ahorro', 300.02, 'Bloqueada', '2026-11-06 18:45:16'),
    (8, 11, '6662430734', 'Ahorro', 6000.10, 'Activa', '2026-11-06 19:41:06');

-- -------------------------------------------------------------
-- Datos de MOVIMIENTOS (12 registros)
-- -------------------------------------------------------------

INSERT INTO movimientos
    (id_movimiento, tipo_movimiento, id_cuenta_origen, id_cuenta_destino,
     monto, fecha_hora, descripcion)
VALUES
    (1, 'Deposito', NULL, 6, 50.00,
     '2026-11-19 04:45:28', 'Depósito bancario'),
    (2, 'Retiro', 6, NULL, 20.00,
     '2026-11-19 05:01:33', 'Retiro bancario'),
    (3, 'Transferencia', 6, 1, 20.00,
     '2026-11-19 05:52:51', 'Prueba de transferencia interna.'),
    (4, 'Deposito', NULL, 6, 50.00,
     '2026-11-20 04:05:09', 'Depósito bancario'),
    (5, 'Retiro', 6, NULL, 20.00,
     '2026-11-20 04:07:37', 'Retiro bancario'),
    (6, 'Transferencia', 6, 1, 20.00,
     '2026-11-20 04:08:55', 'Prueba'),
    (7, 'Deposito', NULL, 5, 100.00,
     '2026-11-20 05:31:44', 'Depósito bancario'),
    (8, 'Deposito', NULL, 5, 400.00,
     '2026-11-20 05:33:34', 'Depósito bancario'),
    (9, 'Transferencia', 1, 4, 40.00,
     '2026-11-20 05:35:51', 'Tranferencia Ej'),
    (10, 'Deposito', NULL, 4, 34.75,
     '2026-11-20 05:39:56', 'Depósito bancario'),
    (11, 'Retiro', 6, NULL, 20.00,
     '2026-11-20 05:41:51', 'Retiro bancario'),
    (12, 'Transferencia', 1, 4, 100.00,
     '2026-11-20 05:43:45', 'Ejemplo Transferencia');

COMMIT;

-- =============================================================
-- VERIFICACIONES
-- =============================================================

SHOW TABLES;

SELECT COUNT(*) AS total_clientes FROM clientes;
SELECT COUNT(*) AS total_cuentas FROM cuentas_bancarias;
SELECT COUNT(*) AS total_movimientos FROM movimientos;

SHOW CREATE TABLE movimientos;
