# Huerto Hogar - Backend API

## Descripción

API REST desarrollada con Spring Boot para la plataforma e-commerce Huerto Hogar. Proporciona servicios completos de gestión de productos, usuarios, categorías, órdenes y autenticación con JWT para tres roles de usuario: Administrador, Vendedor y Cliente.

## Tecnologías

- **Spring Boot 3.3.5** - Framework principal
- **Java 21** - Lenguaje de programación
- **Spring Security** - Autenticación y autorización
- **JWT (JSON Web Tokens)** - Tokens de acceso con expiración de 5 horas
- **Spring Data JPA** - ORM y persistencia de datos
- **MySQL** - Base de datos relacional
- **Maven** - Gestión de dependencias
- **Springdoc OpenAPI 2.6.0** - Documentación Swagger UI

## Requisitos Previos

- **Java JDK 21**
- **Maven 3.8+**
- **MySQL 8.0+**
- Puerto **8080** disponible para el servidor
- Puerto **3306** disponible para MySQL

## Instalación

### 1. Configurar Base de Datos

```bash
# Crear base de datos
mysql -u root -p < bbdd.sql
```

El script `bbdd.sql` crea la base de datos `hh_db` con las siguientes tablas:
- `roles` - Roles de usuario (Admin, Vendedor, Cliente)
- `categorias` - Categorías de productos con prefijos automáticos
- `usuarios` - Usuarios del sistema
- `productos` - Catálogo de productos
- `ordenes` y `ordenes_productos` - Gestión de pedidos

### 2. Configurar Conexión

Edita `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hh_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### 3. Instalar Dependencias

```bash
./mvnw clean install
```

## Ejecución

```bash
./mvnw spring-boot:run
```


El servidor se iniciará en `http://localhost:8080`


## Documentación API

### Swagger UI
Una vez iniciado el servidor, accede a:
```
http://localhost:8080/swagger-ui/index.html
```

### Autenticación en Swagger

1. Ejecuta `POST /api/auth/login` con credenciales válidas
2. Copia el token JWT de la respuesta
3. Haz clic en el botón **"Authorize"** 🔒 (arriba a la derecha)
4. Pega el token (sin el prefijo "Bearer")
5. Todas las peticiones posteriores incluirán automáticamente el token

## Endpoints Principales

### Autenticación
- `POST /api/auth/login` - Login y obtención de token JWT

### Registro Público (sin autenticación)
- `POST /api/public/register/validate-email` - Validar disponibilidad de email
- `POST /api/public/register/create-user` - Crear nuevo usuario (rol Cliente)

### Usuarios (ADMIN)
- `GET /api/usuarios` - Listar todos los usuarios
- `GET /api/usuarios/{id}` - Obtener usuario por ID
- `POST /api/usuarios` - Crear usuario
- `PUT /api/usuarios/{id}` - Actualizar usuario
- `DELETE /api/usuarios/{id}` - Eliminar usuario

### Productos (GET público, POST/PUT/DELETE protegidos)
- `GET /api/productos` - Listar productos activos
- `GET /api/productos/{id}` - Obtener producto por ID
- `POST /api/productos` - Crear producto (ADMIN)
- `PUT /api/productos/{id}` - Actualizar producto (ADMIN)
- `DELETE /api/productos/{id}` - Borrado lógico (ADMIN)

### Categorías
- `GET /api/categorias` - Listar todas las categorías
- `GET /api/categorias/{id}` - Obtener categoría por ID
- `POST /api/categorias` - Crear categoría (ADMIN)
- `PUT /api/categorias/{id}` - Actualizar categoría (ADMIN)
- `DELETE /api/categorias/{id}` - Eliminar categoría (ADMIN)

### Órdenes
- `GET /api/ordenes` - Listar órdenes (ADMIN, VENDEDOR)
- `GET /api/ordenes/{id}` - Obtener orden por ID
- `GET /api/ordenes/cliente/{id}` - Órdenes de un cliente
- `POST /api/ordenes` - Crear orden (CLIENTE)
- `PUT /api/ordenes/{id}` - Actualizar estado (ADMIN, VENDEDOR)
- `POST /api/ordenes/calcular-envio` - Calcular costo de envío (público)

