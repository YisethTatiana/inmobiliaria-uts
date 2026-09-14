-- ============================================================
-- INMOBILIARIA UTS - ACTUALIZACIÓN DE SEGURIDAD (BD existente)
-- Agrega: bloqueo temporal por intentos fallidos y token de
-- recuperación de contraseña. Idempotente para MySQL 8 / MariaDB.
-- ============================================================

USE inmobiliaria_db;

SET @existe_intentos = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = 'inmobiliaria_db' AND TABLE_NAME = 'usuario'
    AND COLUMN_NAME = 'intentos_fallidos');
SET @ddl_intentos = IF(@existe_intentos = 0,
  'ALTER TABLE usuario ADD COLUMN intentos_fallidos INT NOT NULL DEFAULT 0', 'SELECT 1');
PREPARE st FROM @ddl_intentos; EXECUTE st; DEALLOCATE PREPARE st;

SET @existe_bloqueo = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = 'inmobiliaria_db' AND TABLE_NAME = 'usuario'
    AND COLUMN_NAME = 'bloqueado_hasta');
SET @ddl_bloqueo = IF(@existe_bloqueo = 0,
  'ALTER TABLE usuario ADD COLUMN bloqueado_hasta DATETIME NULL', 'SELECT 1');
PREPARE st FROM @ddl_bloqueo; EXECUTE st; DEALLOCATE PREPARE st;

SET @existe_token = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = 'inmobiliaria_db' AND TABLE_NAME = 'usuario'
    AND COLUMN_NAME = 'token_recuperacion');
SET @ddl_token = IF(@existe_token = 0,
  'ALTER TABLE usuario ADD COLUMN token_recuperacion VARCHAR(64) NULL', 'SELECT 1');
PREPARE st FROM @ddl_token; EXECUTE st; DEALLOCATE PREPARE st;

SET @existe_token_exp = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = 'inmobiliaria_db' AND TABLE_NAME = 'usuario'
    AND COLUMN_NAME = 'token_expiracion');
SET @ddl_token_exp = IF(@existe_token_exp = 0,
  'ALTER TABLE usuario ADD COLUMN token_expiracion DATETIME NULL', 'SELECT 1');
PREPARE st FROM @ddl_token_exp; EXECUTE st; DEALLOCATE PREPARE st;

-- Elimina las cuentas distintas de administrador y agente1 (si existen),
-- dejando únicamente estas dos cuentas
DELETE u FROM usuario u WHERE u.id_usuario NOT IN (1, 2)
  AND u.correo IN ('agente2@inmobiliaria.com',
    'carlos.perez@mail.com','maria.rodriguez@mail.com','jose.martinez@mail.com',
    'diana.lopez@mail.com','pedro.sanchez@mail.com','luisa.fernandez@mail.com',
    'jorge.garcia@mail.com','ana.torres@mail.com','miguel.castro@mail.com',
    'sofia.ramirez@mail.com');

-- ============================================================
-- OPERACIÓN DE LA PROPIEDAD (VENTA / ARRIENDO)
-- ============================================================

SET @existe_operacion = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = 'inmobiliaria_db' AND TABLE_NAME = 'propiedad'
    AND COLUMN_NAME = 'operacion');
SET @ddl_operacion = IF(@existe_operacion = 0,
  'ALTER TABLE propiedad ADD COLUMN operacion ENUM(''VENTA'',''ARRIENDO'') NOT NULL DEFAULT ''VENTA'' AFTER estado',
  'SELECT 1');
PREPARE st FROM @ddl_operacion; EXECUTE st; DEALLOCATE PREPARE st;

-- Distribución de muestra: algunas propiedades en arriendo
UPDATE propiedad SET operacion = 'ARRIENDO'
WHERE operacion = 'VENTA' AND id_propiedad IN (2, 3, 4, 6, 7, 8, 9, 12);