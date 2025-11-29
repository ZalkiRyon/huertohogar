-- ========================================================
-- CONFIGURACIÓN INICIAL
-- ========================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Reiniciar Base de Datos
DROP DATABASE IF EXISTS hh_db;
CREATE DATABASE hh_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hh_db;

-- ========================================================
-- TABLAS DE CATÁLOGO (Normalización)
-- ========================================================

-- Tabla de Roles
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_unique` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Categorías
DROP TABLE IF EXISTS `categorias`;
CREATE TABLE `categorias` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_unique` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Estados de Orden
DROP TABLE IF EXISTS `estados`;
CREATE TABLE `estados` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_unique` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================================
-- TABLAS PRINCIPALES
-- ========================================================

-- 2. Tabla de Usuarios
DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role_id` int(11) NOT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `apellido` varchar(100) DEFAULT NULL,
  `run` varchar(20) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `region` varchar(100) DEFAULT NULL,
  `comuna` varchar(100) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `comentario` text,
  `fecha_registro` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_unique` (`email`),
  UNIQUE KEY `run_unique` (`run`), -- Validación de unicidad para RUN
  KEY `fk_usuario_role` (`role_id`),
  CONSTRAINT `fk_usuario_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Tabla de Productos
DROP TABLE IF EXISTS `productos`;
CREATE TABLE `productos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `categoria_id` int(11) NOT NULL,
  `precio` int(11) NOT NULL DEFAULT 0,
  `stock` int(11) NOT NULL DEFAULT 0,
  `descripcion` text,
  `imagen` varchar(255) DEFAULT NULL,
  `activo` BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_producto_unique` (`nombre`), -- Validación de unicidad para Nombre Producto
  KEY `fk_producto_categoria` (`categoria_id`),
  CONSTRAINT `fk_producto_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Tabla de Órdenes (ESTRUCTURA HÍBRIDA / SNAPSHOT)
-- Esta tabla guarda referencia al usuario PERO TAMBIÉN copia sus datos
-- para mantener el historial si el usuario cambia o se borra.
DROP TABLE IF EXISTS `ordenes`;
CREATE TABLE `ordenes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `numero_orden` varchar(50) NOT NULL,
  
  -- REFERENCIA (Mutable): Puede ser NULL si se borra el usuario
  `cliente_id` int(11) NULL, 
  
  -- SNAPSHOT (Inmutable): Datos históricos del cliente al momento de comprar
  `nombre_cliente_snapshot` varchar(200) NOT NULL, 
  `email_cliente_snapshot` varchar(100) NOT NULL,
  
  -- DATOS DE DESPACHO (Son snapshots por naturaleza)
  `direccion_envio` varchar(255) NOT NULL,
  `region_envio` varchar(100) NOT NULL,
  `comuna_envio` varchar(100) NOT NULL,
  `telefono_contacto` varchar(20) DEFAULT NULL,
  
  `fecha` date DEFAULT NULL,
  `estado_id` int(11) NOT NULL,
  `monto_total` int(11) NOT NULL DEFAULT 0,
  `costo_envio` int(11) NOT NULL DEFAULT 0,
  `comentario` text,
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_orden_unique` (`numero_orden`),
  KEY `fk_orden_usuario` (`cliente_id`),
  KEY `fk_orden_estado` (`estado_id`),
  
  -- ON DELETE SET NULL: Si borras el usuario, el ID queda NULL, el historial queda intacto
  CONSTRAINT `fk_orden_usuario` FOREIGN KEY (`cliente_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_orden_estado` FOREIGN KEY (`estado_id`) REFERENCES `estados` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Tabla de Detalles (SNAPSHOT TOTAL DE PRODUCTO)
DROP TABLE IF EXISTS `detalles_orden`;
CREATE TABLE `detalles_orden` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orden_id` int(11) NOT NULL,
  
  -- REFERENCIA (Opcional): Para estadísticas
  `producto_id` int(11) NULL,
  
  -- SNAPSHOT (Obligatorio): Lo que realmente se vendió (nombre y precio congelados)
  `nombre_producto_snapshot` varchar(150) NOT NULL,
  `precio_unitario_snapshot` int(11) NOT NULL,
  
  `cantidad` int(11) NOT NULL DEFAULT 1,
  `subtotal` int(11) NOT NULL,
  
  PRIMARY KEY (`id`),
  KEY `fk_detalle_orden` (`orden_id`),
  KEY `fk_detalle_producto` (`producto_id`),
  
  CONSTRAINT `fk_detalle_orden` FOREIGN KEY (`orden_id`) REFERENCES `ordenes` (`id`) ON DELETE CASCADE,
  -- ON DELETE SET NULL: Si borras el producto, el historial de venta no se pierde
  CONSTRAINT `fk_detalle_producto` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================================
