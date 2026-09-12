-- ============================================================
-- INMOBILIARIA UTS - SCRIPT DML (Datos de prueba)
-- Contraseñas (hash SHA-256 con salt):
--   admin123   -> admin@inmobiliaria.com
--   agente123  -> agente1@inmobiliaria.com / agente2@inmobiliaria.com
-- Roles del sistema (4): VISITANTE (no autenticado, sin registro en BD),
-- CLIENTE (3), INMOBILIARIA (2) y ADMINISTRADOR (1).
-- Solo se siembran cuentas de ADMINISTRADOR e INMOBILIARIA; los CLIENTES
-- se crean desde el registro público (formulario) sin intervención manual.
-- ============================================================

USE inmobiliaria_db;

-- ------------------------------------------------ ROLES (3 en BD; el 4º rol VISITANTE es anónimo, sin sesión)
INSERT INTO rol (id_rol, nombre, descripcion) VALUES
(1, 'ADMINISTRADOR', 'Acceso total: usuarios, catálogos, reportes y auditoría'),
(2, 'INMOBILIARIA',  'Agente inmobiliario: publica y gestiona propiedades, citas y solicitudes'),
(3, 'CLIENTE',       'Cliente final: busca, marca favoritos, agenda citas y radica solicitudes');

-- ------------------------------------------------ CIUDADES (10)
INSERT INTO ciudad (id_ciudad, nombre) VALUES
(1, 'Bucaramanga'), (2, 'Cúcuta'), (3, 'Bogotá'), (4, 'Medellín'),
(5, 'Cali'), (6, 'Barranquilla'), (7, 'Cartagena'), (8, 'Santa Marta'),
(9, 'Manizales'), (10, 'Pereira');

-- ------------------------------------------------ TIPOS DE PROPIEDAD (5)
INSERT INTO tipo_propiedad (id_tipo, nombre) VALUES
(1, 'Casa'), (2, 'Apartamento'), (3, 'Local'), (4, 'Oficina'), (5, 'Terreno');

-- ------------------------------------------------ CARACTERÍSTICAS (8)
INSERT INTO caracteristica (id_caracteristica, nombre) VALUES
(1, 'Piscina'), (2, 'Parqueadero'), (3, 'Ascensor'), (4, 'Gimnasio'),
(5, 'Zona de niños'), (6, 'Seguridad 24h'), (7, 'Balcón'), (8, 'Jardín');

-- ------------------------------------------------ INMOBILIARIAS (3)
INSERT INTO inmobiliaria (id_inmobiliaria, nombre, nit, correo_contacto, telefono, direccion) VALUES
(1, 'Inmobiliaria UTS',        '900.100.001-1', 'contacto@inmobiliaria-uts.com', '600 123 4567', 'Calle 48 # 27-80, Bucaramanga'),
(2, 'Inmobiliaria Horizonte',  '900.100.002-2', 'agentes@horizonte.com',         '601 555 0102', 'Carrera 15 # 93-40, Bogotá'),
(3, 'Inmobiliaria Premium',    '900.100.003-3', 'ventas@premium.com',            '604 333 2211', 'Calle 10 # 43-16, Medellín');

-- ------------------------------------------------ USUARIOS (3: 1 admin, 2 agentes; clientes se registran en la app)
INSERT INTO usuario (id_usuario, correo, password_hash, activo) VALUES
(1,  'admin@inmobiliaria.com',    'eBsYNqFXdwobDSUNwxibetS1yq4o0P3/IHShZrvYC3s=:xuQYqfgMLSGoISZGmFM+vg==', 1),
(2,  'agente1@inmobiliaria.com',  's6P+4lQksbPZdYxBbKb/+bhTNGckidTNUnU+OzMujlk=:SO03E7AAY7d3mFG3cxdH7A==', 1),
(3,  'agente2@inmobiliaria.com',  's6P+4lQksbPZdYxBbKb/+bhTNGckidTNUnU+OzMujlk=:SO03E7AAY7d3mFG3cxdH7A==', 1);

-- ------------------------------------------------ USUARIO_ROLES (N:M)
INSERT INTO usuario_rol (id_usuario, id_rol) VALUES
(1, 1),
(2, 2), (3, 2);

-- ------------------------------------------------ USUARIO_INMOBILIARIA (vincula agentes)
INSERT INTO usuario_inmobiliaria (id_usuario, id_inmobiliaria) VALUES
(2, 1), (3, 2);

-- ------------------------------------------------ PERFILES (3, relación 1:1 con usuario)
INSERT INTO perfil (id_perfil, id_usuario, nombres, apellidos, documento, telefono, direccion, foto) VALUES
(1, 1,  'Administrador', 'Sistema',        '1000000001', '600 111 1111', 'Centro, Bucaramanga', NULL),
(2, 2,  'Laura',          'Gómez Rincón',   '1098000002', '300 222 3344', 'Lagos del Cacique, Bucaramanga', NULL),
(3, 3,  'Andrés',         'Méndez Pardo',   '1010000003', '311 333 4455', 'Usaquén, Bogotá', NULL);

