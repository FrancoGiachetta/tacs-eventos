# README – Autenticación y Sesiones

Este documento explica cómo **crear una cuenta**, **loguearse** para obtener un **token de sesión**, y **consumir el resto de los endpoints protegidos** agregando el token en los headers.


> * Endpoints públicos: `/api/auth/register`, `/api/auth/login`, `/api/auth/logout`


## 1) Registrar usuario (público)

Registra y loguea a la vez.

**Endpoint**

```
POST /api/auth/register
```

**Body (JSON)**

```json
{
  "email": "persona@ejemplo.com",
  "password": "MiPasswordSegura123"
}
```
**Respuesta**

```json
{
  "token": "0d5d7f74-2f1b-4d3a-9faa-2e8a0f2a7a2e",
  "expiresAt": "2025-08-19T16:02:45.678Z"
}
```

---

## 2) Login (público)

**Endpoint**

```
POST /api/auth/login
Content-Type: application/json
```

**Body (JSON)**

```json
{
  "email": "persona@ejemplo.com",
  "password": "MiPasswordSegura123"
}
```

**Respuesta**

```json
{
  "token": "0d5d7f74-2f1b-4d3a-9faa-2e8a0f2a7a2e",
  "expiresAt": "2025-08-19T16:02:45.678Z"
}
```

Guarda el `token` de esta respuesta para usarlo en todos los endpoints protegidos.

---

## 3) Usar el token en requests protegidos

El **`SessionAuthFilter`** busca el token en este orden:

1. `Authorization: Bearer <token>`
2. `X-Session-Token: <token>`

> Con **cualquiera de los dos** alcanza, no es necesario enviar ambos.


Si el token es válido y no expiró, el filtro pobla el `SecurityContext` con el email del usuario y sus **roles** (`ROLE_USUARIO`, etc.).

---

## 4) Logout (público)

Invalida el token actual. El endpoint acepta el token **por header** o **en el body**.

**Endpoint**

```
POST /api/auth/logout
```

**Headers**

Igual que en requests protegidos

**Body (opcional)**

```json
{
  "token": "0d5d7f74-2f1b-4d3a-9faa-2e8a0f2a7a2e"
}
```

---

## 5) Endpoints públicos vs protegidos

En `SecurityConfig` se permite acceso **sin token** a:

* `/swagger-ui/**`, `/v3/api-docs/**`, `/swagger`
* `/api/auth/**` (register, login, logout)

Todo lo demás **requiere** token válido.

> Si queres que un endpoint no requiera autenticación, agregalo a `.requestMatchers(...).permitAll()` en `SecurityConfig`.

---

## 6) Roles y autorización por método

Los roles se mapean a `SimpleGrantedAuthority` con prefijo `ROLE_`. Ejemplos de protección a nivel método/controlador:

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/api/admin/estadisticas")
public Estadisticas getStats() { ... }

@PreAuthorize("hasAnyRole('USUARIO','ADMIN')")
@PostMapping("/api/eventos")
public Evento crear(@RequestBody Evento req) { ... }
```

---

## 7) Configuración

* **`app.session.minutes`**: define la duración del token (por defecto `30`). Ejemplo en `application.properties`:

```
app.session.minutes=60
```

---


## 8) Tener en cuenta

* Intentar usar siempre `Authorization: Bearer` salvo que necesites el header alternativo.
* En endpoints sensibles, validar también **roles**.

### Ya existe un usuario por defecto con rol administrador:

**POST a login - Body**
```json
{
  "email": "admin@events.local",
  "password": "Admin1234"
}
```