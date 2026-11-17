# Spec del Auditor · v1.0 (Puerta 1)

| | |
|---|---|
| Tarjetas | D13 (v0.1 y clarificación) · D14 (v1.0) |
| PBI | PBI-04 |
| Responsable | Juan Hernández |
| Aprueba | Jorge Monroy |
| Estado | v1.0 · pendiente de aprobación |

## 1. Contexto
El premio necesita un rol que verifique la limpieza de la votación **sin romper el secreto del voto** (C-02).

## 2. Alcance
**Dentro:** registro de auditoría de votos y calificaciones; consulta con filtros, anomalías y exportación para el Auditor.
**Fuera:** moderación de comentarios y tickets (otros roles).

## 3. Actor
| Rol | Puede | No puede |
|---|---|---|
| Auditor | Leer el registro, ver anomalías y exportar CSV | Crear, editar o borrar datos; ver por quién se votó |

## 4. Requisitos (EARS)
| ID | Requisito |
|---|---|
| REQ-AUD-001 | **Evento:** cuando un votante emite un voto o un jurado registra una calificación, el sistema deberá guardar usuario, rol, fecha, edición, categoría y canal, **sin** el nominado elegido ni la nota. |
| REQ-AUD-002 | **Estado:** mientras el usuario tenga el rol Auditor, el sistema deberá permitirle consultar el registro filtrado por edición, categoría y tipo, en solo lectura. |
| REQ-AUD-003 | **Evento:** cuando el Auditor abre las anomalías, el sistema deberá mostrar los votos duplicados del mismo usuario en una categoría y los intentos de segundo voto rechazados. |
| REQ-AUD-004 | **Evento:** cuando el Auditor exporta, el sistema deberá generar un archivo CSV con los filtros aplicados. |

### No funcionales
| ID | Requisito |
|---|---|
| NFR-AUD-001 | El registro de auditoría deberá tener **0 campos** que identifiquen al nominado elegido (ni su nota). Verificable por prueba automática. |
| NFR-REN-001 | La consulta deberá mostrar como máximo 200 registros por defecto (tope 1.000); el reporte completo se obtiene exportando. |
| NFR-SEG-001 | Toda ruta del Auditor deberá devolver 403 a cualquier otro rol (C-04). |
| NFR-DAT-001 | El registro guarda solo identificador y nombre de usuario (C-07). |

## 5. Criterios de aceptación (Given / When / Then)
| ID | Criterio |
|---|---|
| AC-AUD-001.1 | **Dado** un votante, **cuando** emite un voto, **entonces** se crea un registro con su usuario, fecha y categoría y **sin** campo de nominado. |
| AC-AUD-002.1 | **Dado** un Votante, **cuando** intenta abrir `/auditor/registros`, **entonces** recibe 403. |
| AC-AUD-003.1 | **Dado** que un votante intenta votar dos veces en la misma categoría, **cuando** el auditor abre las anomalías, **entonces** ve un «intento duplicado» de ese usuario. |
| AC-AUD-004.1 | **Dado** un filtro por edición, **cuando** exporto, **entonces** descargo solo los registros de esa edición. |

## 6. Clarificaciones
| # | Pregunta | Decisión |
|---|---|---|
| CL-01 | ¿Se registran las calificaciones del jurado? | Sí, **solo al crearlas**; corregir una nota no genera otro registro. |
| CL-02 | ¿Se guarda la nota o el nominado calificado? | No (C-02). |
| CL-03 | ¿Qué es una anomalía? | (a) más de un voto del mismo usuario en la misma categoría (`VOTO_DUPLICADO`; no debería ocurrir porque la base lo impide) y (b) un intento de segundo voto rechazado (`INTENTO_DUPLICADO`). |
| CL-04 | ¿Se registran los intentos rechazados? | Sí, con el tipo `INTENTO_DUPLICADO`. |
| CL-05 | ¿Qué formato tiene el reporte? | CSV con separador `;` y BOM UTF-8 para que Excel (configuración regional en español) lo abra bien. |
| CL-06 | ¿Qué pasa con los votos anteriores al sprint? | Al iniciar, se copian al registro (canal `SEMILLA`), siempre sin nominado ni nota. |

## 7. Trazabilidad
Implementación en T009–T012 y pruebas en T016: ver `tasks.md` y `docs/sprint-1/trazabilidad.md`.
