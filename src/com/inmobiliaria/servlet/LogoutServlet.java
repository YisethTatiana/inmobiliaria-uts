package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.modelo.Usuario;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {

    private AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario != null) {
                try {
                    auditoriaDAO.registrar(usuario.getIdUsuario(), "LOGOUT", "USUARIO",
                            usuario.getIdUsuario(), "Cierre de sesión", request.getRemoteAddr());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            session.invalidate();
        }
        response.sendRedirect("login.jsp");
    }
}