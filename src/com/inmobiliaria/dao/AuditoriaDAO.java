package com.inmobiliaria.dao;

import com.inmobiliaria.config.ConexionBD;
import com.inmobiliaria.modelo.Auditoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaDAO {

    public void registrar(Integer idUsuario, String accion, String entidad, Integer idEntidad, String detalle, String ip) {
        String sql = "INSERT INTO auditoria (id_usuario, accion, entidad, id_entidad, detalle, ip) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setObject(1, idUsuario);
            ps.setString(2, accion);
            ps.setString(3, entidad);
            ps.setObject(4, idEntidad);
            ps.setString(5, detalle);
            ps.setString(6, ip);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Auditoria> listarTodas() throws SQLException {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "SELECT a.id_auditoria, a.id_usuario, a.accion, a.entidad, a.id_entidad, "
                   + "a.detalle, a.ip, a.fecha, u.correo AS correo_usuario "
                   + "FROM auditoria a "
                   + "LEFT JOIN usuario u ON a.id_usuario = u.id_usuario "
                   + "ORDER BY a.fecha DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Auditoria a = new Auditoria();
                a.setIdAuditoria(rs.getInt("id_auditoria"));
                a.setIdUsuario((Integer) rs.getObject("id_usuario"));
                a.setAccion(rs.getString("accion"));
                a.setEntidad(rs.getString("entidad"));
                a.setIdEntidad((Integer) rs.getObject("id_entidad"));
                a.setDetalle(rs.getString("detalle"));
                a.setIp(rs.getString("ip"));
                a.setFecha(rs.getTimestamp("fecha"));
                a.setCorreoUsuario(rs.getString("correo_usuario"));
                lista.add(a);
            }
        }
        return lista;
    }
}