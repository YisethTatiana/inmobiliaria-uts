package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.CaracteristicaDAO;
import com.inmobiliaria.dao.CatalogoDAO;
import com.inmobiliaria.dao.ImagenDAO;
import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.modelo.Propiedad;
import com.inmobiliaria.modelo.Usuario;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/EditarPropiedadServlet")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 15 * 1024 * 1024)
public class EditarPropiedadServlet extends HttpServlet {

    private PropiedadDAO propiedadDAO = new PropiedadDAO();
    private CaracteristicaDAO caracteristicaDAO = new CaracteristicaDAO();
    private ImagenDAO imagenDAO = new ImagenDAO();
    private AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        if (!usuario.tieneRol("ADMINISTRADOR")) {
            response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || !idParam.matches("\\d+")) {
            response.sendRedirect(request.getContextPath() + "/AdminPropiedadServlet");
            return;
        }

        try {
            Propiedad p = propiedadDAO.buscarPorId(Integer.parseInt(idParam));
            if (p == null) {
                response.sendRedirect(request.getContextPath() + "/AdminPropiedadServlet?noEncontrada=true");
                return;
            }
            List<Map<String, Object>> ciudades = new CatalogoDAO().listarCiudades();
            List<Map<String, Object>> tipos = new CatalogoDAO().listarTipos();
            List<com.inmobiliaria.modelo.Caracteristica> todas = caracteristicaDAO.listarTodas();
            request.setAttribute("propiedad", p);
            request.setAttribute("ciudades", ciudades);
            request.setAttribute("tipos", tipos);
            request.setAttribute("caracteristicas", todas);
            request.getRequestDispatcher("/admin/editar_propiedad.jsp").forward(request, response);
        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath() + "/AdminPropiedadServlet?error=true");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        if (!usuario.tieneRol("ADMINISTRADOR")) {
            response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
            return;
        }

        String idParam = request.getParameter("id");
        String titulo = request.getParameter("titulo");
        String descripcion = request.getParameter("descripcion");
        String precioStr = request.getParameter("precio");
        String direccion = request.getParameter("direccion");
        String areaStr = request.getParameter("area");
        String habitacionesStr = request.getParameter("habitaciones");
        String baniosStr = request.getParameter("banios");
        String parqueaderosStr = request.getParameter("parqueaderos");
        String idCiudadStr = request.getParameter("idCiudad");
        String idTipoStr = request.getParameter("idTipo");
        String estado = request.getParameter("estado");
        String operacion = request.getParameter("operacion");

        if (idParam == null || !idParam.matches("\\d+")
                || titulo == null || titulo.trim().isEmpty()
                || precioStr == null || precioStr.trim().isEmpty()
                || direccion == null || direccion.trim().isEmpty()
                || idCiudadStr == null || !idCiudadStr.matches("\\d+")
                || idTipoStr == null || !idTipoStr.matches("\\d+")) {
            request.setAttribute("error", "Todos los campos obligatorios deben diligenciarse.");
            request.getRequestDispatcher("/admin/editar_propiedad.jsp").forward(request, response);
            return;
        }

        Propiedad p = new Propiedad();
        p.setIdPropiedad(Integer.parseInt(idParam));
        p.setTitulo(titulo.trim());
        p.setDescripcion(descripcion != null ? descripcion.trim() : "");
        p.setDireccion(direccion.trim());
        p.setIdCiudad(Integer.parseInt(idCiudadStr));
        p.setIdTipo(Integer.parseInt(idTipoStr));
        p.setEstado(estado != null && !estado.isEmpty() ? estado : "DISPONIBLE");
        p.setOperacion("ARRIENDO".equals(operacion) ? "ARRIENDO" : "VENTA");

        try {
            p.setPrecio(new BigDecimal(precioStr.trim()));
            p.setArea(areaStr != null && !areaStr.trim().isEmpty() ? new BigDecimal(areaStr.trim()) : null);
            p.setHabitaciones(parceInt(habitacionesStr));
            p.setBanios(parceInt(baniosStr));
            p.setParqueaderos(parceInt(parqueaderosStr));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Los campos numéricos no son válidos.");
            request.getRequestDispatcher("/admin/editar_propiedad.jsp").forward(request, response);
            return;
        }

        try {
            boolean exito = propiedadDAO.actualizar(p);
            if (!exito) {
                request.setAttribute("error", "No se pudo actualizar la propiedad.");
                request.getRequestDispatcher("/admin/editar_propiedad.jsp").forward(request, response);
                return;
            }

            caracteristicaDAO.eliminarDePropiedad(p.getIdPropiedad());
            String[] caracteristicas = request.getParameterValues("caracteristicas");
            if (caracteristicas != null) {
                for (String idChar : caracteristicas) {
                    if (idChar.matches("\\d+")) {
                        caracteristicaDAO.asignarPropiedad(p.getIdPropiedad(), Integer.parseInt(idChar), 1);
                    }
                }
            }

            boolean subioAlguna = false;
            for (javax.servlet.http.Part part : request.getParts()) {
                if ("imagen".equals(part.getName()) && part.getSize() > 0) {
                    subioAlguna = true;
                    break;
                }
            }
            if (subioAlguna) {
                imagenDAO.eliminarDePropiedad(p.getIdPropiedad());
                com.inmobiliaria.util.ArchivoUtil.guardarImagenesPropiedad(getServletContext(), request, p.getIdPropiedad());
            }

            auditoriaDAO.registrar(usuario.getIdUsuario(), "ACTUALIZAR", "PROPIEDAD", p.getIdPropiedad(),
                    "Actualizó la propiedad " + p.getTitulo(), request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + "/AdminPropiedadServlet?actualizado=true");
        } catch (SQLException e) {
            request.setAttribute("error", "Error al actualizar la propiedad en la base de datos.");
            request.getRequestDispatcher("/admin/editar_propiedad.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar las imágenes.");
            request.getRequestDispatcher("/admin/editar_propiedad.jsp").forward(request, response);
        }
    }

    private int parceInt(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(valor.trim());
    }
}