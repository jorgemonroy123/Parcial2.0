# Spec de Soporte técnico · v1.0 (Puerta 1)

| | |
|---|---|
| Tarjetas | D20 (v0.1 y clarificación) · D21 (v1.0) |
| PBI | PBI-06 |
| Responsable | Juan Hernández |
| Aprueba | Jorge Monroy |
| Estado | v1.0 · pendiente de aprobación |

## 1. Contexto
Cualquier usuario puede tener un problema con la plataforma. Necesita un canal para pedir ayuda y que Soporte técnico le dé seguimiento.

## 2. Alcance
**Dentro:** creación de tickets por cualquier rol, «Mis tickets», bandeja de Soporte y cambio de estado con rastro.
**Fuera:** chat, adjuntos, notificaciones por correo.

## 3. Actores
| Rol | Puede | No puede |
|---|---|---|
| Cualquier rol | Crear tickets y ver **solo los suyos** | Ver tickets ajenos |
| Soporte técnico | Ver todos los tickets y cambiar su estado | — |

## 4. Requisitos (EARS)
| ID | Requisito |
|---|---|
| REQ-SOP-001 | **Evento:** cuando un usuario crea un ticket con asunto y descripción, el sistema deberá registrarlo en estado «Abierto». |
| REQ-SOP-002 | **Evento:** cuando Soporte cambia el estado de un ticket, el sistema deberá registrar el estado, la fecha y el responsable. |
| REQ-SOP-003 | **Estado:** mientras un usuario tenga cualquier rol, el sistema deberá mostrarle en su menú «Crear ticket» y «Mis tickets», y solo le mostrará los tickets que él creó. |
| NFR-SEG-001 | Las rutas de la bandeja (`/soporte/**`) deberán devolver 403 a cualquier rol distinto de Soporte (C-04). |
| NFR-DAT-001 | El ticket guarda solo asunto, descripción y quién lo creó (C-07); el formulario advierte no escribir contraseñas ni datos personales. |

## 5. Criterios de aceptación
| ID | Criterio |
|---|---|
| AC-SOP-001.1 | **Dado** un votante, **cuando** envía un ticket, **entonces** se crea en estado Abierto a su nombre. |
| AC-SOP-002.1 | **Dado** un ticket Abierto, **cuando** Soporte lo pasa a «En atención», **entonces** guarda estado, fecha y quién lo hizo. |
| AC-SOP-003.1 | **Dado** un ticket de otro usuario, **cuando** intento abrirlo, **entonces** el sistema responde como si no existiera (404). |

## 6. Clarificaciones
| # | Pregunta | Decisión |
|---|---|---|
| CL-01 | ¿Qué estados tiene un ticket? | `ABIERTO` → `EN_ATENCION` → `RESUELTO`. |
| CL-02 | ¿Qué cambios permite Soporte? | A `EN_ATENCION` o `RESUELTO`, siempre distinto del actual; nunca vuelve a `ABIERTO`. Un ticket resuelto puede reabrirse pasándolo a `EN_ATENCION`. |
| CL-03 | ¿Cómo queda el rastro? | Cada ticket guarda un historial de cambios (estado, fecha, responsable); la creación es la primera entrada. |
| CL-04 | ¿Qué ve un usuario de un ticket ajeno? | Un 404, para no revelar que existe. |
| CL-05 | ¿Límites de texto? | Asunto 100 y descripción 1.000 caracteres. |
