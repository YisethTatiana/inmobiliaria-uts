package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.util.CorreoUtil;
import com.inmobiliaria.util.PasswordUtils;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RecuperarClaveServlet")
public class RecuperarClaveServlet extends HttpServlet {

    private static final int MINUTOS_VIGENCIA = 15;
    private static final SecureRandom RANDOM = new SecureRandom();

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/recuperar_clave.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        String ip = request.getRemoteAddr();

        if ("pedir".equals(accion)) {
            procesarPedir(request, response, ip);
        } else if ("reset".equals(accion)) {
            procesarReset(request, response, ip);
        } else {
            response.sendRedirect(request.getContextPath() + "/recuperar_clave.jsp");
        }
    }

    private void procesarPedir(HttpServletRequest request, HttpServletResponse response,
                               String ip) throws ServletException, IOException {
        String correo = request.getParameter("correo");
        boolean correoRegistrado = false;
        int resultadoEnvio = CorreoUtil.SIN_CONFIG;

        if (correo != null && !correo.trim().isEmpty()) {
            try {
                String correoOk = correo.trim();
                int idUsuario = usuarioDAO.buscarIdPorCorreo(correoOk);
                if (idUsuario > 0) {
                    correoRegistrado = true;
                    String codigo = String.format("%06d", RANDOM.nextInt(1000000));
                    Timestamp expiracion = new Timestamp(System.currentTimeMillis()
                            + MINUTOS_VIGENCIA * 60L * 1000L);
                    usuarioDAO.establecerToken(idUsuario, PasswordUtils.hashToken(codigo), expiracion);

                    String cuerpo = "Recibimos una solicitud para restablecer tu contrasena.\n\n"
                            + "Tu codigo de verificacion es:\n\n"
                            + "   " + codigo + "\n\n"
                            + "Ingresalo junto con tu nueva contrasena dentro de los proximos "
                            + MINUTOS_VIGENCIA + " minutos.\n\n"
                            + "Si no solicitaste este cambio, ignora este mensaje.";
                    resultadoEnvio = CorreoUtil.enviar(correoOk,
                            "Codigo de recuperacion - Inmobiliaria UTS", cuerpo);

                    String estadoEnvio = resultadoEnvio == CorreoUtil.ENVIADO ? "ENVIADO"
                            : (resultadoEnvio == CorreoUtil.SIN_CONFIG ? "SIN_CONFIG" : "FALLO");
                    auditoriaDAO.registrar(idUsuario, "RECUPERAR_PEDIR", "USUARIO", idUsuario,
                            "Solicitud de recuperacion de contrasena: " + correoOk
                                    + " | envio=" + estadoEnvio, ip);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        request.setAttribute("enviado", true);
        request.setAttribute("correo", correo != null ? correo.trim() : "");
        request.setAttribute("correoRegistrado", correoRegistrado);
        request.setAttribute("resultadoEnvio", resultadoEnvio);
        request.getRequestDispatcher("recuperar_clave.jsp").forward(request, response);
    }

    private void procesarReset(HttpServletRequest request, HttpServletResponse response,
                               String ip) throws ServletException, IOException {
        String correo = request.getParameter("correo") != null ? request.getParameter("correo").trim() : "";
        String codigo = request.getParameter("codigo") != null ? request.getParameter("codigo").trim() : "";
        String password = request.getParameter("password");
        String confirmacion = request.getParameter("password2");

        if (codigo.isEmpty() || !codigo.matches("\\d{6}") || password == null || password.length() < 6
                || !password.equals(confirmacion)) {
            reenviarConError(request, response, correo,
                    password == null || password.length() < 6 || !password.equals(confirmacion)
                        ? "La contrasena debe tener al menos 6 caracteres y coincidir en ambos campos."
                        : "El codigo debe ser de 6 digitos.");
            return;
        }

        try {
            int idUsuario = usuarioDAO.buscarIdPorCorreo(correo);
            int idPorCodigo = usuarioDAO.idUsuarioPorTokenValido(PasswordUtils.hashToken(codigo));
            if (idUsuario <= 0 || idPorCodigo != idUsuario) {
                reenviarConError(request, response, correo,
                        "El codigo es invalido o ya vencio. Solicita uno nuevo.");
                return;
            }

            String salt = PasswordUtils.generarSalt();
            String nuevoHash = PasswordUtils.hashPassword(password, salt) + ":" + salt;
            usuarioDAO.cambiarPassword(idUsuario, nuevoHash);
            auditoriaDAO.registrar(idUsuario, "RECUPERAR_CLAVE", "USUARIO", idUsuario,
                    "Contrasena restablecida correctamente", ip);
            response.sendRedirect(request.getContextPath() + "/login.jsp?clave=1");
        } catch (SQLException e) {
            e.printStackTrace();
            reenviarConError(request, response, correo,
                    "Error interno al restablecer la contrasena. Intenta nuevamente.");
        }
    }

    private void reenviarConError(HttpServletRequest request, HttpServletResponse response,
                                  String correo, String error) throws ServletException, IOException {
        request.setAttribute("enviado", true);
        request.setAttribute("correo", correo);
        request.setAttribute("error", error);
        request.getRequestDispatcher("recuperar_clave.jsp").forward(request, response);
    }
}