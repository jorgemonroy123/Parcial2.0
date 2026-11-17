# plan.md — Soporte técnico · Puerta 2

| | |
|---|---|
| Tarjeta | D22 |
| Responsable | Juan Ortega |
| Revisa | Juan Hernández |
| Depende de | D21 |
| Trazabilidad | REQ-SOP-001 a REQ-SOP-003 · C-08 |

## 1. Modelo de datos
Colección `tickets` — `model/Ticket`: `id, asunto, descripcion, estado, creadoPorId, creadoPorUsername, creadoPorRol, fechaCreacion, historial[]`.
`model/CambioEstadoTicket` (embebido): `estado, fecha, responsable`.

## 2. Componentes
| Pieza | Archivo | Responsabilidad |
|---|---|---|
| Tickets de cualquier rol | `TicketControladorWeb/Rest` | Validar, crear (`ABIERTO`), listar solo los propios, detalle propio. |
| Soporte | `SoporteControladorWeb/Rest` | Bandeja (abiertos primero), validar y aplicar cambio de estado con rastro. |
| Menú y panel | `layout.html` (menú «Ayuda»), `InicioControladorWeb` (tarjeta «Mis tickets») | «Crear ticket» y «Mis tickets» para todos los roles. |
| Estados | `config/Estados` | Constantes de estado de ticket (y de aprobación, ver ADR-004). |

## 3. Rutas y seguridad
| Quién | Web | API | Regla |
|---|---|---|---|
| Cualquier rol autenticado | `/tickets`, `/tickets/nuevo`, `/tickets/crear` | `GET/POST /api/tickets`, `GET /api/tickets/{id}` | autenticado |
| Soporte | `/soporte/tickets`, `/soporte/tickets/{id}/estado` | `GET /api/soporte/tickets`, `PUT /api/soporte/tickets/{id}/estado` | `hasRole("SOPORTE")` |

## 4. Flujo
Usuario crea → `ABIERTO` (historial: creación) → Soporte «Atender» → `EN_ATENCION` (historial: estado, fecha, responsable) → «Resolver» → `RESUELTO`.
