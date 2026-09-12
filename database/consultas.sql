-- ============================================================
-- INMOBILIARIA UTS - CONSULTAS AVANZADAS / REPORTES
-- Cinco consultas obligatorias:
--   1 y 2) dos INNER JOIN entre 3 o más tablas
--   3)     una consulta que resuelve una relación N:M
--   4)     una consulta con LEFT JOIN
--   5)     una consulta de agregación con GROUP BY y HAVING
-- ============================================================

USE inmobiliaria_db;

-- ------------------------------------------------------------
-- 1) INNER JOIN entre 4 tablas: propiedades con su ciudad,
--    su tipo y su inmobiliaria (lista completa del catálogo).
-- ------------------------------------------------------------
SELECT p.id_propiedad AS ID,
       p.titulo       AS Titulo,
       tp.nombre      AS Tipo,
       ci.nombre      AS Ciudad,
       i.nombre       AS Inmobiliaria,
       p.precio       AS Precio,
       p.estado       AS Estado
FROM propiedad p
INNER JOIN tipo_propiedad tp ON p.id_tipo = tp.id_tipo
INNER JOIN ciudad          ci ON p.id_ciudad = ci.id_ciudad
INNER JOIN inmobiliaria     i ON p.id_inmobiliaria = i.id_inmobiliaria
ORDER BY p.id_propiedad;

-- ------------------------------------------------------------
-- 2) INNER JOIN entre 4 tablas: citas con el cliente, la
--    propiedad y la ciudad donde se encuentra el inmueble.
-- ------------------------------------------------------------
SELECT c.id_cita   AS ID_Cita,
       u.correo    AS Cliente,
       p.titulo    AS Propiedad,
       ci.nombre   AS Ciudad,
       c.fecha_hora AS Fecha,
       c.estado    AS Estado
FROM cita c
INNER JOIN usuario   u  ON c.id_usuario_cliente = u.id_usuario
INNER JOIN propiedad p  ON c.id_propiedad      = p.id_propiedad
INNER JOIN ciudad    ci ON p.id_ciudad         = ci.id_ciudad
ORDER BY c.fecha_hora DESC;

-- ------------------------------------------------------------
-- 3) Relación MUCHOS A MUCHOS: características de la
--    propiedad #1, resuelta a través de la tabla intermedia
--    propiedad_caracteristica.
-- ------------------------------------------------------------
SELECT p.id_propiedad AS ID_Propiedad,
       p.titulo       AS Propiedad,
       c.nombre       AS Caracteristica,
       pc.cantidad    AS Cantidad
FROM propiedad p
INNER JOIN propiedad_caracteristica pc ON p.id_propiedad = pc.id_propiedad
INNER JOIN caracteristica            c  ON pc.id_caracteristica = c.id_caracteristica
WHERE p.id_propiedad = 1
ORDER BY c.nombre;

-- ------------------------------------------------------------
-- 4) LEFT JOIN: todos los usuarios con su perfil y cantidad
--    de citas (muestra los que aún no tienen perfil).
-- ------------------------------------------------------------
SELECT u.id_usuario                       AS ID,
       u.correo                           AS Correo,
       IFNULL(TRIM(CONCAT(COALESCE(pf.nombres, ''), ' ', COALESCE(pf.apellidos, ''))), 'Sin perfil') AS Nombre,
       COUNT(c.id_cita)                   AS Cantidad_Citas
FROM usuario u
LEFT JOIN perfil pf ON u.id_usuario = pf.id_usuario
LEFT JOIN cita   c  ON u.id_usuario = c.id_usuario_cliente
GROUP BY u.id_usuario, u.correo, pf.nombres, pf.apellidos
ORDER BY Cantidad_Citas DESC, u.id_usuario;

-- ------------------------------------------------------------
-- 5) Agregación con GROUP BY y HAVING: propiedades por
--    ciudad y estado (alimenta el reporte del administrador).
-- ------------------------------------------------------------
SELECT ci.nombre AS Ciudad,
       p.estado  AS Estado,
       COUNT(*)  AS Total
FROM propiedad p
INNER JOIN ciudad ci ON p.id_ciudad = ci.id_ciudad
GROUP BY ci.nombre, p.estado
HAVING COUNT(*) >= 1
ORDER BY Total DESC, Ciudad;

-- ------------------------------------------------------------
-- 5b) Agregación con GROUP BY y HAVING: propiedades más
--     solicitadas (alimenta el reporte por cita).
-- ------------------------------------------------------------
SELECT p.id_propiedad AS ID,
       p.titulo       AS Propiedad,
       p.direccion    AS Ubicacion,
       COUNT(c.id_cita) AS Numero_Citas
FROM propiedad p
INNER JOIN cita c ON p.id_propiedad = c.id_propiedad
GROUP BY p.id_propiedad, p.titulo, p.direccion
HAVING COUNT(c.id_cita) > 0
ORDER BY Numero_Citas DESC;

-- ------------------------------------------------------------
-- 5c) Agregación con GROUP BY: citas por estado (reporte
--     requerido en el enunciado del proyecto).
-- ------------------------------------------------------------
SELECT c.estado AS Estado,
       COUNT(*) AS Total
FROM cita c
GROUP BY c.estado
ORDER BY Total DESC, Estado;