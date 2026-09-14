-- ============================================================
-- INMOBILIARIA UTS - SCRIPT DML (Datos de prueba)
-- Contraseñas (hash SHA-256 con salt):
--   admin123   -> admin@inmobiliaria.com
--   agente123  -> agente1@inmobiliaria.com
--   cliente123 -> cliente1@inmobiliaria.com (cuenta CLIENTE demo)
-- Cuentas vigentes (10): ADMINISTRADOR (1), INMOBILIARIA (2, 4 y 5:
-- agentes 1-3) y CLIENTE (3, 6-10: clientes 1-6). La ampliación de
-- semilla al final garantiza >=10 registros por tabla principal;
-- las cuentas adicionales REUTILIZAN las contraseñas demo
-- documentadas (admin123 / agente123 / cliente123) para poder probar
-- todos los perfiles con las credenciales publicadas en el manual.
-- Las imágenes de propiedades usan fotografías reales de inmuebles
-- (Unsplash) acordes a cada tipo de propiedad.
-- ============================================================

USE inmobiliaria_db;

-- ------------------------------------------------ ROLES (3 en BD; el 4º rol VISITANTE es anónimo, sin sesión)
INSERT INTO rol (id_rol, nombre, descripcion) VALUES
(1, 'ADMINISTRADOR', 'Acceso total: usuarios, catálogos, reportes y auditoría'),
(2, 'INMOBILIARIA',  'Agente inmobiliario: publica y gestiona propiedades'),
(3, 'CLIENTE',       'Cliente final: busca, marca favoritos, agenda citas y radica solicitudes');

-- ------------------------------------------------ CIUDADES (10)
INSERT INTO ciudad (id_ciudad, nombre) VALUES
(1, 'Bucaramanga'), (2, 'Cúcuta'), (3, 'Bogotá'), (4, 'Medellín'),
(5, 'Cali'), (6, 'Barranquilla'), (7, 'Cartagena'), (8, 'Santa Marta'),
(9, 'Manizales'), (10, 'Pereira');

-- ------------------------------------------------ TIPOS DE PROPIEDAD (5)
INSERT INTO tipo_propiedad (id_tipo, nombre) VALUES
(1, 'Casa'), (2, 'Apartamento'), (3, 'Local'), (4, 'Oficina'), (5, 'Terreno');

-- ------------------------------------------------ CARACTERÍSTICAS (10)
INSERT INTO caracteristica (id_caracteristica, nombre) VALUES
(1, 'Piscina'), (2, 'Parqueadero'), (3, 'Ascensor'), (4, 'Gimnasio'),
(5, 'Zona de niños'), (6, 'Seguridad 24h'), (7, 'Balcón'), (8, 'Jardín'),
(9, 'Terraza'), (10, 'Baño de visitas');

-- ------------------------------------------------ INMOBILIARIAS (3)
INSERT INTO inmobiliaria (id_inmobiliaria, nombre, nit, correo_contacto, telefono, direccion) VALUES
(1, 'Inmobiliaria UTS',        '900.100.001-1', 'contacto@inmobiliaria-uts.com', '600 123 4567', 'Calle 48 # 27-80, Bucaramanga'),
(2, 'Inmobiliaria Horizonte',  '900.100.002-2', 'agentes@horizonte.com',         '601 555 0102', 'Carrera 15 # 93-40, Bogotá'),
(3, 'Inmobiliaria Premium',    '900.100.003-3', 'ventas@premium.com',            '604 333 2211', 'Calle 10 # 43-16, Medellín');

-- ------------------------------------------------ USUARIOS (2: administrador + agente1)
INSERT INTO usuario (id_usuario, correo, password_hash, activo) VALUES
(1, 'admin@inmobiliaria.com',   'eBsYNqFXdwobDSUNwxibetS1yq4o0P3/IHShZrvYC3s=:xuQYqfgMLSGoISZGmFM+vg==', 1),
(2, 'agente1@inmobiliaria.com', 's6P+4lQksbPZdYxBbKb/+bhTNGckidTNUnU+OzMujlk=:SO03E7AAY7d3mFG3cxdH7A==', 1);

