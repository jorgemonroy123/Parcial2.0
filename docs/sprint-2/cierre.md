# Cierre documental del Sprint 2: Review, Retro y acta

| | |
|---|---|
| Tarjeta | D26 |
| Responsable | Juan Hernández |
| Participa | Equipo |
| Depende de | D25 |

## 1. Meta del sprint
Completar los tres roles que quedaban con módulo propio — **Soporte técnico** (PBI-06), **Moderador** (PBI-07) y
**Representante del nominado** (PBI-08) — y registrar el CR-001 como PBI-11.

## 2. Resultado
| Entregable | Estado |
|---|---|
| T017–T019 · tickets de cualquier rol, bandeja de Soporte, menú «Ayuda» | Implementado |
| T020–T022 · comentarios pendientes, cola del Moderador, vista pública solo aprobados | Implementado |
| T023–T026 · vínculo representante–nominado, propuestas de perfil, bandeja del Organizador | Implementado |
| T027 · pruebas de aceptación | Escritas; **pendiente ejecutarlas** con `mvn test` |
| D20–D25 · especificaciones, planes, tareas, ADR-004, CR-001, trazabilidad | Redactados; pendientes de revisión/aprobación |

## 3. Decisiones y hallazgos
1. **Comentarios existentes:** como nunca fueron moderados, al iniciar pasan a `PENDIENTE` y dejan de verse en la encuesta pública hasta que el Moderador los apruebe.
2. **Menú:** «Crear ticket» y «Mis tickets» están en el menú **Ayuda**, visible para los 10 roles.
3. **Matriz de permisos** actualizada a v1.1 (ver tabla de cambios).
4. **Se eliminó** la página provisional `RolProximamenteControladorWeb` del Sprint 1.
5. **Pendiente de Sprint 3:** CR-001 (moderar propuestas de nominación), con preguntas abiertas en `docs/cambios/CR-001.md`.

## 4. Demostración (Review)
1. Con cualquier rol: menú **Ayuda → Crear ticket**, y ver el ticket en **Mis tickets**.
2. Como `soporte`: abrir la bandeja, pasar el ticket a *En atención* y a *Resuelto*; abrir el historial (estado, fecha, responsable).
3. Sin sesión: enviar un comentario en `/publico/feedback` y comprobar que **no** aparece.
4. Como `moderador`: aprobarlo y verlo en la encuesta pública; ocultar otro y comprobar que no aparece.
5. Como `representante`: enviar una propuesta de perfil y comprobar que el perfil público **no** cambia.
6. Como `organizador`: abrir *Propuestas de perfil*, aprobar una (el perfil cambia) y rechazar otra (queda igual).
7. Como `votante`: abrir `/soporte/tickets`, `/moderador/comentarios` y `/representante/perfil` → 403.

## 5. Retrospectiva (completar en la reunión)
| Qué salió bien | Qué mejorar | Acción para el Sprint 3 | Responsable |
|---|---|---|---|
| | | | |

## 6. Impedimentos y riesgos
| # | Descripción | Estado |
|---|---|---|
| 1 | Pruebas sin ejecutar todavía en el entorno del equipo (Sprints 1 y 2) | Abierto |
| 2 | Documentos del Sprint 0 (constitución, ADR-001/002, DoR/DoD, etc.) no están en el repositorio y los demás documentos los citan | Abierto |
| 3 | Confirmar permisos reales de los 5 roles originales en la matriz | Abierto |
| 4 | Responder las preguntas abiertas del CR-001 antes de planear el Sprint 3 | Abierto |

## 7. Bitácora de IA (C-09)
El código y los documentos de este sprint fueron generados con ayuda de IA y deben ser revisados por una persona distinta de quien dirigió la IA.

| Tarjeta | Qué generó la IA | Revisor (según el tablero) | Revisado |
|---|---|---|---|
| T017 | Modelo y lógica de tickets | Juan Hernández | ☐ |
| T018 | Bandeja y cambio de estado de Soporte | Juan Ortega | ☐ |
| T019 | Pantallas de tickets, menú Ayuda, tarjeta del panel | Jorge Monroy | ☐ |
| T020 | Estado de moderación de comentarios y `Estados` | Jorge Monroy | ☐ |
| T021 | Controladores y cola del Moderador | Juan Hernández | ☐ |
| T022 | Filtro público de comentarios aprobados | Juan Ortega | ☐ |
| T023 | Vínculo representante–nominado | Juan Ortega | ☐ |
| T024 | Modelo de propuestas de perfil | Jorge Monroy | ☐ |
| T025 | Controladores y pantalla del Representante | Juan Hernández | ☐ |
| T026 | Bandeja del Organizador | Jorge Monroy | ☐ |
| T027 | `SoporteTest`, `ModeracionTest`, `RepresentanteTest`, `AccesoPorRolTest` | Juan Hernández | ☐ |
| D20–D26 | Documentos SDD | según cada tarjeta | ☐ |

Quién dirigió a la IA en cada tarjeta: _completar_.

## 8. Acta
Fecha: ______ · Asistentes: ______________________ · Acuerdos: ______________________