-- POBLADO DE DATOS
-- ========================================================

-- Insertar Roles
INSERT INTO `roles` (`id`, `nombre`) VALUES
(1, 'admin'), (2, 'cliente'), (3, 'vendedor');

-- Insertar Categorías
INSERT INTO `categorias` (`id`, `nombre`) VALUES
(1, 'Frutas frescas'), (2, 'Verduras organicas'), (3, 'Productos organicos'), (4, 'Productos lacteos');

-- Insertar Estados
INSERT INTO `estados` (`id`, `nombre`) VALUES
(1, 'Enviado'), (2, 'Pendiente'), (3, 'Cancelado'), (4, 'Procesando');

-- Insertar Usuarios
INSERT INTO `usuarios` (`id`, `email`, `password`, `role_id`, `nombre`, `apellido`, `run`, `telefono`, `region`, `comuna`, `direccion`, `comentario`, `fecha_registro`) VALUES
(1, 'admin@duocuc.cl', 'admin123', 1, 'Super', 'Administrador', '12.345.678-9', '912345678', 'region-metropolitana', 'santiago', 'Av. Providencia 1234, Oficina 501', 'Admin sistema', '2024-01-15 08:00:00'),
(2, 'maria.gonzalez@duocuc.cl', 'admin456', 1, 'María José', 'González Pérez', '15.678.234-5', '987654321', 'region-valparaiso', 'valparaiso', 'Calle Esmeralda 789, Casa 12', 'Admin DB', '2024-02-10 09:30:00'),
(3, 'carlos.torres@profesor.duoc.cl', 'admin789', 1, 'Carlos Eduardo', 'Torres Silva', '18.234.567-8', '956789123', 'region-biobio', 'concepcion', 'Av. O''Higgins 2456, Depto 34B', 'Admin académico', '2024-01-25 14:15:00'),
(4, 'ana.martinez@duocuc.cl', 'cliente123', 2, 'Ana María', 'Martínez López', '19.876.543-2', '945678912', 'region-metropolitana', 'las-condes', 'Av. Apoquindo 4567, Casa 78', 'Cliente VIP', '2024-03-05 10:20:00'),
(5, 'pedro.ramirez@duocuc.cl', 'cliente456', 2, 'Pedro Antonio', 'Ramírez Castro', '16.789.123-4', '934567891', 'region-ohiggins', 'rancagua', 'Calle San Martín 1234, Villa El Sauce', 'Cliente frecuente', '2024-02-28 16:45:00'),
(6, 'lucia.fernandez@duocuc.cl', 'cliente789', 2, 'Lucía Elena', 'Fernández Morales', '21.456.789-1', '923456789', 'region-araucania', 'temuco', 'Pasaje Los Aromos 567, Población Nueva', 'Estudiante DUOC', '2024-03-12 11:30:00'),
(7, 'rodrigo.silva@duocuc.cl', 'vendedor123', 3, 'Rodrigo Alejandro', 'Silva Mendoza', '17.345.678-9', '967891234', 'region-metropolitana', 'maipu', 'Av. Pajaritos 3456, Block 12, Depto 204', 'Vendedor frutas', '2024-02-15 08:45:00'),
(8, 'sofia.herrera@duocuc.cl', 'vendedor456', 3, 'Sofía Alejandra', 'Herrera Vásquez', '20.123.456-7', '956781234', 'region-maule', 'talca', 'Calle 1 Norte 2345, Villa Los Jardines', 'Vendedora lácteos', '2024-01-30 13:20:00'),
(9, 'miguel.rojas@profesor.duoc.cl', 'vendedor789', 3, 'Miguel Ángel', 'Rojas Contreras', '14.567.890-1', '912347856', 'region-valparaiso', 'vina-del-mar', 'Av. Libertad 1789, Casa 45', 'Vendedor verduras', '2024-02-05 15:10:00'),
(10, 'juan.perez@duocuc.cl', 'cliente101', 2, 'Juan Carlos', 'Pérez Soto', '13.234.567-8', '956789012', 'region-metropolitana', 'providencia', 'Av. Providencia 2345, Depto 12', 'Cliente orgánico', '2024-04-01 10:00:00'),
(11, 'carla.lopez@duocuc.cl', 'cliente102', 2, 'Carla Andrea', 'López Muñoz', '17.890.123-4', '945678901', 'region-valparaiso', 'vina-del-mar', 'Calle Alvares 567, Casa 23', 'Cliente semanal', '2024-04-05 14:30:00'),
(12, 'roberto.sanchez@duocuc.cl', 'cliente103', 2, 'Roberto Andrés', 'Sánchez Vera', '14.567.234-9', '912345678', 'region-biobio', 'talcahuano', 'Pasaje Los Pinos 890, Villa Mar', 'Cliente VIP', '2024-04-10 09:15:00'),
(13, 'maria.silva@duocuc.cl', 'cliente104', 2, 'María Cristina', 'Silva Rojas', '16.345.678-2', '967890123', 'region-maule', 'curico', 'Av. Manso de Velasco 1234', 'Cliente lácteos', '2024-04-15 16:20:00'),
(14, 'diego.morales@duocuc.cl', 'cliente105', 2, 'Diego Sebastián', 'Morales Castro', '19.123.456-7', '923456780', 'region-metropolitana', 'la-florida', 'Calle Walker Martinez 3456, Block A', 'Cliente eventos', '2024-04-20 11:45:00'),
(15, 'valentina.rojas@duocuc.cl', 'cliente106', 2, 'Valentina Isabel', 'Rojas Hernández', '20.234.567-1', '934567892', 'region-ohiggins', 'san-fernando', 'Av. Libertador 789, Casa 45', 'Cliente regular', '2024-04-25 13:10:00'),
(16, 'francisco.gomez@duocuc.cl', 'cliente107', 2, 'Francisco Javier', 'Gómez Torres', '15.890.234-5', '978901234', 'region-araucania', 'villarrica', 'Camino Villarrica 234, Km 5', 'Cliente certificado', '2024-05-01 08:30:00'),
(17, 'camila.vargas@duocuc.cl', 'cliente108', 2, 'Camila Fernanda', 'Vargas Pérez', '18.456.789-3', '956781235', 'region-metropolitana', 'nunoa', 'Av. Irarrázaval 5678, Depto 301', 'Cliente familia', '2024-05-05 15:00:00'),
(18, 'andres.munoz@duocuc.cl', 'cliente109', 2, 'Andrés Felipe', 'Muñoz Bravo', '12.789.345-6', '989012345', 'region-valparaiso', 'quilpue', 'Calle Freire 123, Villa Esperanza', 'Cliente fiel', '2024-05-10 10:20:00'),
(19, 'daniela.castro@duocuc.cl', 'cliente110', 2, 'Daniela Patricia', 'Castro Fuentes', '21.890.456-8', '945678903', 'region-biobio', 'los-angeles', 'Av. Ricardo Vicuña 456', 'Cliente semanal', '2024-05-15 12:40:00');

