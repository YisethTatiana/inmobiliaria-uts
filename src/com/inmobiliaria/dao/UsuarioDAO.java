package com.inmobiliaria.dao;

import com.inmobiliaria.config.ConexionBD;
import com.inmobiliaria.modelo.Usuario;
import com.inmobiliaria.util.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UsuarioDAO {

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
                   + "r.nombre AS rol "
                   + "FROM usuario u "
                   + "LEFT JOIN perfil p ON u.id_usuario = p.id_usuario "
                   + "INNER JOIN usuario_rol ur ON u.id_usuario = ur.id_usuario "
                   + "INNER JOIN rol r ON ur.id_rol = r.id_rol "
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
                u.setRol(rs.getString("rol"));
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

    public boolean registrarUsuario(String correo, String passwordPlana, int idRol) throws SQLException {
        String sqlUsuario = "INSERT INTO usuario (correo, password_hash, activo) VALUES (?, ?, true)";
        String sqlRol = "INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (?, ?)";

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
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public Usuario autenticar(String correo, String passwordPlana) throws SQLException {
        String sql = "SELECT id_usuario, correo, password_hash, activo FROM usuario WHERE correo = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String[] parts = storedHash.split(":");

                    if (parts.length == 2) {
                        String hashBase = parts[0];
                        String salt = parts[1];
                        String inputHash = PasswordUtils.hashPassword(passwordPlana, salt);

                        if (hashBase.equals(inputHash)) {
                            Usuario usuario = new Usuario();
                            usuario.setIdUsuario(rs.getInt("id_usuario"));
                            usuario.setCorreo(rs.getString("correo"));
                            usuario.setActivo(rs.getBoolean("activo"));

                            List<String> roles = new ArrayList<>();
                            String sqlRoles = "SELECT r.nombre FROM usuario_rol ur "
                                            + "INNER JOIN rol r ON ur.id_rol = r.id_rol "
                                            + "WHERE ur.id_usuario = ? ORDER BY r.id_rol";

                            try (PreparedStatement psRoles = con.prepareStatement(sqlRoles)) {
                                psRoles.setInt(1, usuario.getIdUsuario());
                                try (ResultSet rsRoles = psRoles.executeQuery()) {
                                    while (rsRoles.next()) {
                                        roles.add(rsRoles.getString("nombre"));
                                    }
                                }
                            }
                            usuario.setRoles(roles);
                            if (!roles.isEmpty()) {
                                usuario.setRol(roles.get(0));
                            }
                            return usuario;
                        }
                    }
                }
            }
        }
        return null;
    }
}