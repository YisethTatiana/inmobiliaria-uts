package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.UsuarioDAO;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RegistroServlet")
public class RegistroServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");
        String confirmar = request.getParameter("confirmar");

        if (correo == null || correo.trim().isEmpty()
                || !correo.trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            request.setAttribute("error", "Ingrese un correo electrónico válido.");
            reenviar(request, response);
            return;
        }
        if (password == null || password.length() < 6) {
            request.setAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
            reenviar(request, response);
            return;
        }
        if (confirmar == null || !confirmar.equals(password)) {
            request.setAttribute("error", "Las contraseñas no coinciden.");
            reenviar(request, response);
            return;
        }

        try {
            boolean exito = usuarioDAO.registrarUsuario(correo.trim(), password, 3);
            if (exito) {
                response.sendRedirect("login.jsp?registro=exito");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                request.setAttribute("error", "El correo ya se encuentra registrado.");
            } else {
                request.setAttribute("error", "Error al procesar el registro.");
            }
            reenviar(request, response);
        }
    }

    private void reenviar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("registro.jsp").forward(request, response);
    }
}