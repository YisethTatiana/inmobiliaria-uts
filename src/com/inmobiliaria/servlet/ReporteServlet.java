package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.ReporteDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.modelo.Usuario;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ReporteServlet")
public class ReporteServlet extends HttpServlet {

    private ReporteDAO reporteDAO = new ReporteDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        boolean esAdmin = usuario.tieneRol("ADMINISTRADOR");
        boolean esAgente = usuario.tieneRol("INMOBILIARIA");
        if (!esAdmin && !esAgente) {
            response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
            return;
        }

        try {
            List<Map<String, Object>> citas = reporteDAO.citasConDetalles();
            List<Map<String, Object>> usuarios = reporteDAO.usuariosConPerfilYCitas();
            List<Map<String, Object>> propiedades = reporteDAO.propiedadesMasSolicitadas();
            List<Map<String, Object>> propiedadesCompletas = reporteDAO.propiedadesCompletas();
            List<Map<String, Object>> porCiudadEstado = reporteDAO.propiedadesPorCiudadYEstado();
            List<Map<String, Object>> solicitudesPorInmobiliaria = reporteDAO.solicitudesPorInmobiliaria();
            List<Map<String, Object>> citasPorEstado = reporteDAO.citasPorEstado();
            List<Map<String, Object>> caracteristicas = reporteDAO.caracteristicasDePropiedad(1);

            Integer idInmobiliaria = null;
            if (esAgente) {
                try {
                    idInmobiliaria = usuarioDAO.obtenerInmobiliaria(usuario.getIdUsuario());
                } catch (Exception e) {
                    idInmobiliaria = null;
                }
            }
            List<Map<String, Object>> ventasArriendos = reporteDAO.ventasArriendos(idInmobiliaria);

            request.setAttribute("citas", citas);
            request.setAttribute("usuarios", usuarios);
            request.setAttribute("propiedades", propiedades);
            request.setAttribute("propiedadesCompletas", propiedadesCompletas);
            request.setAttribute("porCiudadEstado", porCiudadEstado);
            request.setAttribute("solicitudesInmobiliaria", solicitudesPorInmobiliaria);
            request.setAttribute("citasPorEstado", citasPorEstado);
            request.setAttribute("caracteristicasDePropiedad", caracteristicas);
            request.setAttribute("ventasArriendos", ventasArriendos);

            if (esAgente) {
                request.getRequestDispatcher("/agente/reportes.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/admin/reportes.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String dashboard = esAgente ? "/agente/dashboard_inmobiliaria.jsp"
                                        : "/admin/dashboard_admin.jsp";
            response.sendRedirect(request.getContextPath() + dashboard + "?error=true");
        }
    }
}