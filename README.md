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
test/                             Pruebas unitarias (PasswordUtils, Usuario)
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
| Inmobiliaria (agente) | `agente1@inmobiliaria.com`, `agente2@inmobiliaria.com`, `agente3@inmobiliaria.com` | `agente123` |
| Cliente | `cliente1@inmobiliaria.com` … `cliente6@inmobiliaria.com` | `cliente123` |

> El DML siembra **10 cuentas** (1 admin, 3 agentes y 6 clientes) para que los
> paneles y reportes de todos los roles tengan datos; los clientes también pueden
> crearse desde "Registrarse". El login bloquea la cuenta 15 min tras 5 intentos
> fallidos y hay recuperación de contraseña por correo con **código de 6 dígitos**
> (`/recuperar_clave.jsp`). El código se envía por SMTP a través de **Mailjet**
> (configurado en `WEB-INF/classes/smtp.properties`, archivo protegido por
> `.gitignore`); no se guarda nada en archivos. Las propiedades indican si están
> "En venta" o "En arriendo"
> (`propiedad.operacion`) y el agente genera reportes de ventas y arriendos
> desde su panel.

> Las contraseñas se almacenan cifradas (SHA-256 + salt). Ver detalle en `docs/MODELO_DATOS.md`.
> El DML siembra 12 propiedades con imágenes reales de inmuebles, 10+ registros
> en catálogos y **mínimo 10 registros por tabla principal** (incluye citas,
> solicitudes, documentos y favoritos), validando las relaciones 1:1, 1:N y N:M.
> Las cuentas adicionales reutilizan el hash de la contraseña demo de su perfil;
> en producción debe generarse un hash individual por cuenta.

## Documentación
- `docs/MODELO_DATOS.md` — MER, modelo relacional 3FN y diccionario de datos.
- `docs/MODELO_DATOS.md` → `docs/imagenes/MER.png` y `docs/imagenes/modelo_relacional.png` (diagramas).
- `docs/SCRUM_DOCUMENTATION.md` — 3 sprints, historias, métricas y retrospectivas.
- `docs/imagenes/tablero_scrum.png` — tablero Scrum de los 3 sprints.
- `docs/imagenes/casos_de_uso.png` — diagrama de casos de uso (actores y UC de los 4 roles).
- `database/consultas.sql` — las 5 consultas exigidas (INNER JOIN de 4 tablas,
  N:M, LEFT JOIN, GROUP BY/HAVING).

## Pruebas unitarias
Ejecutan validaciones sobre el cifrado de contraseñas (incluido el vector de los
datos sembrados) y sobre la lógica de roles múltiples. No requieren Tomcat.

```
javac -encoding UTF-8 -cp "WEB-INF\classes" -d test\out test\com\inmobiliaria\test\*.java
java -cp "test\out;WEB-INF\classes;WEB-INF\lib\mysql-connector-j-8.0.33.jar" com.inmobiliaria.test.EjecutarPruebas
```

Salida esperada: `[OK] PasswordUtilsTest ...`, `[OK] UsuarioTest ...` y
`Todas las pruebas unitarias pasaron correctamente.`

## Despliegue en línea (puntos extra — opcional)

El enunciado otorga puntos extra por una instancia en línea de la base de datos y
de la aplicación. La cadena de conexión está centralizada en
`src/com/inmobiliaria/config/ConexionBD.java`, así que basta cambiar el URL,
usuario y clave **solo allí**:

```java
DB_URL  = "jdbc:mysql://HOST_PUBLICO:3306/BD_NOMBRE?useSSL=false&serverTimezone=UTC";
DB_USER = "USUARIO";
DB_PASS = "CLAVE";
```

Opciones gratuitas frecuentes para la BD MySQL en línea: **Railway**, **Aiven** o
**Clever Cloud**. Para la aplicación servlet (war de Tomcat): **Railway**,
**Render** (tipo *Web Service* con comando de arranque) o un VPS con Tomcat.

Pasos generales:
1. Crear la instancia MySQL en línea y cargar `database/script_ddl.sql` +
   `database/script_dml.sql` con un cliente (por ejemplo, MySQL Workbench).
2. Empaquetar la aplicación: `jar -cf Inmobiliaria.war -C . .` (debe excluirse
   `smtp.properties` con las credenciales reales).
3. Cambiar solo la cadena de conexión en `ConexionBD.java`, recompilar y
   publicar el `war` en el hosting.
4. Probar login + un envío real de correo de recuperación.