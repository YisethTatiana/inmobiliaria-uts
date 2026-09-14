package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.FavoritoDAO;
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

@WebServlet("/FavoritoServlet")
public class FavoritoServlet extends HttpServlet {

    private FavoritoDAO favoritoDAO = new FavoritoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        if (!usuario.tieneRol("CLIENTE")) {
            response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
            return;
        }

        String idParam = request.getParameter("id");

        if (idParam == null) {
            try {
                List<Propiedad> favoritos = favoritoDAO.listarFavoritos(usuario.getIdUsuario());
                request.setAttribute("favoritos", favoritos);
                request.getRequestDispatcher("/cliente/favoritos.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/cliente/dashboard_cliente.jsp?error=true");
            }
            return;
        }

        if (!idParam.matches("\\d+")) {
            response.sendRedirect(request.getContextPath() + "/PropiedadServlet");
            return;
        }

        try {
            int idPropiedad = Integer.parseInt(idParam);
            favoritoDAO.toggleFavorito(usuario.getIdUsuario(), idPropiedad);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String origen = request.getParameter("origen");
        if ("favoritos".equals(origen)) {
            response.sendRedirect(request.getContextPath() + "/FavoritoServlet");
        } else {
            response.sendRedirect(request.getContextPath() + "/PropiedadServlet");
        }
    }
}