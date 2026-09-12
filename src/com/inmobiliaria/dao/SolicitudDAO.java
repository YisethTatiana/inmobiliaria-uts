package com.inmobiliaria.dao;

import com.inmobiliaria.config.ConexionBD;
import com.inmobiliaria.modelo.DocumentoSolicitud;
import com.inmobiliaria.modelo.Solicitud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SolicitudDAO {

    public boolean crear(int idUsuario, int idPropiedad, String tipoSolicitud, String observaciones) throws SQLException {
        String sql = "INSERT INTO solicitud (id_usuario_cliente, id_propiedad, tipo_solicitud, estado, observaciones) "
                   + "VALUES (?, ?, ?, 'RADICADA', ?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idPropiedad);
            ps.setString(3, tipoSolicitud);
            ps.setString(4, observaciones);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Solicitud> listarPorUsuario(int idUsuario) throws SQLException {
        List<Solicitud> lista = new ArrayList<>();
        String sql = "SELECT s.id_solicitud, s.id_usuario_cliente, s.id_propiedad, s.tipo_solicitud, "
                   + "s.estado, s.fecha_solicitud, s.observaciones, p.titulo AS titulo_propiedad, "
                   + "i.nombre AS nombre_inmobiliaria "
                   + "FROM solicitud s "
                   + "INNER JOIN propiedad p ON s.id_propiedad = p.id_propiedad "
                   + "INNER JOIN inmobiliaria i ON p.id_inmobiliaria = i.id_inmobiliaria "
                   + "WHERE s.id_usuario_cliente = ? ORDER BY s.fecha_solicitud DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public List<Solicitud> listarTodas() throws SQLException {
        List<Solicitud> lista = new ArrayList<>();
        String sql = "SELECT s.id_solicitud, s.id_usuario_cliente, s.id_propiedad, s.tipo_solicitud, "
                   + "s.estado, s.fecha_solicitud, s.observaciones, p.titulo AS titulo_propiedad, "
                   + "u.correo AS correo_cliente "
                   + "FROM solicitud s "
                   + "INNER JOIN propiedad p ON s.id_propiedad = p.id_propiedad "
                   + "INNER JOIN usuario u ON s.id_usuario_cliente = u.id_usuario "
                   + "ORDER BY s.fecha_solicitud DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Solicitud> listarPorInmobiliaria(int idInmobiliaria) throws SQLException {
        List<Solicitud> lista = new ArrayList<>();
        String sql = "SELECT s.id_solicitud, s.id_usuario_cliente, s.id_propiedad, s.tipo_solicitud, "
                   + "s.estado, s.fecha_solicitud, s.observaciones, p.titulo AS titulo_propiedad, "
                   + "u.correo AS correo_cliente "
                   + "FROM solicitud s "
                   + "INNER JOIN propiedad p ON s.id_propiedad = p.id_propiedad "
                   + "INNER JOIN usuario u ON s.id_usuario_cliente = u.id_usuario "
                   + "WHERE p.id_inmobiliaria = ? "
                   + "ORDER BY s.fecha_solicitud DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idInmobiliaria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public boolean perteneceAInmobiliaria(int idSolicitud, int idInmobiliaria) throws SQLException {
        String sql = "SELECT 1 FROM solicitud s "
                   + "INNER JOIN propiedad p ON s.id_propiedad = p.id_propiedad "
                   + "WHERE s.id_solicitud = ? AND p.id_inmobiliaria = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSolicitud);
            ps.setInt(2, idInmobiliaria);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean perteneceAUsuario(int idSolicitud, int idUsuario) throws SQLException {
        String sql = "SELECT 1 FROM solicitud WHERE id_solicitud = ? AND id_usuario_cliente = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSolicitud);
            ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean actualizarEstado(int idSolicitud, String nuevoEstado) throws SQLException {
        String sql = "UPDATE solicitud SET estado = ? WHERE id_solicitud = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idSolicitud);
            return ps.executeUpdate() > 0;
        }
    }

    public List<DocumentoSolicitud> listarDocumentos(int idSolicitud) throws SQLException {
        List<DocumentoSolicitud> lista = new ArrayList<>();
        String sql = "SELECT id_documento, id_solicitud, nombre_archivo, ruta, tipo_documento, estado, fecha_carga "
                   + "FROM documento_solicitud WHERE id_solicitud = ? ORDER BY fecha_carga DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSolicitud);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DocumentoSolicitud d = new DocumentoSolicitud();
                    d.setIdDocumento(rs.getInt("id_documento"));
                    d.setIdSolicitud(rs.getInt("id_solicitud"));
                    d.setNombreArchivo(rs.getString("nombre_archivo"));
                    d.setRuta(rs.getString("ruta"));
                    d.setTipoDocumento(rs.getString("tipo_documento"));
                    d.setEstado(rs.getString("estado"));
                    d.setFechaCarga(rs.getTimestamp("fecha_carga"));
                    lista.add(d);
                }
            }
        }
        return lista;
    }

    public boolean insertarDocumento(int idSolicitud, String nombreArchivo, String ruta, String tipoDocumento) throws SQLException {
        String sql = "INSERT INTO documento_solicitud (id_solicitud, nombre_archivo, ruta, tipo_documento) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSolicitud);
            ps.setString(2, nombreArchivo);
            ps.setString(3, ruta);
            ps.setString(4, tipoDocumento);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarEstadoDocumento(int idDocumento, String nuevoEstado) throws SQLException {
        String sql = "UPDATE documento_solicitud SET estado = ? WHERE id_documento = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idDocumento);
            return ps.executeUpdate() > 0;
        }
    }

    private Solicitud mapear(ResultSet rs) throws SQLException {
        Solicitud s = new Solicitud();
        s.setIdSolicitud(rs.getInt("id_solicitud"));
        s.setIdUsuarioCliente(rs.getInt("id_usuario_cliente"));
        s.setIdPropiedad(rs.getInt("id_propiedad"));
        s.setTipoSolicitud(rs.getString("tipo_solicitud"));
        s.setEstado(rs.getString("estado"));
        s.setFechaSolicitud(rs.getTimestamp("fecha_solicitud"));
        s.setObservaciones(rs.getString("observaciones"));
        s.setTituloPropiedad(rs.getString("titulo_propiedad"));
        try {
            s.setCorreoCliente(rs.getString("correo_cliente"));
        } catch (SQLException ignore) {
            // columna opcional cuando se lista por usuario
        }
        return s;
    }
}