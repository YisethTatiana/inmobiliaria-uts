package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.modelo.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/EliminarPropiedadServlet")
public class EliminarPropiedadServlet extends HttpServlet {

    private PropiedadDAO propiedadDAO = new PropiedadDAO();

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
            boolean exito = propiedadDAO.eliminar(Integer.parseInt(idParam));
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/AdminPropiedadServlet?eliminado=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/AdminPropiedadServlet?eliminarError=true");
            }
        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath() + "/AdminPropiedadServlet?eliminarError=true");
        }
    }
}