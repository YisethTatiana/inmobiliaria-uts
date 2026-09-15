-- LIMPIA LA BD DE EJECUCION Y DEJA SOLO LOS 3 USUARIOS BASE:
--   admin@inmobiliaria.com (id 1), agente1@inmobiliaria.com (id 2), cliente1@inmobiliaria.com (id 3)
-- Los demas clientes se crean desde el panel de administrador (AdminUsuarioServlet).
--
-- Las tablas dependientes (perfil, usuario_rol, usuario_inmobiliaria, cita,
-- solicitud+documento_solicitud, favorito) usan ON DELETE CASCADE; auditoria usa
-- ON DELETE SET NULL sobre id_usuario, por lo que no se viola ninguna FK.
--
-- NOTA: Este script NO reemplaza a script_dml.sql (que conserva los >=10 registros
-- por tabla principal que exige el enunciado como evidencia). Si se vuelve a cargar
-- script_dml.sql, los usuarios demo reapareceran y habra que ejecutar este script
-- de nuevo antes de la sustentacion.

DELETE FROM usuario WHERE id_usuario NOT IN (1, 2, 3);