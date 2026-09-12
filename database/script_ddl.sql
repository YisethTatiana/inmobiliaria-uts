-- ============================================================
-- INMOBILIARIA UTS - SCRIPT DDL (Modelo Relacional en 3FN)
-- Motor: MySQL 8 / MariaDB
-- Contiene: llaves primarias, foráneas, restricciones UNIQUE,
--           acciones referenciales y los tres tipos de relación
--           (1:1, 1:N, N:M) exigidos.
-- ============================================================

CREATE DATABASE IF NOT EXISTS inmobiliaria_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE inmobiliaria_db;

-- Eliminación en orden inverso de dependencias (para re-ejecución)
DROP TABLE IF EXISTS auditoria;
DROP TABLE IF EXISTS documento_solicitud;
DROP TABLE IF EXISTS solicitud;
DROP TABLE IF EXISTS cita;
DROP TABLE IF EXISTS favorito;
DROP TABLE IF EXISTS propiedad_caracteristica;
DROP TABLE IF EXISTS imagen_propiedad;
DROP TABLE IF EXISTS propiedad;
DROP TABLE IF EXISTS caracteristica;
DROP TABLE IF EXISTS tipo_propiedad;
DROP TABLE IF EXISTS ciudad;
DROP TABLE IF EXISTS usuario_inmobiliaria;
DROP TABLE IF EXISTS inmobiliaria;
DROP TABLE IF EXISTS perfil;
DROP TABLE IF EXISTS usuario_rol;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS rol;

-- ------------------------------------------------------------
-- CATÁLOGOS BASE (1:N hacia propiedad / usuario)
-- ------------------------------------------------------------

