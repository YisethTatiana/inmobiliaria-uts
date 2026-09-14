<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.inmobiliaria.modelo.Propiedad" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%
    request.setAttribute("titulo", "Mis Favoritos");
    Usuario u = (Usuario) session.getAttribute("usuario");
    String ctx = request.getContextPath();
    if (u == null) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }
    List<Propiedad> favoritos = (List<Propiedad>) request.getAttribute("favoritos");
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Mis Propiedades Favoritas</h2>
        <div>
            <a href="<%= ctx %>/PropiedadServlet" class="btn btn-primary btn-sm">Ver Cat&aacute;logo</a>
            <a href="<%= ctx %>/cliente/dashboard_cliente.jsp" class="btn btn-outline-secondary btn-sm">Panel</a>
        </div>
    </div>

    <div class="row g-4">
        <% if (favoritos != null && !favoritos.isEmpty()) {
            for (Propiedad p : favoritos) { %>
                <div class="col-md-6 col-lg-4">
                    <div class="card card-propiedad h-100 shadow-sm">
                        <img src="<%= !p.getImagenPrincipal().isEmpty() ? p.getImagenPrincipal() : "https://images.unsplash.com/photo-1568605114967-8130f3a36994?auto=format&fit=crop&w=800&h=500&q=60" %>"
                             class="card-img-top" alt="<%= p.getTitulo() %>">
                        <div class="card-body d-flex flex-column">
                            <div class="d-flex justify-content-between align-items-start">
                                <h5 class="card-title"><%= p.getTitulo() %></h5>
                                <span class="badge <%= "ARRIENDO".equals(p.getOperacion()) ? "bg-info text-dark" : "bg-primary" %>"><%= "ARRIENDO".equals(p.getOperacion()) ? "En arriendo" : "En venta" %></span>
                            </div>
                            <p class="text-muted small"><%= p.getNombreCiudad() %> &middot; <%= p.getNombreTipo() %></p>
                            <p class="card-text text-muted small flex-grow-1">
                                <%= p.getDescripcion() != null && p.getDescripcion().length() > 120
                                        ? p.getDescripcion().substring(0, 120) + "\u2026" : p.getDescripcion() %>
                            </p>
                            <h6 class="fw-bold text-success">$ <%= String.format("%,.0f", p.getPrecio()) %></h6>
                            <div class="d-flex gap-2 mt-2">
                                <a href="<%= ctx %>/DetallePropiedadServlet?id=<%= p.getIdPropiedad() %>" class="btn btn-outline-primary btn-sm flex-fill">Ver ficha</a>
                                <a href="<%= ctx %>/FavoritoServlet?id=<%= p.getIdPropiedad() %>&origen=favoritos" class="btn btn-warning btn-sm">&#9733; Quitar</a>
                            </div>
                        </div>
                    </div>
                </div>
        <% }
        } else { %>
            <div class="col-12 text-center py-5">
                <p class="lead text-muted">A&uacute;n no tienes propiedades favoritas.</p>
                <a href="<%= ctx %>/PropiedadServlet" class="btn btn-primary">Explorar Cat&aacute;logo</a>
            </div>
        <% } %>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>