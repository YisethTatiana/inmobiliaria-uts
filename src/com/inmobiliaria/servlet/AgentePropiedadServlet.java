package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.CaracteristicaDAO;
import com.inmobiliaria.dao.CatalogoDAO;
import com.inmobiliaria.dao.ImagenDAO;
import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.modelo.Caracteristica;
import com.inmobiliaria.modelo.Propiedad;
import com.inmobiliaria.modelo.Usuario;
import com.inmobiliaria.util.ArchivoUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AgentePropiedadServlet")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 15 * 1024 * 1024)
public class AgentePropiedadServlet extends HttpServlet {

    private PropiedadDAO propiedadDAO = new PropiedadDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private CaracteristicaDAO caracteristicaDAO = new CaracteristicaDAO();
    private ImagenDAO imagenDAO = new ImagenDAO();
    private CatalogoDAO catalogoDAO = new CatalogoDAO();
    private AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    private int idInmobiliariaValida(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return -1;
        }
        if (!usuario.tieneRol("INMOBILIARIA") && !usuario.tieneRol("ADMINISTRADOR")) {
            response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
            return -1;
        }

        try {
            int idInmobiliaria = usuarioDAO.obtenerInmobiliaria(usuario.getIdUsuario());
            if (idInmobiliaria <= 0) {
                response.sendRedirect(request.getContextPath() + "/agente/dashboard_inmobiliaria.jsp?sinInmobiliaria=true");
                return -1;
            }
            return idInmobiliaria;
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return -1;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idInmobiliaria = idInmobiliariaValida(request, response);
        if (idInmobiliaria <= -1) {
            return;
        }

        String accion = request.getParameter("accion");

        try {
            if ("editar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Propiedad p = propiedadDAO.buscarPorId(id);
                if (p == null || p.getIdInmobiliaria() != idInmobiliaria) {
                    response.sendRedirect(request.getContextPath() + "/AgentePropiedadServlet");
                    return;
                }
                request.setAttribute("propiedad", p);
                request.setAttribute("ciudades", catalogoDAO.listarCiudades());
                request.setAttribute("tipos", catalogoDAO.listarTipos());
                request.setAttribute("caracteristicas", caracteristicaDAO.listarTodas());
                request.getRequestDispatcher("/agente/editar_propiedad.jsp").forward(request, response);
                return;
            }

            if ("darBaja".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Propiedad p = propiedadDAO.buscarPorId(id);
                if (p == null || p.getIdInmobiliaria() != idInmobiliaria) {
                    response.sendRedirect(request.getContextPath() + "/AgentePropiedadServlet");
                    return;
                }
                propiedadDAO.darDeBaja(id);
                auditoriaDAO.registrar(idUsuarioSesion(request), "DAR_BAJA", "PROPIEDAD", id,
                        "Dio de baja la propiedad " + p.getTitulo(), request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/AgentePropiedadServlet?darBaja=true");
                return;
            }

            if ("reactivar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Propiedad p = propiedadDAO.buscarPorId(id);
                if (p == null || p.getIdInmobiliaria() != idInmobiliaria) {
                    response.sendRedirect(request.getContextPath() + "/AgentePropiedadServlet");
                    return;
                }
                propiedadDAO.reactivar(id);
                auditoriaDAO.registrar(idUsuarioSesion(request), "REACTIVAR", "PROPIEDAD", id,
                        "Reactivó la propiedad " + p.getTitulo(), request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/AgentePropiedadServlet?reactivada=true");
                return;
            }

            if ("eliminar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Propiedad p = propiedadDAO.buscarPorId(id);
                if (p == null || p.getIdInmobiliaria() != idInmobiliaria) {
                    response.sendRedirect(request.getContextPath() + "/AgentePropiedadServlet");
                    return;
                }
                propiedadDAO.eliminar(id);
                auditoriaDAO.registrar(idUsuarioSesion(request), "ELIMINAR", "PROPIEDAD", id,
                        "Eliminó la propiedad " + p.getTitulo(), request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/AgentePropiedadServlet?eliminada=true");
                return;
            }

            request.setAttribute("ciudades", catalogoDAO.listarCiudades());
            request.setAttribute("tipos", catalogoDAO.listarTipos());
            request.setAttribute("caracteristicas", caracteristicaDAO.listarTodas());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            List<Propiedad> propiedades = propiedadDAO.listarPorInmobiliaria(idInmobiliaria);
            request.setAttribute("propiedades", propiedades);
            request.getRequestDispatcher("/agente/propiedades.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        int idInmobiliaria = idInmobiliariaValida(request, response);
        if (idInmobiliaria <= -1) {
            return;
        }
        Usuario usuario = (Usuario) request.getSession(false).getAttribute("usuario");

        String accion = request.getParameter("accion");
        String titulo = request.getParameter("titulo");
        String descripcion = request.getParameter("descripcion");
        String direccion = request.getParameter("direccion");
        String precioStr = request.getParameter("precio");
        String idCiudadStr = request.getParameter("idCiudad");
        String idTipoStr = request.getParameter("idTipo");
        String operacion = request.getParameter("operacion");

        if (titulo == null || titulo.trim().isEmpty()
                || precioStr == null || precioStr.trim().isEmpty()
                || direccion == null || direccion.trim().isEmpty()
                || idCiudadStr == null || !idCiudadStr.matches("\\d+")
                || idTipoStr == null || !idTipoStr.matches("\\d+")) {
            response.sendRedirect(request.getContextPath() + "/AgentePropiedadServlet?error=true");
            return;
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(precioStr.trim());
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/AgentePropiedadServlet?error=true");
            return;
        }

        try {
            if ("crear".equals(accion)) {
                Propiedad p = new Propiedad();
                p.setTitulo(titulo.trim());
                p.setDescripcion(descripcion != null ? descripcion.trim() : "");
                p.setDireccion(direccion.trim());
                p.setPrecio(precio);
                p.setIdCiudad(Integer.parseInt(idCiudadStr));
                p.setIdTipo(Integer.parseInt(idTipoStr));
                p.setIdInmobiliaria(idInmobiliaria);
                p.setMatriculaInmobiliaria("MAT-" + System.currentTimeMillis());
                p.setArea(areaParam(request));
                p.setHabitaciones(parceInt(request.getParameter("habitaciones")));
                p.setBanios(parceInt(request.getParameter("banios")));
                p.setParqueaderos(parceInt(request.getParameter("parqueaderos")));
                p.setEstado("DISPONIBLE");
                p.setOperacion(operacionValida(operacion));

                int idPropiedad = propiedadDAO.insertar(p);
                if (idPropiedad > 0) {
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
                            "Publicó la propiedad " + p.getTitulo(), request.getRemoteAddr());
                }
                response.sendRedirect(request.getContextPath() + "/AgentePropiedadServlet?creada=true");
                return;
            }

            if ("editar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Propiedad p = propiedadDAO.buscarPorId(id);
                if (p == null || p.getIdInmobiliaria() != idInmobiliaria) {
                    response.sendRedirect(request.getContextPath() + "/AgentePropiedadServlet");
                    return;
                }
                p.setTitulo(titulo.trim());
                p.setDescripcion(descripcion != null ? descripcion.trim() : "");
                p.setDireccion(direccion.trim());
                p.setPrecio(precio);
                p.setIdCiudad(Integer.parseInt(idCiudadStr));
                p.setIdTipo(Integer.parseInt(idTipoStr));
                p.setArea(areaParam(request));
                p.setHabitaciones(parceInt(request.getParameter("habitaciones")));
                p.setBanios(parceInt(request.getParameter("banios")));
                p.setParqueaderos(parceInt(request.getParameter("parqueaderos")));
                p.setOperacion(operacionValida(operacion));

                propiedadDAO.actualizar(p);

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
                    ArchivoUtil.guardarImagenesPropiedad(getServletContext(), request, p.getIdPropiedad());
                }

                auditoriaDAO.registrar(usuario.getIdUsuario(), "ACTUALIZAR", "PROPIEDAD", p.getIdPropiedad(),
                        "Actualizó la propiedad " + p.getTitulo(), request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/AgentePropiedadServlet?actualizada=true");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/AgentePropiedadServlet");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/AgentePropiedadServlet?error=true");
        }
    }

    private BigDecimal areaParam(HttpServletRequest request) {
        String v = request.getParameter("area");
        if (v == null || v.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(v.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String operacionValida(String operacion) {
        return ("ARRIENDO".equals(operacion)) ? "ARRIENDO" : "VENTA";
    }

    private int parceInt(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(valor.trim());
    }

    private Integer idUsuarioSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Usuario u = (session != null) ? (Usuario) session.getAttribute("usuario") : null;
        return u != null ? u.getIdUsuario() : null;
    }
}