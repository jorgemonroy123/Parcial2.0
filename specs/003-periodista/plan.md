# plan.md — Periodista · Puerta 2

| | |
|---|---|
| Tarjeta | D16 |
| Responsable | Juan Ortega |
| Revisa | Juan Hernández |
| Depende de | D14 (spec v1.0) |
| Trazabilidad | REQ-PER-001 a REQ-PER-003 |

## 1. Enfoque
Mismo patrón (C-08): `PeriodistaControladorWeb/Rest` para el rol y `NoticiaControladorWeb/Rest` para la página pública. Sin `service`.

## 2. Modelo de datos
Colección `noticias` — `model/Noticia`: `id, titulo, resumen, contenido, edicionId, autorId, autorNombre, estado, fechaCreacion, fechaPublicacion`.
`estado` ∈ `BORRADOR`, `PUBLICADA`.

## 3. Componentes
| Pieza | Archivo | Responsabilidad |
|---|---|---|
| Periodista | `PeriodistaControladorWeb/Rest` | Crear borrador, editar, publicar (solo las propias). |
| Noticias públicas | `NoticiaControladorWeb/Rest` | Lista y detalle de noticias `PUBLICADA`; un borrador responde 404. |

## 4. Rutas y seguridad
| Rol | Web | API | Regla |
|---|---|---|---|
| Periodista | `/periodista/noticias`, `…/guardar`, `…/{id}/publicar` | `/api/periodista/noticias` (GET, POST), `…/{id}` (PUT), `…/{id}/publicar` (PUT) | `hasRole("PERIODISTA")` |
| Público | `/publico/noticias`, `/publico/noticias/{id}` | `/api/publico/noticias`, `/{id}` | abierto |

## 5. Flujo
Periodista crea `BORRADOR` → edita → publica (`PUBLICADA` + fecha) → aparece en `/publico/noticias`.