### Roles
- `GET /api/roles` - Listar roles (ADMIN)

## Seguridad y Autenticación

### JWT Configuration
- **Algoritmo**: HS256
- **Expiración**: 5 horas
- **Secret Key**: Configurada en código
- **Claims**: role, userId, username

### Validaciones Implementadas
- **Email**: Debe terminar en `@duocuc.cl` o `@profesor.duoc.cl`
- **RUN**: Formato `##.###.###-K` con validación de patrón
- **Contraseña**: Mínimo 4 caracteres (sin encriptación)
- **Dirección**: Mínimo 5 caracteres

### Control de Acceso (Role-Based)
```
ADMIN     → Acceso completo a todos los endpoints
VENDEDOR  → Gestión de órdenes y visualización de productos
CLIENTE   → Creación de órdenes y navegación de tienda
PÚBLICO   → Login, registro, catálogo de productos, cálculo de envío
```

## Estructura del Proyecto

```
src/main/java/com/backend/huertohogar/
├── config/              # Configuraciones (Security, OpenAPI, CORS)
├── controller/          # Endpoints REST
├── dto/                 # Data Transfer Objects
├── exception/           # Manejadores de excepciones
├── model/               # Entidades JPA
├── repository/          # Repositorios de datos
├── security/            # JWT Utils, Filters, UserDetailsService
└── service/             # Lógica de negocio
```

## Configuración CORS

El backend está configurado para aceptar peticiones desde:
```
http://localhost:3000 (frontend React)
```

Métodos permitidos: GET, POST, PUT, DELETE, OPTIONS

## Datos de Prueba

El script `bbdd.sql` incluye datos iniciales:

**Usuarios:**
```
admin@duocuc.cl / admin123 (ADMIN)
vendedor@duocuc.cl / vendedor123 (VENDEDOR)
cliente@duocuc.cl / cliente123 (CLIENTE)
```

**Categorías:**
- Frutas frescas (FR)
- Verduras orgánicas (VR)
- Productos orgánicos (PO)
- Productos lácteos (PL)

## Scripts Maven

```bash
./mvnw clean install         # Compilar e instalar
./mvnw spring-boot:run       # Ejecutar aplicación
```

## Autores

