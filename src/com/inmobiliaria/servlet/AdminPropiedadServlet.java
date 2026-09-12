package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.modelo.Propiedad;
import com.inmobiliaria.modelo.Usuario;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AdminPropiedadServlet")
public class AdminPropiedadServlet extends HttpServlet {

    private PropiedadDAO propiedadDAO = new PropiedadDAO();

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

        try {
            List<Propiedad> lista = propiedadDAO.listarTodas(false);
            request.setAttribute("propiedades", lista);
            request.getRequestDispatcher("/admin/propiedades.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.getRequestDispatcher("/admin/propiedades.jsp").forward(request, response);
        }
    }
}