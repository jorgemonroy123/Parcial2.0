# Golden Awards

Plataforma de gestión, nominación y votación de premios deportivos.

**Java 17 · Spring Boot 3.2 · MongoDB Atlas · Thymeleaf · Bootstrap 5 · JWT**

## Estructura importante

```
com.goldenawards
├── controller/          ← Web + Rest en la MISMA carpeta
│   ├── *ControladorWeb.java
│   ├── *ControladorRest.java
│   ├── AuthControladorRest.java      (login JWT)
│   ├── AuditorControladorWeb/Rest    (rol Auditor: registro, anomalías, exportación)
│   ├── PeriodistaControladorWeb/Rest (rol Periodista: noticias)
│   ├── NoticiaControladorWeb/Rest    (noticias públicas)
│   ├── JuradoControladorWeb/Rest     (rol Jurado)
│   ├── TicketControladorWeb/Rest     (tickets de cualquier rol)
│   ├── SoporteControladorWeb/Rest    (rol Soporte: bandeja y estados)
│   ├── ModeradorControladorWeb/Rest  (rol Moderador: cola de comentarios)
│   ├── RepresentanteControladorWeb/Rest (rol Representante: propuestas de perfil)
│   ├── PropuestaPerfilControladorWeb/Rest (bandeja del Organizador)
│   ├── PublicoControladorWeb.java
│   └── PublicoControladorRest.java
├── security/jwt/
│   ├── JwtUtil.java                  ← generación/validación JWT
│   └── JwtAuthFilter.java            ← filtro Bearer para /api/**
├── gateway/
│   └── ApiGatewayController.java     ← mapa de rutas /api/gateway/**
├── config/              SecurityConfig, DataInitializer, Auditoria (registro de votación), Estados (estados de aprobación), Ponderacion
├── model/
└── repository/
```

## JWT, Gateway y “microservicios”

| Componente | Ubicación | Qué hace |
|------------|-----------|----------|
| **JWT** | `security/jwt/JwtUtil.java` + `JwtAuthFilter.java` | Login en `POST /api/auth/login` devuelve Bearer token. El filtro valida tokens en rutas `/api/**` protegidas. |
| **API Gateway** | `gateway/ApiGatewayController.java` | Punto de entrada documentado: `GET /api/gateway/routes` y `/api/gateway/health`. En producción sería Spring Cloud Gateway; aquí centraliza el mapa de servicios. |
| **Microservicios lógicos** | Misma app, paquetes por dominio | Auth, Admin, Organizador, Votante, Nominado, Resultados, Público. Listos para extraer a jars independientes manteniendo los mismos paths `/api/...`. |

No son procesos separados en este prototipo (un solo deploy Spring Boot), pero la API y el JWT siguen el contrato de microservicios + gateway.

### Ejemplo JWT

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Usar el token:
curl http://localhost:8080/api/admin/... -H "Authorization: Bearer <token>"
```

## Área pública (sin ser votante)

| Ruta | Función |
|------|---------|
| `/publico/catalogo` | Filtros: edición, categoría, estado, búsqueda, orden A-Z |
| `/publico/comparar` | Comparar 2–3 nominados (bio + goles, asistencias, partidos, trofeos) |
| `/publico/feedback` | Encuesta estrellas + comentario |
| `/publico/estadisticas` | Gráficos Chart.js (barras / dona) |

API pública: `/api/publico/catalogo`, `/api/publico/feedback`, `/api/publico/estadisticas`

## Los 10 roles (Sprint 1)

| Rol | Usuario | Contraseña | Módulo |
|-----|---------|------------|--------|
| Administrador | `admin` | `admin123` | Usuarios, jugadores, parámetros |
| Organizador | `organizador` | `organizador123` | Ediciones, categorías, nominaciones |
| Votante | `votante` | `votante123` | Votar, proponer, historial |
| Nominado | `nominado` | `nominado123` | Perfil y resultados |
| Jurado | `jurado` | `jurado123` | Calificar nominados de 1 a 10 |
| **Auditor** | `auditor` | `auditor123` | Registro de votación (solo lectura), anomalías, exportar CSV |
| **Periodista** | `periodista` | `periodista123` | Redactar y publicar noticias |
| **Moderador** | `moderador` | `moderador123` | Cola de comentarios: aprobar u ocultar |
| **Representante** | `representante` | `representante123` | Propone cambios al perfil de su nominado |
| **Soporte técnico** | `soporte` | `soporte123` | Bandeja de tickets y cambio de estado |

Extras: `votante2` y `votante3` (`votante123`). El rol *Público* no cuenta como rol: es el acceso abierto sin iniciar sesión.

Cada ruta de un rol responde **403** a los demás roles (`SecurityConfig`). Al arrancar, `DataInitializer` agrega los usuarios que falten
aunque la base ya tenga datos.

### Auditor
- Web: `/auditor/registros` (filtros por edición, categoría y tipo), `/auditor/anomalias`, `/auditor/exportar` (CSV con `;`).
- API: `GET /api/auditor/registros`, `/anomalias`, `/exportar`.
- El registro guarda **quién votó o calificó, cuándo y en qué categoría; nunca por quién ni la nota** (ver `docs/adr/ADR-003-auditoria-anonima.md`).
  Los intentos de segundo voto rechazados quedan como `INTENTO_DUPLICADO`.

### Periodista
- Web: `/periodista/noticias` (crear, editar, publicar). API: `/api/periodista/noticias`.
- Público (sin sesión): `/publico/noticias` y `/publico/noticias/{id}` · API `/api/publico/noticias`. Un borrador responde 404.

### Sprint 2
- **Tickets (todos los roles):** menú **Ayuda → Crear ticket / Mis tickets** (`/tickets`, `/api/tickets`). Cada usuario ve solo los suyos.
- **Soporte:** `/soporte/tickets` · API `/api/soporte/tickets` (estados `ABIERTO` → `EN_ATENCION` → `RESUELTO`, con historial de quién y cuándo).
- **Moderador:** `/moderador/comentarios` · API `/api/moderador/comentarios`. Los comentarios de la encuesta nacen **pendientes y ocultos**;
  solo los aprobados se ven en `/publico/feedback`. Los comentarios que ya existían quedan pendientes.
- **Representante:** `/representante/perfil` · API `/api/representante/propuestas`. Se vincula a un nominado desde *Usuarios* (como el rol Nominado).
  Sus propuestas **no cambian el perfil** hasta que el Organizador las apruebe en `/organizador/propuestas-perfil`.

## Pruebas

```bash
mvn test
```

- `AccesoPorRolTest`: cada ruta × cada rol → 403 si no es el propietario (no necesita MongoDB).
- `AuditoriaTest`: voto/calificación sin nominado, exportación por edición, anomalías, límite de consulta.
- `NoticiasTest`: borrador no visible, publicar, autor y edición.
- `SoporteTest`, `ModeracionTest`, `RepresentanteTest`: criterios de aceptación del Sprint 2.

## Documentación SDD

`specs/001-roles-base` a `specs/006-representante` (spec, plan y tasks por rol), `docs/adr/` (ADR-003, ADR-004), `docs/cambios/CR-001.md`,
`docs/matriz-permisos.md` y `docs/sprint-1/` y `docs/sprint-2/` (trazabilidad y cierre).

## Ejecutar

```bash
mvn spring-boot:run
```

http://localhost:8080

Usuarios de prueba: ver la tabla de roles de arriba o la pantalla de login.
