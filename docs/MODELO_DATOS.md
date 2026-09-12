# MODELO DE DATOS — Inmobiliaria UTS

Proyecto `Inmobiliaria UTS` (Java EE / JSP / JDBC / MySQL).

Base de datos: `inmobiliaria_db` — creada y poblada con `database/script_ddl.sql` y `database/script_dml.sql`.
Las 5 consultas exigidas están en `database/consultas.sql`.

---

## 1. Modelo Entidad-Relación (MER)

**Diagrama (imagen):** `imagenes/MER.png` — 17 entidades y las relaciones 1:1, 1:N y N:M (filas de relación marcadas con diamante).

**Diagrama del esquema relacional (tablas y claves):** `imagenes/modelo_relacional.png`.

```
ROL *----------------+ * USUARIO      USUARIO 1---------0..1 PERFIL
                       |                  |
                     USUARIO_ROL          |              USUARIO *---USUARIO_INMOBILIARIA---* INMOBILIARIA
                                           |
CIUDAD 1-----* PROPIEDAD *------1 TIPO_PROPEDAD
                |      |
                |      +-- * CARACTERISTICA   (vía PROPIEDAD_CARACTERISTICA, N:M con cantidad)
                |
                +--------- * IMAGEN_PROPEDAD (1:N)
                |
                +--------- * CITA (1:N)
                |              |
                |              |  USUARIO(CLIENTE)
                |
                +--------- * SOLICITUD (1:N, de un cliente)
                              |
                              +--- * DOCUMENTO_SOLICITUD (1:N)

USUARIO(CLIENTE) *-------- * PROPIEDAD (vía FAVORITO, N:M)
```

**Resumen de cardinalidades:**
- `usuario` ⟷ `rol`: **N:M** por `usuario_rol` (un usuario puede tener CLIENTE + INMOBILIARIA; p. ej. el admin).
- `usuario` ⟷ `perfil`: **1:1** (`perfil.id_usuario` es UNIQUE).
- `usuario` ⟷ `inmobiliaria`: **N:M** por `usuario_inmobiliaria` (varios agentes por inmobiliaria).
- `inmobiliaria` ⟷ `propiedad`: **1:N** (`propiedad.id_inmobiliaria`).
- `ciudad` ⟷ `propiedad`: **1:N**; `tipo_propiedad` ⟷ `propiedad`: **1:N**.
- `propiedad` ⟷ `caracteristica`: **N:M** por `propiedad_caracteristica` (con `cantidad`).
- `propiedad` ⟷ `imagen_propiedad`: **1:N**.
- `cliente` (usuario) ⟷ `propiedad`: **N:M** por `favorito`.
- `cliente` (usuario) ⟷ `cita` ⟷ `propiedad`: **1:N** cada lado (cita pertenece a un cliente y una propiedad).
- `cliente` ⟷ `solicitud` ⟷ `propiedad`: **1:N**; `solicitud` ⟷ `documento_solicitud`: **1:N**.

---

## 2. Modelo Relacional (3FN)

> Claves: **PK** subrayada, **FK** marcada, UNIQUE por `(U)`.

