# tasks.md — Grupo 2 (parte Auditor) · Puerta 3

| | |
|---|---|
| Tarjeta | D17 |
| Responsable | Juan Hernández |
| Revisa | Juan Ortega |
| Depende de | D16 |
| Trazabilidad | C-06 (ninguna tarea sin requisito) |
| Estado | Implementadas · pendientes de revisión y de `mvn test` |

| ID | PBI | Tarea | Resp. | Revisa | Depende | Trazabilidad | Archivos |
|---|---|---|---|---|---|---|---|
| T009 | PBI-04 | Colección y servicio de auditoría | Juan Ortega | Jorge Monroy | D17 | REQ-AUD-001, ADR-003 | `model/RegistroAuditoria.java`, `repository/RegistroAuditoriaRepository.java`, `config/Auditoria.java` |
| T010 | PBI-04 | Registrar cada voto y calificación sin el nominado elegido | Juan Ortega | Jorge Monroy | T009 | REQ-AUD-001, NFR-AUD-001, C-01, C-02 | `VotanteControladorWeb/Rest`, `JuradoControladorWeb/Rest`, `DataInitializer` |
| T011 | PBI-04 | Controladores Web y Rest del Auditor (solo lectura) | Juan Hernández | Juan Ortega | T009, T007 | REQ-AUD-002, NFR-REN-001, C-08 | `AuditorControladorWeb/Rest`, `templates/auditor/registros.html` |
| T012 | PBI-04 | Anomalías de voto duplicado y exportación del reporte | Jorge Monroy | Juan Hernández | T011 | REQ-AUD-003, REQ-AUD-004 | `AuditorControladorRest`, `templates/auditor/anomalias.html` |
| T016 | PBI-04 | Pruebas de aceptación (parte Auditor) | Juan Ortega | Juan Hernández | T012 | AC-AUD-001.1, AC-AUD-004.1 | `src/test/java/com/goldenawards/AuditoriaTest.java` |

> «Servicio de auditoría» se implementó **sin clases `service`**: es una utilidad estática (`config/Auditoria.java`).

```
T009 ─► T010
T009 ─► T011 ─► T012 ─► T016
```

## Verificación
- [ ] `mvn test`: `AuditoriaTest` y `AccesoPorRolTest` en verde.
- [ ] Votar como `votante` y ver el registro como `auditor` (sin nominado en ninguna columna).
- [ ] Revisión del diff (C-09) y bitácora de IA.
