package com.inmobiliaria.dao;

import com.inmobiliaria.config.ConexionBD;
import com.inmobiliaria.modelo.Caracteristica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CaracteristicaDAO {

    public List<Caracteristica> listarTodas() throws SQLException {
        List<Caracteristica> lista = new ArrayList<>();
        String sql = "SELECT id_caracteristica, nombre FROM caracteristica ORDER BY nombre";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Caracteristica c = new Caracteristica();
                c.setIdCaracteristica(rs.getInt("id_caracteristica"));
                c.setNombre(rs.getString("nombre"));
                lista.add(c);
            }
        }
        return lista;
    }

    public List<Caracteristica> listarDePropiedad(int idPropiedad) throws SQLException {
        List<Caracteristica> lista = new ArrayList<>();
        String sql = "SELECT c.id_caracteristica, c.nombre, pc.cantidad "
                   + "FROM caracteristica c "
                   + "INNER JOIN propiedad_caracteristica pc ON c.id_caracteristica = pc.id_caracteristica "
                   + "WHERE pc.id_propiedad = ? ORDER BY c.nombre";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPropiedad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Caracteristica c = new Caracteristica();
                    c.setIdCaracteristica(rs.getInt("id_caracteristica"));
                    c.setNombre(rs.getString("nombre"));
                    c.setCantidad(rs.getInt("cantidad"));
                    lista.add(c);
                }
            }
        }
        return lista;
    }

    public boolean insertar(String nombre) throws SQLException {
        String sql = "INSERT INTO caracteristica (nombre) VALUES (?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(int id, String nombre) throws SQLException {
        String sql = "UPDATE caracteristica SET nombre = ? WHERE id_caracteristica = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM caracteristica WHERE id_caracteristica = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean asignarPropiedad(int idPropiedad, int idCaracteristica, int cantidad) throws SQLException {
        String sql = "INSERT INTO propiedad_caracteristica (id_propiedad, id_caracteristica, cantidad) "
                   + "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE cantidad = VALUES(cantidad)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPropiedad);
            ps.setInt(2, idCaracteristica);
            ps.setInt(3, cantidad);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarDePropiedad(int idPropiedad) throws SQLException {
        String sql = "DELETE FROM propiedad_caracteristica WHERE id_propiedad = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPropiedad);
            return ps.executeUpdate() > 0;
        }
    }
}