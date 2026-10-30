CREATE DATABASE IF NOT EXISTS sistema_bancario_udb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE sistema_bancario_udb;

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

CREATE TABLE IF NOT EXISTS cuentas_bancarias (
    id_cuenta INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
    tipo_cuenta ENUM('Ahorro', 'Corriente') NOT NULL,
    saldo DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    estado ENUM('Activa', 'Inactiva')
        NOT NULL DEFAULT 'Activa',
    fechaHoraApertura DATETIME NULL,
    cliente VARCHAR(100) NULL,
    CONSTRAINT fk_cuenta_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;