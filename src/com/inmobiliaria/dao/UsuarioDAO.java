package com.inmobiliaria.dao;

import com.inmobiliaria.config.ConexionBD;
import com.inmobiliaria.modelo.ResultadoLogin;
import com.inmobiliaria.modelo.Usuario;
import com.inmobiliaria.util.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UsuarioDAO {

    public static final int MAX_INTENTOS = 5;
    public static final int MINUTOS_BLOQUEO = 15;

    public Map<Integer, String> listarRoles() throws SQLException {
        Map<Integer, String> roles = new LinkedHashMap<>();
        String sql = "SELECT id_rol, nombre FROM rol ORDER BY id_rol";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.put(rs.getInt("id_rol"), rs.getString("nombre"));
            }
        }
        return roles;
    }

    public boolean cambiarEstado(int idUsuario, boolean activo) throws SQLException {
        String sql = "UPDATE usuario SET activo = ? WHERE id_usuario = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean asignarRol(int idUsuario, int idRol) throws SQLException {
        String delete = "DELETE FROM usuario_rol WHERE id_usuario = ?";
        String insert = "INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (?, ?)";
        String limpiarInmobiliaria = "DELETE FROM usuario_inmobiliaria WHERE id_usuario = ?";

        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(delete)) {
                    ps.setInt(1, idUsuario);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(insert)) {
                    ps.setInt(1, idUsuario);
                    ps.setInt(2, idRol);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(limpiarInmobiliaria)) {
                    ps.setInt(1, idUsuario);
                    ps.executeUpdate();
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public boolean anadirRol(int idUsuario, int idRol) throws SQLException {
        String sql = "INSERT IGNORE INTO usuario_rol (id_usuario, id_rol) VALUES (?, ?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idRol);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean quitarRol(int idUsuario, int idRol) throws SQLException {
        String sql = "DELETE FROM usuario_rol WHERE id_usuario = ? AND id_rol = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idRol);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Integer> rolesDe(int idUsuario) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_rol FROM usuario_rol WHERE id_usuario = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id_rol"));
                }
            }
        }
        return ids;
    }

    public int obtenerInmobiliaria(int idUsuario) throws SQLException {
        String sql = "SELECT id_inmobiliaria FROM usuario_inmobiliaria WHERE id_usuario = ? LIMIT 1";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_inmobiliaria");
                }
            }
        }
        return -1;
    }

    public boolean asignarInmobiliaria(int idUsuario, int idInmobiliaria) throws SQLException {
        String sql = "INSERT INTO usuario_inmobiliaria (id_usuario, id_inmobiliaria) VALUES (?, ?) "
                   + "ON DUPLICATE KEY UPDATE id_inmobiliaria = VALUES(id_inmobiliaria)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idInmobiliaria);

            return ps.executeUpdate() > 0;
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.correo, u.activo, u.fecha_creacion, "
                   + "TRIM(CONCAT(COALESCE(p.nombres, ''), ' ', COALESCE(p.apellidos, ''))) AS nombre, "
                   + "COALESCE(GROUP_CONCAT(r.nombre ORDER BY r.id_rol SEPARATOR ','), '') AS roles_nombres "
                   + "FROM usuario u "
                   + "LEFT JOIN perfil p ON u.id_usuario = p.id_usuario "
                   + "LEFT JOIN usuario_rol ur ON u.id_usuario = ur.id_usuario "
                   + "LEFT JOIN rol r ON ur.id_rol = r.id_rol "
                   + "GROUP BY u.id_usuario, u.correo, u.activo, u.fecha_creacion, nombre "
                   + "ORDER BY u.id_usuario";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setCorreo(rs.getString("correo"));
                u.setActivo(rs.getBoolean("activo"));
                u.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                u.setNombre(rs.getString("nombre"));

                String rolesStr = rs.getString("roles_nombres");
                List<String> nombresRoles = new ArrayList<>();
                if (rolesStr != null && !rolesStr.trim().isEmpty()) {
                    for (String r : rolesStr.split(",")) {
                        if (!r.trim().isEmpty()) {
                            nombresRoles.add(r.trim());
                        }
                    }
                }
                u.setRoles(nombresRoles);
                if (!nombresRoles.isEmpty()) {
                    u.setRol(nombresRoles.get(0));
                }
                lista.add(u);
            }
        }
        return lista;
    }

    public boolean eliminar(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean registrarUsuario(String correo, String passwordPlana, int idRol,
                                    String nombres, String apellidos, String documento, String telefono) throws SQLException {
        String sqlUsuario = "INSERT INTO usuario (correo, password_hash, activo) VALUES (?, ?, true)";
        String sqlRol = "INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (?, ?)";
        String sqlPerfil = "INSERT INTO perfil (id_usuario, nombres, apellidos, documento, telefono) "
                         + "VALUES (?, ?, ?, ?, ?)";

        String salt = PasswordUtils.generarSalt();
        String passwordHash = PasswordUtils.hashPassword(passwordPlana, salt) + ":" + salt;

        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);

            try (PreparedStatement psUser = con.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, correo);
                psUser.setString(2, passwordHash);
                psUser.executeUpdate();

                ResultSet rs = psUser.getGeneratedKeys();
                if (rs.next()) {
                    int idUsuario = rs.getInt(1);

                    try (PreparedStatement psRol = con.prepareStatement(sqlRol)) {
                        psRol.setInt(1, idUsuario);
                        psRol.setInt(2, idRol);
                        psRol.executeUpdate();
                    }
                    try (PreparedStatement psPerfil = con.prepareStatement(sqlPerfil)) {
                        psPerfil.setInt(1, idUsuario);
                        psPerfil.setString(2, nombres);
                        psPerfil.setString(3, apellidos);
                        psPerfil.setString(4, documento);
                        psPerfil.setString(5, telefono);
                        psPerfil.executeUpdate();
                    }
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public ResultadoLogin autenticarConBloqueo(String correo, String passwordPlana) throws SQLException {
        String sql = "SELECT id_usuario, correo, password_hash, activo, "
                   + "(bloqueado_hasta IS NOT NULL AND NOW() < bloqueado_hasta) AS bloqueado, "
                   + "TIMESTAMPDIFF(MINUTE, NOW(), bloqueado_hasta) AS minutos_restantes "
                   + "FROM usuario WHERE correo = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return new ResultadoLogin(ResultadoLogin.CREDENCIALES);
                }

                int idUsuario = rs.getInt("id_usuario");

                if (rs.getBoolean("bloqueado")) {
                    ResultadoLogin bloqueado = new ResultadoLogin(ResultadoLogin.BLOQUEADO);
                    bloqueado.setMinutosRestantes(Math.max(1, rs.getInt("minutos_restantes")));
                    return bloqueado;
                }

                String storedHash = rs.getString("password_hash");
                String[] parts = storedHash.split(":");
                if (parts.length != 2) {
                    return new ResultadoLogin(ResultadoLogin.CREDENCIALES);
                }

                String inputHash = PasswordUtils.hashPassword(passwordPlana, parts[1]);
                if (!parts[0].equals(inputHash)) {
                    registrarIntentoFallido(con, idUsuario);
                    return new ResultadoLogin(ResultadoLogin.CREDENCIALES);
                }

                limpiarIntentos(con, idUsuario);

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(idUsuario);
                usuario.setCorreo(rs.getString("correo"));
                usuario.setActivo(rs.getBoolean("activo"));
                cargarRoles(con, usuario);
                return new ResultadoLogin(ResultadoLogin.OK, usuario);
            }
        }
    }

    private void registrarIntentoFallido(Connection con, int idUsuario) throws SQLException {
        String inc = "UPDATE usuario SET intentos_fallidos = intentos_fallidos + 1 WHERE id_usuario = ?";
        try (PreparedStatement ps = con.prepareStatement(inc)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }

        boolean alcanzoLimite = false;
        String q = "SELECT intentos_fallidos FROM usuario WHERE id_usuario = ?";
        try (PreparedStatement ps = con.prepareStatement(q)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) >= MAX_INTENTOS) {
                    alcanzoLimite = true;
                }
            }
        }

        if (alcanzoLimite) {
            String lock = "UPDATE usuario SET bloqueado_hasta = DATE_ADD(NOW(), INTERVAL ? MINUTE), "
                        + "intentos_fallidos = 0 WHERE id_usuario = ?";
            try (PreparedStatement ps = con.prepareStatement(lock)) {
                ps.setInt(1, MINUTOS_BLOQUEO);
                ps.setInt(2, idUsuario);
                ps.executeUpdate();
            }
        }
    }

    private void limpiarIntentos(Connection con, int idUsuario) throws SQLException {
        String sql = "UPDATE usuario SET intentos_fallidos = 0, bloqueado_hasta = NULL WHERE id_usuario = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    private void cargarRoles(Connection con, Usuario usuario) throws SQLException {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT r.nombre FROM usuario_rol ur "
                   + "INNER JOIN rol r ON ur.id_rol = r.id_rol "
                   + "WHERE ur.id_usuario = ? ORDER BY r.id_rol";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuario.getIdUsuario());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("nombre"));
                }
            }
        }
        usuario.setRoles(roles);
        if (!roles.isEmpty()) {
            usuario.setRol(roles.get(0));
        }
    }

    public int buscarIdPorCorreo(String correo) throws SQLException {
        String sql = "SELECT id_usuario FROM usuario WHERE correo = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean establecerToken(int idUsuario, String tokenHash, Timestamp expiracion) throws SQLException {
        String sql = "UPDATE usuario SET token_recuperacion = ?, token_expiracion = ? WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tokenHash);
            ps.setTimestamp(2, expiracion);
            ps.setInt(3, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    public int idUsuarioPorTokenValido(String tokenHash) throws SQLException {
        String sql = "SELECT id_usuario FROM usuario WHERE token_recuperacion = ? AND token_expiracion > NOW()";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tokenHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean cambiarPassword(int idUsuario, String nuevoHash) throws SQLException {
        String sql = "UPDATE usuario SET password_hash = ?, token_recuperacion = NULL, "
                   + "token_expiracion = NULL, intentos_fallidos = 0, bloqueado_hasta = NULL "
                   + "WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoHash);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }
}