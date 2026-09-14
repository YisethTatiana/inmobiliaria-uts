package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.modelo.ResultadoLogin;
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
        String ip = request.getRemoteAddr();

        try {
            ResultadoLogin resultado = usuarioDAO.autenticarConBloqueo(correo, password);

            if (resultado.getEstado() == ResultadoLogin.OK) {
                Usuario usuario = resultado.getUsuario();
                if (!usuario.isActivo()) {
                    auditoriaDAO.registrar(usuario.getIdUsuario(), "LOGIN_INACTIVA", "USUARIO",
                            usuario.getIdUsuario(), "Intento de ingreso con cuenta inactiva: " + correo, ip);
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
                        usuario.getIdUsuario(), "Inicio de sesión", ip);

                response.sendRedirect(request.getContextPath() + usuario.getPanelSegunRol());
            } else if (resultado.getEstado() == ResultadoLogin.BLOQUEADO) {
                int idUsuario = usuarioDAO.buscarIdPorCorreo(correo);
                auditoriaDAO.registrar(idUsuario > 0 ? idUsuario : null, "LOGIN_BLOQUEADO", "USUARIO",
                        idUsuario > 0 ? idUsuario : null,
                        "Cuenta bloqueada temporalmente por intentos fallidos: " + correo, ip);
                request.setAttribute("error", "Cuenta bloqueada temporalmente por intentos fallidos. "
                        + "Intente nuevamente en " + resultado.getMinutosRestantes() + " minuto(s).");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            } else {
                int idUsuario = usuarioDAO.buscarIdPorCorreo(correo);
                auditoriaDAO.registrar(idUsuario > 0 ? idUsuario : null, "LOGIN_FALLIDO", "USUARIO",
                        idUsuario > 0 ? idUsuario : null,
                        "Intento de inicio de sesión fallido: " + correo, ip);
                request.setAttribute("error", "Credenciales incorrectas. Despu\u00e9s de "
                        + UsuarioDAO.MAX_INTENTOS + " intentos fallidos la cuenta se bloquea por "
                        + UsuarioDAO.MINUTOS_BLOQUEO + " minutos.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Error en el servidor de base de datos.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}