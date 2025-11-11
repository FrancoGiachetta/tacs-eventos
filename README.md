# TACS - Eventos

Proyecto de la materia **TACS** - Gestión de Eventos con **Spring Boot** y **React**.

Sistema completo de gestión de eventos con roles de usuario (Admin, Organizador, Usuario), inscripciones, waitlist y panel de administración.

## 🐳 Ejecución con Docker (Recomendado)

### Prerequisitos
- [Docker](https://www.docker.com/get-started/) instalado y ejecutándose
- [Docker Compose](https://docs.docker.com/compose/install/) (incluido con Docker Desktop)

### 1. Construir las imágenes Docker (primera vez o después de cambios)
```bash
docker-compose build --no-cache
```
### 2. Levantar todos los servicios
```bash
docker-compose up -d
```
### 3. Verificar que los servicios estén funcionando
```bash
docker-compose ps
```
### 4. Ver logs de los servicios (opcional)
```bash
# Ver todos los logs
docker-compose logs

# Ver logs de un servicio específico
docker-compose logs eventos-backend
docker-compose logs eventos-frontend
```

## 🌐 URLs de acceso

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **Frontend** | http://localhost:3000 | Aplicación React |
| **Backend** | http://localhost:8080 | API REST Spring Boot |
| **Health Check** | http://localhost:8080/actuator/health | Estado del backend |

## 👥 Usuarios predefinidos

| Email | Contraseña | Rol |
|-------|-----------|-----|
| `admin@eventos.com` | `admin123` | **ADMIN** |
| `organizador@eventos.com` | `org123` | **ORGANIZADOR** |

## 🔧 Comandos útiles

### Reconstruir solo un servicio
```bash
# Reconstruir solo el backend
docker-compose build eventos-backend

# Reconstruir solo el frontend  
docker-compose build eventos-frontend
```

## 🔧 Desarrollo Local

## Levantar bases de datos
### Levantar Redis
Ejecutar el siguiente comando de docker:
```bash
docker run -p 6379:6379 --name redis-app-eventos-sin-compose -d redis:8.2 redis-server --save "" --appendonly no
```
### Levantar MongoDB
Ejecutar el siguiente comando de docker:
```bash
docker run -d -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=nraboy -e MONGO_INITDB_ROOT_PASSWORD=password1234 --name mongodb-sin-compose mongodb/mongodb-community-server
```

## Backend

### Requisitos Previos

Antes de ejecutar el proyecto

- [Java 17+](https://jdk.java.net/java-se-ri/17-MR1)
- [Maven 3.9+](https://maven.apache.org/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/)

###  Instalación

```shell
cd backend
mvn clean install
```

### Ejecución

Desde Powershell:

```shell
cd backend
mvn spring-boot:run
```

O desde el IDE (ejecutar la clase principal tacs.eventos.EventosApplication).

Por defecto la aplicación apunta a
http://localhost:8080

### Documentación de la API con Swagger

Una vez ejecutado el server, dirigirse a: http://localhost:8080/swagger

## Frontend

### Requisitos Previos

- [Node.js](https://nodejs.org/en)

### Instalación 

```shell
cd frontend
npm install // una sola vez
```

### Ejecución

```shell
cd frontend
npm run dev
```

## Políticas de Desarrollo 

### Formato

Cada vez que vamos a hacer un push, debemos formatear el código correctamente:

- Backend: 

```shell
mvn net.revelc.code.formatter:formatter-maven-plugin:2.20.0:format
```

- Frontend: 

```shell
npm run fmt
```

### Usuarios hardcodeados

#### ADMIN

- Email: admin@eventos.com
- Password: admin123
- Permisos: Acceso completo al sistema + Panel de administración

#### ORGANIZADOR

- Email: organizador@eventos.com  
- Password: org123
- Permisos: Crear y gestionar eventos

## CLOUD AWS

La aplicacion se instala en EC2-AWS // atlas mongoDB

URL:http://ec2-3-94-168-55.compute-1.amazonaws.com:3000/login

## Equipo

- Carlos Renfige
- Francisco Veiga
- Franco Giachetta
- Leonel Lucas Morsella
- Lucas Thier
- Marco Rodriguez Melgarejo
