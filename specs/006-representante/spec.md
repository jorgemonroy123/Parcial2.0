# Spec del Representante · v1.0 (Puerta 1)

| | |
|---|---|
| Tarjetas | D20 (v0.1 y clarificación) · D21 (v1.0) |
| PBI | PBI-08 |
| Responsable | Juan Hernández |
| Aprueba | Jorge Monroy |
| Estado | v1.0 · pendiente de aprobación |

## 1. Contexto
Hoy solo el Administrador edita el perfil de un jugador. El representante del nominado conoce sus datos, pero la información pública
debe seguir controlada: sus cambios los aprueba el Organizador antes de publicarse.

## 2. Alcance
**Dentro:** vínculo representante–nominado, propuestas de cambio de biografía y estadísticas, bandeja del Organizador.
**Fuera:** subir documentos o fotos; historial de versiones del perfil.

## 3. Actores
| Rol | Puede | No puede |
|---|---|---|
| Representante | Ver el perfil de **su** nominado y proponer cambios | Cambiar el perfil público directamente; actuar sobre otro nominado |
| Organizador | Aprobar o rechazar propuestas (y con ello publicar) | — |

## 4. Requisitos (EARS)
| ID | Requisito |
|---|---|
| REQ-REP-001 | **Evento:** cuando el representante edita la biografía o las estadísticas, el sistema deberá crear una propuesta en estado «Pendiente» sin modificar el perfil público. |
| REQ-REP-002 | **Evento:** cuando el organizador aprueba la propuesta, el sistema deberá publicar el cambio; si rechaza, deberá conservar el perfil actual. |
| REQ-REP-003 | **Estado:** mientras un usuario tenga el rol Representante, el sistema deberá permitirle proponer cambios únicamente sobre el nominado al que está vinculado. |
| NFR-SEG-001 | Las rutas del Representante (`/representante/**`) deberán devolver 403 a cualquier otro rol (C-04). |

## 5. Criterios de aceptación
| ID | Criterio |
|---|---|
| AC-REP-001.1 | **Dado** que edito la biografía, **cuando** envío el cambio, **entonces** se crea una propuesta Pendiente y el perfil no cambia. |
| AC-REP-002.1 | **Dado** una propuesta Pendiente, **cuando** el organizador la aprueba, **entonces** el perfil muestra el cambio; **si** la rechaza, queda igual. |
| AC-REP-003.1 | **Dado** un representante vinculado al nominado A, **cuando** intenta proponer sobre el nominado B, **entonces** la propuesta se crea sobre A (el cliente no elige el jugador). |

## 6. Clarificaciones
| # | Pregunta | Decisión |
|---|---|---|
| CL-01 | ¿Qué campos se pueden proponer? | Biografía y las estadísticas públicas del jugador: partidos, goles, asistencias y trofeos. |
| CL-02 | ¿Cómo se vincula un representante a un nominado? | El Administrador lo asigna al crear o editar el usuario (igual que el rol Nominado). |
| CL-03 | ¿Cuántas propuestas pendientes puede haber por jugador? | Una; hasta que se decida no se admite otra. |
| CL-04 | ¿Se admite una propuesta idéntica al perfil actual? | No: debe cambiar algo. |
| CL-05 | ¿Qué queda al decidir? | Estado, quién decidió, cuándo y un motivo opcional (C-05). |
| CL-06 | Límites | Biografía 1.000 caracteres; estadísticas de 0 a 9.999; motivo 200 caracteres. |