-- ------------------------------------------------ PROPIEDADES (12)
INSERT INTO propiedad (id_propiedad, matricula_inmobiliaria, titulo, descripcion, precio, direccion, area, habitaciones, banios, parqueaderos, estado, id_ciudad, id_tipo, id_inmobiliaria, fecha_publicacion) VALUES
(1,  'MAT-000001', 'Casa de dos pisos en Cabecera',        'Hermosa casa familiar con jardín, zona de parqueo para dos autos y amplia zona social.', 850000000, 'Cra 33 # 50-12, Cabecera',      220, 4, 3, 2, 'DISPONIBLE',  1, 1, 1, '2026-05-10 10:00:00'),
(2,  'MAT-000002', 'Apartamento en El Poblado',            'Apartamento moderno con balcón, ascensor, piscina y gimnasio en el conjunto.',           520000000, 'Calle 10 # 43-16, El Poblado',   95, 3, 2, 1, 'DISPONIBLE',  4, 2, 3, '2026-05-12 11:30:00'),
(3,  'MAT-000003', 'Local comercial en Chapinero',         'Local ideal para comercio con excelente flujo peatonal.',                               350000000, 'Cra 13 # 65-10, Chapinero',     48, 0, 1, 0, 'DISPONIBLE',  3, 3, 2, '2026-05-15 09:00:00'),
(4,  'MAT-000004', 'Oficina en la 93',                     'Oficina amoblada con ascensor, seguridad 24h y parqueadero.',                           280000000, 'Cra 15 # 93-40, Chicó',         60, 0, 1, 1, 'DISPONIBLE',  3, 4, 2, '2026-05-18 15:20:00'),
(5,  'MAT-000005', 'Terreno en Ruitoque',                  'Terreno de 2000 m2 con vista a las montañas, apto para vivienda campestre.',            120000000, 'Vereda Ruitoque, km 5',        2000, 0, 0, 0, 'DISPONIBLE',  1, 5, 1, '2026-05-20 08:45:00'),
(6,  'MAT-000006', 'Casa campestre en Girón',              'Casa con piscina, jardín y amplio parqueadero, ideal para recreo.',                      950000000, 'Vereda Chocoa, Girón',          350, 5, 4, 4, 'DISPONIBLE',  1, 1, 1, '2026-05-22 17:00:00'),
(7,  'MAT-000007', 'Apartamento estudiante en Cúcuta',     'Económico aparta estudio cerca a la universidad.',                                      98000000,  'Av. 4 # 10-30, Centro',        32, 1, 1, 0, 'DISPONIBLE',  2, 2, 1, '2026-05-25 14:10:00'),
(8,  'MAT-000008', 'Casa en Ciudad Jardín (Cali)',         'Casa amplia con zona de niños y jardín.',                                                 780000000, 'Cra 10 # 31-22, Ciudad Jardín', 280, 4, 3, 2, 'ARRENDADO',   5, 1, 2, '2026-06-01 12:00:00'),
(9,  'MAT-000009', 'Local comercial en el Centro',         'Local con bodega pequeña anexa.',                                                         260000000, 'Cra 19 # 35-14, Centro',        55, 0, 1, 1, 'DISPONIBLE',  7, 3, 3, '2026-06-05 10:30:00'),
(10, 'MAT-000010', 'Apartamento amoblado en Cartagena',   'Apartamento turístico con vista al mar, seguridad 24h.',                                 680000000, 'Cra 3 # 8-99, Bocagrande',      88, 3, 2, 1, 'VENDIDO',     7, 2, 3, '2026-06-08 16:45:00'),
(11, 'MAT-000011', 'Casa verde en Manizales',             'Casa con balcón panorámico desde el sector Alto Candela.',                                420000000, 'Cra 23A # 68-10, Alto Candela', 160, 3, 2, 1, 'INACTIVO',    9, 1, 1, '2026-06-10 09:15:00'),
(12, 'MAT-000012', 'Oficina compartida en Pereira',       'Oficina en coworking con ascensor y gimnasio.',                                          150000000, 'Calle 13 # 6-35, Centro',       40, 0, 1, 0, 'DISPONIBLE', 10, 4, 2, '2026-06-12 11:00:00');

