package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.modelo.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/EliminarUsuarioServlet")
public class EliminarUsuarioServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        String rol = (session != null) ? (String) session.getAttribute("rol") : null;
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        if (rol == null || !"ADMINISTRADOR".equalsIgnoreCase(rol)) {
            response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || !idParam.matches("\\d+")) {
            response.sendRedirect(request.getContextPath() + "/AdminUsuarioServlet");
            return;
        }

        try {
            boolean exito = usuarioDAO.eliminar(Integer.parseInt(idParam));
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/AdminUsuarioServlet?eliminado=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/AdminUsuarioServlet?eliminarError=true");
            }
        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath() + "/AdminUsuarioServlet?eliminarError=true");
        }
    }
}