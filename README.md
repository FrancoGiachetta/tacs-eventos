# TACS - Eventos

Proyecto de la materia **TACS** - Gestión de Eventos con **Spring Boot**.

## Docker

Para poder ejecutar el proyecto con docker, desde el directorio raíz ejecutar:

```shell
docker-compose up
```
Este comando se encarga de buildear y levantar todos los containers necesarios.

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

-Usuarios hardcodeados
🔴 ADMIN
Email: admin@eventos.com
Password: admin123
Permisos: Acceso completo al sistema + Panel de administración

🔵 ORGANIZADOR (Ejemplo)
Email: organizador@eventos.com  
Password: org123
Permisos: Crear y gestionar eventos
```


## Equipo

Franco Giachetta
Leonel Lucas Morsella
Francisco Veiga
Carlos Renfige
Marco Rodriguez Melgarejo
Lucas Thier
