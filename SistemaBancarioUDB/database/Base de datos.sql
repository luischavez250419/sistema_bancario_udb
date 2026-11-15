-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 07-11-2026 a las 10:39:07
-- Versión del servidor: 8.4.7
-- Versión de PHP: 8.3.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `sistema_bancario_udb`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `clientes`
--

DROP TABLE IF EXISTS `clientes`;
CREATE TABLE IF NOT EXISTS `clientes` (
  `id_cliente` int NOT NULL AUTO_INCREMENT,
  `dui` varchar(10) NOT NULL,
  `nombre_completo` varchar(100) NOT NULL,
  `telefono` varchar(15) NOT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `direccion` varchar(200) NOT NULL,
  `estado` enum('Activo','Inactivo','Bloqueado') NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `dui` (`dui`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `clientes`
--

INSERT INTO `clientes` (`id_cliente`, `dui`, `nombre_completo`, `telefono`, `correo`, `direccion`, `estado`) VALUES
(1, '12345678-9', 'Carlos Alberto Pérez', '71234567', 'carlos.perez.prueba@example.com', 'Santa Tecla', 'Activo'),
(3, '06566014-0', 'alexander diaz', '61126862', 'alexander1@ejemplo.com', 'Mejicanos', 'Activo'),
(8, '98765432-1', 'LaurA Villa', '61111234', 'lautara1@gmail.com', 'San Martin', 'Activo'),
(10, '25252525-2', 'Cliente Prueba', '71234567', 'clientel@ejemplo.com', 'San Salvador', 'Inactivo'),
(11, '23232323-3', 'jeferson diaz', '71552959', NULL, 'San Salvador', 'Bloqueado'),
(12, '10101010-1', 'gaston edul', '22222222', NULL, 'argentina', 'Activo');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cuentas_bancarias`
--

DROP TABLE IF EXISTS `cuentas_bancarias`;
CREATE TABLE IF NOT EXISTS `cuentas_bancarias` (
  `id_cuenta` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int NOT NULL,
  `numero_cuenta` varchar(10) NOT NULL,
  `tipo_cuenta` enum('Ahorro','Corriente') NOT NULL,
  `saldo` decimal(12,2) NOT NULL DEFAULT '0.00',
  `estado` enum('Activa','Inactiva','Bloqueada') NOT NULL DEFAULT 'Activa',
  `fechaHoraApertura` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_cuenta`),
  UNIQUE KEY `numero_cuenta` (`numero_cuenta`),
  KEY `fk_cuenta_cliente` (`id_cliente`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `cuentas_bancarias`
--

INSERT INTO `cuentas_bancarias` (`id_cuenta`, `id_cliente`, `numero_cuenta`, `tipo_cuenta`, `saldo`, `estado`, `fechaHoraApertura`) VALUES
(1, 1, '7123681428', 'Ahorro', 200.00, 'Activa', '2026-11-06 17:56:45'),
(3, 8, '1170743022', 'Ahorro', 20.00, 'Inactiva', '2026-11-06 18:34:43'),
(4, 8, '2135868682', 'Corriente', 25.25, 'Activa', '2026-11-06 18:37:35'),
(5, 1, '7138915324', 'Corriente', 3000.00, 'Activa', '2026-11-06 18:40:53'),
(6, 3, '6330860292', 'Ahorro', 1000.00, 'Activa', '2026-11-06 18:44:38'),
(7, 3, '1609250370', 'Ahorro', 300.02, 'Bloqueada', '2026-11-06 18:45:16'),
(8, 11, '6662430734', 'Ahorro', 6000.10, 'Activa', '2026-11-06 19:41:06');

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `cuentas_bancarias`
--
ALTER TABLE `cuentas_bancarias`
  ADD CONSTRAINT `fk_cuenta_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`) ON DELETE RESTRICT ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
