# Sustentación — Inmobiliaria UTS (guion de 5 minutos)

## 1. Módulos (qué se construyó)
- **Catálogo y ficha:** propiedades con galería (1:N en `imagen_propiedad`), características (N:M en `propiedad_caracteristica`), badges de operación venta/arriendo.
- **Seguridad:** registro y login con SHA-256 + *salt*, filtro de rutas por rol, bloqueo a los 5 intentos, recuperación de contraseña por código de 6 dígitos enviado por correo (SMTP real vía Mailjet).
- **Operación comercial:** citas (con restricción de única cita por propiedad y hora), solicitudes con documentos, publicación de propiedades con cambio de estado.
- **Reportes:** ventas (operación Venta/estado Vendida), arriendos, propiedades por ciudad/estado, top 5 más solicitadas, citas por estado.

## 2. Roles y verificación del control de acceso
Los 4 roles se representan en `usuario_rol` (N:M). El filtro `FiltroAutenticacion`
intercepta cada ruta (`/admin/*`, `/agente/*`, `/cliente/*`); si el rol no
coincide vía las tablas `usuario_rol`–`rol`, redirige al login o a
`acceso_denegado.jsp`.

## 3. Modelo de datos y cardinalidades
- **1:1** → `perfil.id_usuario` es UNIQUE (un usuario = un perfil).
- **1:N** → `imagen_propiedad.id_propiedad`, `cita.id_propiedad`,
  `documento_solicitud.id_solicitud`.
- **N:M** → `propiedad_caracteristica` y `usuario_rol` con sus tablas intermedias.
- **UNIQUE** → `usuario.correo`, `propiedad.matricula_inmobiliaria`, `perfil.documento`, `cita(id_propiedad, fecha_hora)`; la app captura el error de duplicado y muestra un mensaje claro ("el correo ya se encuentra registrado", "la matrícula ya se encuentra registrada").

## 4. La consulta de LEFT JOIN (J#4)
```sql
SELECT p.titulo AS Titulo, p.descripcion AS Descripcion, p.precio AS Precio,
       (p.precio * 3) AS Precio_Maximo, 1000000 AS Precio_Minimo
FROM propiedad p
LEFT JOIN cita c ON p.id_propiedad = c.id_propiedad
WHERE c.id_cita IS NULL AND p.precio > 100000000
ORDER BY p.precio DESC;
```
**Para qué sirve:** lista las propiedades que aún **no tienen citas** (LEFT JOIN conserva las propiedades sin coincidencia en `cita`; `WHERE c.id_cita IS NULL` filtra solo las que no se han agendado). Además de precio y descripción, se calcula *precio máximo* (3 veces el precio) y el *precio mínimo* como constante ($1.000.000). En el sistema se usa para identificar inmuebles poco visitados y priorizar la promoción.

## 5. Para qué sirvió la consulta N:M (J#3)
La consulta sobre `propiedad_caracteristica` muestra qué características (garaje, piscina, seguridad…) tiene cada propiedad. Sirvió para construir la ficha del detalle y el filtro por características del catálogo, y para el reporte de inventario.

## 6. Permisos excluidos (justificación)
Por diseño se dejaron fuera acciones de alto privilegio como eliminar físicamente usuarios o modificar sus contraseñas desde el panel administrativo; se prefiere **baja lógica** (estado inactivo) para conservar la trazabilidad y la integridad referencial en auditoría y citas.

## 7. Metodología Scrum y versiones
- 3 sprints de 7 días con planning, review y retrospectiva documentados.
- Tablero de seguimiento en línea con las 15 historias (H1–H15).
- Repositorio Git público con historial de commits desde la primera entrega.

## 8. Preguntas frecuentes que pueden surgir
- **¿Por qué el agente no digita la matrícula?** Se autogenera (`MAT-` + marca de tiempo) para garantizar la unicidad de la restricción UNIQUE.
- **¿Dónde está centralizada la conexión?** En `ConexionBD.java`; para publicar en línea solo se cambia URL/usuario/clave en ese archivo.
- **¿Cómo se probó el correo?** Pruebas unitarias (2/2 OK) + envíos reales SMTP con verificación de entrega en Mailjet.