package com.inmobiliaria.dao;

import com.inmobiliaria.config.ConexionBD;
import com.inmobiliaria.modelo.Cita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    public boolean agendarCita(int idUsuario, int idPropiedad, String fechaHora, String observaciones) throws SQLException {
        String sql = "INSERT INTO cita (id_usuario_cliente, id_propiedad, fecha_hora, observaciones, estado) "
                   + "VALUES (?, ?, ?, ?, 'PENDIENTE')";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idPropiedad);
            ps.setTimestamp(3, Timestamp.valueOf(fechaHora.replace("T", " ") + ":00"));
            ps.setString(4, observaciones);

            return ps.executeUpdate() > 0;
        }
    }

    public List<Cita> listarPorUsuario(int idUsuario) throws SQLException {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT c.id_cita, c.id_usuario_cliente, c.id_propiedad, c.fecha_hora, c.estado, "
                   + "c.observaciones, p.titulo AS titulo_propiedad "
                   + "FROM cita c INNER JOIN propiedad p ON c.id_propiedad = p.id_propiedad "
                   + "WHERE c.id_usuario_cliente = ? ORDER BY c.fecha_hora DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCitaDetalle(rs));
                }
            }
        }
        return lista;
    }

    public List<Cita> listarTodas() throws SQLException {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT id_cita, id_usuario_cliente, id_propiedad, fecha_hora, estado, observaciones "
                   + "FROM cita ORDER BY fecha_hora DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cita c = new Cita();
                c.setIdCita(rs.getInt("id_cita"));
                c.setIdUsuario(rs.getInt("id_usuario_cliente"));
                c.setIdPropiedad(rs.getInt("id_propiedad"));
                c.setFechaCita(rs.getTimestamp("fecha_hora"));
                c.setEstado(rs.getString("estado"));
                c.setObservaciones(rs.getString("observaciones"));
                lista.add(c);
            }
        }
        return lista;
    }

    public List<Cita> listarTodasConDetalles() throws SQLException {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT c.id_cita, c.id_usuario_cliente, c.id_propiedad, c.fecha_hora, c.estado, "
                   + "c.observaciones, u.correo AS correo_cliente, p.titulo AS titulo_propiedad, "
                   + "ci.nombre AS nombre_ciudad "
                   + "FROM cita c "
                   + "INNER JOIN usuario u ON c.id_usuario_cliente = u.id_usuario "
                   + "INNER JOIN propiedad p ON c.id_propiedad = p.id_propiedad "
                   + "INNER JOIN ciudad ci ON p.id_ciudad = ci.id_ciudad "
                   + "ORDER BY c.fecha_hora DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCitaDetalle(rs));
            }
        }
        return lista;
    }

    public List<Cita> listarPorInmobiliariaConDetalles(int idInmobiliaria) throws SQLException {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT c.id_cita, c.id_usuario_cliente, c.id_propiedad, c.fecha_hora, c.estado, "
                   + "c.observaciones, u.correo AS correo_cliente, p.titulo AS titulo_propiedad, "
                   + "ci.nombre AS nombre_ciudad "
                   + "FROM cita c "
                   + "INNER JOIN usuario u ON c.id_usuario_cliente = u.id_usuario "
                   + "INNER JOIN propiedad p ON c.id_propiedad = p.id_propiedad "
                   + "INNER JOIN ciudad ci ON p.id_ciudad = ci.id_ciudad "
                   + "WHERE p.id_inmobiliaria = ? "
                   + "ORDER BY c.fecha_hora DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idInmobiliaria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCitaDetalle(rs));
                }
            }
        }
        return lista;
    }

    public boolean perteneceAInmobiliaria(int idCita, int idInmobiliaria) throws SQLException {
        String sql = "SELECT 1 FROM cita c "
                   + "INNER JOIN propiedad p ON c.id_propiedad = p.id_propiedad "
                   + "WHERE c.id_cita = ? AND p.id_inmobiliaria = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCita);
            ps.setInt(2, idInmobiliaria);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean perteneceAUsuario(int idCita, int idUsuario) throws SQLException {
        String sql = "SELECT 1 FROM cita WHERE id_cita = ? AND id_usuario_cliente = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCita);
            ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Cita mapearCitaDetalle(ResultSet rs) throws SQLException {
        Cita c = new Cita();
        c.setIdCita(rs.getInt("id_cita"));
        c.setIdUsuario(rs.getInt("id_usuario_cliente"));
        c.setIdPropiedad(rs.getInt("id_propiedad"));
        c.setFechaCita(rs.getTimestamp("fecha_hora"));
        c.setEstado(rs.getString("estado"));
        c.setObservaciones(rs.getString("observaciones"));
        try {
            c.setCorreoCliente(rs.getString("correo_cliente"));
            c.setTituloPropiedad(rs.getString("titulo_propiedad"));
            c.setNombreCiudad(rs.getString("nombre_ciudad"));
        } catch (SQLException ignore) {
            // columnas opcionales según la consulta
        }
        return c;
    }

    public boolean actualizarEstado(int idCita, String nuevoEstado) throws SQLException {
        String sql = "UPDATE cita SET estado = ? WHERE id_cita = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idCita);

            return ps.executeUpdate() > 0;
        }
    }
}