-- ------------------------------------------------ USUARIO_ROLES (N:M)
INSERT INTO usuario_rol (id_usuario, id_rol) VALUES
(1, 1),
(2, 2);

-- ------------------------------------------------ USUARIO_INMOBILIARIA (vincula agente1)
INSERT INTO usuario_inmobiliaria (id_usuario, id_inmobiliaria) VALUES
(2, 1);

-- ------------------------------------------------ PERFILES (2, relación 1:1 con usuario)
INSERT INTO perfil (id_perfil, id_usuario, nombres, apellidos, documento, telefono, direccion, foto) VALUES
(1, 1, 'Administrador', 'Sistema',      '1000000001', '600 111 1111', 'Centro, Bucaramanga', NULL),
(2, 2, 'Laura',         'Gómez Rincón', '1098000002', '300 222 3344', 'Lagos del Cacique, Bucaramanga', NULL);

-- ------------------------------------------------ PROPIEDADES (12)
INSERT INTO propiedad (id_propiedad, matricula_inmobiliaria, titulo, descripcion, precio, direccion, area, habitaciones, banios, parqueaderos, estado, operacion, id_ciudad, id_tipo, id_inmobiliaria, fecha_publicacion) VALUES
(1,  'MAT-000001', 'Casa de dos pisos en Cabecera',        'Hermosa casa familiar con jardín, zona de parqueo para dos autos y amplia zona social.', 850000000, 'Cra 33 # 50-12, Cabecera',      220, 4, 3, 2, 'DISPONIBLE',  'VENTA',    1, 1, 1, '2026-05-10 10:00:00'),
(2,  'MAT-000002', 'Apartamento en El Poblado',            'Apartamento moderno con balcón, ascensor, piscina y gimnasio en el conjunto.',           520000000, 'Calle 10 # 43-16, El Poblado',   95, 3, 2, 1, 'DISPONIBLE',  'ARRIENDO', 4, 2, 3, '2026-05-12 11:30:00'),
(3,  'MAT-000003', 'Local comercial en Chapinero',         'Local ideal para comercio con excelente flujo peatonal.',                               350000000, 'Cra 13 # 65-10, Chapinero',     48, 0, 1, 0, 'DISPONIBLE',  'ARRIENDO', 3, 3, 2, '2026-05-15 09:00:00'),
(4,  'MAT-000004', 'Oficina en la 93',                     'Oficina amoblada con ascensor, seguridad 24h y parqueadero.',                           280000000, 'Cra 15 # 93-40, Chicó',         60, 0, 1, 1, 'DISPONIBLE',  'ARRIENDO', 3, 4, 2, '2026-05-18 15:20:00'),
(5,  'MAT-000005', 'Terreno en Ruitoque',                  'Terreno de 2000 m2 con vista a las montañas, apto para vivienda campestre.',            120000000, 'Vereda Ruitoque, km 5',        2000, 0, 0, 0, 'DISPONIBLE',  'VENTA',    1, 5, 1, '2026-05-20 08:45:00'),
(6,  'MAT-000006', 'Casa campestre en Girón',              'Casa con piscina, jardín y amplio parqueadero, ideal para recreo.',                     950000000, 'Vereda Chocoa, Girón',          350, 5, 4, 4, 'DISPONIBLE',  'ARRIENDO', 1, 1, 1, '2026-05-22 17:00:00'),
(7,  'MAT-000007', 'Apartamento estudiante en Cúcuta',     'Económico aparta estudio cerca a la universidad.',                                       98000000,  'Av. 4 # 10-30, Centro',        32, 1, 1, 0, 'DISPONIBLE',  'ARRIENDO', 2, 2, 1, '2026-05-25 14:10:00'),
(8,  'MAT-000008', 'Casa en Ciudad Jardín (Cali)',         'Casa amplia con zona de niños y jardín.',                                                780000000, 'Cra 10 # 31-22, Ciudad Jardín', 280, 4, 3, 2, 'ARRENDADO',   'ARRIENDO', 5, 1, 2, '2026-06-01 12:00:00'),
(9,  'MAT-000009', 'Local comercial en el Centro',         'Local con bodega pequeña anexa.',                                                        260000000, 'Cra 19 # 35-14, Centro',        55, 0, 1, 1, 'DISPONIBLE',  'ARRIENDO', 7, 3, 3, '2026-06-05 10:30:00'),
(10, 'MAT-000010', 'Apartamento amoblado en Cartagena',   'Apartamento turístico con vista al mar, seguridad 24h.',                                 680000000, 'Cra 3 # 8-99, Bocagrande',      88, 3, 2, 1, 'VENDIDO',     'VENTA',    7, 2, 3, '2026-06-08 16:45:00'),
(11, 'MAT-000011', 'Casa verde en Manizales',             'Casa con balcón panorámico desde el sector Alto Candela.',                                420000000, 'Cra 23A # 68-10, Alto Candela', 160, 3, 2, 1, 'INACTIVO',    'VENTA',    9, 1, 1, '2026-06-10 09:15:00'),
(12, 'MAT-000012', 'Oficina compartida en Pereira',       'Oficina en coworking con ascensor y gimnasio.',                                          150000000, 'Calle 13 # 6-35, Centro',       40, 0, 1, 0, 'DISPONIBLE',  'ARRIENDO', 10, 4, 2, '2026-06-12 11:00:00');

