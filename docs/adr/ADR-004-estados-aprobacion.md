# ADR-004 — Estados de aprobación reutilizables (Pendiente → Aprobado / Rechazado)

- **Estado:** Propuesto (Sprint 2) · pendiente de aceptación del equipo
- **Tarjeta:** D22
- **Trazabilidad:** C-05 · REQ-MOD-001 · REQ-MOD-002 · REQ-REP-001 · REQ-REP-002 · CR-001 (REQ-MOD-004)

## Contexto
En el Sprint 2 aparecen dos flujos más en los que alguien propone algo y otro rol decide: los **comentarios** del público (decide el
Moderador) y las **propuestas de perfil** del Representante (decide el Organizador). Ya existía un tercero, las **nominaciones**
(proponen los votantes, decide el Organizador), y el CR-001 añadirá la moderación de propuestas de nominación. La Constitución (C-05)
exige estados explícitos con quién decidió y cuándo.

## Decisión
Todos los flujos de aprobación usan el **mismo ciclo y las mismas palabras**: `PENDIENTE` → `APROBADA` | `RECHAZADA`, definidos
una sola vez en `config/Estados`. Reglas comunes:
1. Un registro nuevo nace `PENDIENTE` y **no modifica ni se muestra en datos públicos** hasta que se decide.
2. Toda decisión guarda **quién** la tomó y **cuándo** (`moderadoPor/moderadoEn` en comentarios; `decididoPor/decididoEn` en propuestas).
3. El estado inicial lo fija **el servidor**, nunca el cliente.
4. Una decisión no vuelve a `PENDIENTE`; solo las propuestas de perfil son definitivas (los comentarios pueden cambiar entre aprobado y oculto).
5. La interfaz reutiliza las mismas insignias (`estado-pendiente`, `estado-aprobada`, `estado-rechazada`); en comentarios `RECHAZADA` se muestra como «Oculto».

Los **tickets** no entran en este ciclo: tienen su propio flujo de atención (`ABIERTO` → `EN_ATENCION` → `RESUELTO`) con historial.

## Alternativas consideradas
| Alternativa | Por qué no |
|---|---|
| Un enum o textos distintos por módulo | Cada flujo nuevo reinventaría estados y se perdería la coherencia que pide C-05. |
| Una colección genérica «Aprobación» que apunte a cualquier objeto | Más flexible, pero obliga a resolver referencias polimórficas y complica las consultas para un volumen pequeño. |

## Consecuencias
- (+) El CR-001 (propuestas de nominación) puede reutilizar estados, insignias y reglas sin diseño nuevo.
- (+) Fácil de probar: las mismas comprobaciones sirven para los tres flujos.
- (−) Los estados viven como texto (`String`) en cada colección; no hay un tipo que impida un valor inválido, así que cada controlador valida con `Estados.esDecision(...)`.
- (−) Los datos de decisión están repetidos en cada colección (no hay un historial común de decisiones).
