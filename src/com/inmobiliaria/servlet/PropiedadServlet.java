package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.CatalogoDAO;
import com.inmobiliaria.dao.FavoritoDAO;
import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.modelo.Propiedad;
import com.inmobiliaria.modelo.Usuario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/PropiedadServlet")
public class PropiedadServlet extends HttpServlet {

    private PropiedadDAO propiedadDAO = new PropiedadDAO();
    private FavoritoDAO favoritoDAO = new FavoritoDAO();
    private CatalogoDAO catalogoDAO = new CatalogoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {

            String texto = request.getParameter("texto");
            String ciudad = request.getParameter("ciudad");
            String tipo = request.getParameter("tipo");
            String precioMin = request.getParameter("precioMin");
            String precioMax = request.getParameter("precioMax");

            Integer idCiudad = null;
            Integer idTipo = null;
            if (ciudad != null && ciudad.matches("\\d+")) {
                idCiudad = Integer.parseInt(ciudad);
            }
            if (tipo != null && tipo.matches("\\d+")) {
                idTipo = Integer.parseInt(tipo);
            }

            List<Propiedad> lista;
            if (texto == null && idCiudad == null && idTipo == null
                    && (precioMin == null || precioMin.isEmpty())
                    && (precioMax == null || precioMax.isEmpty())) {
                lista = propiedadDAO.listarTodas(true);
            } else {
                lista = propiedadDAO.buscarConFiltros(texto, idCiudad, idTipo, precioMin, precioMax, "DISPONIBLE", null);
            }
            request.setAttribute("propiedades", lista);

            HttpSession session = request.getSession(false);
            Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;
            if (usuario != null) {
                List<Integer> favs = favoritoDAO.idsFavoritos(usuario.getIdUsuario());
                request.setAttribute("idsFavoritos", favs);
            } else {
                request.setAttribute("idsFavoritos", new ArrayList<Integer>());
            }

            List<Map<String, Object>> ciudades = catalogoDAO.listarCiudades();
            List<Map<String, Object>> tipos = catalogoDAO.listarTipos();
            request.setAttribute("ciudades", ciudades);
            request.setAttribute("tipos", tipos);

            request.setAttribute("filtroTexto", texto == null ? "" : texto);
            request.setAttribute("filtroCiudad", ciudad == null ? "" : ciudad);
            request.setAttribute("filtroTipo", tipo == null ? "" : tipo);
            request.setAttribute("filtroPrecioMin", precioMin == null ? "" : precioMin);
            request.setAttribute("filtroPrecioMax", precioMax == null ? "" : precioMax);

            request.getRequestDispatcher("cliente/catalogo.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("cliente/dashboard_cliente.jsp?error=true");
        }
    }
}