-- Insertar Productos
INSERT INTO `productos` (`id`, `nombre`, `categoria_id`, `precio`, `stock`, `descripcion`, `imagen`, `activo`) VALUES
(1, 'FR001 - Manzanas Fuji', 1, 1200, 150, 'Manzanas Fuji crujientes y dulces, cultivadas en el Valle del Maule.', 'manzana.jpg', TRUE),
(2, 'FR002 - Naranjas Valencia', 1, 1000, 200, 'Jugosas y ricas en vitamina C, ideales para zumos frescos.', 'naranja.jpg', TRUE),
(3, 'FR003 - Plátanos Cavendish', 1, 800, 250, 'Plátanos maduros y dulces, perfectos para el desayuno.', 'platano.jpg', TRUE),
(4, 'VR001 - Zanahorias Organicas', 2, 900, 100, 'Zanahorias crujientes cultivadas sin pesticidas.', 'zanahoria.jpg', TRUE),
(5, 'VR002 - Espinacas Frescas', 2, 700, 80, 'Espinacas frescas y nutritivas, perfectas para ensaladas.', 'espinaca.jpg', TRUE),
(6, 'VR003 - Pimentones Tricolores', 2, 1500, 120, 'Pimientos rojos, amarillos y verdes, ideales para salteados.', 'pimenton.jpg', TRUE),
(7, 'PO001 - Miel Organica', 3, 5000, 50, 'Miel pura y orgánica producida por apicultores locales.', 'miel.jpg', TRUE),
(8, 'PO002 - Quinua Organica', 3, 3000, 70, 'Grano andino altamente nutritivo, ideal para ensaladas.', 'quinoa.jpg', TRUE),
(9, 'PL001 - Leche Entera', 4, 1200, 100, 'Leche fresca y pasteurizada, rica en calcio y vitaminas.', 'leche.jpg', TRUE);

