package com.inmobiliaria.dao;

import com.inmobiliaria.config.ConexionBD;
import com.inmobiliaria.modelo.Caracteristica;
import com.inmobiliaria.modelo.ImagenPropiedad;
import com.inmobiliaria.modelo.Propiedad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PropiedadDAO {

    private static final String CAMPOS = "p.id_propiedad, p.matricula_inmobiliaria, p.titulo, p.descripcion, "
            + "p.precio, p.direccion, p.area, p.habitaciones, p.banios, p.parqueaderos, "
            + "p.estado, p.fecha_publicacion, p.id_ciudad, p.id_tipo, p.id_inmobiliaria, p.operacion, "
            + "ci.nombre AS nombre_ciudad, tp.nombre AS nombre_tipo, i.nombre AS nombre_inmobiliaria, "
            + "i.telefono AS telefono_inmobiliaria, i.correo_contacto AS email_inmobiliaria ";

    private static final String DESDE = "FROM propiedad p "
            + "INNER JOIN ciudad ci ON p.id_ciudad = ci.id_ciudad "
            + "INNER JOIN tipo_propiedad tp ON p.id_tipo = tp.id_tipo "
            + "INNER JOIN inmobiliaria i ON p.id_inmobiliaria = i.id_inmobiliaria ";

    public List<Propiedad> listarTodas(boolean soloDisponibles) throws SQLException {
        List<Propiedad> lista = new ArrayList<>();
        String sql = "SELECT " + CAMPOS + DESDE;
        if (soloDisponibles) {
            sql += "WHERE p.estado = 'DISPONIBLE' ";
        }
        sql += "ORDER BY p.id_propiedad DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Propiedad p = mapear(rs);
                p.setImagenes(cargarImagenes(con, p.getIdPropiedad()));
                p.setCaracteristicas(cargarCaracteristicas(con, p.getIdPropiedad()));
                lista.add(p);
            }
        }
        return lista;
    }

    public Propiedad buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + CAMPOS + DESDE + "WHERE p.id_propiedad = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Propiedad p = mapear(rs);
                    p.setImagenes(cargarImagenes(con, p.getIdPropiedad()));
                    p.setCaracteristicas(cargarCaracteristicas(con, p.getIdPropiedad()));
                    return p;
                }
            }
        }
        return null;
    }

    public List<Propiedad> buscarConFiltros(String texto, Integer idCiudad, Integer idTipo,
                                            String precioMin, String precioMax, String estado,
                                            Integer idInmobiliaria, List<Integer> idCaracteristicas) throws SQLException {
        List<Propiedad> lista = new ArrayList<>();
        String sql = "SELECT " + CAMPOS + DESDE + "WHERE 1=1 ";

        String sEstado = (estado != null && !estado.trim().isEmpty())
                ? estado.trim() : "DISPONIBLE";
        sql += "AND p.estado = ? ";

        if (texto != null && !texto.trim().isEmpty()) {
            sql += "AND (p.titulo LIKE ? OR p.direccion LIKE ? OR p.descripcion LIKE ?) ";
        }
        if (idCiudad != null && idCiudad > 0) {
            sql += "AND p.id_ciudad = ? ";
        }
        if (idTipo != null && idTipo > 0) {
            sql += "AND p.id_tipo = ? ";
        }
        if (precioMin != null && !precioMin.trim().isEmpty()) {
            sql += "AND p.precio >= ? ";
        }
        if (precioMax != null && !precioMax.trim().isEmpty()) {
            sql += "AND p.precio <= ? ";
        }
        if (idInmobiliaria != null && idInmobiliaria > 0) {
            sql += "AND p.id_inmobiliaria = ? ";
        }
        if (idCaracteristicas != null && !idCaracteristicas.isEmpty()) {
            // N:M: la propiedad debe tener TODAS las características seleccionadas
            StringBuilder ph = new StringBuilder();
            for (int i = 0; i < idCaracteristicas.size(); i++) {
                if (i > 0) {
                    ph.append(", ");
                }
                ph.append("?");
            }
            sql += "AND p.id_propiedad IN (SELECT pc.id_propiedad FROM propiedad_caracteristica pc "
                 + "WHERE pc.id_caracteristica IN (" + ph + ") GROUP BY pc.id_propiedad "
                 + "HAVING COUNT(DISTINCT pc.id_caracteristica) = ?) ";
        }
        sql += "ORDER BY p.fecha_publicacion DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int idx = 1;
            ps.setString(idx++, sEstado);
            if (texto != null && !texto.trim().isEmpty()) {
                String patron = "%" + texto.trim() + "%";
                ps.setString(idx++, patron);
                ps.setString(idx++, patron);
                ps.setString(idx++, patron);
            }
            if (idCiudad != null && idCiudad > 0) {
                ps.setInt(idx++, idCiudad);
            }
            if (idTipo != null && idTipo > 0) {
                ps.setInt(idx++, idTipo);
            }
            if (precioMin != null && !precioMin.trim().isEmpty()) {
                ps.setBigDecimal(idx++, new java.math.BigDecimal(precioMin.trim()));
            }
            if (precioMax != null && !precioMax.trim().isEmpty()) {
                ps.setBigDecimal(idx++, new java.math.BigDecimal(precioMax.trim()));
            }
            if (idInmobiliaria != null && idInmobiliaria > 0) {
                ps.setInt(idx++, idInmobiliaria);
            }
            if (idCaracteristicas != null && !idCaracteristicas.isEmpty()) {
                for (Integer idc : idCaracteristicas) {
                    ps.setInt(idx++, idc);
                }
                ps.setInt(idx++, idCaracteristicas.size());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Propiedad p = mapear(rs);
                    p.setImagenes(cargarImagenes(con, p.getIdPropiedad()));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    public List<Propiedad> listarDestacadas(int limite) throws SQLException {
        List<Propiedad> lista = new ArrayList<>();
        String sql = "SELECT " + CAMPOS + DESDE
                   + "WHERE p.estado = 'DISPONIBLE' "
                   + "ORDER BY (SELECT COUNT(*) FROM favorito f WHERE f.id_propiedad = p.id_propiedad) DESC, "
                   + "p.fecha_publicacion DESC LIMIT " + Math.max(1, limite);

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Propiedad p = mapear(rs);
                p.setImagenes(cargarImagenes(con, p.getIdPropiedad()));
                lista.add(p);
            }
        }
        return lista;
    }

    public int insertar(Propiedad p) throws SQLException {
        String sql = "INSERT INTO propiedad (matricula_inmobiliaria, titulo, descripcion, precio, direccion, "
                   + "area, habitaciones, banios, parqueaderos, estado, operacion, id_ciudad, id_tipo, id_inmobiliaria) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getMatriculaInmobiliaria());
            ps.setString(2, p.getTitulo());
            ps.setString(3, p.getDescripcion());
            ps.setBigDecimal(4, p.getPrecio());
            ps.setString(5, p.getDireccion());
            ps.setObject(6, p.getArea());
            ps.setObject(7, p.getHabitaciones() == 0 ? null : p.getHabitaciones());
            ps.setObject(8, p.getBanios() == 0 ? null : p.getBanios());
            ps.setObject(9, p.getParqueaderos() == 0 ? null : p.getParqueaderos());
            ps.setString(10, p.getEstado() == null ? "DISPONIBLE" : p.getEstado());
            ps.setString(11, p.getOperacion() == null ? "VENTA" : p.getOperacion());
            ps.setInt(12, p.getIdCiudad());
            ps.setInt(13, p.getIdTipo());
            ps.setInt(14, p.getIdInmobiliaria());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        }
    }

    public boolean actualizar(Propiedad p) throws SQLException {
        String sql = "UPDATE propiedad SET titulo = ?, descripcion = ?, precio = ?, direccion = ?, "
                   + "area = ?, habitaciones = ?, banios = ?, parqueaderos = ?, estado = ?, "
                   + "operacion = ?, id_ciudad = ?, id_tipo = ? WHERE id_propiedad = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getDescripcion());
            ps.setBigDecimal(3, p.getPrecio());
            ps.setString(4, p.getDireccion());
            ps.setObject(5, p.getArea());
            ps.setObject(6, p.getHabitaciones() == 0 ? null : p.getHabitaciones());
            ps.setObject(7, p.getBanios() == 0 ? null : p.getBanios());
            ps.setObject(8, p.getParqueaderos() == 0 ? null : p.getParqueaderos());
            ps.setString(9, p.getEstado());
            ps.setString(10, p.getOperacion() == null ? "VENTA" : p.getOperacion());
            ps.setInt(11, p.getIdCiudad());
            ps.setInt(12, p.getIdTipo());
            ps.setInt(13, p.getIdPropiedad());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM propiedad WHERE id_propiedad = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean darDeBaja(int id) throws SQLException {
        String sql = "UPDATE propiedad SET estado = 'INACTIVO' WHERE id_propiedad = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean reactivar(int id) throws SQLException {
        String sql = "UPDATE propiedad SET estado = 'DISPONIBLE' WHERE id_propiedad = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Propiedad> listarPorInmobiliaria(int idInmobiliaria) throws SQLException {
        List<Propiedad> lista = new ArrayList<>();
        String sql = "SELECT " + CAMPOS + DESDE + "WHERE p.id_inmobiliaria = ? ORDER BY p.id_propiedad DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idInmobiliaria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Propiedad p = mapear(rs);
                    p.setImagenes(cargarImagenes(con, p.getIdPropiedad()));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    private List<ImagenPropiedad> cargarImagenes(Connection con, int idPropiedad) throws SQLException {
        List<ImagenPropiedad> lista = new ArrayList<>();
        String sql = "SELECT id_imagen, id_propiedad, ruta, titulo, es_principal "
                   + "FROM imagen_propiedad WHERE id_propiedad = ? ORDER BY es_principal DESC, id_imagen";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
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

    private List<Caracteristica> cargarCaracteristicas(Connection con, int idPropiedad) throws SQLException {
        List<Caracteristica> lista = new ArrayList<>();
        String sql = "SELECT c.id_caracteristica, c.nombre, pc.cantidad "
                   + "FROM caracteristica c "
                   + "INNER JOIN propiedad_caracteristica pc ON c.id_caracteristica = pc.id_caracteristica "
                   + "WHERE pc.id_propiedad = ? ORDER BY c.nombre";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
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

    private Propiedad mapear(ResultSet rs) throws SQLException {
        Propiedad p = new Propiedad();
        p.setIdPropiedad(rs.getInt("id_propiedad"));
        p.setMatriculaInmobiliaria(rs.getString("matricula_inmobiliaria"));
        p.setTitulo(rs.getString("titulo"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setDireccion(rs.getString("direccion"));
        p.setUbicacion(rs.getString("direccion"));
        p.setArea(rs.getBigDecimal("area"));
        p.setHabitaciones(rs.getInt("habitaciones"));
        p.setBanios(rs.getInt("banios"));
        p.setParqueaderos(rs.getInt("parqueaderos"));
        p.setEstado(rs.getString("estado"));
        p.setOperacion(rs.getString("operacion"));
        p.setIdCiudad(rs.getInt("id_ciudad"));
        p.setIdTipo(rs.getInt("id_tipo"));
        p.setIdInmobiliaria(rs.getInt("id_inmobiliaria"));
        p.setNombreCiudad(rs.getString("nombre_ciudad"));
        p.setNombreTipo(rs.getString("nombre_tipo"));
        p.setNombreInmobiliaria(rs.getString("nombre_inmobiliaria"));
        p.setTelefonoInmobiliaria(rs.getString("telefono_inmobiliaria"));
        p.setEmailInmobiliaria(rs.getString("email_inmobiliaria"));
        return p;
    }
}