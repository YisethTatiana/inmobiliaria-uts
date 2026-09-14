<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.inmobiliaria.modelo.Propiedad" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%
    request.setAttribute("titulo", "Gesti\u00F3n de Propiedades");
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    String ctx = request.getContextPath();
    if (usuario == null) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }
    if (!usuario.tieneRol("ADMINISTRADOR")) {
        response.sendRedirect(ctx + "/acceso_denegado.jsp");
        return;
    }
    List<Propiedad> propiedades = (List<Propiedad>) request.getAttribute("propiedades");
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Gesti&oacute;n de Propiedades</h2>
        <div>
            <a href="<%= ctx %>/admin/dashboard_admin.jsp" class="btn btn-outline-secondary btn-sm">&larr; Panel admin</a>
            <a href="<%= ctx %>/admin/crear_propiedad.jsp" class="btn btn-primary btn-sm">Crear Propiedad</a>
            <a href="<%= ctx %>/AdminPropiedadServlet" class="btn btn-outline-secondary btn-sm">Refrescar</a>
        </div>
    </div>

    <% if ("true".equals(request.getParameter("creado"))) { %>
        <div class="alert alert-success">Propiedad creada correctamente.</div>
    <% } else if ("true".equals(request.getParameter("actualizado"))) { %>
        <div class="alert alert-success">Propiedad actualizada correctamente.</div>
    <% } else if ("true".equals(request.getParameter("eliminado"))) { %>
        <div class="alert alert-success">Propiedad eliminada correctamente.</div>
    <% } else if ("true".equals(request.getParameter("eliminarError"))) { %>
        <div class="alert alert-danger">No se pudo eliminar la propiedad porque tiene registros asociados.</div>
    <% } else if ("true".equals(request.getParameter("noEncontrada"))) { %>
        <div class="alert alert-warning">La propiedad solicitada no existe.</div>
    <% } %>

    <div class="card shadow-sm">
        <div class="card-body">
            <% if (propiedades == null || propiedades.isEmpty()) { %>
                <p class="text-muted text-center mb-0">No hay propiedades registradas.</p>
            <% } else { %>
                <div class="table-responsive">
                    <table class="table table-striped align-middle small tabla-sm responsive-cards">
                        <thead class="table-dark">
                            <tr>
                                <th>#</th>
                                <th>Inmueble</th>
                                <th>Precio</th>
                                <th>Ciudad / Tipo</th>
                                <th>Inmobiliaria</th>
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
                                            <span><%= p.getTitulo() %><br>
                                                <small class="text-muted"><%= p.getDireccion() != null ? p.getDireccion() : "" %></small>
                                            </span>
                                        </div>
                                    </td>
                                    <td data-label="Precio">$ <%= String.format("%,.0f", p.getPrecio()) %></td>
                                    <td data-label="Ciudad/Tipo"><%= p.getNombreCiudad() %><br><small class="text-muted"><%= p.getNombreTipo() %></small></td>
                                    <td data-label="Inmobiliaria"><%= p.getNombreInmobiliaria() %></td>
                                    <td data-label="Estado"><span class="badge <%= badge %>"><%= p.getEstado() %></span></td>
                                    <td data-label="Operación">
                                        <span class="badge <%= "ARRIENDO".equals(p.getOperacion()) ? "bg-info text-dark" : "bg-primary" %>">
                                            <%= "ARRIENDO".equals(p.getOperacion()) ? "En arriendo" : "En venta" %>
                                        </span>
                                    </td>
                                    <td data-label="Acciones" class="text-nowrap">
                                        <a href="<%= ctx %>/DetallePropiedadServlet?id=<%= p.getIdPropiedad() %>" class="btn btn-sm btn-outline-info">Ficha</a>
                                        <a href="<%= ctx %>/EditarPropiedadServlet?id=<%= p.getIdPropiedad() %>" class="btn btn-sm btn-outline-primary">Editar</a>
                                        <a href="<%= ctx %>/EliminarPropiedadServlet?id=<%= p.getIdPropiedad() %>" class="btn btn-sm btn-outline-danger"
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