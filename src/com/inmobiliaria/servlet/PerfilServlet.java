package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.PerfilDAO;
import com.inmobiliaria.modelo.Perfil;
import com.inmobiliaria.modelo.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/PerfilServlet")
public class PerfilServlet extends HttpServlet {

    private PerfilDAO perfilDAO = new PerfilDAO();
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

        try {
            Perfil perfil = perfilDAO.buscarPorUsuario(usuario.getIdUsuario());
            request.setAttribute("perfil", perfil);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.getRequestDispatcher("/cliente/perfil.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String documento = request.getParameter("documento");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");

        if (nombres == null || nombres.trim().isEmpty()
                || apellidos == null || apellidos.trim().isEmpty()
                || documento == null || documento.trim().isEmpty()) {
            request.setAttribute("error", "Nombres, apellidos y documento son obligatorios.");
            reenviar(request, response, usuario.getIdUsuario());
            return;
        }
        if (!documento.trim().matches("\\d{6,15}")) {
            request.setAttribute("error", "El documento debe contener solo números (6 a 15 dígitos).");
            reenviar(request, response, usuario.getIdUsuario());
            return;
        }
        if (telefono != null && !telefono.trim().isEmpty()
                && !telefono.trim().matches("[0-9+\\s\\-]{7,20}")) {
            request.setAttribute("error", "El teléfono ingresado no tiene un formato válido.");
            reenviar(request, response, usuario.getIdUsuario());
            return;
        }

        Perfil p = new Perfil();
        p.setIdUsuario(usuario.getIdUsuario());
        p.setNombres(nombres.trim());
        p.setApellidos(apellidos.trim());
        p.setDocumento(documento.trim());
        p.setTelefono(telefono != null ? telefono.trim() : "");
        p.setDireccion(direccion != null ? direccion.trim() : "");

        try {
            perfilDAO.guardar(p);
            auditoriaDAO.registrar(usuario.getIdUsuario(), "ACTUALIZAR", "PERFIL",
                    usuario.getIdUsuario(), "Actualizó su perfil", request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + "/PerfilServlet?guardado=true");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                request.setAttribute("error", "El documento ingresado ya se encuentra registrado.");
            } else {
                request.setAttribute("error", "Error al guardar el perfil en la base de datos.");
            }
            reenviar(request, response, usuario.getIdUsuario());
        }
    }

    private void reenviar(HttpServletRequest request, HttpServletResponse response, int idUsuario)
            throws ServletException, IOException {
        try {
            request.setAttribute("perfil", perfilDAO.buscarPorUsuario(idUsuario));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.getRequestDispatcher("/cliente/perfil.jsp").forward(request, response);
    }
}