Desarrollado por:
- [Sebastián Valdivia](https://github.com/ZalkiRyon)
- [Paula Frías](https://github.com/paufriasest)

```
⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠟⠛⠛⠉⠙⠛⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⠟⠉⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠘⠿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠿⠛⠁⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠛⢿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠿⣿⣟⣛⣻⡿⣿⣿⣿⣫⡿⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠘⢿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠟⣫⠴⠿⠛⡛⠣⠤⣤⡤⣠⣾⣿⣷⡀⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⢠⣀⠄⠄⠄⠘⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⣿⣿⣿⠃⠈⣃⢌⣭⣬⣭⠻⠁⣴⠆⣿⣿⣿⣿⣷⡀⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠹⣿⡖⣀⣀⣾⣯⡻⣿⣿
⣿⣿⣿⣿⣿⣿⣿⣿⠃⠄⠄⢫⣬⣭⠛⢱⣾⣶⢤⣾⠙⣿⣿⣿⣿⣿⣦⣄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⢀⣽⢸⣿⣿⣿⣿⣿⡝⣿
⣿⣿⣿⣿⣿⣿⣿⣿⡄⠄⠄⠈⠉⢉⠄⡟⣿⡏⣸⣿⣷⡜⢿⣿⣿⣿⣿⣿⣷⣶⣤⣀⠄⠄⠄⠄⠄⠄⢀⡐⣿⣿⡘⣿⣿⣿⣿⣿⣰⣿
⣿⣿⣿⣿⣿⣿⣿⣿⣇⠄⠐⠄⠄⢸⡄⣷⡘⢰⣿⣿⣿⣿⣷⡹⢿⣿⣿⣿⣿⣿⣿⣿⣿⣟⣿⣿⣿⣾⣿⣧⢸⣿⣿⣮⣙⣛⣫⣴⣿⣿
⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇⢰⠄⢀⠐⢷⣸⣿⣾⣿⣿⣿⣿⡏⣿⣷⣍⡛⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣦⡀⠄⣀⠚⢧⡁⣿⣿⣿⣿⣿⡇⡫⢅⢰⡶⣀⢰⡦⡈⢉⡛⡛⡛⣛⠛⡛⡉⠄⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⡿⠟⠛⠋⠉⠁⠄⠄⢀⠄⡁⣿⣿⣿⣿⣿⠃⠐⠛⠈⠐⠻⢬⣴⣷⣬⣴⠃⠐⠉⢀⡀⣡⣤⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⡀⠄⠄⠄⠄⠄⠄⠄⡀⠄⠄⣿⣿⣿⣿⣿⢰⠄⠄⠄⠄⢲⣾⣿⣿⣿⣿⠄⠄⠄⠄⡇⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⣧⡀⠄⠄⠄⠄⠄⠄⠯⠈⠄⢿⣿⣿⡿⡿⢸⠄⠄⠄⠄⣸⣿⣿⣿⣿⣿⡀⠄⠄⠄⣧⡙⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⣿⣿⣄⠄⠄⠄⠄⠄⠑⠠⠈⢘⢻⣿⡇⠁⢸⣧⡀⠄⣠⣿⣿⣿⣿⣿⣿⣷⣄⣀⣴⣿⡇⡟⢹⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⣤⣠⣤⣶⠄⠄⠈⠄⠙⢿⡇⠄⣸⣿⣿⣿⣿⠏⣼⣿⣿⣿⣿⣼⣿⣿⠟⠋⡀⠄⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇⠄⠄⠄⠄⠄⠄⡁⠈⠉⠉⠉⠛⠛⠃⠹⠻⠛⠋⠁⠉⠁⠄⡆⢀⠨⣴⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇⠄⠄⢀⣀⡀⢀⣄⣀⡀⠄⠄⠄⠄⠄⠄⠄⠄⠄⠐⡶⢾⣿⣿⣿⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣾⣿⣿⣿⣗⠻⣿⣿⣿⣿⣷⣶⠄⠄⠄⡀⠄⢀⣸⣿⣮⠻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⠿⣟⣫⣥⣶⣶⣶⣦⣤⣭⣛⡿⣿⣿⣿⡿⠗⠄⠄⠉⠉⠛⠃⠄⢒⣛⣛⡂⠛⠉⢡⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⢛⣥⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣬⠛⠁⠄⠄⠄⠄⠄⠄⠄⠄⠄⠘⣿⣿⣷⡄⠄⠄⠉⠛⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⡿⢃⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⣿⡄⢀⣤⣴⣶⣶⣶⣦⣀⠄⠄⢻⣿⣿⣿⡀⠄⠄⠄⠄⠄⠛⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⡇⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣝⢣⠘⠿⠟⠛⠛⠛⢛⠛⠄⠄⢸⣿⣿⣿⣧⠄⠄⠄⠄⣀⣀⣬⣭⢻⣿⣿⣿⣿⣿⣿⣿
⡇⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠄⣶⢀⡀⠲⣶⣯⣹⣶⣰⣾⣿⣿⣿⣿⣶⣶⣿⣿⣿⣿⣿⣿⣷⡽⣿⣿⣿⣿⣿⣿
⣧⠹⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣯⡅⣠⣤⡅⣤⣴⣬⡝⢫⣬⣥⣿⣿⣿⣿⣿⡿⠿⠿⠟⢛⣛⣻⣯⣥⣴⣿⣿⣿⣿⣿⣿
⣿⣷⡙⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣴⣽⣷⣧⣿⣟⣿⡗⣰⣶⣶⣶⣶⣦⣀⠄⣶⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣧⣤⣛⣛⣛⣛⣛⣛⣛⣛⣛⣋⣉⣉⣉⣉⣉⣉⣉⣉⣉⣥⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
```
