package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.CaracteristicaDAO;
import com.inmobiliaria.dao.CatalogoDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.modelo.Caracteristica;
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

@WebServlet("/AdminCatalogoServlet")
public class AdminCatalogoServlet extends HttpServlet {

    private CatalogoDAO catalogoDAO = new CatalogoDAO();
    private CaracteristicaDAO caracteristicaDAO = new CaracteristicaDAO();
    private AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    private boolean sesionAdministradorValida(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }
        if (!usuario.tieneRol("ADMINISTRADOR")) {
            response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!sesionAdministradorValida(request, response)) {
            return;
        }

        try {
            List<Map<String, Object>> tipos = catalogoDAO.listarTipos();
            List<Map<String, Object>> ciudades = catalogoDAO.listarCiudades();
            List<Caracteristica> caracteristicas = caracteristicaDAO.listarTodas();

            request.setAttribute("tipos", tipos);
            request.setAttribute("ciudades", ciudades);
            request.setAttribute("caracteristicas", caracteristicas);
            request.getRequestDispatcher("/admin/catalogos.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/dashboard_admin.jsp?error=true");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!sesionAdministradorValida(request, response)) {
            return;
        }
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        String accion = request.getParameter("accion");
        String nombre = request.getParameter("nombre");

        try {
            switch (accion) {
                case "tipoCrear":
                    catalogoDAO.insertarTipo(nombre);
                    redirigirOk(request, response, usuario, "tipo de propiedad", nombre);
                    return;
                case "tipoEditar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    catalogoDAO.actualizarTipo(id, nombre);
                    redirigirOk(request, response, usuario, "tipo de propiedad", nombre);
                    return;
                }
                case "tipoEliminar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    catalogoDAO.eliminarTipo(id);
                    redirigirOk(request, response, usuario, "tipo de propiedad", "id " + id);
                    return;
                }
                case "ciudadCrear":
                    catalogoDAO.insertarCiudad(nombre);
                    redirigirOk(request, response, usuario, "ciudad", nombre);
                    return;
                case "ciudadEditar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    catalogoDAO.actualizarCiudad(id, nombre);
                    redirigirOk(request, response, usuario, "ciudad", nombre);
                    return;
                }
                case "ciudadEliminar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    catalogoDAO.eliminarCiudad(id);
                    redirigirOk(request, response, usuario, "ciudad", "id " + id);
                    return;
                }
                case "caracteristicaCrear":
                    caracteristicaDAO.insertar(nombre);
                    redirigirOk(request, response, usuario, "característica", nombre);
                    return;
                case "caracteristicaEditar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    caracteristicaDAO.actualizar(id, nombre);
                    redirigirOk(request, response, usuario, "característica", nombre);
                    return;
                }
                case "caracteristicaEliminar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    caracteristicaDAO.eliminar(id);
                    redirigirOk(request, response, usuario, "característica", "id " + id);
                    return;
                }
                default:
                    response.sendRedirect(request.getContextPath() + "/AdminCatalogoServlet");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/AdminCatalogoServlet?error=true");
        }
    }

    private void redirigirOk(HttpServletRequest request, HttpServletResponse response, Usuario usuario,
                             String entidad, String detalle) throws IOException {
        auditoriaDAO.registrar(usuario.getIdUsuario(), "CATALOGO", entidad.toUpperCase(),
                null, "Modificación del catálogo: " + entidad + " " + detalle, request.getRemoteAddr());
        response.sendRedirect(request.getContextPath() + "/AdminCatalogoServlet?guardado=true");
    }
}