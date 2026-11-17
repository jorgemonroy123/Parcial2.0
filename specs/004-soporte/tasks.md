# tasks.md — Grupo 3 (parte Soporte) · Puerta 3

| | |
|---|---|
| Tarjeta | D23 |
| Responsable | Juan Hernández |
| Revisa | Juan Ortega |
| Depende de | D22 |
| Trazabilidad | C-06 |
| Estado | Implementadas · pendientes de revisión y de `mvn test` |

| ID | PBI | Tarea | Resp. | Revisa | Depende | Trazabilidad | Archivos |
|---|---|---|---|---|---|---|---|
| T017 | PBI-06 | Colección y servicio de tickets (cualquier rol) | Jorge Monroy | Juan Hernández | D23 | REQ-SOP-001, REQ-SOP-003 | `model/Ticket.java`, `model/CambioEstadoTicket.java`, `repository/TicketRepository.java`, `TicketControladorRest` |
| T018 | PBI-06 | Controladores Web y Rest del Soporte: bandeja y estados | Jorge Monroy | Juan Ortega | T017 | REQ-SOP-002, C-08 | `SoporteControladorWeb/Rest`, `templates/soporte/tickets.html` |
| T019 | PBI-06 | Crear ticket y Mis tickets en el menú de todos los roles `[P]` | Juan Hernández | Jorge Monroy | T017 | REQ-SOP-001, REQ-SOP-003 | `TicketControladorWeb`, `templates/tickets/*`, `layout.html`, `InicioControladorWeb` |
| T027 | PBI-08 | Pruebas de aceptación (parte Soporte) | Jorge Monroy | Juan Hernández | T018 | AC-SOP-002.1 | `SoporteTest`, `AccesoPorRolTest` |

> «Servicio de tickets» sin clase `service`: la lógica vive en métodos públicos de `TicketControladorRest` y `SoporteControladorRest`.

```
T017 ─► T018 ─┐
T017 ─► T019  │ [P]
              └─► T027
```

## Verificación
- [ ] `mvn test`: `SoporteTest` y `AccesoPorRolTest` en verde.
- [ ] Con cada rol: crear un ticket desde el menú **Ayuda** y verlo en *Mis tickets*.
- [ ] Como `soporte`: pasar el ticket a *En atención* y comprobar fecha y responsable en su historial.
