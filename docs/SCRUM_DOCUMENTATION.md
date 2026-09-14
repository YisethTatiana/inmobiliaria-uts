# SCRUM DOCUMENTATION — Proyecto Inmobiliaria UTS

Sistema web Java EE (JSP/Servlets/JDBC/MySQL) para la gestión integral de una inmobiliaria:
catálogo de propiedades, citas, solicitudes con documentos, favoritos, perfiles,
usuarios multi-rol (CLIENTE / INMOBILIARIA / ADMINISTRADOR), auditoría, catálogos y reportes.

---

## 1. Equipo Scrum

| Rol | Responsable |
|---|---|
| Product Owner | Tatiana |
| Scrum Master | Tatiana |
| Equipo de Desarrollo | Tatiana |
| Stack | Java 21, JSP/Servlets (Tomcat 9), MySQL, Bootstrap 5, JDBC |

## 2. Marco de trabajo

- **Sprints:** duración fija de 1 semana (7 días), 3 sprints en total.
- **Reuniones:** Daily (práctica diaria), Sprint Planning, Sprint Review y Sprint Retrospective al cierre de cada sprint.
- **Estimaciones:** puntos de historia (Fibonacci: 1, 2, 3, 5, 8).
- **Sprint Velocity objetivo:** 21 puntos/sprint.
- **Definition of Done (DoD):**
  1. Código compilado sin errores (`javac`, encoding UTF-8).
  2. Funcionalidad operando contra MySQL (`inmobiliaria_db`) con el DDL/DML cargados.
  3. Navegación controlada por roles (cliente, inmobiliaria, administrador).
  4. Formularios validados (servidor y cliente) con mensajes de retroalimentación.
  5. Auditoría registrada para acciones críticas.
  6. Vistas responsivas, coherentes y sin contenido duplicado.

## 3. Product Backlog (priorizado)

| # | Historia de usuario | Prioridad | Estimación |
|---|---|---|---|
| H1 | Como visitante quiero ver el catálogo de propiedades y filtrar por ciudad/tipo/precio | Alta | 3 |
| H2 | Como visitante quiero registrarme y entrar a la plataforma según mi rol | Alta | 5 |
| H3 | Como cliente quiero ver la ficha de una propiedad con imágenes y características | Alta | 3 |
| H4 | Como cliente quiero agendar citas a propiedades | Alta | 5 |
| H5 | Como cliente quiero guardar propiedades favoritas | Media | 2 |
| H6 | Como cliente quiero radicar solicitudes de compra/arriendo y adjuntar documentos | Alta | 5 |
| H7 | Como usuario quiero editar mi perfil personal | Media | 2 |
| H8 | Como inmobiliaria quiero gestionar mis propiedades (crear, editar, dar de baja) | Alta | 5 |
| H9 | Como admin/inmobiliaria quiero gestionar citas (cambiar estado) | Alta | 3 |
| H10 | Como inmobiliaria/admin quiero revisar solicitudes y documentos | Media | 3 |
| H11 | Como admin quiero administrar usuarios, roles e inmobiliarias | Media | 3 |
| H12 | Como admin quiero gestionar catálogos (tipos, ciudades, características) | Media | 2 |
| H13 | Como admin quiero consultar reportes SQL (JOIN, N:M, LEFT JOIN, GROUP BY) | Media | 5 |
| H14 | Como admin quiero ver la auditoría del sistema | Media | 2 |
| H15 | Como usuario quiero cerrar sesión de forma segura | Alta | 1 |
| **Total** | | | **49** |

## 4. Planificación de Sprints

### Sprint 1 — Cimientos (7 días) — objetivo: base de datos + login/registro + catálogo
**Sprint Backlog:** H1, H2, H3, H7, H15.
| Historia | Estimación |
|---|---|
| H2 Registro, autenticación y roles | 5 |
| H1 Catálogo y filtros | 3 |
| H3 Ficha de propiedad | 3 |
| H7 Perfil de usuario | 2 |
| H15 Logout | 1 |
| **Suma** | **14** |

**Tareas técnicas:** MER/modelo relacional → DDL + DML + `consultas.sql`; modelos y DAOs (UsuarioDAO, PerfilDAO, PropiedadDAO, CatalogoDAO); `LoginServlet`, `RegistroServlet`, `LogoutServlet`, filtro de sesión; páginas `index.jsp`, `login.jsp`, `registro.jsp`, `cliente/catalogo.jsp`, `cliente/detalle_propiedad.jsp`, `cliente/perfil.jsp`.

**Sprint Review:** al cierre se demostró registro/login con contraseña cifrada, redirección por rol, catálogo filtrable y ficha de propiedad con imágenes y características. Se completó el 100% del sprint.

**Sprint Retrospective:**
- *Bien:* definición temprana del MER; DML con datos reales para pruebas; cierre de sesión con invalidación de sesión y auditoría.
- *A mejorar:* centralizar el HTML repetido en fragmentos JSPF y un único CSS.
- *Acción de mejora adoptada:* crear `WEB-INF/jspf/{head,menu,footer}.jspf` y `css/styles.css` desde el siguiente sprint.

### Sprint 2 — Negocio del cliente e inmobiliaria (7 días) — objetivo: citas, favoritos, solicitudes, propiedades
**Sprint Backlog:** H4, H5, H6, H8, H9.
| Historia | Estimación |
|---|---|
| H8 Gestión de propiedades (agente/admin) | 5 |
| H4 Agendar citas | 5 |
| H6 Solicitudes + documentos | 5 |
| H9 Gestión de estado de citas | 3 |
| H5 Favoritos | 2 |
| **Suma** | **20** |

