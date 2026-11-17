# Spec del Moderador · v1.0 (Puerta 1)

| | |
|---|---|
| Tarjetas | D20 (v0.1 y clarificación) · D21 (v1.0) |
| PBI | PBI-07 (y PBI-11 / CR-001 para el Sprint 3) |
| Responsable | Juan Hernández |
| Aprueba | Jorge Monroy |
| Estado | v1.0 · pendiente de aprobación |

## 1. Contexto
El público deja comentarios en la encuesta de satisfacción. Para mantener un entorno respetuoso, ningún comentario debe verse sin revisión.

## 2. Alcance
**Dentro:** estado de moderación de los comentarios, cola del Moderador y vista pública solo con comentarios aprobados.
**Fuera (CR-001, Sprint 3):** moderar también las propuestas de nominación (REQ-MOD-004).

## 3. Actor
| Rol | Puede | No puede |
|---|---|---|
| Moderador | Ver la cola de comentarios y aprobarlos u ocultarlos | Editar el texto de un comentario |

## 4. Requisitos (EARS)
| ID | Requisito |
|---|---|
| REQ-MOD-001 | **Evento:** cuando el público envía un comentario, el sistema deberá guardarlo en estado «Pendiente» y oculto. |
| REQ-MOD-002 | **Evento:** cuando el Moderador aprueba u oculta un comentario, el sistema deberá guardar la decisión con quién la tomó y cuándo. |
| REQ-MOD-003 | **No deseado:** si un comentario no está aprobado, entonces el sistema no deberá mostrarlo en la vista pública. |
| REQ-MOD-004 | *(CR-001 · Sprint 3)* **Evento:** cuando se registra una propuesta de nominación, el sistema deberá agregarla a la cola de moderación. |
| NFR-SEG-001 | Las rutas del Moderador (`/moderador/**`) deberán devolver 403 a cualquier otro rol (C-04). |

## 5. Criterios de aceptación
| ID | Criterio |
|---|---|
| AC-MOD-001.1 | **Dado** un comentario nuevo, **cuando** se guarda, **entonces** queda Pendiente y no se muestra al público. |
| AC-MOD-002.1 | **Dado** un comentario Pendiente, **cuando** el moderador lo aprueba, **entonces** se muestra al público; **si** lo oculta, no se muestra. |
| AC-MOD-003.1 | **Dado** un comentario Pendiente u Oculto, **cuando** un visitante abre la encuesta, **entonces** no lo ve. |

## 6. Clarificaciones
| # | Pregunta | Decisión |
|---|---|---|
| CL-01 | ¿Qué estados usa? | Los reutilizables de ADR-004: `PENDIENTE` → `APROBADA` \| `RECHAZADA`. En pantalla `RECHAZADA` se muestra como «Oculto». |
| CL-02 | ¿Y las calificaciones sin texto? | Solo estrellas: no hay nada que moderar y siguen visibles. |
| CL-03 | ¿Puede cambiar su decisión? | Sí: un comentario aprobado puede ocultarse después y viceversa; nunca vuelve a Pendiente. |
| CL-04 | ¿Qué pasa con los comentarios anteriores al sprint? | Nunca fueron moderados: al iniciar quedan `PENDIENTE` (y por tanto ocultos) hasta que el Moderador decida. |
| CL-05 | ¿El comentario oculto afecta los reportes? | Se oculta el **texto**; las estrellas siguen contando en los conteos de satisfacción. Ningún reporte muestra el texto de comentarios. |
