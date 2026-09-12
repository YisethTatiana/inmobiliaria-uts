<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.inmobiliaria.modelo.Propiedad" %>
<%@ page import="com.inmobiliaria.modelo.Caracteristica" %>
<%@ page import="com.inmobiliaria.modelo.ImagenPropiedad" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%
    request.setAttribute("titulo", "Editar Propiedad");
    Usuario u = (Usuario) session.getAttribute("usuario");
    String ctx = request.getContextPath();
    if (u == null || !u.tieneRol("ADMINISTRADOR")) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }
    Propiedad prop = (Propiedad) request.getAttribute("propiedad");
    if (prop == null) {
        response.sendRedirect(ctx + "/AdminPropiedadServlet");
        return;
    }
    List<Map<String, Object>> ciudades = (List<Map<String, Object>>) request.getAttribute("ciudades");
    List<Map<String, Object>> tipos = (List<Map<String, Object>>) request.getAttribute("tipos");
    List<Caracteristica> todas = (List<Caracteristica>) request.getAttribute("caracteristicas");

    Set<Integer> idsAsignadas = new HashSet<Integer>();
    for (Caracteristica c : prop.getCaracteristicas()) {
        idsAsignadas.add(c.getIdCaracteristica());
    }
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Editar Propiedad #<%= prop.getIdPropiedad() %></h2>
        <a href="<%= ctx %>/AdminPropiedadServlet" class="btn btn-outline-secondary btn-sm">&larr; Volver a propiedades</a>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
    <% } %>

    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <h6 class="text-muted mb-3">Im&aacute;genes actuales</h6>
            <div class="d-flex flex-wrap gap-2">
                <% for (ImagenPropiedad img : prop.getImagenes()) { %>
                    <img src="<%= img.getRuta() %>" class="img-sello <%= img.isPrincipal() ? "activa" : "" %>" alt="Imagen" title="<%= img.isPrincipal() ? "Principal" : "" %>">
                <% } %>
                <% if (prop.getImagenes().isEmpty()) { %>
                    <p class="text-muted small mb-0">Sin im&aacute;genes. Cargar una reemplazar&aacute; las existentes.</p>
                <% } %>
            </div>
            <p class="text-muted small mt-2 mb-0">Si seleccionas nuevas fotograf&iacute;as, estas reemplazar&aacute;n todas las actuales.</p>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-body">
            <form action="<%= ctx %>/EditarPropiedadServlet" method="post" enctype="multipart/form-data">
                <input type="hidden" name="id" value="<%= prop.getIdPropiedad() %>">
                <div class="row g-3">
                    <div class="col-md-8">
                        <label class="form-label">T&iacute;tulo *</label>
                        <input type="text" name="titulo" class="form-control" maxlength="100" value="<%= prop.getTitulo() != null ? prop.getTitulo() : "" %>" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Estado</label>
                        <select name="estado" class="form-select">
                            <option value="DISPONIBLE" <%= "DISPONIBLE".equals(prop.getEstado()) ? "selected" : "" %>>Disponible</option>
                            <option value="RESERVADA" <%= "RESERVADA".equals(prop.getEstado()) ? "selected" : "" %>>Reservada</option>
                            <option value="INACTIVO" <%= "INACTIVO".equals(prop.getEstado()) ? "selected" : "" %>>Inactivo</option>
                        </select>
                    </div>
                    <div class="col-12">
                        <label class="form-label">Descripci&oacute;n</label>
                        <textarea name="descripcion" class="form-control" rows="3"><%= prop.getDescripcion() != null ? prop.getDescripcion() : "" %></textarea>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Precio (COP) *</label>
                        <input type="number" name="precio" class="form-control" step="0.01" min="0" value="<%= prop.getPrecio() != null ? prop.getPrecio() : "" %>" required>
                    </div>
                    <div class="col-md-8">
                        <label class="form-label">Direcci&oacute;n *</label>
                        <input type="text" name="direccion" class="form-control" maxlength="150" value="<%= prop.getDireccion() != null ? prop.getDireccion() : "" %>" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">&Aacute;rea (m&sup2;)</label>
                        <input type="number" name="area" class="form-control" step="0.01" min="0" value="<%= prop.getArea() != null ? prop.getArea() : "" %>">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Habitaciones</label>
                        <input type="number" name="habitaciones" class="form-control" min="0" value="<%= prop.getHabitaciones() > 0 ? prop.getHabitaciones() : "" %>">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Ba&ntilde;os</label>
                        <input type="number" name="banios" class="form-control" min="0" value="<%= prop.getBanios() > 0 ? prop.getBanios() : "" %>">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Parqueaderos</label>
                        <input type="number" name="parqueaderos" class="form-control" min="0" value="<%= prop.getParqueaderos() > 0 ? prop.getParqueaderos() : "" %>">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Ciudad *</label>
                        <select name="idCiudad" class="form-select" required>
                            <% if (ciudades != null) for (Map<String, Object> c : ciudades) {
                                boolean sel = c.get("id").toString().equals(String.valueOf(prop.getIdCiudad())); %>
                                <option value="<%= c.get("id") %>" <%= sel ? "selected" : "" %>><%= c.get("nombre") %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Tipo de propiedad *</label>
                        <select name="idTipo" class="form-select" required>
                            <% if (tipos != null) for (Map<String, Object> t : tipos) {
                                boolean sel = t.get("id").toString().equals(String.valueOf(prop.getIdTipo())); %>
                                <option value="<%= t.get("id") %>" <%= sel ? "selected" : "" %>><%= t.get("nombre") %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-12">
                        <label class="form-label">Nuevas fotograf&iacute;as (opcional; reemplazan las actuales)</label>
                        <input type="file" name="imagen" class="form-control" accept=".jpg,.jpeg,.png,.gif,.webp" multiple>
                    </div>
                    <div class="col-12">
                        <label class="form-label">Caracter&iacute;sticas</label>
                        <% if (todas == null || todas.isEmpty()) { %>
                            <p class="text-muted small">No hay caracter&iacute;sticas registradas en el cat&aacute;logo.</p>
                        <% } else { %>
                            <div class="row g-2">
                                <% for (Caracteristica c : todas) { %>
                                    <div class="col-md-3 col-6">
                                        <div class="form-check">
                                            <input class="form-check-input" type="checkbox" name="caracteristicas" value="<%= c.getIdCaracteristica() %>" id="ec<%= c.getIdCaracteristica() %>"
                                                   <%= idsAsignadas.contains(c.getIdCaracteristica()) ? "checked" : "" %>>
                                            <label class="form-check-label" for="ec<%= c.getIdCaracteristica() %>"><%= c.getNombre() %></label>
                                        </div>
                                    </div>
                                <% } %>
                            </div>
                        <% } %>
                    </div>
                    <div class="col-12">
                        <button type="submit" class="btn btn-primary">Actualizar Propiedad</button>
                        <a href="<%= ctx %>/AdminPropiedadServlet" class="btn btn-outline-secondary">Cancelar</a>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>