-- ------------------------------------------------ IMÁGENES DE PROPIEDADES (1:N, 25 registros)
-- Fotografías reales de inmuebles (Unsplash): casas, apartamentos, salas,
-- piscinas, jardines, oficinas y locales; acordes al inmueble anunciado.
INSERT INTO imagen_propiedad (id_imagen, id_propiedad, ruta, titulo, es_principal) VALUES
(1, 1,  'https://images.unsplash.com/photo-1568605114967-8130f3a36994?auto=format&fit=crop&w=1200&h=750&q=60',  'Fachada',      1),
(2, 1,  'https://images.unsplash.com/photo-1554995207-c18c203602cb?auto=format&fit=crop&w=1200&h=750&q=60',  'Sala',         0),
(3, 1,  'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?auto=format&fit=crop&w=1200&h=750&q=60',  'Jardín',       0),
(4, 2,  'https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=1200&h=750&q=60',  'Fachada',      1),
(5, 2,  'https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=1200&h=750&q=60',  'Sala comedor', 0),
(6, 3,  'https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=1200&h=750&q=60',  'Vista exterior', 1),
(7, 3,  'https://images.unsplash.com/photo-1556740738-b6a63e27c4df?auto=format&fit=crop&w=1200&h=750&q=60',  'Interior',     0),
(8, 4,  'https://images.unsplash.com/photo-1497215728101-856f4ea42174?auto=format&fit=crop&w=1200&h=750&q=60',  'Puesto 1',     1),
(9, 4,  'https://images.unsplash.com/photo-1524758631624-e2822e304c36?auto=format&fit=crop&w=1200&h=750&q=60',  'Puesto 2',     0),
(10, 5, 'https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=1200&h=750&q=60',  'Vista del terreno', 1),
(11, 6, 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1200&h=750&q=60',  'Fachada',      1),
(12, 6, 'https://images.unsplash.com/photo-1576013551627-0cc20b96c2a7?auto=format&fit=crop&w=1200&h=750&q=60',  'Piscina',      0),
(13, 6, 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=1200&h=750&q=60',  'Jardín',       0),
(14, 7, 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=1200&h=750&q=60',  'Habitación',   1),
(15, 8, 'https://images.unsplash.com/photo-1570129477492-45c003edd2be?auto=format&fit=crop&w=1200&h=750&q=60',  'Fachada',      1),
(16, 8, 'https://images.unsplash.com/photo-1472162072942-cd5147eb3902?auto=format&fit=crop&w=1200&h=750&q=60',  'Zona de niños', 0),
(17, 9, 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=1200&h=750&q=60',  'Local',        1),
(18, 10,'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&h=750&q=60',  'Vista al mar', 1),
(19, 10,'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=1200&h=750&q=60',  'Balcón',       0),
(20, 11,'https://images.unsplash.com/photo-1615873968403-89e068629265?auto=format&fit=crop&w=1200&h=750&q=60',  'Fachada',      1),
(21, 12,'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1200&h=750&q=60',  'Sala coworking', 1),
(22, 2,  'https://images.unsplash.com/photo-1560185893-a55cbc8c57e8?auto=format&fit=crop&w=1200&h=750&q=60',  'Piscina',      0),
(23, 4,  'https://images.unsplash.com/photo-1521737604893-d14cc237f11d?auto=format&fit=crop&w=1200&h=750&q=60',  'Recepción',    0),
(24, 9,  'https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?auto=format&fit=crop&w=1200&h=750&q=60',  'Bodega',       0),
(25, 7,  'https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=1200&h=750&q=60',  'Estudio',      0);

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

-- ------------------------------------------------ CLIENTE DEMO (permite citas, solicitudes y favoritos sembrados)
INSERT INTO usuario (id_usuario, correo, password_hash, activo) VALUES
(3, 'cliente1@inmobiliaria.com', 'aHudONtG1X1GzK996IfmcRlsxLx65zztXKBEfzPTPmY=:aW5tby1zYWx0LWRiLTIwMjY=', 1);

INSERT INTO usuario_rol (id_usuario, id_rol) VALUES
(3, 3);

INSERT INTO perfil (id_perfil, id_usuario, nombres, apellidos, documento, telefono, direccion, foto) VALUES
(3, 3, 'Carlos', 'Méndez Torres', '1098000003', '301 555 6677', 'Cabecera, Bucaramanga', NULL);

-- ------------------------------------------------ FAVORITOS (N:M cliente - propiedad)
INSERT INTO favorito (id_usuario, id_propiedad) VALUES
(3, 1), (3, 5), (3, 6), (3, 9);

-- ------------------------------------------------ CITAS (10) sobre propiedades de la Inmobiliaria UTS (inmobiliaria 1)
INSERT INTO cita (id_cita, id_usuario_cliente, id_propiedad, fecha_hora, observaciones, estado) VALUES
(2,  3, 1, '2026-09-20 10:00:00', 'Visita familiar',              'APROBADA'),
(3,  3, 1, '2026-09-22 15:30:00', 'Segunda visita con mi esposa', 'PENDIENTE'),
(4,  3, 5, '2026-09-21 09:00:00', 'Quiero recorrer el terreno',   'APROBADA'),
(5,  3, 6, '2026-09-23 16:00:00', 'Reunión el fin de semana',     'PENDIENTE'),
(6,  3, 7, '2026-09-25 11:00:00', 'Para mi hijo universitario',   'CANCELADA'),
(7,  3, 1, '2026-08-30 10:00:00', 'Primera visita',               'COMPLETADA'),
(8,  3, 6, '2026-08-15 17:00:00', 'Visita inicial',               'COMPLETADA'),
(9,  3, 5, '2026-09-28 08:30:00', 'Pendiente por confirmar',      'PENDIENTE'),
(10, 3, 7, '2026-09-29 14:00:00', 'Con el asesor comercial',      'APROBADA'),
(11, 3, 6, '2026-10-02 10:30:00', 'Negociación de cláusulas',     'PENDIENTE');

-- ------------------------------------------------ SOLICITUDES (3) con sus documentos (1:N)
INSERT INTO solicitud (id_solicitud, id_usuario_cliente, id_propiedad, tipo_solicitud, estado, observaciones) VALUES
(1, 3, 1, 'COMPRA',  'APROBADA', 'Crédito hipotecario preaprobado'),
(2, 3, 6, 'ARRIENDO','RADICADA', 'Contrato anual con póliza'),
(3, 3, 9, 'COMPRA',  'RADICADA', 'En espera de documentos de la inmobiliaria');

INSERT INTO documento_solicitud (id_documento, id_solicitud, nombre_archivo, ruta, tipo_documento) VALUES
(1, 1, 'cedula_carlos.pdf',               'uploads/solicitud_1/cedula_carlos.pdf',                'Cédula'),
(2, 1, 'certificado_laboral.pdf',         'uploads/solicitud_1/certificado_laboral.pdf',          'Certificado laboral'),
(3, 2, 'referencias.pdf',                 'uploads/solicitud_2/referencias.pdf',                  'Referencias'),
(4, 3, 'certificado_tradicion.pdf',       'uploads/solicitud_3/certificado_tradicion.pdf',        'Certificado de tradición');

-- ------------------------------------------------ AUDITORÍA (12 registros: admin y agente1)
INSERT INTO auditoria (id_usuario, accion, entidad, id_entidad, detalle, ip) VALUES
(1,  'LOGIN',    'USUARIO',   1,  'Inicio de sesión del administrador',            '127.0.0.1'),
(2,  'LOGIN',    'USUARIO',   2,  'Inicio de sesión del agente 1',                 '192.168.1.20'),
(2,  'CREAR',    'PROPIEDAD', 1,  'Publicó la propiedad Casa de dos pisos',        '192.168.1.20'),
(2,  'CREAR',    'PROPIEDAD', 6,  'Publicó la propiedad Casa campestre en Girón',  '192.168.1.20'),
(2,  'EDITAR',   'PROPIEDAD', 2,  'Actualizó el precio del apartamento',           '192.168.1.20'),
(2,  'DAR_BAJA', 'PROPIEDAD', 11, 'Dio de baja la propiedad Casa verde Manizales', '192.168.1.20'),
(2,  'REACTIVAR','PROPIEDAD', 11, 'Reactivó la propiedad Casa verde Manizales',    '192.168.1.20'),
(1,  'ACTUALIZAR','USUARIO',  2,  'Cambió el estado de la cuenta del agente 1',    '127.0.0.1'),
(1,  'CREAR',    'CIUDAD',    10, 'Registró la ciudad Pereira',                    '127.0.0.1'),
(1,  'CREAR',    'TIPO',      5,  'Registró el tipo de propiedad Terreno',         '127.0.0.1'),
(1,  'LOGIN',    'USUARIO',   1,  'Segundo inicio de sesión del administrador',    '127.0.0.1'),
(2,  'LOGOUT',   'USUARIO',   2,  'Cierre de sesión del agente 1',                 '192.168.1.20');

-- ============================================================
-- AMPLIACIÓN DE SEMILLA: garantiza >= 10 registros por tabla
-- principal (espín de la entrega). Las cuentas adicionales
-- REUTILIZAN los hashes de contraseña demo ya documentados
-- (admin123 / agente123 / cliente123); en un entorno productivo
-- debe generarse un hash individual por cuenta.
-- ============================================================

-- ------------------------------------------------ ROLES (3 + 7 = 10)
INSERT INTO rol (id_rol, nombre, descripcion) VALUES
(4, 'VISITANTE',       'Usuario anónimo: solo navega y filtra el catálogo'),
(5, 'DIRECCION',       'Nivel directivo: aprueba operaciones y consulta reportes'),
(6, 'FINANCIERO',      'Gestión de precios, recaudo y comisiones'),
(7, 'MARKETING',       'Publicidad y campañas de las propiedades'),
(8, 'CONTABILIDAD',    'Contabilidad, facturación y cierre contable'),
(9, 'JURIDICO',        'Revisión legal de contratos y documentos'),
(10, 'SOPORTE_TECNICO','Atención de incidencias de la plataforma');

-- ------------------------------------------------ TIPOS DE PROPIEDAD (5 + 5 = 10)
INSERT INTO tipo_propiedad (id_tipo, nombre) VALUES
(6, 'Bodega'), (7, 'Parqueadero'), (8, 'Consultorio'),
(9, 'Casa campestre'), (10, 'Edificio');

-- ------------------------------------------------ INMOBILIARIAS (3 + 7 = 10)
INSERT INTO inmobiliaria (id_inmobiliaria, nombre, nit, correo_contacto, telefono, direccion) VALUES
(4, 'Inmobiliaria del Norte',       '900.100.004-4', 'contacto@norte.com',    '601 888 0001', 'Av Calle 127 # 154, Bogotá'),
(5, 'Inmobiliaria Jardín Real',     '900.100.005-5', 'hola@jardinreal.com',  '604 444 0010', 'Calle 5A # 17-30, Medellín'),
(6, 'Inmobiliaria Costa Caribe',    '900.100.006-6', 'ventas@costacaribe.com','605 335 2200', 'Cra 51 B # 39-51, Barranquilla'),
(7, 'Inmobiliaria Colonial',        '900.100.007-7', 'info@colonial.com',    '605 640 1212', 'Calle del Coliseo # 0-90, Cartagena'),
(8, 'Inmobiliaria del Café',        '900.100.008-8', 'agencias@delcafe.com', '606 884 4400', 'Cra 20 # 8-12, Manizales'),
(9, 'Inmobiliaria Sur Andina',      '900.100.009-9', 'contacto@surs.com',    '606 321 0009', 'Calle 18 # 8-55, Pereira'),
(10, 'Inmobiliaria Litoral',        '900.100.010-0', 'info@litoral.com',     '605 423 3333', 'Calle 29 # 25-10, Santa Marta');

-- ------------------------------------------------ USUARIOS (3 + 7 = 10) — contraseñas demo reutilizadas
INSERT INTO usuario (id_usuario, correo, password_hash, activo) VALUES
(4, 'agente2@inmobiliaria.com',    's6P+4lQksbPZdYxBbKb/+bhTNGckidTNUnU+OzMujlk=:SO03E7AAY7d3mFG3cxdH7A==', 1),
(5, 'agente3@inmobiliaria.com',    's6P+4lQksbPZdYxBbKb/+bhTNGckidTNUnU+OzMujlk=:SO03E7AAY7d3mFG3cxdH7A==', 1),
(6, 'cliente2@inmobiliaria.com',   'aHudONtG1X1GzK996IfmcRlsxLx65zztXKBEfzPTPmY=:aW5tby1zYWx0LWRiLTIwMjY=', 1),
(7, 'cliente3@inmobiliaria.com',   'aHudONtG1X1GzK996IfmcRlsxLx65zztXKBEfzPTPmY=:aW5tby1zYWx0LWRiLTIwMjY=', 1),
(8, 'cliente4@inmobiliaria.com',   'aHudONtG1X1GzK996IfmcRlsxLx65zztXKBEfzPTPmY=:aW5tby1zYWx0LWRiLTIwMjY=', 1),
(9, 'cliente5@inmobiliaria.com',   'aHudONtG1X1GzK996IfmcRlsxLx65zztXKBEfzPTPmY=:aW5tby1zYWx0LWRiLTIwMjY=', 1),
(10,'cliente6@inmobiliaria.com',   'aHudONtG1X1GzK996IfmcRlsxLx65zztXKBEfzPTPmY=:aW5tby1zYWx0LWRiLTIwMjY=', 1);

-- ------------------------------------------------ USUARIO_ROL (N:M, 3 + 7 = 10)
INSERT INTO usuario_rol (id_usuario, id_rol) VALUES
(4, 2), (5, 2), (6, 3), (7, 3), (8, 3), (9, 3), (10, 3);

-- ------------------------------------------------ USUARIO_INMOBILIARIA (agentes -> inmobiliarias, 1 + 9 = 10)
INSERT INTO usuario_inmobiliaria (id_usuario, id_inmobiliaria) VALUES
(2, 4), (2, 5), (4, 2), (4, 6), (5, 3), (5, 7), (2, 8), (4, 9), (5, 10);

-- ------------------------------------------------ PERFIL (1:1 con usuario, 3 + 7 = 10)
INSERT INTO perfil (id_perfil, id_usuario, nombres, apellidos, documento, telefono, direccion) VALUES
(4, 4, 'Ana',           'Rojas Peña',     '1098000004', '300 444 0004', 'Cra 27 # 34-10, Bucaramanga'),
(5, 5, 'Jorge',         'Martínez Cruz',  '1098000005', '301 555 0005', 'Calle 51 # 29-40, Bucaramanga'),
(6, 6, 'Diana',         'Morales Ruiz',   '1098000006', '302 666 0006', 'Calle 45 # 23-18, Bucaramanga'),
(7, 7, 'Felipe',        'Ortega Salas',   '1098000007', '303 777 0007', 'Cra 33 # 50-12, Bucaramanga'),
(8, 8, 'Valentina',     'Castro Lima',    '1098000008', '304 888 0008', 'Calle 46 # 35-22, Bucaramanga'),
(9, 9, 'Andrés',        'Vargas Uribe',   '1098000009', '305 999 0009', 'Cra 30 # 48-10, Bucaramanga'),
(10, 10, 'Paula',       'Suárez Gallego', '1098000010', '306 000 0010', 'Calle 53 # 28-15, Bucaramanga');

-- ------------------------------------------------ FAVORITOS (N:M, 4 + 6 = 10)
INSERT INTO favorito (id_usuario, id_propiedad) VALUES
(6, 2), (6, 7), (7, 3), (7, 10), (8, 1), (8, 8);

-- ------------------------------------------------ SOLICITUDES (3 + 7 = 10)
INSERT INTO solicitud (id_solicitud, id_usuario_cliente, id_propiedad, tipo_solicitud, estado, observaciones) VALUES
(4, 6, 2,  'COMPRA',   'RADICADA',  'Solicitud de compra del cliente 2'),
(5, 6, 6,  'ARRIENDO', 'RADICADA',  'Arriendo con mascota permitido'),
(6, 7, 10, 'COMPRA',   'APROBADA',  'Crédito preaprobado de Bancolombia'),
(7, 7, 12, 'ARRIENDO', 'RADICADA',  'Contrato por 12 meses'),
(8, 8, 3,  'COMPRA',   'RADICADA',  'En estudio de referencias'),
(9, 9, 7,  'ARRIENDO', 'RECHAZADA', 'Documentación incompleta'),
(10, 10, 9, 'COMPRA',  'RADICADA',  'Pendiente de visita del perito');

-- ------------------------------------------------ DOCUMENTO_SOLICITUD (4 + 6 = 10)
INSERT INTO documento_solicitud (id_documento, id_solicitud, nombre_archivo, ruta, tipo_documento) VALUES
(5, 4, 'cedula_cliente2.pdf',        'uploads/solicitud_4/cedula.pdf',        'Cédula'),
(6, 5, 'certificado_ingresos.pdf',   'uploads/solicitud_5/certificado.pdf',   'Certificado de ingresos'),
(7, 6, 'certificado_tradicion.pdf',  'uploads/solicitud_6/tradicion.pdf',     'Certificado de tradición'),
(8, 7, 'referencias_arraigo.pdf',    'uploads/solicitud_7/referencias.pdf',   'Referencias'),
(9, 8, 'desprendible_pago.pdf',      'uploads/solicitud_8/desprendible.pdf',  'Desprendible de pago'),
(10, 9, 'certificado_laboral.pdf',   'uploads/solicitud_9/laboral.pdf',       'Certificado laboral');