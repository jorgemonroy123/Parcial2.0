# plan.md — Auditor · Puerta 2

| | |
|---|---|
| Tarjeta | D16 (junto con ADR-003 y el plan del Periodista) |
| Responsable | Juan Ortega |
| Revisa | Juan Hernández |
| Depende de | D14 (spec v1.0) |
| Trazabilidad | ADR-003 · NFR-AUD-001 · C-02 |

## 1. Enfoque
Patrón del Jurado (C-08): par `AuditorControladorWeb` / `AuditorControladorRest` en `controller/`, plantillas propias, menú, tarjeta y
usuario de prueba. Sin clases `service`: la lógica compartida vive en métodos públicos del controlador Rest que el Web reutiliza.

## 2. Modelo de datos
Colección `auditoria` — `model/RegistroAuditoria`: `id, tipo, usuarioId, username, rol, edicionId, categoriaId, canal, fecha`.
**Sin** nominado ni nota. `tipo` ∈ `VOTO`, `CALIFICACION`, `INTENTO_DUPLICADO`; `canal` ∈ `WEB`, `API`, `SEMILLA`.

## 3. Componentes
| Pieza | Archivo | Responsabilidad |
|---|---|---|
| Registrador | `config/Auditoria.java` | Utilidad estática (no es un service). Recibe usuario, edición y categoría; **no tiene parámetros para el nominado ni la nota**. Si falla deja una advertencia y no interrumpe el voto. |
| Puntos de captura | `VotanteControladorWeb/Rest`, `JuradoControladorWeb/Rest` | Llaman al registrador tras guardar un voto o una calificación nueva y ante un intento de segundo voto. |
| Auditor | `AuditorControladorWeb/Rest` | Filtros, resumen, anomalías, exportación CSV. Solo `GET`. |

## 4. Rutas y seguridad
| Web | API | Regla |
|---|---|---|
| `/auditor/registros`, `/auditor/anomalias`, `/auditor/exportar` | `/api/auditor/registros`, `/anomalias`, `/exportar` | `hasRole("AUDITOR")` |

El Administrador **no** accede a `/auditor/**` (función exclusiva, C-03).

## 5. Flujo
**Voto →** validar (nominación aprobada, edición en votación, sin voto previo) → guardar `Voto` → `Auditoria.registrar(VOTO, …)`.
Si ya había voto: rechazar y registrar `INTENTO_DUPLICADO`.

## 6. Rendimiento y datos
Consulta limitada a 200 registros (tope 1.000); la exportación trae todos los filtrados. Se filtra en memoria (volumen de un premio);
si crece, indexar por `edicionId` y `fecha`. Datos personales mínimos (NFR-DAT-001).

## 7. Riesgos
| Riesgo | Mitigación |
|---|---|
| Que alguien añada el nominado al registro | Prueba que revisa por reflexión los campos de `RegistroAuditoria` |
| Cruzar fechas del registro con `votos` | Fuera de alcance; se documenta en ADR-003 |
| Fallo del registro durante un voto | El voto no se cancela; queda advertencia en el log |
