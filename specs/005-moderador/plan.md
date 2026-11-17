# plan.md — Moderador · Puerta 2

| | |
|---|---|
| Tarjeta | D22 |
| Responsable | Juan Ortega |
| Revisa | Juan Hernández |
| Depende de | D21 |
| Trazabilidad | ADR-004 · C-05 · REQ-MOD-001 a REQ-MOD-003 |

## 1. Modelo de datos
Se amplía `model/Feedback` (los comentarios de la encuesta) con: `estadoComentario` (`PENDIENTE`/`APROBADA`/`RECHAZADA`),
`moderadoPor` (usuario) y `moderadoEn` (fecha). `FeedbackRepository.findByEstadoComentario(estado)`.

## 2. Componentes
| Pieza | Archivo | Responsabilidad |
|---|---|---|
| Estados reutilizables | `config/Estados` | Constantes de estado, `estadoInicialComentario`, `comentarioPublico`. |
| Alta de comentarios | `PublicoControladorWeb/Rest` | Fijan el estado inicial **en el servidor** (el cliente no puede enviarlo). |
| Vista pública | `PublicoControladorWeb` | Filtra con `Estados::comentarioPublico`: aprobados o sin texto. |
| Moderador | `ModeradorControladorWeb/Rest` | Cola por estado, validar y aplicar la decisión (quién y cuándo). |
| Migración | `DataInitializer.migrarComentarios` | Comentarios existentes sin estado pasan a `PENDIENTE`. |

## 3. Rutas y seguridad
| Web | API | Regla |
|---|---|---|
| `/moderador/comentarios`, `/moderador/comentarios/{id}/estado` | `GET /api/moderador/comentarios?estado=`, `PUT /api/moderador/comentarios/{id}/estado?estado=` | `hasRole("MODERADOR")` |

## 4. Flujo
Público comenta → `PENDIENTE` (oculto) → Moderador **Aprueba** (`APROBADA`, visible) u **Oculta** (`RECHAZADA`) → queda `moderadoPor` y `moderadoEn`.