**Tareas técnicas:** `CitaServlet`/`AdminCitaServlet`/`AgenteCitaServlet`; `SolicitudServlet`/`GestionSolicitudServlet`; carga multipart de imágenes y documentos (`ArchivoUtil`, `ImagenDAO`, `DocumentoSolicitudDAO`); `CrearPropiedadServlet`, `EditarPropiedadServlet`, `AgentePropiedadServlet`, `FavoritoServlet`; vistas: `cliente/citas.jsp`, `cliente/favoritos.jsp`, `cliente/solicitudes.jsp`, `agente/**`, `admin/propiedades.jsp`, detalle de solicitud.

**Sprint Review:** demostración funcional de agenda de citas con validación de fechas y duplicados (UNIQUE por propiedad), favoritos, radicación de solicitudes con adjunto de documentos, alta/edición/baja de propiedades con imagenes y caracteristicas.

**Sprint Retrospective:**
- *Bien:* control de permisos `tieneRol()` centralizado en el filtro; transacciones para solicitudes con documento.
- *A mejorar:* demasiadas vistas antiguas duplicadas (`gestion_citas.jsp`, `dashboard_agente.jsp`).
- *Acción de mejora adoptada:* eliminar duplicados y unificar dashboards en el Sprint 3; migrar todas las páginas al layout JSPF.

### Sprint 3 — Administración y reportes (7 días) — objetivo: usuarios, catálogos, auditoría, reportes
**Sprint Backlog:** H10, H11, H12, H13, H14.
| Historia | Estimación |
|---|---|
| H13 Reportes SQL | 5 |
| H10 Revisión de solicitudes/documentos | 3 |
| H11 Gestión de usuarios y roles | 3 |
| H12 Catálogos | 2 |
| H14 Auditoría | 2 |
| **Suma** | **15** |

**Tareas técnicas:** `AdminUsuarioServlet`, `AdminCatalogoServlet` (tipos, ciudades, características), `AuditoriaServlet`, `ReporteServlet` + `ReporteDAO`; pantallas `admin/usuarios.jsp`, `admin/catalogos.jsp`, `admin/auditoria.jsp`, `admin/reportes.jsp`, `agente/reportes.jsp`, `acceso_denegado.jsp`; auditoría en Login, Logout, citas, solicitudes, propiedades, usuarios, catálogos y perfil; seguridad de acceso (bloqueo temporal tras 5 intentos fallidos y recuperación de contraseña por correo con código de 6 dígitos); pruebas unitarias (`test/`): `PasswordUtilsTest` y `UsuarioTest` ejecutadas con `EjecutarPruebas` (ninguna requirió Tomcat) y validación de los hashes sembrados.

**Sprint Review:** se mostró la consola administrativa completa: usuarios con roles e inmobiliaria, CRUD de catálogos, trazabilidad de auditoría y los 5+ reportes exigidos (JOIN multi-tabla, relación N:M, LEFT JOIN, GROUP BY/HAVING) en `admin` y `agente`.

**Sprint Retrospective:**
- *Bien:* los reportes se reutilizan entre roles evitando código duplicado; compilación final sin errores.
- *A mejorar:* documentar el modelo de datos y el proceso Scrum para el entregable.
- *Acción de mejora adoptada:* `docs/MODELO_DATOS.md`, `docs/SCRUM_DOCUMENTATION.md` y este repositorio Git.

## 5. Administración del proyecto (Gráfica Burn-down acumulada)

**Tablero Scrum (imagen):** `imagenes/tablero_scrum.png` — columnas Pendiente / En Progreso / Hecho por sprint, con las historias H1–H15 y su estimación en puntos.

| Sprint | Puntos planificados | Puntos comprometidos | Puntos entregados | Días |
|---|---|---|---|---|
| Sprint 1 | 21 | 14 | 14 | 7 |
| Sprint 2 | 21 | 20 | 20 | 7 |
| Sprint 3 | 21 | 15 | 15 | 7 |

El equipo planificó el máximo (21), comprometió un total de 49 puntos en 3 sprints y los entregó en su totalidad (velocidad media ≈ 16,3 ptos/sprint).

**Enlaces de seguimiento (en línea):**
- Tablero de seguimiento: `[pegar aquí la URL de Tu/Trello/GitHub Projects]` *(pendiente: crear el tablero en línea y pegar el enlace)*
- Repositorio Git público: `https://github.com/YisethTatiana/inmobiliaria-uts` *(creado y subido)*

## 6. Entregables y aceptación

| Criterio evaluativo | Estado |
|---|---|
| Base de datos (DDL/DML/consultas según entregable) | ✅ |
| Modelo entidad-relación + relacional 3FN | ✅ |
| Diagrama de casos de uso (`docs/imagenes/casos_de_uso.png`) | ✅ |
| Autenticación, roles y control de acceso | ✅ |
| Catálogo, ficha, favoritos, citas | ✅ |
| Solicitudes con documentos | ✅ |
| Gestión de propiedades, usuarios, catálogos | ✅ |
| Reportes SQL y auditoría | ✅ |
| Vistas responsivas y fragmentos JSPF compartidos | ✅ |
| Código compilado y documentación | ✅ |
| Control de versiones (Git) | ✅ |