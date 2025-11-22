-- Configuración inicial
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Creación de la Base de Datos
DROP DATABASE IF EXISTS hh_db;
CREATE DATABASE hh_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hh_db;

-- --------------------------------------------------------
-- TABLAS DE CATALOGO (Normalización)
-- --------------------------------------------------------

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

-- --------------------------------------------------------
-- TABLAS PRINCIPALES
-- --------------------------------------------------------

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
  PRIMARY KEY (`id`),
  KEY `fk_producto_categoria` (`categoria_id`),
  CONSTRAINT `fk_producto_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Tabla de Órdenes
DROP TABLE IF EXISTS `ordenes`;
CREATE TABLE `ordenes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `numero_orden` varchar(50) NOT NULL,
  `cliente_id` int(11) NOT NULL,
  `fecha` date DEFAULT NULL,
  `estado_id` int(11) NOT NULL,
  `monto_total` int(11) NOT NULL DEFAULT 0,
  `departamento` varchar(100) DEFAULT NULL,
  `comentario` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_orden_unique` (`numero_orden`),
  KEY `fk_orden_usuario` (`cliente_id`),
  KEY `fk_orden_estado` (`estado_id`),
  CONSTRAINT `fk_orden_usuario` FOREIGN KEY (`cliente_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_orden_estado` FOREIGN KEY (`estado_id`) REFERENCES `estados` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Tabla de Detalles de Orden
DROP TABLE IF EXISTS `detalles_orden`;
CREATE TABLE `detalles_orden` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orden_id` int(11) NOT NULL,
  `producto_id` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL DEFAULT 1,
  `precio_unitario` int(11) NOT NULL,
  `subtotal` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_detalle_orden` (`orden_id`),
  KEY `fk_detalle_producto` (`producto_id`),
  CONSTRAINT `fk_detalle_orden` FOREIGN KEY (`orden_id`) REFERENCES `ordenes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_detalle_producto` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- POBLADO DE DATOS
-- --------------------------------------------------------

-- Insertar Roles
INSERT INTO `roles` (`id`, `nombre`) VALUES
(1, 'admin'),
(2, 'cliente'),
(3, 'vendedor');

-- Insertar Categorías
INSERT INTO `categorias` (`id`, `nombre`) VALUES
(1, 'Frutas frescas'),
(2, 'Verduras organicas'),
(3, 'Productos organicos'),
(4, 'Productos lacteos');

-- Insertar Estados
INSERT INTO `estados` (`id`, `nombre`) VALUES
(1, 'Enviado'),
(2, 'Pendiente'),
(3, 'Cancelado'),
(4, 'Procesando');

-- Insertar Usuarios (Nota: role_id 1=admin, 2=cliente, 3=vendedor)
-- Corrección aplicada: 'Av. O''Higgins'
INSERT INTO `usuarios` (`id`, `email`, `password`, `role_id`, `nombre`, `apellido`, `run`, `telefono`, `region`, `comuna`, `direccion`, `comentario`, `fecha_registro`) VALUES
(1, 'admin@duoc.cl', 'admin123', 1, 'Super', 'Administrador', '12.345.678-9', '912345678', 'region-metropolitana', 'santiago', 'Av. Providencia 1234, Oficina 501', 'Usuario administrador principal del sistema', '2024-01-15 08:00:00'),
(2, 'maria.gonzalez@duoc.cl', 'admin456', 1, 'María José', 'González Pérez', '15.678.234-5', '987654321', 'region-valparaiso', 'valparaiso', 'Calle Esmeralda 789, Casa 12', 'Administradora de sistemas y base de datos', '2024-02-10 09:30:00'),
(3, 'carlos.torres@profesor.duoc.cl', 'admin789', 1, 'Carlos Eduardo', 'Torres Silva', '18.234.567-8', '956789123', 'region-biobio', 'concepcion', 'Av. O''Higgins 2456, Depto 34B', 'Administrador de contenido y gestión académica', '2024-01-25 14:15:00'),
(4, 'ana.martinez@gmail.com', 'cliente123', 2, 'Ana María', 'Martínez López', '19.876.543-2', '945678912', 'region-metropolitana', 'las-condes', 'Av. Apoquindo 4567, Casa 78', 'Cliente VIP, compra productos orgánicos semanalmente', '2024-03-05 10:20:00'),
(5, 'pedro.ramirez@gmail.com', 'cliente456', 2, 'Pedro Antonio', 'Ramírez Castro', '16.789.123-4', '934567891', 'region-ohiggins', 'rancagua', 'Calle San Martín 1234, Villa El Sauce', 'Cliente frecuente, prefiere frutas frescas locales', '2024-02-28 16:45:00'),
(6, 'lucia.fernandez@duoc.cl', 'cliente789', 2, 'Lucía Elena', 'Fernández Morales', '21.456.789-1', '923456789', 'region-araucania', 'temuco', 'Pasaje Los Aromos 567, Población Nueva', 'Estudiante DUOC, compra productos económicos y saludables', '2024-03-12 11:30:00'),
(7, 'rodrigo.silva@duoc.cl', 'vendedor123', 3, 'Rodrigo Alejandro', 'Silva Mendoza', '17.345.678-9', '967891234', 'region-metropolitana', 'maipu', 'Av. Pajaritos 3456, Block 12, Depto 204', 'Vendedor especializado en productos orgánicos y frutas', '2024-02-15 08:45:00'),
(8, 'sofia.herrera@gmail.com', 'vendedor456', 3, 'Sofía Alejandra', 'Herrera Vásquez', '20.123.456-7', '956781234', 'region-maule', 'talca', 'Calle 1 Norte 2345, Villa Los Jardines', 'Vendedora con experiencia en atención al cliente y productos lácteos', '2024-01-30 13:20:00'),
(9, 'miguel.rojas@profesor.duoc.cl', 'vendedor789', 3, 'Miguel Ángel', 'Rojas Contreras', '14.567.890-1', '912347856', 'region-valparaiso', 'vina-del-mar', 'Av. Libertad 1789, Casa 45', 'Vendedor tiempo parcial, especialista en verduras orgánicas', '2024-02-05 15:10:00'),
(10, 'juan.perez@gmail.com', 'cliente101', 2, 'Juan Carlos', 'Pérez Soto', '13.234.567-8', '956789012', 'region-metropolitana', 'providencia', 'Av. Providencia 2345, Depto 12', 'Cliente frecuente de productos orgánicos', '2024-04-01 10:00:00'),
(11, 'carla.lopez@gmail.com', 'cliente102', 2, 'Carla Andrea', 'López Muñoz', '17.890.123-4', '945678901', 'region-valparaiso', 'vina-del-mar', 'Calle Alvares 567, Casa 23', 'Compra frutas y verduras semanalmente', '2024-04-05 14:30:00'),
(12, 'roberto.sanchez@gmail.com', 'cliente103', 2, 'Roberto Andrés', 'Sánchez Vera', '14.567.234-9', '912345678', 'region-biobio', 'talcahuano', 'Pasaje Los Pinos 890, Villa Mar', 'Cliente VIP, pedidos grandes mensuales', '2024-04-10 09:15:00'),
(13, 'maria.silva@gmail.com', 'cliente104', 2, 'María Cristina', 'Silva Rojas', '16.345.678-2', '967890123', 'region-maule', 'curico', 'Av. Manso de Velasco 1234', 'Prefiere productos lácteos y orgánicos', '2024-04-15 16:20:00'),
(14, 'diego.morales@gmail.com', 'cliente105', 2, 'Diego Sebastián', 'Morales Castro', '19.123.456-7', '923456780', 'region-metropolitana', 'la-florida', 'Calle Walker Martinez 3456, Block A', 'Compra productos para eventos', '2024-04-20 11:45:00'),
(15, 'valentina.rojas@gmail.com', 'cliente106', 2, 'Valentina Isabel', 'Rojas Hernández', '20.234.567-1', '934567892', 'region-ohiggins', 'san-fernando', 'Av. Libertador 789, Casa 45', 'Cliente regular, compra frutas frescas', '2024-04-25 13:10:00'),
(16, 'francisco.gomez@gmail.com', 'cliente107', 2, 'Francisco Javier', 'Gómez Torres', '15.890.234-5', '978901234', 'region-araucania', 'villarrica', 'Camino Villarrica 234, Km 5', 'Prefiere productos orgánicos certificados', '2024-05-01 08:30:00'),
(17, 'camila.vargas@gmail.com', 'cliente108', 2, 'Camila Fernanda', 'Vargas Pérez', '18.456.789-3', '956781235', 'region-metropolitana', 'nunoa', 'Av. Irarrázaval 5678, Depto 301', 'Compra productos para su familia', '2024-05-05 15:00:00'),
(18, 'andres.munoz@gmail.com', 'cliente109', 2, 'Andrés Felipe', 'Muñoz Bravo', '12.789.345-6', '989012345', 'region-valparaiso', 'quilpue', 'Calle Freire 123, Villa Esperanza', 'Cliente desde el inicio, muy satisfecho', '2024-05-10 10:20:00'),
(19, 'daniela.castro@gmail.com', 'cliente110', 2, 'Daniela Patricia', 'Castro Fuentes', '21.890.456-8', '945678903', 'region-biobio', 'los-angeles', 'Av. Ricardo Vicuña 456', 'Compra productos semanalmente', '2024-05-15 12:40:00');

-- Insertar Productos (Nota: categoria_id 1=Frutas, 2=Verduras, 3=Orgánicos, 4=Lácteos)
INSERT INTO `productos` (`id`, `nombre`, `categoria_id`, `precio`, `stock`, `descripcion`, `imagen`) VALUES
(1, 'FR001 - Manzanas Fuji', 1, 1200, 150, 'Manzanas Fuji crujientes y dulces, cultivadas en el Valle del Maule. Perfectas para meriendas saludables o como ingrediente en postres.', 'manzana.jpg'),
(2, 'FR002 - Naranjas Valencia', 1, 1000, 200, 'Jugosas y ricas en vitamina C, estas naranjas Valencia son ideales para zumos frescos y refrescantes.', 'naranja.jpg'),
(3, 'FR003 - Plátanos Cavendish', 1, 800, 250, 'Plátanos maduros y dulces, perfectos para el desayuno o como snack energético.', 'platano.jpg'),
(4, 'VR001 - Zanahorias Organicas', 2, 900, 100, 'Zanahorias crujientes cultivadas sin pesticidas en la Región de O''Higgins.', 'zanahoria.jpg'),
(5, 'VR002 - Espinacas Frescas', 2, 700, 80, 'Espinacas frescas y nutritivas, perfectas para ensaladas y batidos verdes.', 'espinaca.jpg'),
(6, 'VR003 - Pimentones Tricolores', 2, 1500, 120, 'Pimientos rojos, amarillos y verdes, ideales para salteados y platos coloridos.', 'pimenton.jpg'),
(7, 'PO001 - Miel Organica', 3, 5000, 50, 'Miel pura y orgánica producida por apicultores locales.', 'miel.jpg'),
(8, 'PO002 - Quinua Organica', 3, 3000, 70, 'Grano andino altamente nutritivo, ideal para ensaladas, sopas y guarniciones.', 'quinoa.jpg'),
(9, 'PL001 - Leche Entera', 4, 1200, 100, 'Leche fresca y pasteurizada, rica en calcio y vitaminas.', 'leche.jpg');

-- Insertar Órdenes (Nota: estado_id 1=Enviado, 2=Pendiente, 3=Cancelado, 4=Procesando)
INSERT INTO `ordenes` (`id`, `numero_orden`, `cliente_id`, `fecha`, `estado_id`, `monto_total`, `departamento`, `comentario`) VALUES
(1, 'SO1001', 4, '2024-06-01', 1, 21300, '', ''),
(2, 'SO1002', 5, '2024-06-02', 2, 14500, '', ''),
(3, 'SO1003', 6, '2024-06-02', 3, 8000, '', ''),
(4, 'SO1004', 10, '2024-06-03', 4, 12000, '', ''),
(5, 'SO1005', 11, '2024-06-04', 1, 16800, '', ''),
(6, 'SO1006', 12, '2024-06-05', 2, 18500, '', ''),
(7, 'SO1007', 13, '2024-06-06', 1, 16800, '', ''),
(8, 'SO1008', 14, '2024-06-07', 4, 28000, '', ''),
(9, 'SO1009', 15, '2024-06-08', 3, 6000, '', ''),
(10, 'SO1010', 16, '2024-06-09', 1, 14700, '', ''),
(11, 'SO1011', 17, '2024-06-10', 2, 13800, '', ''),
(12, 'SO1012', 18, '2024-06-11', 1, 8000, '', ''),
(13, 'SO1013', 19, '2024-06-12', 4, 9600, '', ''),
(14, 'SO1014', 4, '2024-06-13', 1, 29800, '', ''),
(15, 'SO1015', 5, '2024-06-14', 3, 5400, '', ''),
(16, 'SO1016', 10, '2024-06-15', 2, 21600, '', ''),
(17, 'SO1017', 11, '2024-06-16', 4, 10300, '', ''),
(18, 'SO1018', 12, '2024-06-17', 1, 15000, '', ''),
(19, 'SO1019', 13, '2024-06-18', 2, 14400, '', ''),
(20, 'SO1020', 14, '2024-06-19', 1, 38000, '', '');

-- Insertar Detalles de Órdenes
INSERT INTO `detalles_orden` (`orden_id`, `producto_id`, `cantidad`, `precio_unitario`, `subtotal`) VALUES
(1, 1, 5, 1200, 6000), (1, 7, 2, 5000, 10000), (1, 5, 3, 700, 2100), (1, 3, 4, 800, 3200),
(2, 2, 10, 1000, 10000), (2, 4, 5, 900, 4500),
(3, 3, 10, 800, 8000),
(4, 6, 4, 1500, 6000), (4, 8, 2, 3000, 6000),
(5, 1, 8, 1200, 9600), (5, 9, 6, 1200, 7200),
(6, 7, 3, 5000, 15000), (6, 5, 5, 700, 3500),
(7, 9, 10, 1200, 12000), (7, 3, 6, 800, 4800),
(8, 7, 4, 5000, 20000), (8, 2, 8, 1000, 8000),
(9, 1, 5, 1200, 6000),
(10, 8, 4, 3000, 12000), (10, 4, 3, 900, 2700),
(11, 6, 6, 1500, 9000), (11, 1, 4, 1200, 4800),
(12, 2, 8, 1000, 8000),
(13, 5, 8, 700, 5600), (13, 3, 5, 800, 4000),
(14, 7, 5, 5000, 25000), (14, 9, 4, 1200, 4800),
(15, 4, 6, 900, 5400),
(16, 8, 6, 3000, 18000), (16, 1, 3, 1200, 3600),
(17, 6, 5, 1500, 7500), (17, 5, 4, 700, 2800),
(18, 2, 15, 1000, 15000),
(19, 9, 12, 1200, 14400),
(20, 7, 6, 5000, 30000), (20, 3, 10, 800, 8000);

SET FOREIGN_KEY_CHECKS = 1;