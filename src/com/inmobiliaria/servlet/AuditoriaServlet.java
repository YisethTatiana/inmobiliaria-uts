package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.PerfilDAO;
import com.inmobiliaria.modelo.Auditoria;
import com.inmobiliaria.modelo.Perfil;
import com.inmobiliaria.modelo.Usuario;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AuditoriaServlet")
public class AuditoriaServlet extends HttpServlet {

    private AuditoriaDAO auditoriaDAO = new AuditoriaDAO();
    private PerfilDAO perfilDAO = new PerfilDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (!usuario.tieneRol("ADMINISTRADOR") && !usuario.tieneRol("AUDITOR")) {
            response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
            return;
        }

        try {
            List<Auditoria> registros = auditoriaDAO.listarTodas();
            Perfil perfil = perfilDAO.buscarPorUsuario(usuario.getIdUsuario());
            request.setAttribute("registros", registros);
            request.setAttribute("perfil", perfil);
            request.getRequestDispatcher("/admin/auditoria.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard_admin.jsp?error=true");
        }
    }
}