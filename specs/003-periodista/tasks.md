# tasks.md — Grupo 2 (parte Periodista) · Puerta 3

| | |
|---|---|
| Tarjeta | D17 |
| Responsable | Juan Hernández |
| Revisa | Juan Ortega |
| Depende de | D16 |
| Trazabilidad | C-06 |
| Estado | Implementadas · pendientes de revisión y de `mvn test` |

| ID | PBI | Tarea | Resp. | Revisa | Depende | Trazabilidad | Archivos |
|---|---|---|---|---|---|---|---|
| T013 | PBI-05 | Colección y servicio de noticias | Jorge Monroy | Juan Hernández | D17 | REQ-PER-001, REQ-PER-003 | `model/Noticia.java`, `repository/NoticiaRepository.java` |
| T014 | PBI-05 | Controladores Web y Rest del Periodista | Juan Hernández | Juan Ortega | T013, T007 | REQ-PER-001, REQ-PER-002, C-08 | `PeriodistaControladorWeb/Rest`, `templates/periodista/noticias.html` |
| T015 | PBI-05 | Página pública de noticias | Jorge Monroy | Juan Ortega | T013 | REQ-PER-002 | `NoticiaControladorWeb/Rest`, `templates/publico/noticias.html`, `noticia.html` |
| T016 | PBI-05 | Pruebas de aceptación (parte Periodista) | Juan Ortega | Juan Hernández | T014, T015 | AC-PER-001.1, AC-PER-002.1 | `src/test/java/com/goldenawards/NoticiasTest.java` |

> «Servicio de noticias» sin clase `service`: la lógica vive en métodos públicos de `PeriodistaControladorRest`.

```
T013 ─► T014 ─┐
T013 ─► T015 ─┴─► T016
```

## Verificación
- [ ] `mvn test`: `NoticiasTest` en verde.
- [ ] Publicar una noticia como `periodista` y abrir `/publico/noticias` sin sesión; el borrador no aparece.
