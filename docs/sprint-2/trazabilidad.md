# Matriz de trazabilidad S2 y verificación de la DoD

| | |
|---|---|
| Tarjeta | D25 |
| Responsable | Juan Hernández |
| Participa | Equipo |
| Depende de | T027 |
| Formato | REQ → tarea → implementación → prueba |

## 1. Trazabilidad
| Requisito | Tarea | Implementación | Prueba |
|---|---|---|---|
| REQ-SOP-001 (ticket nuevo ABIERTO) | T017, T019 | `Ticket`, `TicketControladorWeb/Rest`, `tickets/nuevo.html` | `SoporteTest.unTicketNuevoQuedaAbiertoANombreDeQuienLoCrea` · `validaAsuntoYDescripcion` |
| REQ-SOP-002 (estado, fecha, responsable) | T018 | `SoporteControladorWeb/Rest` (`validarCambio`, `cambiarEstado`), `CambioEstadoTicket` | `SoporteTest.alPasarloAEnAtencionSeGuardaEstadoFechaYResponsable` · `rechazaCambiosInvalidos` |
| REQ-SOP-003 (menú para todos; solo los suyos) | T017, T019 | `layout.html` (menú Ayuda), `TicketControladorRest.delUsuario/detalle` | `SoporteTest.cadaUsuarioSoloVeSusPropiosTickets` · `unTicketAjenoRespondeComoSiNoExistiera` · `AccesoPorRolTest.todosLosRolesPuedenUsarLosTickets` |
| REQ-MOD-001 (comentario nuevo Pendiente y oculto) | T020 | `Feedback.estadoComentario`, `Estados`, `PublicoControladorWeb/Rest` | `ModeracionTest.unComentarioNuevoNaceVisibleSoloParaElModerador` · `unaCalificacionSinTextoNoNecesitaModeracion` |
| REQ-MOD-002 (aprobar u ocultar con quién y cuándo) | T021 | `ModeradorControladorWeb/Rest` | `ModeracionTest.alAprobarloSeMuestraYQuedaQuienYCuando` · `alOcultarloNoSeMuestra` · `sePuedeCambiarUnaDecisionPeroNoRepetirla` |
| REQ-MOD-003 (público solo aprobados) | T022 | `PublicoControladorWeb` (filtro `Estados::comentarioPublico`) | `ModeracionTest` (`Estados.comentarioPublico`) · manual de la encuesta pública |
| REQ-MOD-004 (CR-001) | D24 | `docs/cambios/CR-001.md` | No aplica: se implementa en el Sprint 3 |
| REQ-REP-001 (propuesta Pendiente sin cambiar el perfil) | T024, T025 | `PropuestaPerfil`, `RepresentanteControladorWeb/Rest` | `RepresentanteTest.laPropuestaNacePendienteYNoCambiaElPerfil` · `validaLaPropuesta` · `soloUnaPropuestaPendientePorJugador` |
| REQ-REP-002 (aprobar publica; rechazar conserva) | T026 | `PropuestaPerfilControladorWeb/Rest` | `RepresentanteTest.alAprobarElPerfilMuestraElCambio` · `alRechazarElPerfilQuedaIgual` · `unaPropuestaYaDecididaNoSeDecideDeNuevo` |
| REQ-REP-003 (solo su nominado) | T023, T025 | `AdminControladorWeb/Rest`, `app.js`, `RepresentanteControladorRest.jugadorDe` | `RepresentanteTest.elRepresentanteSoloActuaSobreSuNominado` · `aunqueElClienteMandeOtroJugadorLaPropuestaVaAlSuyo` |
| NFR-SEG-001 | T018, T021, T025, T026 | `SecurityConfig` | `AccesoPorRolTest` (rutas de Soporte, Moderador, Representante y la bandeja del Organizador) |
| NFR-DAT-001 | T017 | `Ticket` con asunto, descripción y creador; advertencia en el formulario | Revisión del modelo |
| AC-SOP-002.1 | T027 | — | `SoporteTest.alPasarloAEnAtencionSeGuardaEstadoFechaYResponsable` |
| AC-MOD-002.1 | T027 | — | `ModeracionTest.alAprobarloSeMuestraYQuedaQuienYCuando` y `alOcultarloNoSeMuestra` |
| AC-REP-002.1 | T027 | — | `RepresentanteTest.alAprobarElPerfilMuestraElCambio` y `alRechazarElPerfilQuedaIgual` |
| ADR-004 | D22 | `config/Estados` | Revisión documental |

## 2. Verificación de la DoD
Como en el Sprint 1, el documento de DoD del Sprint 0 no estaba entre los archivos revisados; se usan los principios de la Constitución.

| Verificación | Principio | Estado |
|---|---|---|
| Ninguna tarea sin requisito (esta matriz) | C-06 | ☑ |
| Acceso por rol en el servidor y probado (incluye las rutas nuevas) | C-04, NFR-SEG-001 | ☐ ejecutar `mvn test` |
| Cada decisión guarda quién y cuándo | C-05 | ☑ (comentarios y propuestas) · ☐ ejecutar `mvn test` |
| Los datos pendientes no cambian lo público | C-05, REQ-MOD-003, REQ-REP-001 | ☐ ejecutar `mvn test` y prueba manual |
| Cada rol nuevo tiene par de controladores, menú, tarjeta y usuario de prueba | C-08 | ☑ Soporte, Moderador y Representante |
| Los roles de los Sprints 0 y 1 no se rompieron | C-10 | ☐ prueba de humo |
| Datos personales mínimos | C-07 | ☑ |
| Diff revisado por otra persona y anotado en la bitácora de IA | C-09 | ☐ ver `cierre.md` |
