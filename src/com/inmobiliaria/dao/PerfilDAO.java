package com.inmobiliaria.dao;

import com.inmobiliaria.config.ConexionBD;
import com.inmobiliaria.modelo.Perfil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PerfilDAO {

    public Perfil buscarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT id_perfil, id_usuario, nombres, apellidos, documento, telefono, direccion, foto "
                   + "FROM perfil WHERE id_usuario = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public boolean guardar(Perfil p) throws SQLException {
        String sql = "INSERT INTO perfil (id_usuario, nombres, apellidos, documento, telefono, direccion, foto) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE nombres = VALUES(nombres), apellidos = VALUES(apellidos), "
                   + "documento = VALUES(documento), telefono = VALUES(telefono), "
                   + "direccion = VALUES(direccion), foto = VALUES(foto)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getIdUsuario());
            ps.setString(2, p.getNombres());
            ps.setString(3, p.getApellidos());
            ps.setString(4, p.getDocumento());
            ps.setString(5, p.getTelefono());
            ps.setString(6, p.getDireccion());
            ps.setString(7, p.getFoto());

            return ps.executeUpdate() > 0;
        }
    }

    private Perfil mapear(ResultSet rs) throws SQLException {
        Perfil p = new Perfil();
        p.setIdPerfil(rs.getInt("id_perfil"));
        p.setIdUsuario(rs.getInt("id_usuario"));
        p.setNombres(rs.getString("nombres"));
        p.setApellidos(rs.getString("apellidos"));
        p.setDocumento(rs.getString("documento"));
        p.setTelefono(rs.getString("telefono"));
        p.setDireccion(rs.getString("direccion"));
        p.setFoto(rs.getString("foto"));
        return p;
    }
}