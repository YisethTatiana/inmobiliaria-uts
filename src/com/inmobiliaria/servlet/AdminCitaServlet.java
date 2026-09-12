package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.CitaDAO;
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

@WebServlet("/AdminCitaServlet")
public class AdminCitaServlet extends HttpServlet {

    private CitaDAO citaDAO = new CitaDAO();
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

        try {
            List<Cita> citas = citaDAO.listarTodasConDetalles();
            request.setAttribute("citas", citas);
            request.getRequestDispatcher("/admin/citas.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard_admin.jsp?error=true");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;
        String rol = (session != null) ? (String) session.getAttribute("rol") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        if (!usuario.tieneRol("ADMINISTRADOR")) {
            response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String idParam = request.getParameter("idCita");
        String nuevoEstado = request.getParameter("estado");

        if (idParam != null && nuevoEstado != null) {
            try {
                int idCita = Integer.parseInt(idParam);
                citaDAO.actualizarEstado(idCita, nuevoEstado);
                auditoriaDAO.registrar(usuario.getIdUsuario(), "ESTADO", "CITA", idCita,
                        "Cambió el estado de la cita #" + idCita + " a " + nuevoEstado, request.getRemoteAddr());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response.sendRedirect(request.getContextPath() + "/AdminCitaServlet?actualizado=true");
    }
}