- **rol**(<ins>id_rol</ins>, nombre `(U)`)
- **usuario**(<ins>id_usuario</ins>, correo `(U)`, password_hash, fecha_creacion, activo)
- **usuario_rol**(<ins>id_usuario</ins>, <ins>id_rol</ins>)  — FK → usuario, rol (CASCADE)
- **perfil**(<ins>id_perfil</ins>, *id_usuario* `(U)`, nombres, apellidos, documento `(U)`, telefono, direccion) — FK → usuario
- **inmobiliaria**(<ins>id_inmobiliaria</ins>, nombre `(U)`, telefono, email)
- **usuario_inmobiliaria**(<ins>id_usuario</ins>, <ins>id_inmobiliaria</ins>)  — FK → usuario, inmobiliaria
- **ciudad**(<ins>id_ciudad</ins>, nombre)
- **tipo_propiedad**(<ins>id_tipo</ins>, nombre)
- **caracteristica**(<ins>id_caracteristica</ins>, nombre)
- **propiedad**(<ins>id_propiedad</ins>, matricula_inmobiliaria `(U)`, titulo, descripcion, precio, direccion, area, habitaciones, banios, parqueaderos, estado `ENUM('DISPONIBLE','INACTIVO','VENDIDO','ARRENDADO','RESERVADO')`, fecha_publicacion, *id_ciudad*, *id_tipo*, *id_inmobiliaria*) — FK → ciudad, tipo_propiedad, inmobiliaria
- **imagen_propiedad**(<ins>id_imagen</ins>, *id_propiedad*, ruta, titulo, es_principal) — FK → propiedad (CASCADE)
- **propiedad_caracteristica**(<ins>id_propiedad</ins>, <ins>id_caracteristica</ins>, cantidad) — FK → propiedad, caracteristica (CASCADE)
- **favorito**(<ins>id_usuario</ins>, <ins>id_propiedad</ins>, fecha) — FK → usuario, propiedad (CASCADE)
- **cita**(<ins>id_cita</ins>, *id_usuario_cliente*, *id_propiedad*, fecha_hora, observaciones, estado `ENUM('PENDIENTE','APROBADA','CANCELADA','COMPLETADA')`) — FK → usuario, propiedad (CASCADE); **UNIQUE**(id_propiedad, fecha_hora) para evitar doble reserva por inmueble
- **solicitud**(<ins>id_solicitud</ins>, *id_usuario_cliente*, *id_propiedad*, tipo_solicitud `ENUM('COMPRA','ARRIENDO')`, estado `ENUM('RADICADA','APROBADA','RECHAZADA','ANULADA')`, fecha_solicitud, observaciones) — FK → usuario, propiedad
- **documento_solicitud**(<ins>id_documento</ins>, *id_solicitud*, nombre_archivo, ruta, tipo_documento, estado `ENUM('RADICADO','REVISADO','APROBADO','RECHAZADO')`, fecha_carga) — FK → solicitud (CASCADE)
- **auditoria**(<ins>id_auditoria</ins>, *id_usuario*, accion, entidad, id_entidad, detalle, ip, fecha) — FK → usuario (SET NULL)

Todas las tablas cumplen 3FN (sin dependencias transitivas; las dependencias parciales se resuelven con tablas puente N:M).

---

## 3. Diccionario de datos (resumen)

| Tabla | Atributos clave | Tipo | Restricción |
|---|---|---|---|
| usuario | correo | VARCHAR(100) | UNIQUE, NOT NULL |
| usuario | password_hash | VARCHAR(100) | hash SHA-256 + salt (`hash:salt`) |
| perfil | documento | VARCHAR(15) | UNIQUE, NOT NULL, solo dígitos (6–15) |
| propiedad | matricula_inmobiliaria | VARCHAR(50) | UNIQUE, NOT NULL |
| propiedad | precio | DECIMAL(14,2) | NOT NULL |
| propiedad | estado | ENUM | DISPONIBLE / INACTIVO / VENDIDO / ARRENDADO / RESERVADO |
| cita | (id_propiedad, fecha_hora) | — | UNIQUE (evita cita duplicada) |
| solicitud | tipo_solicitud | ENUM | COMPRA / ARRIENDO |
| documento_solicitud | ruta | VARCHAR(255) | archivo físico en `/uploads` |
| auditoria | fecha | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

---

## 4. Formatos y convenciones
- Passwords de prueba (formato `SHA256(salt+clave)` codificado en Base64, separado por `:`):
  - `admin123`  → `eBsYNqFXdwobDSUNwxibetS1yq4o0P3/IHShZrvYC3s=:xuQYqfgMLSGoISZGmFM+vg==`
  - `agente123` → `s6P+4lQksbPZdYxBbKb/+bhTNGckidTNUnU+OzMujlk=:SO03E7AAY7d3mFG3cxdH7A==`
- Las cuentas CLIENTE no se siembran en el DML; se crean desde el registro público
  (`registro.jsp`).
- Fechas: `yyyy-MM-dd HH:mm:ss` (MySQL `DATETIME`/`TIMESTAMP`).
- Moneda: `DECIMAL(14,2)`, formateo `#,###.##` en las vistas.