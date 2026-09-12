package com.inmobiliaria.dao;

import com.inmobiliaria.config.ConexionBD;
import com.inmobiliaria.modelo.ImagenPropiedad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImagenDAO {

    public List<ImagenPropiedad> listarPorPropiedad(int idPropiedad) throws SQLException {
        List<ImagenPropiedad> lista = new ArrayList<>();
        String sql = "SELECT id_imagen, id_propiedad, ruta, titulo, es_principal "
                   + "FROM imagen_propiedad WHERE id_propiedad = ? ORDER BY es_principal DESC, id_imagen";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPropiedad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ImagenPropiedad img = new ImagenPropiedad();
                    img.setIdImagen(rs.getInt("id_imagen"));
                    img.setIdPropiedad(rs.getInt("id_propiedad"));
                    img.setRuta(rs.getString("ruta"));
                    img.setTitulo(rs.getString("titulo"));
                    img.setPrincipal(rs.getInt("es_principal") == 1);
                    lista.add(img);
                }
            }
        }
        return lista;
    }

    public boolean insertar(int idPropiedad, String ruta, String titulo, boolean principal) throws SQLException {
        String sql = "INSERT INTO imagen_propiedad (id_propiedad, ruta, titulo, es_principal) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPropiedad);
            ps.setString(2, ruta);
            ps.setString(3, titulo);
            ps.setInt(4, principal ? 1 : 0);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idImagen) throws SQLException {
        String sql = "DELETE FROM imagen_propiedad WHERE id_imagen = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idImagen);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarDePropiedad(int idPropiedad) throws SQLException {
        String sql = "DELETE FROM imagen_propiedad WHERE id_propiedad = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPropiedad);
            return ps.executeUpdate() > 0;
        }
    }
}