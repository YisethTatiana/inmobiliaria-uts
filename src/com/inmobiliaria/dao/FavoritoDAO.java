package com.inmobiliaria.dao;

import com.inmobiliaria.config.ConexionBD;
import com.inmobiliaria.modelo.Propiedad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoritoDAO {

    public boolean esFavorito(int idUsuario, int idPropiedad) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM favorito WHERE id_usuario = ? AND id_propiedad = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idPropiedad);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        }
        return false;
    }

    public boolean agregarFavorito(int idUsuario, int idPropiedad) throws SQLException {
        String sql = "INSERT INTO favorito (id_usuario, id_propiedad) VALUES (?, ?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idPropiedad);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarFavorito(int idUsuario, int idPropiedad) throws SQLException {
        String sql = "DELETE FROM favorito WHERE id_usuario = ? AND id_propiedad = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idPropiedad);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean toggleFavorito(int idUsuario, int idPropiedad) throws SQLException {
        if (esFavorito(idUsuario, idPropiedad)) {
            eliminarFavorito(idUsuario, idPropiedad);
            return false;
        } else {
            agregarFavorito(idUsuario, idPropiedad);
            return true;
        }
    }

    public List<Integer> idsFavoritos(int idUsuario) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_propiedad FROM favorito WHERE id_usuario = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id_propiedad"));
                }
            }
        }
        return ids;
    }

    public List<Propiedad> listarFavoritos(int idUsuario) throws SQLException {
        List<Propiedad> lista = new ArrayList<>();
        String sql = "SELECT p.id_propiedad, p.titulo, p.descripcion, p.precio, p.direccion, p.estado "
                   + "FROM favorito f "
                   + "INNER JOIN propiedad p ON f.id_propiedad = p.id_propiedad "
                   + "WHERE f.id_usuario = ? "
                   + "ORDER BY f.fecha_agregado DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Propiedad p = new Propiedad();
                    p.setIdPropiedad(rs.getInt("id_propiedad"));
                    p.setTitulo(rs.getString("titulo"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setPrecio(rs.getBigDecimal("precio"));
                    p.setDireccion(rs.getString("direccion"));
                    p.setEstado(rs.getString("estado"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }
}