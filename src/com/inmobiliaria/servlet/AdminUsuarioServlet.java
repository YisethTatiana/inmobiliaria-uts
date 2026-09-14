package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.CatalogoDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.modelo.Usuario;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AdminUsuarioServlet")
public class AdminUsuarioServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private CatalogoDAO catalogoDAO = new CatalogoDAO();
    private AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

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

        try {
            List<Usuario> usuarios = usuarioDAO.listarTodos();
            Map<Integer, String> roles = usuarioDAO.listarRoles();
            List<Map<String, Object>> inmobiliarias = catalogoDAO.listarInmobiliarias();

            request.setAttribute("usuarios", usuarios);
            request.setAttribute("roles", roles);
            request.setAttribute("inmobiliarias", inmobiliarias);
            request.getRequestDispatcher("/admin/usuarios.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.getRequestDispatcher("/admin/usuarios.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

        String accion = request.getParameter("accion");
        int idUsuario;
        try {
            idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/AdminUsuarioServlet");
            return;
        }

        try {
            if ("estado".equals(accion)) {
                boolean activo = "true".equals(request.getParameter("activo"));
                usuarioDAO.cambiarEstado(idUsuario, activo);
                auditoriaDAO.registrar(usuario.getIdUsuario(), "ACTUALIZAR", "USUARIO", idUsuario,
                        (activo ? "Activó" : "Inactivó") + " la cuenta del usuario #" + idUsuario,
                        request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/AdminUsuarioServlet?actualizado=true");
                return;
            }

            if ("rol".equals(accion)) {
                int idRol;
                try {
                    idRol = Integer.parseInt(request.getParameter("idRol"));
                } catch (Exception e) {
                    response.sendRedirect(request.getContextPath() + "/AdminUsuarioServlet");
                    return;
                }

                usuarioDAO.anadirRol(idUsuario, idRol);

                String nombreRol = obtenerNombreRol(idRol);
                if ("INMOBILIARIA".equalsIgnoreCase(nombreRol)) {
                    int idInmobiliaria;
                    try {
                        idInmobiliaria = Integer.parseInt(request.getParameter("idInmobiliaria"));
                    } catch (Exception e) {
                        idInmobiliaria = -1;
                    }
                    if (idInmobiliaria > 0) {
                        usuarioDAO.asignarInmobiliaria(idUsuario, idInmobiliaria);
                    }
                }

                auditoriaDAO.registrar(usuario.getIdUsuario(), "ROL", "USUARIO", idUsuario,
                        "Asignó el rol " + nombreRol + " al usuario #" + idUsuario, request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/AdminUsuarioServlet?actualizado=true");
                return;
            }

            if ("quitarRol".equals(accion)) {
                int idRol;
                try {
                    idRol = Integer.parseInt(request.getParameter("idRol"));
                } catch (Exception e) {
                    response.sendRedirect(request.getContextPath() + "/AdminUsuarioServlet");
                    return;
                }
                usuarioDAO.quitarRol(idUsuario, idRol);
                auditoriaDAO.registrar(usuario.getIdUsuario(), "ROL", "USUARIO", idUsuario,
                        "Revocó el rol #" + idRol + " al usuario #" + idUsuario, request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/AdminUsuarioServlet?actualizado=true");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/AdminUsuarioServlet");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/AdminUsuarioServlet?error=true");
        }
    }

    private String obtenerNombreRol(int idRol) throws Exception {
        for (Map.Entry<Integer, String> e : usuarioDAO.listarRoles().entrySet()) {
            if (e.getKey() == idRol) {
                return e.getValue();
            }
        }
        return null;
    }
}