CREATE TABLE rol (
    id_rol      INT NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(50)  NOT NULL,
    descripcion VARCHAR(200) NULL,
    PRIMARY KEY (id_rol),
    CONSTRAINT uq_rol_nombre UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE ciudad (
    id_ciudad INT NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_ciudad),
    CONSTRAINT uq_ciudad_nombre UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE tipo_propiedad (
    id_tipo INT NOT NULL AUTO_INCREMENT,
    nombre  VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_tipo),
    CONSTRAINT uq_tipo_nombre UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE caracteristica (
    id_caracteristica INT NOT NULL AUTO_INCREMENT,
    nombre            VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_caracteristica),
    CONSTRAINT uq_caracteristica_nombre UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE inmobiliaria (
    id_inmobiliaria INT NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(150) NOT NULL,
    nit             VARCHAR(20)  NOT NULL,
    correo_contacto VARCHAR(100) NULL,
    telefono        VARCHAR(20)  NULL,
    direccion       VARCHAR(200) NULL,
    PRIMARY KEY (id_inmobiliaria),
    CONSTRAINT uq_inmobiliaria_nombre UNIQUE (nombre),
    CONSTRAINT uq_inmobiliaria_nit    UNIQUE (nit)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ------------------------------------------------------------
-- USUARIO Y PERFIL
-- Relación 1:1 materializada: perfil.id_usuario es UNIQUE.
-- Usuario guarda solo credenciales/estado; perfil los datos personales.
-- ------------------------------------------------------------

CREATE TABLE usuario (
    id_usuario    INT NOT NULL AUTO_INCREMENT,
    correo        VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    activo        TINYINT(1) NOT NULL DEFAULT 1,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_correo UNIQUE (correo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE perfil (
    id_perfil  INT NOT NULL AUTO_INCREMENT,
    id_usuario INT          NOT NULL,
    nombres    VARCHAR(100) NOT NULL,
    apellidos  VARCHAR(100) NOT NULL,
    documento  VARCHAR(30)  NOT NULL,
    telefono   VARCHAR(20)  NULL,
    direccion  VARCHAR(200) NULL,
    foto       VARCHAR(255) NULL,
    PRIMARY KEY (id_perfil),
    -- FK con UNIQUE => relación 1:1 (un usuario NO puede tener dos perfiles)
    CONSTRAINT uq_perfil_id_usuario UNIQUE (id_usuario),
    CONSTRAINT uq_perfil_documento   UNIQUE (documento),
    CONSTRAINT fk_perfil_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ------------------------------------------------------------
-- RELACIÓN N:M USUARIO - ROL  (tabla intermedia con PK compuesta)
-- Un usuario puede tener varios roles y un rol pertenece a varios
-- usuarios. La PK compuesta (id_usuario, id_rol) impide roles repetidos.
-- ------------------------------------------------------------

CREATE TABLE usuario_rol (
    id_usuario INT NOT NULL,
    id_rol     INT NOT NULL,
    fecha_asignacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_ur_rol FOREIGN KEY (id_rol)
        REFERENCES rol (id_rol)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Vincula a un usuario INMOBILIARIA con su inmobiliaria (1:N agente->inmobiliaria)
CREATE TABLE usuario_inmobiliaria (
    id_usuario      INT NOT NULL,
    id_inmobiliaria INT NOT NULL,
    PRIMARY KEY (id_usuario, id_inmobiliaria),
    CONSTRAINT fk_ui_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_ui_inmobiliaria FOREIGN KEY (id_inmobiliaria)
        REFERENCES inmobiliaria (id_inmobiliaria)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ------------------------------------------------------------
-- PROPIEDAD (lado "muchos" de inmobiliaria 1:N y ciudad/tipo 1:N)
-- matricula_inmobiliaria UNIQUE => identifica irrepetiblemente el inmueble
-- ------------------------------------------------------------

CREATE TABLE propiedad (
    id_propiedad          INT NOT NULL AUTO_INCREMENT,
    matricula_inmobiliaria VARCHAR(50)  NOT NULL,
    titulo                VARCHAR(150) NOT NULL,
    descripcion           TEXT         NULL,
    precio                DECIMAL(15,2) NOT NULL,
    direccion             VARCHAR(200) NOT NULL,
    area                  DECIMAL(10,2) NULL,
    habitaciones          INT          NULL,
    banios                INT          NULL,
    parqueaderos          INT          NULL,
    estado                ENUM('DISPONIBLE','INACTIVO','VENDIDO','ARRENDADO','RESERVADO') NOT NULL DEFAULT 'DISPONIBLE',
    fecha_publicacion     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_ciudad             INT NOT NULL,
    id_tipo               INT NOT NULL,
    id_inmobiliaria       INT NOT NULL,
    PRIMARY KEY (id_propiedad),
    CONSTRAINT uq_propiedad_matricula UNIQUE (matricula_inmobiliaria),
    CONSTRAINT fk_propiedad_ciudad     FOREIGN KEY (id_ciudad)
        REFERENCES ciudad (id_ciudad)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_propiedad_tipo       FOREIGN KEY (id_tipo)
        REFERENCES tipo_propiedad (id_tipo)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_propiedad_inmobiliaria FOREIGN KEY (id_inmobiliaria)
        REFERENCES inmobiliaria (id_inmobiliaria)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Relación 1:N propiedad -> imagen_propiedad (FK en el lado muchos)
CREATE TABLE imagen_propiedad (
    id_imagen       INT NOT NULL AUTO_INCREMENT,
    id_propiedad    INT NOT NULL,
    ruta            VARCHAR(255) NOT NULL,
    titulo          VARCHAR(100) NULL,
    es_principal    TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id_imagen),
    CONSTRAINT fk_imagen_propiedad FOREIGN KEY (id_propiedad)
        REFERENCES propiedad (id_propiedad)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Relación N:M propiedad - caracteristica (tabla intermedia con
-- atributo propio "cantidad" y PK compuesta)
CREATE TABLE propiedad_caracteristica (
    id_propiedad       INT NOT NULL,
    id_caracteristica  INT NOT NULL,
    cantidad           INT NOT NULL DEFAULT 1,
    fecha_asignacion   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_propiedad, id_caracteristica),
    CONSTRAINT fk_pc_propiedad      FOREIGN KEY (id_propiedad)
        REFERENCES propiedad (id_propiedad)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_pc_caracteristica FOREIGN KEY (id_caracteristica)
        REFERENCES caracteristica (id_caracteristica)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ------------------------------------------------------------
-- FAVORITO (N:M cliente - propiedad)
-- ------------------------------------------------------------

CREATE TABLE favorito (
    id_usuario      INT NOT NULL,
    id_propiedad    INT NOT NULL,
    fecha_agregado  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario, id_propiedad),
    CONSTRAINT fk_favorito_usuario   FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_favorito_propiedad FOREIGN KEY (id_propiedad)
        REFERENCES propiedad (id_propiedad)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ------------------------------------------------------------
-- CITA (1:N cliente - cita / propiedad - cita)
-- UNIQUE (id_propiedad, fecha_hora) => no se agendan dos visitas
-- a la misma propiedad en el mismo horario.
-- ------------------------------------------------------------

CREATE TABLE cita (
    id_cita            INT NOT NULL AUTO_INCREMENT,
    id_usuario_cliente INT NOT NULL,
    id_propiedad       INT NOT NULL,
    fecha_hora         DATETIME NOT NULL,
    observaciones      VARCHAR(255) NULL,
    estado             ENUM('PENDIENTE','APROBADA','CANCELADA','COMPLETADA') NOT NULL DEFAULT 'PENDIENTE',
    PRIMARY KEY (id_cita),
    CONSTRAINT uq_cita_propiedad_fecha UNIQUE (id_propiedad, fecha_hora),
    CONSTRAINT fk_cita_usuario   FOREIGN KEY (id_usuario_cliente)
        REFERENCES usuario (id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_cita_propiedad FOREIGN KEY (id_propiedad)
        REFERENCES propiedad (id_propiedad)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ------------------------------------------------------------
-- SOLICITUD Y DOCUMENTOS (trámite de compra/arriendo)
-- 1:N solicitud -> documento_solicitud
-- ------------------------------------------------------------

CREATE TABLE solicitud (
    id_solicitud       INT NOT NULL AUTO_INCREMENT,
    id_usuario_cliente INT NOT NULL,
    id_propiedad       INT NOT NULL,
    tipo_solicitud     ENUM('COMPRA','ARRIENDO') NOT NULL,
    estado             ENUM('RADICADA','APROBADA','RECHAZADA','ANULADA') NOT NULL DEFAULT 'RADICADA',
    fecha_solicitud    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones      VARCHAR(255) NULL,
    PRIMARY KEY (id_solicitud),
    CONSTRAINT fk_solicitud_usuario   FOREIGN KEY (id_usuario_cliente)
        REFERENCES usuario (id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_solicitud_propiedad FOREIGN KEY (id_propiedad)
        REFERENCES propiedad (id_propiedad)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE documento_solicitud (
    id_documento     INT NOT NULL AUTO_INCREMENT,
    id_solicitud     INT NOT NULL,
    nombre_archivo   VARCHAR(200) NOT NULL,
    ruta             VARCHAR(255) NOT NULL,
    tipo_documento   VARCHAR(60)  NOT NULL,
    estado           ENUM('RADICADO','REVISADO','APROBADO','RECHAZADO') NOT NULL DEFAULT 'RADICADO',
    fecha_carga      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_documento),
    CONSTRAINT fk_documento_solicitud FOREIGN KEY (id_solicitud)
        REFERENCES solicitud (id_solicitud)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ------------------------------------------------------------
-- AUDITORÍA (registro de actividad de la aplicación)
-- ------------------------------------------------------------

CREATE TABLE auditoria (
    id_auditoria INT NOT NULL AUTO_INCREMENT,
    id_usuario   INT NULL,
    accion       VARCHAR(60)  NOT NULL,
    entidad      VARCHAR(60)  NOT NULL,
    id_entidad   INT NULL,
    detalle      VARCHAR(255) NULL,
    ip           VARCHAR(45)  NULL,
    fecha        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_auditoria),
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;