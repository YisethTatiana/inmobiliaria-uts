package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.modelo.Usuario;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("correo");
        String password = request.getParameter("password");

        try {
            Usuario usuario = usuarioDAO.autenticar(correo, password);

            if (usuario != null) {
                if (!usuario.isActivo()) {
                    request.setAttribute("error", "Su cuenta se encuentra inactiva.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                    return;
                }

                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuario);
                List<String> roles = usuario.getRoles();
                session.setAttribute("roles", roles);
                if (roles != null && !roles.isEmpty()) {
                    session.setAttribute("rol", roles.get(0));
                }

                auditoriaDAO.registrar(usuario.getIdUsuario(), "LOGIN", "USUARIO",
                        usuario.getIdUsuario(), "Inicio de sesión", request.getRemoteAddr());

                response.sendRedirect(request.getContextPath() + usuario.getPanelSegunRol());
            } else {
                request.setAttribute("error", "Credenciales incorrectas.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Error en el servidor de base de datos.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}