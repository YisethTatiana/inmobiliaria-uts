package com.inmobiliaria.dao;

import com.inmobiliaria.config.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CatalogoDAO {

    public List<Map<String, Object>> listarTipos() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT id_tipo, nombre FROM tipo_propiedad ORDER BY id_tipo";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> f = new LinkedHashMap<>();
                f.put("id", rs.getInt("id_tipo"));
                f.put("nombre", rs.getString("nombre"));
                lista.add(f);
            }
        }
        return lista;
    }

    public boolean insertarTipo(String nombre) throws SQLException {
        String sql = "INSERT INTO tipo_propiedad (nombre) VALUES (?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarTipo(int id, String nombre) throws SQLException {
        String sql = "UPDATE tipo_propiedad SET nombre = ? WHERE id_tipo = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarTipo(int id) throws SQLException {
        String sql = "DELETE FROM tipo_propiedad WHERE id_tipo = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Map<String, Object>> listarCiudades() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT id_ciudad, nombre FROM ciudad ORDER BY id_ciudad";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> f = new LinkedHashMap<>();
                f.put("id", rs.getInt("id_ciudad"));
                f.put("nombre", rs.getString("nombre"));
                lista.add(f);
            }
        }
        return lista;
    }

    public boolean insertarCiudad(String nombre) throws SQLException {
        String sql = "INSERT INTO ciudad (nombre) VALUES (?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarCiudad(int id, String nombre) throws SQLException {
        String sql = "UPDATE ciudad SET nombre = ? WHERE id_ciudad = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarCiudad(int id) throws SQLException {
        String sql = "DELETE FROM ciudad WHERE id_ciudad = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Map<String, Object>> listarCaracteristicas() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT id_caracteristica, nombre FROM caracteristica ORDER BY id_caracteristica";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> f = new LinkedHashMap<>();
                f.put("id", rs.getInt("id_caracteristica"));
                f.put("nombre", rs.getString("nombre"));
                lista.add(f);
            }
        }
        return lista;
    }

    public List<Map<String, Object>> listarInmobiliarias() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT id_inmobiliaria, nombre FROM inmobiliaria ORDER BY id_inmobiliaria";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> f = new LinkedHashMap<>();
                f.put("id", rs.getInt("id_inmobiliaria"));
                f.put("nombre", rs.getString("nombre"));
                lista.add(f);
            }
        }
        return lista;
    }
}