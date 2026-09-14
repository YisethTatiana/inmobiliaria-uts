# Manual de usuario — Inmobiliaria UTS

Aplicación web JSP + JDBC + Bootstrap desplegada en Apache Tomcat.

## 1. Cómo acceder
- **URL base:** `http://localhost:8080/Inmobiliaria/`
- **Registro:** desde la página de inicio → "Registrarse" (solo para el rol Cliente; los usuarios son invitados hasta que inician sesión).

## 2. Roles y qué puede hacer cada uno
| Rol | Qué puede hacer |
|---|---|
| **Invitado** | Ver la página de aterrizaje y el catálogo público. No agenda citas ni guarda favoritos. |
| **Cliente** | Catálogo, ficha de la propiedad, favoritos, agendar citas, enviar solicitudes con documentos, editar su perfil 1:1 y ver sus actividades. |
| **Inmobiliaria / Agente** | Publicar y editar propiedades con características e imágenes, gestionar citas y solicitudes, y consultar reportes para la toma de decisiones. |
| **Administrador** | CRUD de catálogos base (roles, ciudades, tipos, características, inmobiliarias), gestión de usuarios y propiedades, reportes, auditoría y bitácora. |

## 3. Flujos principales
1. **Consultar catálogo:** página de inicio (publicaciones destacadas) → **Buscar** por título → ficha con galería de imágenes.
2. **Agendar una cita:** cliente autenticado → ficha de la propiedad → "Agendar cita" (fecha y hora). No se permiten dos citas sobre la misma propiedad a la misma hora.
3. **Enviar solicitud:** cliente → ficha → "Solicitar" (arriendo/compra) adjuntando documentos.
4. **Publicar propiedad (agente):** "Nueva propiedad" → diligenciar datos, operación (venta/arriendo), características e imágenes.
5. **Reportes:** panel del administrador/agente → pestañas de ventas, arriendos, propiedades y citas.
6. **Recuperar contraseña:** en el login → "Olvidé mi contraseña" → ingrese el correo → llegará un código de 6 dígitos por correo → digitarlo → elegir nueva contraseña.
7. **Bloqueo de seguridad:** después de 5 intentos fallidos de login la cuenta se bloquea por 15 minutos.

## 4. Cuentas de acceso iniciales
| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | `admin@inmobiliaria.com` | `admin123` |
| Agente | `agente1@inmobiliaria.com` — `agente3@inmobiliaria.com` | `agente123` |
| Cliente (demo) | `cliente1@inmobiliaria.com` — `cliente6@inmobiliaria.com` | `cliente123` |