-- Insertar Órdenes (CORREGIDO CON SNAPSHOTS Y CAMPOS DE DESPACHO)
-- Nota: Se han extraído los datos de los usuarios correspondientes para llenar los snapshots.
INSERT INTO `ordenes` (id, numero_orden, cliente_id, nombre_cliente_snapshot, email_cliente_snapshot, direccion_envio, region_envio, comuna_envio, telefono_contacto, fecha, estado_id, monto_total, costo_envio, comentario) VALUES
(1, 'SO1001', 4, 'Ana María Martínez López', 'ana.martinez@duocuc.cl', 'Av. Apoquindo 4567, Casa 78', 'region-metropolitana', 'las-condes', '945678912', '2024-06-01', 1, 21300, 5000, ''),
(2, 'SO1002', 5, 'Pedro Antonio Ramírez Castro', 'pedro.ramirez@duocuc.cl', 'Calle San Martín 1234, Villa El Sauce', 'region-ohiggins', 'rancagua', '934567891', '2024-06-02', 2, 14500, 4500, ''),
(3, 'SO1003', 6, 'Lucía Elena Fernández Morales', 'lucia.fernandez@duocuc.cl', 'Pasaje Los Aromos 567, Población Nueva', 'region-araucania', 'temuco', '923456789', '2024-06-02', 3, 8000, 3500, ''),
(4, 'SO1004', 10, 'Juan Carlos Pérez Soto', 'juan.perez@duocuc.cl', 'Av. Providencia 2345, Depto 12', 'region-metropolitana', 'providencia', '956789012', '2024-06-03', 4, 12000, 5500, ''),
(5, 'SO1005', 11, 'Carla Andrea López Muñoz', 'carla.lopez@duocuc.cl', 'Calle Alvares 567, Casa 23', 'region-valparaiso', 'vina-del-mar', '945678901', '2024-06-04', 1, 16800, 6200, ''),
(6, 'SO1006', 12, 'Roberto Andrés Sánchez Vera', 'roberto.sanchez@duocuc.cl', 'Pasaje Los Pinos 890, Villa Mar', 'region-biobio', 'talcahuano', '912345678', '2024-06-05', 2, 18500, 4800, ''),
(7, 'SO1007', 13, 'María Cristina Silva Rojas', 'maria.silva@duocuc.cl', 'Av. Manso de Velasco 1234', 'region-maule', 'curico', '967890123', '2024-06-06', 1, 16800, 5300, ''),
(8, 'SO1008', 14, 'Diego Sebastián Morales Castro', 'diego.morales@duocuc.cl', 'Calle Walker Martinez 3456, Block A', 'region-metropolitana', 'la-florida', '923456780', '2024-06-07', 4, 28000, 6500, ''),
(9, 'SO1009', 15, 'Valentina Isabel Rojas Hernández', 'valentina.rojas@duocuc.cl', 'Av. Libertador 789, Casa 45', 'region-ohiggins', 'san-fernando', '934567892', '2024-06-08', 3, 6000, 3000, ''),
(10, 'SO1010', 16, 'Francisco Javier Gómez Torres', 'francisco.gomez@duocuc.cl', 'Camino Villarrica 234, Km 5', 'region-araucania', 'villarrica', '978901234', '2024-06-09', 1, 14700, 4200, ''),
(11, 'SO1011', 17, 'Camila Fernanda Vargas Pérez', 'camila.vargas@duocuc.cl', 'Av. Irarrázaval 5678, Depto 301', 'region-metropolitana', 'nunoa', '956781235', '2024-06-10', 2, 13800, 5700, ''),
(12, 'SO1012', 18, 'Andrés Felipe Muñoz Bravo', 'andres.munoz@duocuc.cl', 'Calle Freire 123, Villa Esperanza', 'region-valparaiso', 'quilpue', '989012345', '2024-06-11', 1, 8000, 3800, ''),
(13, 'SO1013', 19, 'Daniela Patricia Castro Fuentes', 'daniela.castro@duocuc.cl', 'Av. Ricardo Vicuña 456', 'region-biobio', 'los-angeles', '945678903', '2024-06-12', 4, 9600, 4100, ''),
(14, 'SO1014', 4, 'Ana María Martínez López', 'ana.martinez@duocuc.cl', 'Av. Apoquindo 4567, Casa 78', 'region-metropolitana', 'las-condes', '945678912', '2024-06-13', 1, 29800, 6800, ''),
(15, 'SO1015', 5, 'Pedro Antonio Ramírez Castro', 'pedro.ramirez@duocuc.cl', 'Calle San Martín 1234, Villa El Sauce', 'region-ohiggins', 'rancagua', '934567891', '2024-06-14', 3, 5400, 3200, ''),
(16, 'SO1016', 10, 'Juan Carlos Pérez Soto', 'juan.perez@duocuc.cl', 'Av. Providencia 2345, Depto 12', 'region-metropolitana', 'providencia', '956789012', '2024-06-15', 2, 21600, 5900, ''),
(17, 'SO1017', 11, 'Carla Andrea López Muñoz', 'carla.lopez@duocuc.cl', 'Calle Alvares 567, Casa 23', 'region-valparaiso', 'vina-del-mar', '945678901', '2024-06-16', 4, 10300, 4600, ''),
(18, 'SO1018', 12, 'Roberto Andrés Sánchez Vera', 'roberto.sanchez@duocuc.cl', 'Pasaje Los Pinos 890, Villa Mar', 'region-biobio', 'talcahuano', '912345678', '2024-06-17', 1, 15000, 5100, ''),
(19, 'SO1019', 13, 'María Cristina Silva Rojas', 'maria.silva@duocuc.cl', 'Av. Manso de Velasco 1234', 'region-maule', 'curico', '967890123', '2024-06-18', 2, 14400, 4900, ''),
(20, 'SO1020', 14, 'Diego Sebastián Morales Castro', 'diego.morales@duocuc.cl', 'Calle Walker Martinez 3456, Block A', 'region-metropolitana', 'la-florida', '923456780', '2024-06-19', 1, 38000, 7000, '');

