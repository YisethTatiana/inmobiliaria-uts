package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.CitaDAO;
import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.modelo.Cita;
import com.inmobiliaria.modelo.Propiedad;
import com.inmobiliaria.modelo.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/CitaServlet")
public class CitaServlet extends HttpServlet {

    private CitaDAO citaDAO = new CitaDAO();
    private PropiedadDAO propiedadDAO = new PropiedadDAO();
    private AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        if (!usuario.tieneRol("CLIENTE") && !usuario.tieneRol("ADMINISTRADOR")) {
            response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
            return;
        }

        List<Cita> citas = new ArrayList<>();
        List<Propiedad> propiedades = new ArrayList<>();
        try {
            citas = citaDAO.listarPorUsuario(usuario.getIdUsuario());
            propiedades = propiedadDAO.listarTodas(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("citas", citas);
        request.setAttribute("propiedades", propiedades);
        request.getRequestDispatcher("/cliente/citas.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        if (!usuario.tieneRol("CLIENTE") && !usuario.tieneRol("ADMINISTRADOR")) {
            response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        try {
            if ("cancelar".equals(accion)) {
                int idCita;
                try {
                    idCita = Integer.parseInt(request.getParameter("idCita"));
                } catch (Exception e) {
                    response.sendRedirect(request.getContextPath() + "/CitaServlet");
                    return;
                }
                if (citaDAO.perteneceAUsuario(idCita, usuario.getIdUsuario())) {
                    citaDAO.actualizarEstado(idCita, "CANCELADA");
                    auditoriaDAO.registrar(usuario.getIdUsuario(), "CANCELAR", "CITA", idCita,
                            "Canceló la cita #" + idCita, request.getRemoteAddr());
                }
                response.sendRedirect(request.getContextPath() + "/CitaServlet?cancelada=true");
                return;
            }

            int idPropiedad = Integer.parseInt(request.getParameter("idPropiedad"));
            String fechaHora = request.getParameter("fechaHora");
            String observaciones = request.getParameter("observaciones");

            if (fechaHora == null || fechaHora.length() < 14) {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?fechaInvalida=true");
                return;
            }

            boolean exito = citaDAO.agendarCita(usuario.getIdUsuario(), idPropiedad, fechaHora, observaciones);
            if (exito) {
                auditoriaDAO.registrar(usuario.getIdUsuario(), "CREAR", "CITA", idPropiedad,
                        "Agendó cita para la propiedad #" + idPropiedad, request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/CitaServlet?agendada=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?error=true");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?duplicada=true");
            } else {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/CitaServlet?error=true");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/CitaServlet?error=true");
        }
    }
}