-- ------------------------------------------------ IMÁGENES DE PROPIEDADES (1:N, 25 registros)
INSERT INTO imagen_propiedad (id_imagen, id_propiedad, ruta, titulo, es_principal) VALUES
(1, 1,  'https://picsum.photos/seed/casa1a/800/500',  'Fachada',      1),
(2, 1,  'https://picsum.photos/seed/casa1b/800/500',  'Sala',         0),
(3, 1,  'https://picsum.photos/seed/casa1c/800/500',  'Jardín',       0),
(4, 2,  'https://picsum.photos/seed/apt2a/800/500',   'Fachada',      1),
(5, 2,  'https://picsum.photos/seed/apt2b/800/500',   'Sala comedor', 0),
(6, 3,  'https://picsum.photos/seed/loc3a/800/500',   'Vista exterior', 1),
(7, 3,  'https://picsum.photos/seed/loc3b/800/500',   'Interior',     0),
(8, 4,  'https://picsum.photos/seed/of4a/800/500',    'Puesto 1',     1),
(9, 4,  'https://picsum.photos/seed/of4b/800/500',    'Puesto 2',     0),
(10, 5, 'https://picsum.photos/seed/ter5a/800/500',   'Vista del terreno', 1),
(11, 6, 'https://picsum.photos/seed/casa6a/800/500',  'Fachada',      1),
(12, 6, 'https://picsum.photos/seed/casa6b/800/500',  'Piscina',      0),
(13, 6, 'https://picsum.photos/seed/casa6c/800/500',  'Jardín',       0),
(14, 7, 'https://picsum.photos/seed/apt7a/800/500',   'Habitación',   1),
(15, 8, 'https://picsum.photos/seed/casa8a/800/500',  'Fachada',      1),
(16, 8, 'https://picsum.photos/seed/casa8b/800/500',  'Zona de niños', 0),
(17, 9, 'https://picsum.photos/seed/loc9a/800/500',   'Local',        1),
(18, 10,'https://picsum.photos/seed/apt10a/800/500',  'Vista al mar', 1),
(19, 10,'https://picsum.photos/seed/apt10b/800/500',  'Balcón',       0),
(20, 11,'https://picsum.photos/seed/casa11a/800/500', 'Fachada',      1),
(21, 12,'https://picsum.photos/seed/of12a/800/500',   'Sala coworking', 1),
(22, 2,  'https://picsum.photos/seed/apt2c/800/500',  'Piscina',      0),
(23, 4,  'https://picsum.photos/seed/of4c/800/500',   'Recepción',    0),
(24, 9,  'https://picsum.photos/seed/loc9b/800/500',  'Bodega',       0),
(25, 7,  'https://picsum.photos/seed/apt7b/800/500',  'Estudio',      0);

-- ------------------------------------------------ PROPIEDAD_CARACTERISTICA (N:M, 28 registros)
INSERT INTO propiedad_caracteristica (id_propiedad, id_caracteristica, cantidad) VALUES
(1, 2, 2), (1, 8, 1), (1, 6, 1),
(2, 1, 1), (2, 3, 1), (2, 4, 1), (2, 7, 1),
(3, 6, 1),
(4, 2, 1), (4, 3, 1), (4, 6, 1),
(6, 1, 1), (6, 2, 4), (6, 5, 1), (6, 8, 1),
(7, 6, 1),
(8, 2, 2), (8, 5, 1), (8, 8, 1), (8, 6, 1),
(9, 2, 1),
(10, 1, 1), (10, 6, 1), (10, 7, 1),
(11, 7, 1), (11, 8, 1),
(12, 3, 1), (12, 4, 1);

-- ------------------------------------------------ CITAS, SOLICITUDES, DOCUMENTOS Y FAVORITOS
-- No se siembran: dependen de cuentas CLIENTE y estas se crean desde el registro
-- público (cada cliente nuevo registra su perfil y opera citas/solicitudes/favoritos
-- desde los paneles de la aplicación). Tablas quedan listas para uso en línea.

-- ------------------------------------------------ AUDITORÍA (usuarios sembrados: admin y agentes)
INSERT INTO auditoria (id_usuario, accion, entidad, id_entidad, detalle, ip) VALUES
(1,  'LOGIN',    'USUARIO',  1,  'Inicio de sesión del administrador',          '127.0.0.1'),
(2,  'LOGIN',    'USUARIO',  2,  'Inicio de sesión del agente 1',               '192.168.1.20'),
(2,  'CREAR',    'PROPIEDAD', 6, 'Creó la propiedad Casa campestre en Girón',   '192.168.1.20'),
(2,  'DAR_BAJA', 'PROPIEDAD', 11,'Dio de baja la propiedad Casa verde Manizales','192.168.1.20'),
(1,  'ACTUALIZAR','USUARIO',  3, 'Cambió el estado de la cuenta del usuario',   '127.0.0.1'),
(3,  'APROBAR',  'CITA',     1,  'Aprobó la cita #1',                           '192.168.1.21'),
(2,  'APROBAR',  'SOLICITUD', 1, 'Aprobó la solicitud #1',                      '192.168.1.20');