package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.CaracteristicaDAO;
import com.inmobiliaria.dao.ImagenDAO;
import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.modelo.Propiedad;
import com.inmobiliaria.modelo.Usuario;
import com.inmobiliaria.util.ArchivoUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet("/CrearPropiedadServlet")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 15 * 1024 * 1024)
public class CrearPropiedadServlet extends HttpServlet {

    private PropiedadDAO propiedadDAO = new PropiedadDAO();
    private CaracteristicaDAO caracteristicaDAO = new CaracteristicaDAO();
    private ImagenDAO imagenDAO = new ImagenDAO();
    private AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

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
        String matricula = request.getParameter("matricula");
        String operacion = request.getParameter("operacion");

        if (titulo == null || titulo.trim().isEmpty()
                || precioStr == null || precioStr.trim().isEmpty()
                || direccion == null || direccion.trim().isEmpty()
                || idCiudadStr == null || !idCiudadStr.matches("\\d+")
                || idTipoStr == null || !idTipoStr.matches("\\d+")) {
            request.setAttribute("error", "Complete todos los campos obligatorios.");
            request.getRequestDispatcher("/admin/crear_propiedad.jsp").forward(request, response);
            return;
        }

        Propiedad p = new Propiedad();
        p.setTitulo(titulo.trim());
        p.setDescripcion(descripcion != null ? descripcion.trim() : "");
        p.setDireccion(direccion.trim());
        p.setIdCiudad(Integer.parseInt(idCiudadStr));
        p.setIdTipo(Integer.parseInt(idTipoStr));
        p.setIdInmobiliaria(1);
        p.setEstado("DISPONIBLE");
        p.setOperacion("ARRIENDO".equals(operacion) ? "ARRIENDO" : "VENTA");
        if (matricula != null && !matricula.trim().isEmpty()) {
            p.setMatriculaInmobiliaria(matricula.trim());
        } else {
            p.setMatriculaInmobiliaria("MAT-" + System.currentTimeMillis());
        }

        try {
            p.setPrecio(new BigDecimal(precioStr.trim()));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "El precio ingresado no es válido.");
            request.getRequestDispatcher("/admin/crear_propiedad.jsp").forward(request, response);
            return;
        }

        try {
            p.setArea(areaStr != null && !areaStr.trim().isEmpty() ? new BigDecimal(areaStr.trim()) : null);
            p.setHabitaciones(parceInt(habitacionesStr));
            p.setBanios(parceInt(baniosStr));
            p.setParqueaderos(parceInt(parqueaderosStr));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Los campos numéricos (área, habitaciones, baños, parqueaderos) no son válidos.");
            request.getRequestDispatcher("/admin/crear_propiedad.jsp").forward(request, response);
            return;
        }

        try {
            int idPropiedad = propiedadDAO.insertar(p);
            if (idPropiedad <= 0) {
                request.setAttribute("error", "No se pudo crear la propiedad.");
                request.getRequestDispatcher("/admin/crear_propiedad.jsp").forward(request, response);
                return;
            }

            ArchivoUtil.guardarImagenesPropiedad(getServletContext(), request, idPropiedad);

            String[] caracteristicas = request.getParameterValues("caracteristicas");
            if (caracteristicas != null) {
                for (String idChar : caracteristicas) {
                    if (idChar.matches("\\d+")) {
                        caracteristicaDAO.asignarPropiedad(idPropiedad, Integer.parseInt(idChar), 1);
                    }
                }
            }

            auditoriaDAO.registrar(usuario.getIdUsuario(), "CREAR", "PROPIEDAD", idPropiedad,
                    "Creó la propiedad " + p.getTitulo(), request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + "/AdminPropiedadServlet?creado=true");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                request.setAttribute("error", "La matrícula inmobiliaria ya se encuentra registrada.");
            } else {
                request.setAttribute("error", "Error al guardar la propiedad en la base de datos.");
            }
            request.getRequestDispatcher("/admin/crear_propiedad.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar las imágenes.");
            request.getRequestDispatcher("/admin/crear_propiedad.jsp").forward(request, response);
        }
    }

    private int parceInt(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(valor.trim());
    }
}