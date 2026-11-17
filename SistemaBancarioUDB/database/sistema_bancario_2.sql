-- =============================================================
-- SISTEMA BANCARIO UDB - FASE 2
-- Actualización de la base de datos de Fase 1.
-- Conserva las tablas clientes y cuentas_bancarias.
-- Compatible con MySQL 8.x y MariaDB 10.4+.
-- =============================================================

CREATE DATABASE IF NOT EXISTS sistema_bancario_udb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE sistema_bancario_udb;

-- FASE 1: clientes
CREATE TABLE IF NOT EXISTS clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    dui VARCHAR(10) NOT NULL UNIQUE,
    nombre_completo VARCHAR(100) NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    correo VARCHAR(100) UNIQUE,
    direccion VARCHAR(200) NOT NULL,
    estado ENUM('Activo', 'Inactivo', 'Bloqueado')
        NOT NULL DEFAULT 'Activo'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- FASE 1: cuentas bancarias
CREATE TABLE IF NOT EXISTS cuentas_bancarias (
    id_cuenta INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    numero_cuenta VARCHAR(10) NOT NULL UNIQUE,
    tipo_cuenta ENUM('Ahorro', 'Corriente') NOT NULL,
    saldo DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    estado ENUM('Activa', 'Inactiva', 'Bloqueada')
        NOT NULL DEFAULT 'Activa',
    fechaHoraApertura DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_cuenta_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- FASE 2: historial de depósitos, retiros y transferencias.
-- Una transferencia se almacena en una sola fila.
CREATE TABLE IF NOT EXISTS movimientos (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    tipo_movimiento ENUM('Deposito', 'Retiro', 'Transferencia') NOT NULL,
    id_cuenta_origen INT NULL,
    id_cuenta_destino INT NULL,
    monto DECIMAL(12,2) NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion VARCHAR(255) NOT NULL,

    INDEX idx_movimientos_cuenta_origen (id_cuenta_origen),
    INDEX idx_movimientos_cuenta_destino (id_cuenta_destino),
    INDEX idx_movimientos_fecha (fecha_hora),

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
            (tipo_movimiento = 'Transferencia'
                AND id_cuenta_origen IS NOT NULL
                AND id_cuenta_destino IS NOT NULL
                AND id_cuenta_destino <> id_cuenta_origen
            ) 
        OR
            (tipo_movimiento = 'Deposito'
                AND id_cuenta_origen IS NULL
                AND id_cuenta_destino IS NOT NULL
            )
        OR 
            (tipo_movimiento = 'Retiro'
                AND id_cuenta_origen IS NOT NULL
                AND id_cuenta_destino IS NULL
            )
            
        ) 

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Verificación de la estructura creada (no inserta datos de prueba).
SHOW TABLES;
DESCRIBE movimientos;
