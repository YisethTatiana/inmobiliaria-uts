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
        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String documento = request.getParameter("documento");
        String telefono = request.getParameter("telefono");

        if (nombres == null || nombres.trim().isEmpty()
                || apellidos == null || apellidos.trim().isEmpty()) {
            request.setAttribute("error", "Debe ingresar sus nombres y apellidos.");
            reenviar(request, response);
            return;
        }
        if (documento == null || documento.trim().isEmpty()
                || !documento.trim().matches("\\d{6,12}")) {
            request.setAttribute("error", "Ingrese un número de documento válido (solo dígitos).");
            reenviar(request, response);
            return;
        }
        if (correo == null || correo.trim().isEmpty()
                || !correo.trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            request.setAttribute("error", "Ingrese un correo eléctrónico válido.");
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
            boolean exito = usuarioDAO.registrarUsuario(correo.trim(), password, 3,
                    nombres.trim(), apellidos.trim(), documento.trim(),
                    telefono != null ? telefono.trim() : "");
            if (exito) {
                response.sendRedirect("login.jsp?registro=exito");
                return;
            }
            request.setAttribute("error", "Error al procesar el registro.");
            reenviar(request, response);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                String mensaje = e.getMessage() != null && e.getMessage().contains("documento")
                        ? "El documento ya se encuentra registrado."
                        : "El correo ya se encuentra registrado.";
                request.setAttribute("error", mensaje);
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