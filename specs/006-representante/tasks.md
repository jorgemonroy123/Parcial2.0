# tasks.md — Grupo 3 (parte Representante) · Puerta 3

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
| T023 | PBI-08 | Vínculo representante–nominado | Jorge Monroy | Juan Ortega | D23 | REQ-REP-003 | `AdminControladorWeb/Rest`, `static/js/app.js`, `DataInitializer` |
| T024 | PBI-08 | Colección de propuestas de cambio del perfil | Juan Ortega | Jorge Monroy | T023 | REQ-REP-001, ADR-004 | `model/PropuestaPerfil.java`, `repository/PropuestaPerfilRepository.java` |
| T025 | PBI-08 | Controladores Web y Rest del Representante: editar y proponer | Juan Ortega | Juan Hernández | T024 | REQ-REP-001, REQ-REP-003, C-08 | `RepresentanteControladorWeb/Rest`, `templates/representante/perfil.html` |
| T026 | PBI-08 | Bandeja del Organizador: aprobar, rechazar y publicar el cambio | Juan Hernández | Jorge Monroy | T024 | REQ-REP-002, C-05 | `PropuestaPerfilControladorWeb/Rest`, `templates/organizador/propuestas-perfil.html` |
| T027 | PBI-08 | Pruebas de aceptación (parte Representante) | Jorge Monroy | Juan Hernández | T025, T026 | AC-REP-002.1 | `RepresentanteTest` |

```
T023 ─► T024 ─► T025 ─┐
              └─► T026 ─┴─► T027
```

## Verificación
- [ ] `mvn test`: `RepresentanteTest` en verde.
- [ ] Como `admin`, crear un usuario REPRESENTANTE: pide el jugador asociado.
- [ ] Como `representante`: enviar una propuesta → el perfil público no cambia. Como `organizador`: aprobar → el perfil cambia; rechazar otra → queda igual.
