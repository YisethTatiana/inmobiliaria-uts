<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%@ page import="com.inmobiliaria.modelo.Caracteristica" %>
<%
    request.setAttribute("titulo", "Cat\u00E1logos");
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
    List<Map<String, Object>> tipos = (List<Map<String, Object>>) request.getAttribute("tipos");
    List<Map<String, Object>> ciudades = (List<Map<String, Object>>) request.getAttribute("ciudades");
    List<Caracteristica> caracteristicas = (List<Caracteristica>) request.getAttribute("caracteristicas");
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Cat&aacute;logos y Parametrizaci&oacute;n</h2>
        <a href="<%= ctx %>/admin/dashboard_admin.jsp" class="btn btn-outline-secondary btn-sm">&larr; Panel admin</a>
    </div>

    <% if ("true".equals(request.getParameter("guardado"))) { %>
        <div class="alert alert-success">Registro guardado correctamente.</div>
    <% } else if ("true".equals(request.getParameter("error"))) { %>
        <div class="alert alert-danger">No se pudo completar la operaci&oacute;n. Verifica que el registro no est&eacute; en uso.</div>
    <% } %>

    <div class="row g-4">
        <div class="col-lg-6">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-white"><strong class="text-primary">Tipos de Propiedad</strong></div>
                <div class="card-body">
                    <form action="<%= ctx %>/AdminCatalogoServlet" method="post" class="d-flex gap-2 mb-3">
                        <input type="hidden" name="accion" value="tipoCrear">
                        <input type="text" name="nombre" class="form-control form-control-sm" placeholder="Nombre del tipo (ej. Apartamento)" maxlength="50" required>
                        <button type="submit" class="btn btn-success btn-sm flex-shrink-0">Agregar</button>
                    </form>
                    <div class="table-responsive">
                        <table class="table table-sm align-middle tabla-sm">
                            <thead><tr><th>#</th><th>Nombre</th><th></th></tr></thead>
                            <tbody>
                                <% if (tipos != null) for (Map<String, Object> t : tipos) { %>
                                    <tr>
                                        <td><%= t.get("id") %></td>
                                        <td>
                                            <form action="<%= ctx %>/AdminCatalogoServlet" method="post" class="d-flex gap-2">
                                                <input type="hidden" name="accion" value="tipoEditar">
                                                <input type="hidden" name="id" value="<%= t.get("id") %>">
                                                <input type="text" name="nombre" class="form-control form-control-sm" value="<%= t.get("nombre") %>" maxlength="50" required>
                                                <button type="submit" class="btn btn-sm btn-primary">Guardar</button>
                                            </form>
                                        </td>
                                        <td>
                                            <form action="<%= ctx %>/AdminCatalogoServlet" method="post">
                                                <input type="hidden" name="accion" value="tipoEliminar">
                                                <input type="hidden" name="id" value="<%= t.get("id") %>">
                                                <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('&#191;Eliminar este tipo de propiedad?');">Eliminar</button>
                                            </form>
                                        </td>
                                    </tr>
                                <% } else { %>
                                    <tr><td colspan="3" class="text-muted small">Sin tipos registrados.</td></tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-lg-6">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-white"><strong class="text-success">Ciudades</strong></div>
                <div class="card-body">
                    <form action="<%= ctx %>/AdminCatalogoServlet" method="post" class="d-flex gap-2 mb-3">
                        <input type="hidden" name="accion" value="ciudadCrear">
                        <input type="text" name="nombre" class="form-control form-control-sm" placeholder="Nombre de la ciudad" maxlength="100" required>
                        <button type="submit" class="btn btn-success btn-sm flex-shrink-0">Agregar</button>
                    </form>
                    <div class="table-responsive">
                        <table class="table table-sm align-middle tabla-sm">
                            <thead><tr><th>#</th><th>Nombre</th><th></th></tr></thead>
                            <tbody>
                                <% if (ciudades != null) for (Map<String, Object> c : ciudades) { %>
                                    <tr>
                                        <td><%= c.get("id") %></td>
                                        <td>
                                            <form action="<%= ctx %>/AdminCatalogoServlet" method="post" class="d-flex gap-2">
                                                <input type="hidden" name="accion" value="ciudadEditar">
                                                <input type="hidden" name="id" value="<%= c.get("id") %>">
                                                <input type="text" name="nombre" class="form-control form-control-sm" value="<%= c.get("nombre") %>" maxlength="100" required>
                                                <button type="submit" class="btn btn-sm btn-primary">Guardar</button>
                                            </form>
                                        </td>
                                        <td>
                                            <form action="<%= ctx %>/AdminCatalogoServlet" method="post">
                                                <input type="hidden" name="accion" value="ciudadEliminar">
                                                <input type="hidden" name="id" value="<%= c.get("id") %>">
                                                <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('&#191;Eliminar esta ciudad?');">Eliminar</button>
                                            </form>
                                        </td>
                                    </tr>
                                <% } else { %>
                                    <tr><td colspan="3" class="text-muted small">Sin ciudades registradas.</td></tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="card shadow-sm mt-4">
        <div class="card-header bg-white"><strong class="text-info">Caracter&iacute;sticas</strong></div>
        <div class="card-body">
            <p class="text-muted small">Las caracter&iacute;sticas se asocian a cada propiedad (ascensor, piscina, seguridad 24h, etc.).</p>
            <form action="<%= ctx %>/AdminCatalogoServlet" method="post" class="d-flex gap-2 mb-3 col-md-6 col-lg-4">
                <input type="hidden" name="accion" value="caracteristicaCrear">
                <input type="text" name="nombre" class="form-control form-control-sm" placeholder="Nombre de la caracter&iacute;stica (ej. Piscina)" maxlength="50" required>
                <button type="submit" class="btn btn-success btn-sm flex-shrink-0">Agregar</button>
            </form>
            <div class="table-responsive">
                <table class="table table-sm align-middle tabla-sm">
                    <thead><tr><th>#</th><th>Nombre</th><th></th></tr></thead>
                    <tbody>
                        <% if (caracteristicas != null && !caracteristicas.isEmpty()) {
                            for (Caracteristica c : caracteristicas) { %>
                                <tr>
                                    <td><%= c.getIdCaracteristica() %></td>
                                    <td>
                                        <form action="<%= ctx %>/AdminCatalogoServlet" method="post" class="d-flex gap-2">
                                            <input type="hidden" name="accion" value="caracteristicaEditar">
                                            <input type="hidden" name="id" value="<%= c.getIdCaracteristica() %>">
                                            <input type="text" name="nombre" class="form-control form-control-sm" value="<%= c.getNombre() %>" maxlength="50" required>
                                            <button type="submit" class="btn btn-sm btn-primary">Guardar</button>
                                        </form>
                                    </td>
                                    <td>
                                        <form action="<%= ctx %>/AdminCatalogoServlet" method="post">
                                            <input type="hidden" name="accion" value="caracteristicaEliminar">
                                            <input type="hidden" name="id" value="<%= c.getIdCaracteristica() %>">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('&#191;Eliminar esta caracter&iacute;stica?');">Eliminar</button>
                                        </form>
                                    </td>
                                </tr>
                        <% }
                        } else { %>
                            <tr><td colspan="3" class="text-muted small">Sin caracter&iacute;sticas registradas.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>