-- Insertar Detalles (CORREGIDO CON SNAPSHOTS DE PRODUCTOS)
-- Nota: Se han extraído los nombres y precios históricos de la tabla de productos.
INSERT INTO `detalles_orden` (orden_id, producto_id, nombre_producto_snapshot, precio_unitario_snapshot, cantidad, subtotal) VALUES
-- Orden 1
(1, 1, 'FR001 - Manzanas Fuji', 1200, 5, 6000),
(1, 7, 'PO001 - Miel Organica', 5000, 2, 10000),
(1, 5, 'VR002 - Espinacas Frescas', 700, 3, 2100),
(1, 3, 'FR003 - Plátanos Cavendish', 800, 4, 3200),
-- Orden 2
(2, 2, 'FR002 - Naranjas Valencia', 1000, 10, 10000),
(2, 4, 'VR001 - Zanahorias Organicas', 900, 5, 4500),
-- Orden 3
(3, 3, 'FR003 - Plátanos Cavendish', 800, 10, 8000),
-- Orden 4
(4, 6, 'VR003 - Pimentones Tricolores', 1500, 4, 6000),
(4, 8, 'PO002 - Quinua Organica', 3000, 2, 6000),
-- Orden 5
(5, 1, 'FR001 - Manzanas Fuji', 1200, 8, 9600),
(5, 9, 'PL001 - Leche Entera', 1200, 6, 7200),
-- Orden 6
(6, 7, 'PO001 - Miel Organica', 5000, 3, 15000),
(6, 5, 'VR002 - Espinacas Frescas', 700, 5, 3500),
-- Orden 7
(7, 9, 'PL001 - Leche Entera', 1200, 10, 12000),
(7, 3, 'FR003 - Plátanos Cavendish', 800, 6, 4800),
-- Orden 8
(8, 7, 'PO001 - Miel Organica', 5000, 4, 20000),
(8, 2, 'FR002 - Naranjas Valencia', 1000, 8, 8000),
-- Orden 9
(9, 1, 'FR001 - Manzanas Fuji', 1200, 5, 6000),
-- Orden 10
(10, 8, 'PO002 - Quinua Organica', 3000, 4, 12000),
(10, 4, 'VR001 - Zanahorias Organicas', 900, 3, 2700),
-- Orden 11
(11, 6, 'VR003 - Pimentones Tricolores', 1500, 6, 9000),
(11, 1, 'FR001 - Manzanas Fuji', 1200, 4, 4800),
-- Orden 12
(12, 2, 'FR002 - Naranjas Valencia', 1000, 8, 8000),
-- Orden 13
(13, 5, 'VR002 - Espinacas Frescas', 700, 8, 5600),
(13, 3, 'FR003 - Plátanos Cavendish', 800, 5, 4000),
-- Orden 14
(14, 7, 'PO001 - Miel Organica', 5000, 5, 25000),
(14, 9, 'PL001 - Leche Entera', 1200, 4, 4800),
-- Orden 15
(15, 4, 'VR001 - Zanahorias Organicas', 900, 6, 5400),
-- Orden 16
(16, 8, 'PO002 - Quinua Organica', 3000, 6, 18000),
(16, 1, 'FR001 - Manzanas Fuji', 1200, 3, 3600),
-- Orden 17
(17, 6, 'VR003 - Pimentones Tricolores', 1500, 5, 7500),
(17, 5, 'VR002 - Espinacas Frescas', 700, 4, 2800),
-- Orden 18
(18, 2, 'FR002 - Naranjas Valencia', 1000, 15, 15000),
-- Orden 19
(19, 9, 'PL001 - Leche Entera', 1200, 12, 14400),
-- Orden 20
(20, 7, 'PO001 - Miel Organica', 5000, 6, 30000),
(20, 3, 'FR003 - Plátanos Cavendish', 800, 10, 8000);

SET FOREIGN_KEY_CHECKS = 1;