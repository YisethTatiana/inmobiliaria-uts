# Inmobiliaria UTS

Sistema web (Java EE) para la gestión integral de una inmobiliaria: catálogo de
propiedades, citas, solicitudes con documentos, favoritos, perfiles, usuarios
multi-rol, auditoría, catálogos y reportes.

## Stack
- Java 21 · javax Servlets + JSP · Tomcat 8.5 (XAMPP)
- MySQL 8 (`inmobiliaria_db`) · JDBC (mysql-connector-j 8.0.33)
- Bootstrap 5 (CDN) + `css/styles.css` propio · Git

## Estructura
```
index.jsp                         Portada (landing + destacadas)
login.jsp / registro.jsp          Autenticación y registro
login/ logout/ acceso_denegado    Seguridad y control por roles
cliente/                          Catálogo, ficha, citas, favoritos, solicitudes, perfil
agente/                           Propiedades, citas, solicitudes, reportes (rol INMOBILIARIA)
admin/                            Usuarios, catálogos, propiedades, citas, solicitudes,
                                  auditoría y reportes (rol ADMINISTRADOR)
WEB-INF/jspf/                     Fragmentos compartidos (head, menu, footer)
WEB-INF/classes                   Clases compiladas
src/                              Código fuente (modelo, dao, servlet, filtro, util, config)
database/                         script_ddl.sql, script_dml.sql, consultas.sql
docs/                             MODELO_DATOS.md, SCRUM_DOCUMENTATION.md
```

## Instalación
1. Levantar MySQL (XAMPP) y ejecutar `database/script_ddl.sql`, luego
   `database/script_dml.sql` (crea `inmobiliaria_db`, tablas y datos de prueba).
   Ajustar credenciales en `src/com/inmobiliaria/config/ConexionBD.java` si difieren.
2. Copiar el proyecto a `C:\xampp\tomcat\webapps\Inmobiliaria` (ya está
   implementado como webapp desplegable).
3. `WEB-INF/lib/` debe contener el driver MySQL (`mysql-connector-j-8.0.33.jar`).
4. Compilar las fuentes en `WEB-INF/classes`:
   ```
   javac -encoding UTF-8 -cp "C:\xampp\tomcat\lib\servlet-api.jar;C:\xampp\tomcat\lib\jsp-api.jar;C:\xampp\tomcat\webapps\Inmobiliaria\WEB-INF\lib\mysql-connector-j-8.0.33.jar" -d "C:\xampp\tomcat\webapps\Inmobiliaria\WEB-INF\classes" <*.java de src/>
   ```
5. Iniciar Tomcat y abrir `http://localhost:8080/Inmobiliaria/`.

## Cuentas de prueba (4 roles)

> Los 4 roles son: **Visitante** (no necesita cuenta, navega el catálogo),
> **Cliente**, **Inmobiliaria** (agente) y **Administrador**.

| Rol | Correo | Clave |
|---|---|---|
| Administrador | `admin@inmobiliaria.com` | `admin123` |
| Inmobiliaria (agente) | `agente1@inmobiliaria.com`, `agente2@inmobiliaria.com` | `agente123` |
| Cliente | — (se registran desde `registro.jsp`; no vienen sembrados en el DML) | creada al registrarse |

> Las contraseñas se almacenan cifradas (SHA-256 + salt). Ver detalle en `docs/MODELO_DATOS.md`.

## Documentación
- `docs/MODELO_DATOS.md` — MER, modelo relacional 3FN y diccionario de datos.
- `docs/MODELO_DATOS.md` → `docs/imagenes/MER.png` y `docs/imagenes/modelo_relacional.png` (diagramas).
- `docs/SCRUM_DOCUMENTATION.md` — 3 sprints, historias, métricas y retrospectivas.
- `docs/imagenes/tablero_scrum.png` — tablero Scrum de los 3 sprints.
- `database/consultas.sql` — las 5 consultas exigidas (INNER JOIN de 4 tablas,
  N:M, LEFT JOIN, GROUP BY/HAVING).