<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.inmobiliaria.modelo.Propiedad" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%
    request.setAttribute("titulo", "Mis Propiedades");
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    String ctx = request.getContextPath();
    if (usuario == null) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }
    if (!usuario.tieneRol("INMOBILIARIA") && !usuario.tieneRol("ADMINISTRADOR")) {
        response.sendRedirect(ctx + "/acceso_denegado.jsp");
        return;
    }
    List<Propiedad> propiedades = (List<Propiedad>) request.getAttribute("propiedades");
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Mis Propiedades</h2>
        <div>
            <a href="<%= ctx %>/agente/dashboard_inmobiliaria.jsp" class="btn btn-outline-secondary btn-sm">&larr; Panel principal</a>
            <a href="<%= ctx %>/AgentePropiedadServlet" class="btn btn-outline-secondary btn-sm">Refrescar</a>
        </div>
    </div>

    <% if ("true".equals(request.getParameter("creada"))) { %>
        <div class="alert alert-success">Propiedad publicada correctamente.</div>
    <% } else if ("true".equals(request.getParameter("actualizada"))) { %>
        <div class="alert alert-success">Propiedad actualizada correctamente.</div>
    <% } else if ("true".equals(request.getParameter("darBaja"))) { %>
        <div class="alert alert-success">Propiedad dada de baja.</div>
    <% } else if ("true".equals(request.getParameter("reactivada"))) { %>
        <div class="alert alert-success">Propiedad reactivada.</div>
    <% } else if ("true".equals(request.getParameter("eliminada"))) { %>
        <div class="alert alert-success">Propiedad eliminada correctamente.</div>
    <% } else if ("true".equals(request.getParameter("error"))) { %>
        <div class="alert alert-danger">Ocurri&oacute; un error al procesar la solicitud.</div>
    <% } %>

    <div class="card shadow-sm">
        <div class="card-body">
            <% if (propiedades == null || propiedades.isEmpty()) { %>
                <p class="text-muted text-center mb-0">No tienes propiedades registradas. Publica la primera.</p>
            <% } else { %>
                <div class="table-responsive">
                    <table class="table table-striped align-middle small tabla-sm responsive-cards">
                        <thead class="table-dark">
                            <tr>
                                <th>#</th>
                                <th>Inmueble</th>
                                <th>Precio</th>
                                <th>Ciudad / Tipo</th>
                                <th>Estado</th>
                                <th>Operaci&oacute;n</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Propiedad p : propiedades) {
                                String badge = "DISPONIBLE".equals(p.getEstado()) ? "bg-success"
                                             : "RESERVADA".equals(p.getEstado()) ? "bg-warning text-dark" : "bg-secondary"; %>
                                <tr>
                                    <td data-label="ID"><%= p.getIdPropiedad() %></td>
                                    <td data-label="Inmueble">
                                        <div class="d-flex align-items-center gap-2">
                                            <img src="<%= !p.getImagenPrincipal().isEmpty() ? p.getImagenPrincipal() : "https://images.unsplash.com/photo-1568605114967-8130f3a36994?auto=format&fit=crop&w=60&h=60&q=60" %>"
                                                 class="rounded" width="50" height="50" style="object-fit:cover;" alt="">
                                            <span><%= p.getTitulo() %></span>
                                        </div>
                                    </td>
                                    <td data-label="Precio">$ <%= String.format("%,.0f", p.getPrecio()) %></td>
                                    <td data-label="Ciudad/Tipo"><%= p.getNombreCiudad() %><br><small class="text-muted"><%= p.getNombreTipo() %></small></td>
                                    <td data-label="Estado"><span class="badge <%= badge %>"><%= p.getEstado() %></span></td>
                                    <td data-label="Operación">
                                        <span class="badge <%= "ARRIENDO".equals(p.getOperacion()) ? "bg-info text-dark" : "bg-primary" %>">
                                            <%= "ARRIENDO".equals(p.getOperacion()) ? "En arriendo" : "En venta" %>
                                        </span>
                                    </td>
                                    <td data-label="Acciones" class="text-nowrap">
                                        <a href="<%= ctx %>/AgentePropiedadServlet?accion=editar&id=<%= p.getIdPropiedad() %>" class="btn btn-sm btn-outline-primary">Editar</a>
                                        <% if ("DISPONIBLE".equals(p.getEstado())) { %>
                                            <a href="<%= ctx %>/AgentePropiedadServlet?accion=darBaja&id=<%= p.getIdPropiedad() %>" class="btn btn-sm btn-outline-warning"
                                               onclick="return confirm('&#191;Dar de baja esta propiedad? Se ocultar&aacute; del cat&aacute;logo.');">Baja</a>
                                        <% } else { %>
                                            <a href="<%= ctx %>/AgentePropiedadServlet?accion=reactivar&id=<%= p.getIdPropiedad() %>" class="btn btn-sm btn-outline-success">Reactivar</a>
                                        <% } %>
                                        <a href="<%= ctx %>/AgentePropiedadServlet?accion=eliminar&id=<%= p.getIdPropiedad() %>" class="btn btn-sm btn-outline-danger"
                                           onclick="return confirm('&#191;Eliminar definitivamente esta propiedad?');">Eliminar</a>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>