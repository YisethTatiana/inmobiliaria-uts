package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.dao.SolicitudDAO;
import com.inmobiliaria.modelo.DocumentoSolicitud;
import com.inmobiliaria.modelo.Propiedad;
import com.inmobiliaria.modelo.Solicitud;
import com.inmobiliaria.modelo.Usuario;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet("/SolicitudServlet")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class SolicitudServlet extends HttpServlet {

    private SolicitudDAO solicitudDAO = new SolicitudDAO();
    private PropiedadDAO propiedadDAO = new PropiedadDAO();
    private AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!esCliente(request, response)) {
            return;
        }
        Usuario usuario = (Usuario) request.getSession(false).getAttribute("usuario");

        try {
            List<Solicitud> solicitudes = solicitudDAO.listarPorUsuario(usuario.getIdUsuario());
            List<Propiedad> propiedades = propiedadDAO.listarTodas(true);
            request.setAttribute("solicitudes", solicitudes);
            request.setAttribute("propiedades", propiedades);
            request.getRequestDispatcher("/cliente/solicitudes.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/cliente/solicitudes.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!esCliente(request, response)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");
        Usuario usuario = (Usuario) request.getSession(false).getAttribute("usuario");

        String accion = request.getParameter("accion");

        try {
            if ("crear".equals(accion)) {
                int idPropiedad = Integer.parseInt(request.getParameter("idPropiedad"));
                String tipo = request.getParameter("tipoSolicitud");
                String observaciones = request.getParameter("observaciones");

                if (tipo == null || (!tipo.equals("COMPRA") && !tipo.equals("ARRIENDO"))) {
                    response.sendRedirect(request.getContextPath() + "/SolicitudServlet?error=true");
                    return;
                }
                solicitudDAO.crear(usuario.getIdUsuario(), idPropiedad, tipo, observaciones);
                auditoriaDAO.registrar(usuario.getIdUsuario(), "RADICAR", "SOLICITUD", idPropiedad,
                        "Radicó solicitud de " + tipo.toLowerCase() + " para propiedad #" + idPropiedad,
                        request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/SolicitudServlet?creada=true");
                return;
            }

            if ("documento".equals(accion)) {
                int idSolicitud;
                try {
                    idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));
                } catch (Exception e) {
                    response.sendRedirect(request.getContextPath() + "/SolicitudServlet?error=true");
                    return;
                }

                if (!solicitudDAO.perteneceAUsuario(idSolicitud, usuario.getIdUsuario())) {
                    response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
                    return;
                }

                Part archivo = request.getPart("archivo");
                String tipoDocumento = request.getParameter("tipoDocumento");

                if (archivo == null || archivo.getSize() == 0) {
                    response.sendRedirect(request.getContextPath() + "/SolicitudServlet?docError=true");
                    return;
                }

                String nombreOriginal = Paths.get(archivo.getSubmittedFileName()).getFileName().toString();
                String extension = nombreOriginal.contains(".")
                        ? nombreOriginal.substring(nombreOriginal.lastIndexOf('.')).toLowerCase()
                        : "";
                if (!extension.matches("\\.(pdf|jpg|jpeg|png|docx?)$")) {
                    response.sendRedirect(request.getContextPath() + "/SolicitudServlet?docTipoError=true");
                    return;
                }

                String nombreUnico = "doc_" + System.currentTimeMillis() + extension;
                String rutaCarpeta = exportPath();
                File carpeta = new File(rutaCarpeta);
                if (!carpeta.exists()) {
                    carpeta.mkdirs();
                }
                String rutaArchivo = rutaCarpeta + File.separator + nombreUnico;
                archivo.write(rutaArchivo);

                String webPath = "uploads/" + nombreUnico;
                solicitudDAO.insertarDocumento(idSolicitud, nombreOriginal, webPath,
                        tipoDocumento != null ? tipoDocumento : "Documento");
                auditoriaDAO.registrar(usuario.getIdUsuario(), "RADICAR", "DOCUMENTO", idSolicitud,
                        "Radicó documento a la solicitud #" + idSolicitud, request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/SolicitudServlet?docOk=true");
                return;
            }

            if ("cancelar".equals(accion)) {
                int idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));
                if (solicitudDAO.perteneceAUsuario(idSolicitud, usuario.getIdUsuario())) {
                    solicitudDAO.actualizarEstado(idSolicitud, "ANULADA");
                    auditoriaDAO.registrar(usuario.getIdUsuario(), "ANULAR", "SOLICITUD", idSolicitud,
                            "Anuló la solicitud #" + idSolicitud, request.getRemoteAddr());
                }
                response.sendRedirect(request.getContextPath() + "/SolicitudServlet?anulada=true");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/SolicitudServlet");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/SolicitudServlet?error=true");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/SolicitudServlet?error=true");
        }
    }

    private boolean esCliente(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }
        Object roles = session.getAttribute("roles");
        if (roles == null || !((java.util.List<String>) roles).contains("CLIENTE")) {
            response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
            return false;
        }
        return true;
    }

    private String exportPath() throws ServletException {
        String realPath = getServletContext().getRealPath("/uploads");
        if (realPath == null) {
            throw new ServletException("No se pudo resolver el directorio de subida.");
        }
        return realPath;
    }
}