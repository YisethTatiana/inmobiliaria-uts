package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.SolicitudDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.modelo.DocumentoSolicitud;
import com.inmobiliaria.modelo.Solicitud;
import com.inmobiliaria.modelo.Usuario;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/GestionSolicitudServlet")
public class GestionSolicitudServlet extends HttpServlet {

    private SolicitudDAO solicitudDAO = new SolicitudDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!estaAutorizado(request, response)) {
            return;
        }
        Usuario usuario = (Usuario) request.getSession(false).getAttribute("usuario");
        boolean esAgente = usuario.tieneRol("INMOBILIARIA");

        try {
            String idSolicitudParam = request.getParameter("idSolicitud");

            if (idSolicitudParam != null && idSolicitudParam.matches("\\d+")) {
                int idSolicitud = Integer.parseInt(idSolicitudParam);
                Solicitud sol = detalleSolicitud(idSolicitud, request);
                if (sol == null) {
                    response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
                    return;
                }
                List<DocumentoSolicitud> documentos = solicitudDAO.listarDocumentos(idSolicitud);
                request.setAttribute("solicitud", sol);
                request.setAttribute("documentos", documentos);
                request.getRequestDispatcher(esAgente
                        ? "/agente/detalle_solicitud.jsp" : "/admin/detalle_solicitud.jsp")
                        .forward(request, response);
                return;
            }

            List<Solicitud> solicitudes;
            if (esAgente) {
                int idInmobiliaria = usuarioDAO.obtenerInmobiliaria(usuario.getIdUsuario());
                solicitudes = solicitudDAO.listarPorInmobiliaria(idInmobiliaria);
            } else {
                solicitudes = solicitudDAO.listarTodas();
            }
            request.setAttribute("solicitudes", solicitudes);
            request.getRequestDispatcher(esAgente
                    ? "/agente/solicitudes.jsp" : "/admin/solicitudes.jsp")
                    .forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath()
                    + (esAgente ? "/agente/dashboard_inmobiliaria.jsp" : "/admin/dashboard_admin.jsp") + "?error=true");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!estaAutorizado(request, response)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");
        Usuario usuario = (Usuario) request.getSession(false).getAttribute("usuario");
        boolean esAgente = usuario.tieneRol("INMOBILIARIA");

        String accion = request.getParameter("accion");

        try {
            if ("estado".equals(accion)) {
                int idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));
                String estado = request.getParameter("estado");

                if (!estado.equals("APROBADA") && !estado.equals("RECHAZADA") && !estado.equals("ANULADA")) {
                    response.sendRedirect(request.getContextPath() + "/GestionSolicitudServlet?error=true");
                    return;
                }
                if (esAgente) {
                    int idInmobiliaria = usuarioDAO.obtenerInmobiliaria(usuario.getIdUsuario());
                    if (!solicitudDAO.perteneceAInmobiliaria(idSolicitud, idInmobiliaria)) {
                        response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
                        return;
                    }
                }
                solicitudDAO.actualizarEstado(idSolicitud, estado);
                auditoriaDAO.registrar(usuario.getIdUsuario(), "ESTADO", "SOLICITUD", idSolicitud,
                        "Cambió la solicitud #" + idSolicitud + " a " + estado, request.getRemoteAddr());
                response.sendRedirect(request.getContextPath()
                        + "/GestionSolicitudServlet?idSolicitud=" + idSolicitud + "&actualizado=true");
                return;
            }

            if ("estadoDocumento".equals(accion)) {
                int idDocumento = Integer.parseInt(request.getParameter("idDocumento"));
                int idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));
                String estado = request.getParameter("estado");

                if (!estado.equals("REVISADO") && !estado.equals("APROBADO") && !estado.equals("RECHAZADO")) {
                    response.sendRedirect(request.getContextPath() + "/GestionSolicitudServlet?error=true");
                    return;
                }
                if (esAgente) {
                    int idInmobiliaria = usuarioDAO.obtenerInmobiliaria(usuario.getIdUsuario());
                    if (!solicitudDAO.perteneceAInmobiliaria(idSolicitud, idInmobiliaria)) {
                        response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
                        return;
                    }
                }
                solicitudDAO.actualizarEstadoDocumento(idDocumento, estado);
                auditoriaDAO.registrar(usuario.getIdUsuario(), "ESTADO", "DOCUMENTO", idDocumento,
                        "Revisó el documento de la solicitud #" + idSolicitud, request.getRemoteAddr());
                response.sendRedirect(request.getContextPath()
                        + "/GestionSolicitudServlet?idSolicitud=" + idSolicitud + "&docActualizado=true");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/GestionSolicitudServlet");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/GestionSolicitudServlet?error=true");
        }
    }

    private boolean estaAutorizado(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (!usuario.tieneRol("ADMINISTRADOR") && !usuario.tieneRol("INMOBILIARIA")) {
            response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
            return false;
        }
        return true;
    }

    private Solicitud detalleSolicitud(int idSolicitud, HttpServletRequest request) {
        try {
            Usuario usuario = (Usuario) request.getSession(false).getAttribute("usuario");
            List<Solicitud> lista;
            if (usuario.tieneRol("INMOBILIARIA")) {
                int idInmobiliaria = usuarioDAO.obtenerInmobiliaria(usuario.getIdUsuario());
                lista = solicitudDAO.listarPorInmobiliaria(idInmobiliaria);
            } else {
                lista = solicitudDAO.listarTodas();
            }
            for (Solicitud s : lista) {
                if (s.getIdSolicitud() == idSolicitud) {
                    return s;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}