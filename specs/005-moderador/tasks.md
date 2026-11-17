# tasks.md — Grupo 3 (parte Moderador) · Puerta 3

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
| T020 | PBI-07 | Comentarios nuevos quedan en estado Pendiente | Juan Ortega | Jorge Monroy | D23 | REQ-MOD-001, ADR-004 | `model/Feedback.java`, `config/Estados.java`, `FeedbackRepository`, `PublicoControladorWeb/Rest`, `DataInitializer` |
| T021 | PBI-07 | Controladores Web y Rest del Moderador: cola, aprobar u ocultar | Juan Ortega | Juan Hernández | T020 | REQ-MOD-002, C-05, C-08 | `ModeradorControladorWeb/Rest`, `templates/moderador/comentarios.html` |
| T022 | PBI-07 | Vista pública solo con comentarios aprobados | Juan Hernández | Juan Ortega | T020 | REQ-MOD-003 | `PublicoControladorWeb` (filtro `Estados::comentarioPublico`) |
| T027 | PBI-08 | Pruebas de aceptación (parte Moderador) | Jorge Monroy | Juan Hernández | T022 | AC-MOD-002.1 | `ModeracionTest` |

```
T020 ─► T021 ─┐
T020 ─► T022 ─┴─► T027
```

## Verificación
- [ ] `mvn test`: `ModeracionTest` en verde.
- [ ] Enviar un comentario en `/publico/feedback`: no aparece; como `moderador`, aprobarlo y verlo; ocultarlo y comprobar que desaparece.
