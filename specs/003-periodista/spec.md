# Spec del Periodista · v1.0 (Puerta 1)

| | |
|---|---|
| Tarjetas | D13 (v0.1 y clarificación) · D14 (v1.0) |
| PBI | PBI-05 |
| Responsable | Juan Hernández |
| Aprueba | Jorge Monroy |
| Estado | v1.0 · pendiente de aprobación |

## 1. Contexto
El premio necesita un rol que informe al público sobre cada edición con noticias y crónicas.

## 2. Alcance
**Dentro:** noticias del Periodista (borrador, edición, publicación) y página pública de noticias.
**Fuera:** borrar o despublicar noticias; comentarios de las noticias.

## 3. Actores
| Rol | Puede | No puede |
|---|---|---|
| Periodista | Crear, editar y publicar **sus** noticias | Borrar noticias; tocar las de otro autor |
| Público (sin sesión) | Leer noticias **publicadas** | Ver borradores |

## 4. Requisitos (EARS)
| ID | Requisito |
|---|---|
| REQ-PER-001 | **Evento:** cuando el Periodista publica una noticia, el sistema deberá mostrarla en la página pública de noticias. |
| REQ-PER-002 | **No deseado:** si una noticia está en borrador, entonces el sistema no deberá mostrarla en la página pública ni en la API pública. |
| REQ-PER-003 | **Ubicuo:** el sistema deberá guardar de cada noticia su autor, fecha de creación, fecha de publicación, estado y edición asociada, y solo el autor podrá editarla o publicarla. |
| NFR-SEG-001 | Toda ruta del Periodista deberá devolver 403 a cualquier otro rol (C-04). |

## 5. Criterios de aceptación
| ID | Criterio |
|---|---|
| AC-PER-001.1 | **Dado** una noticia en borrador, **cuando** el periodista la publica, **entonces** es visible sin iniciar sesión. |
| AC-PER-002.1 | **Dado** una noticia en borrador, **cuando** un visitante abre su dirección pública, **entonces** recibe 404. |
| AC-PER-003.1 | **Dado** una noticia de otro autor, **cuando** un periodista intenta editarla, **entonces** el sistema lo rechaza. |

## 6. Clarificaciones
| # | Pregunta | Decisión |
|---|---|---|
| CL-01 | ¿Pueden editarse noticias ya publicadas? | Sí, por su autor. |
| CL-02 | ¿Se pueden borrar o despublicar? | No en este sprint. |
| CL-03 | Límites de texto | Título 120, resumen 280, contenido 5.000 caracteres. |
