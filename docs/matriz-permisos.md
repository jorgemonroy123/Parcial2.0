# Matriz de permisos v1.1 (10 roles × módulos)

| | |
|---|---|
| Tarjeta | D15 (v1.0, Sprint 1) · actualizada en el Sprint 2 |
| Responsable | Jorge Monroy |
| Revisa | Juan Hernández |
| Trazabilidad | REQ-ROL-005 · C-03 |
| Estado | v1.1 · pendiente de aprobación |

**Leyenda:** L leer · C crear · E editar · B borrar · A aprobar/ocultar · V votar o calificar · P publicar · X exportar ·
`—` sin acceso (el servidor responde 403). En «Comentarios de la encuesta», `L` significa ver los comentarios **aprobados** en la página pública.

## 1. Matriz
| Rol | Usuarios y roles | Catálogos | Ediciones y categorías | Nominaciones | Votación | Calificación del jurado | Resultados y rankings | Perfil del nominado | Comentarios de la encuesta | Noticias | Registro de auditoría | Tickets de soporte | Propuestas de cambio de perfil |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Administrador | CLEB | CLEB | L | L | — | — | L | CLEB | L | L | — | C (los suyos) | — |
| Organizador | — | L | CLEBA | CLEBA | — | — | L·P | L | L | L | — | C (los suyos) | **LA** |
| Votante | — | — | L | L | V | — | L | L | L | L | — | C (los suyos) | — |
| Nominado | — | — | L | L (las suyas) | — | — | L (los suyos) | L (el propio) | — | L | — | C (los suyos) | — |
| Jurado | — | — | L | L | — | V | L | L | — | L | — | C (los suyos) | — |
| Auditor | — | — | L | — | — | — | — | — | — | L (pública) | **L·X** | C (los suyos) | — |
| **Moderador** | — | — | L | L | — | — | L | L | **LA** | L | — | C (los suyos) | — |
| Periodista | — | — | L | L | — | — | L | L | — | **CLEP (las suyas)** | — | C (los suyos) | — |
| **Representante** | — | — | L | L | — | — | L | L (el suyo) | — | L | — | C (los suyos) | **CL (solo su nominado)** |
| **Soporte técnico** | — | — | — | — | — | — | — | — | — | L | — | **CLE (todos)** | — |

Público (no cuenta como rol): lee ediciones, nominaciones, resultados publicados, noticias y comentarios aprobados; puede comentar la encuesta.

## 2. Cambios
| Versión | Celda | Antes | Ahora | Motivo |
|---|---|---|---|---|
| v1.0 | Auditor · Noticias | — | L (pública) | La página `/publico/noticias` es abierta a cualquiera. |
| v1.0 | Periodista · Noticias | CLEP | CLEP (sin B) | No hay endpoint para borrar noticias. |
| v1.1 | Administrador · Tickets | L | C (los suyos) | Solo Soporte ve los tickets de todos; el Administrador puede crear los suyos como cualquier rol. |
| v1.1 | Todos · Tickets | C | C (los suyos) | REQ-SOP-003: cada usuario ve solo los que creó. |
| v1.1 | Soporte · Tickets | LE | CLE (todos) | Puede ver y cambiar el estado de todos, y crear los suyos. |
| v1.1 | Administrador · Propuestas de perfil | L | — | La bandeja es del Organizador; el Administrador edita jugadores directamente. |
| v1.1 | Moderador · Comentarios | LA | LA | Confirmado: cola de comentarios con aprobar u ocultar. |

Los permisos de los 5 roles originales siguen siendo los inferidos en la v0.1; deben confirmarse contra el comportamiento real.

## 3. Función exclusiva por rol (C-03) y cómo se verifica
| Rol | Función que ningún otro rol hace | Evidencia (ruta) | Estado |
|---|---|---|---|
| Administrador | Asignar roles y gestionar usuarios, jugadores y parámetros | `/admin/**` | Sprint 0 |
| Organizador | Abrir/cerrar periodos, aprobar nominaciones, publicar resultados y **decidir las propuestas de perfil** | `/organizador/**` | Sprint 0 y Sprint 2 |
| Votante | Emitir el voto único por categoría | `/votante/votar` | Sprint 0 |
| Nominado | Ver su propio perfil y resultados | `/nominado/perfil` | Sprint 0 |
| Jurado | Calificar de 1 a 10 a los nominados | `/jurado/evaluar` | Completado en Sprint 1 |
| Auditor | Leer el registro de actividad sin ver por quién se votó, ver anomalías y exportar | `/auditor/**` | Sprint 1 |
| Periodista | Redactar y publicar noticias | `/periodista/**` | Sprint 1 |
| Moderador | Aprobar u ocultar comentarios de la encuesta | `/moderador/**` | **Sprint 2** |
| Representante | Proponer cambios al perfil de su nominado | `/representante/**` | **Sprint 2** |
| Soporte técnico | Atender tickets y cambiar su estado | `/soporte/**` | **Sprint 2** |

Verificación automática de los 403: `AccesoPorRolTest` (cada ruta × cada rol que no es el propietario). Los tickets (`/tickets`) los puede usar cualquier rol: también se prueba.
