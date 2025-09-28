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
|-------|------------|-----|
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

## Backend

### Requisitos Previos

Antes de ejecutar el proyecto

- [Java 17+](https://jdk.java.net/java-se-ri/17-MR1)
- [Maven 3.9+](https://maven.apache.org/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/)

### Instalación

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

- [Node.js](https://nodejs.org)

### Instalación (una sola vez) 

```shell
cd frontend
npm install
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

## Equipo

Franco Giachetta
Leonel Lucas Morsella
Francisco Veiga
Carlos Renfige
Marco Rodriguez Melgarejo
Lucas Thier
