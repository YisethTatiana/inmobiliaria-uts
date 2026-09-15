package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.CitaDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.modelo.Cita;
import com.inmobiliaria.modelo.Usuario;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AgenteCitaServlet")
public class AgenteCitaServlet extends HttpServlet {

    private CitaDAO citaDAO = new CitaDAO();
    private AuditoriaDAO auditoriaDAO = new AuditoriaDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    private boolean accesoValido(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }
        if (!usuario.tieneRol("INMOBILIARIA") && !usuario.tieneRol("ADMINISTRADOR")) {
            response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!accesoValido(request, response)) {
            return;
        }

        try {
            Usuario usuario = (Usuario) request.getSession(false).getAttribute("usuario");
            List<Cita> citas;
            if (usuario != null && usuario.tieneRol("INMOBILIARIA")) {
                int idInmobiliaria = usuarioDAO.obtenerInmobiliaria(usuario.getIdUsuario());
                citas = citaDAO.listarPorInmobiliariaConDetalles(idInmobiliaria);
            } else {
                citas = citaDAO.listarTodasConDetalles();
            }
            request.setAttribute("citas", citas);
            request.getRequestDispatcher("/agente/citas.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!accesoValido(request, response)) {
            return;
        }

        try {
            int idCita = Integer.parseInt(request.getParameter("idCita"));
            String estado = request.getParameter("estado");

            if (!estadoValido(estado)) {
                response.sendRedirect(request.getContextPath() + "/AgenteCitaServlet?actualizado=false");
                return;
            }

            Usuario usuario = (Usuario) request.getSession(false).getAttribute("usuario");
            if (usuario != null && usuario.tieneRol("INMOBILIARIA")) {
                int idInmobiliaria = usuarioDAO.obtenerInmobiliaria(usuario.getIdUsuario());
                if (!citaDAO.perteneceAInmobiliaria(idCita, idInmobiliaria)) {
                    response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
                    return;
                }
            }

            if (citaDAO.actualizarEstado(idCita, estado)) {
                auditoriaDAO.registrar(usuario.getIdUsuario(), "ESTADO", "CITA", idCita,
                        "Cambió la cita #" + idCita + " a " + estado, request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/AgenteCitaServlet?actualizado=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/AgenteCitaServlet?actualizado=false");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/AgenteCitaServlet?actualizado=false");
        }
    }

    private boolean estadoValido(String estado) {
        return "PENDIENTE".equals(estado) || "APROBADA".equals(estado)
                || "RECHAZADA".equals(estado) || "CANCELADA".equals(estado);
    }
}