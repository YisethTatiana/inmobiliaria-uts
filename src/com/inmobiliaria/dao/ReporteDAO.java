package com.inmobiliaria.dao;

import com.inmobiliaria.config.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReporteDAO {

    private List<Map<String, Object>> aMapas(ResultSet rs) throws SQLException {
        List<Map<String, Object>> filas = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        int columnas = meta.getColumnCount();
        while (rs.next()) {
            Map<String, Object> fila = new LinkedHashMap<>();
            for (int i = 1; i <= columnas; i++) {
                fila.put(meta.getColumnLabel(i), rs.getObject(i));
            }
            filas.add(fila);
        }
        return filas;
    }

    // 1) INNER JOIN de 4 tablas: propiedades con ciudad, tipo e inmobiliaria
    public List<Map<String, Object>> propiedadesCompletas() throws SQLException {
        String sql = "SELECT p.id_propiedad AS ID, p.titulo AS Titulo, tp.nombre AS Tipo, "
                   + "ci.nombre AS Ciudad, i.nombre AS Inmobiliaria, p.precio AS Precio, p.estado AS Estado "
                   + "FROM propiedad p "
                   + "INNER JOIN tipo_propiedad tp ON p.id_tipo = tp.id_tipo "
                   + "INNER JOIN ciudad ci ON p.id_ciudad = ci.id_ciudad "
                   + "INNER JOIN inmobiliaria i ON p.id_inmobiliaria = i.id_inmobiliaria "
                   + "ORDER BY p.id_propiedad";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return aMapas(rs);
        }
    }

    // 2) INNER JOIN de 4 tablas: citas con cliente, propiedad y ciudad
    public List<Map<String, Object>> citasConDetalles() throws SQLException {
        String sql = "SELECT c.id_cita AS ID_Cita, "
                   + "u.correo AS Cliente, "
                   + "p.titulo AS Propiedad, "
                   + "ci.nombre AS Ciudad, "
                   + "c.fecha_hora AS Fecha, "
                   + "c.estado AS Estado "
                   + "FROM cita c "
                   + "INNER JOIN usuario u ON c.id_usuario_cliente = u.id_usuario "
                   + "INNER JOIN propiedad p ON c.id_propiedad = p.id_propiedad "
                   + "INNER JOIN ciudad ci ON p.id_ciudad = ci.id_ciudad "
                   + "ORDER BY c.fecha_hora DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return aMapas(rs);
        }
    }

    // 3) Relación N:M: características de una propiedad
    public List<Map<String, Object>> caracteristicasDePropiedad(int idPropiedad) throws SQLException {
        String sql = "SELECT p.id_propiedad AS ID_Propiedad, p.titulo AS Propiedad, "
                   + "c.nombre AS Caracteristica, pc.cantidad AS Cantidad "
                   + "FROM propiedad p "
                   + "INNER JOIN propiedad_caracteristica pc ON p.id_propiedad = pc.id_propiedad "
                   + "INNER JOIN caracteristica c ON pc.id_caracteristica = c.id_caracteristica "
                   + "WHERE p.id_propiedad = ? ORDER BY c.nombre";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPropiedad);
            try (ResultSet rs = ps.executeQuery()) {
                return aMapas(rs);
            }
        }
    }

    // 4) LEFT JOIN: usuarios con su perfil y cantidad de citas
    public List<Map<String, Object>> usuariosConPerfilYCitas() throws SQLException {
        String sql = "SELECT u.id_usuario AS ID, "
                   + "u.correo AS Correo, "
                   + "IFNULL(TRIM(CONCAT(COALESCE(p.nombres, ''), ' ', COALESCE(p.apellidos, ''))), 'Sin perfil') AS Nombre, "
                   + "COUNT(c.id_cita) AS Cantidad_Citas "
                   + "FROM usuario u "
                   + "LEFT JOIN perfil p ON u.id_usuario = p.id_usuario "
                   + "LEFT JOIN cita c ON u.id_usuario = c.id_usuario_cliente "
                   + "GROUP BY u.id_usuario, u.correo, p.nombres, p.apellidos "
                   + "ORDER BY Cantidad_Citas DESC, u.id_usuario";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return aMapas(rs);
        }
    }

    // 5) GROUP BY y HAVING: propiedades por ciudad y estado
    public List<Map<String, Object>> propiedadesPorCiudadYEstado() throws SQLException {
        String sql = "SELECT ci.nombre AS Ciudad, p.estado AS Estado, COUNT(*) AS Total "
                   + "FROM propiedad p "
                   + "INNER JOIN ciudad ci ON p.id_ciudad = ci.id_ciudad "
                   + "GROUP BY ci.nombre, p.estado "
                   + "HAVING COUNT(*) >= 1 "
                   + "ORDER BY Total DESC, Ciudad";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return aMapas(rs);
        }
    }

    // 5b) GROUP BY y HAVING: propiedades más solicitadas
    public List<Map<String, Object>> propiedadesMasSolicitadas() throws SQLException {
        String sql = "SELECT p.id_propiedad AS ID, "
                   + "p.titulo AS Propiedad, "
                   + "p.direccion AS Ubicacion, "
                   + "COUNT(c.id_cita) AS Numero_Citas "
                   + "FROM propiedad p "
                   + "INNER JOIN cita c ON p.id_propiedad = c.id_propiedad "
                   + "GROUP BY p.id_propiedad, p.titulo, p.direccion "
                   + "HAVING COUNT(c.id_cita) > 0 "
                   + "ORDER BY Numero_Citas DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return aMapas(rs);
        }
    }

    // 5c) GROUP BY: citas por estado (reporte requerido)
    public List<Map<String, Object>> citasPorEstado() throws SQLException {
        String sql = "SELECT c.estado AS Estado, COUNT(*) AS Total "
                   + "FROM cita c "
                   + "GROUP BY c.estado "
                   + "ORDER BY Total DESC, Estado";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return aMapas(rs);
        }
    }

    // Reporte extra: solicitudes por inmobiliaria (requerido en los reportes)
    public List<Map<String, Object>> solicitudesPorInmobiliaria() throws SQLException {
        String sql = "SELECT i.nombre AS Inmobiliaria, s.tipo_solicitud AS Tipo, s.estado AS Estado, "
                   + "COUNT(*) AS Total "
                   + "FROM solicitud s "
                   + "INNER JOIN propiedad p ON s.id_propiedad = p.id_propiedad "
                   + "INNER JOIN inmobiliaria i ON p.id_inmobiliaria = i.id_inmobiliaria "
                   + "GROUP BY i.nombre, s.tipo_solicitud, s.estado "
                   + "ORDER BY Inmobiliaria, Total DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return aMapas(rs);
        }
    }

    // Reporte de ventas y arriendos por operación del inmueble
    public List<Map<String, Object>> ventasArriendos(Integer idInmobiliaria) throws SQLException {
        String sql = "SELECT CASE p.operacion WHEN 'VENTA' THEN 'Venta' ELSE 'Arriendo' END AS Operacion, "
                   + "COUNT(*) AS Propiedades, "
                   + "SUM(CASE WHEN p.estado = 'DISPONIBLE' THEN 1 ELSE 0 END) AS Disponibles, "
                   + "SUM(CASE WHEN p.estado = 'VENDIDO' THEN 1 ELSE 0 END) AS Vendidas, "
                   + "SUM(CASE WHEN p.estado = 'ARRENDADO' THEN 1 ELSE 0 END) AS Arrendadas, "
                   + "COALESCE(SUM(p.precio), 0) AS Valor_Total "
                   + "FROM propiedad p ";
        if (idInmobiliaria != null && idInmobiliaria > 0) {
            sql += "WHERE p.id_inmobiliaria = ? ";
        }
        sql += "GROUP BY p.operacion ORDER BY Operacion";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (idInmobiliaria != null && idInmobiliaria > 0) {
                ps.setInt(1, idInmobiliaria);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return aMapas(rs);
            }
        }
    }
}