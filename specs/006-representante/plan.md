# plan.md — Representante · Puerta 2

| | |
|---|---|
| Tarjeta | D22 |
| Responsable | Juan Ortega |
| Revisa | Juan Hernández |
| Depende de | D21 |
| Trazabilidad | ADR-004 · C-05 · REQ-REP-001 a REQ-REP-003 |

## 1. Modelo de datos
- `Usuario.jugadorId` ya existe (lo usa el rol Nominado); ahora también lo usa el Representante.
- Colección `propuestas_perfil` — `model/PropuestaPerfil`: `id, jugadorId, jugadorNombre, representanteId, representanteUsername,
  biografia, partidos, goles, asistencias, trofeos, estado, fecha, decididoPor, decididoEn, motivo`.

## 2. Componentes
| Pieza | Archivo | Responsabilidad |
|---|---|---|
| Vínculo | `AdminControladorWeb/Rest`, `app.js` | El selector «Jugador asociado» aparece también para REPRESENTANTE; es obligatorio. |
| Representante | `RepresentanteControladorWeb/Rest` | Ver el perfil, validar y crear propuestas `PENDIENTE` (nunca toca al jugador). El jugador siempre es el vinculado. |
| Bandeja del Organizador | `PropuestaPerfilControladorWeb/Rest` | Listar por estado; aprobar (copia los valores al jugador) o rechazar (no lo toca). |
| Datos de prueba | `DataInitializer.vincularRepresentante` | `representante` queda vinculado al mismo jugador que `nominado`. |

## 3. Rutas y seguridad
| Quién | Web | API | Regla |
|---|---|---|---|
| Representante | `/representante/perfil`, `/representante/propuestas` | `GET /api/representante/perfil`, `GET/POST /api/representante/propuestas` | `hasRole("REPRESENTANTE")` |
| Organizador | `/organizador/propuestas-perfil`, `…/{id}/estado` | `GET /api/organizador/propuestas-perfil`, `PUT …/{id}/estado` | `hasRole("ORGANIZADOR")` |

## 4. Flujo
Representante propone → `PENDIENTE` (perfil sin cambios) → Organizador **Aprueba y publica** (`APROBADA`, el jugador recibe los valores)
o **Rechaza** (`RECHAZADA`, perfil intacto) → queda `decididoPor`, `decididoEn` y motivo.

## 5. Riesgos
| Riesgo | Mitigación |
|---|---|
| Que el cliente envíe otro `jugadorId` | El servidor ignora el campo y usa el jugador vinculado (prueba `aunqueElClienteMandeOtroJugador…`) |
| Aprobar una propuesta de un jugador eliminado | El Administrador no puede eliminar jugadores con usuario asociado; además se valida al aprobar |
