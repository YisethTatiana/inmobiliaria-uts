package com.inmobiliaria.servlet;

import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.modelo.Propiedad;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DetallePropiedadServlet")
public class DetallePropiedadServlet extends HttpServlet {

    private PropiedadDAO propiedadDAO = new PropiedadDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || !idParam.matches("\\d+")) {
            response.sendRedirect(request.getContextPath() + "/PropiedadServlet");
            return;
        }

        try {
            Propiedad propiedad = propiedadDAO.buscarPorId(Integer.parseInt(idParam));
            if (propiedad == null) {
                response.sendRedirect(request.getContextPath() + "/PropiedadServlet?noEncontrada=true");
                return;
            }
            request.setAttribute("propiedad", propiedad);
            request.getRequestDispatcher("/cliente/detalle_propiedad.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/PropiedadServlet?error=true");
        }
    }
}