<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%@ page import="com.inmobiliaria.modelo.Caracteristica" %>
<%@ page import="com.inmobiliaria.dao.CatalogoDAO" %>
<%@ page import="com.inmobiliaria.dao.CaracteristicaDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    request.setAttribute("titulo", "Registrar Propiedad");
    Usuario u = (Usuario) session.getAttribute("usuario");
    String ctx = request.getContextPath();
    if (u == null || !u.tieneRol("ADMINISTRADOR")) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }
    List<Map<String, Object>> ciudades = null;
    List<Map<String, Object>> tipos = null;
    List<Caracteristica> caracteristicas = null;
    try {
        ciudades = new CatalogoDAO().listarCiudades();
        tipos = new CatalogoDAO().listarTipos();
        caracteristicas = new CaracteristicaDAO().listarTodas();
    } catch (Exception e) { e.printStackTrace(); }
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Registrar Propiedad</h2>
        <a href="<%= ctx %>/AdminPropiedadServlet" class="btn btn-outline-secondary btn-sm">&larr; Volver a propiedades</a>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
    <% } %>

    <div class="card shadow-sm">
        <div class="card-body">
            <form action="<%= ctx %>/CrearPropiedadServlet" method="post" enctype="multipart/form-data">
                <div class="row g-3">
                    <div class="col-md-8">
                        <label class="form-label">T&iacute;tulo *</label>
                        <input type="text" name="titulo" class="form-control" maxlength="100" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Matr&iacute;cula inmobiliaria</label>
                        <input type="text" name="matricula" class="form-control" maxlength="50" placeholder="Ej: 123-45-6789">
                    </div>
                    <div class="col-12">
                        <label class="form-label">Descripci&oacute;n</label>
                        <textarea name="descripcion" class="form-control" rows="3"></textarea>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Precio (COP) *</label>
                        <input type="number" name="precio" class="form-control" step="0.01" min="0" required>
                    </div>
                    <div class="col-md-8">
                        <label class="form-label">Direcci&oacute;n *</label>
                        <input type="text" name="direccion" class="form-control" maxlength="150" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">&Aacute;rea (m&sup2;)</label>
                        <input type="number" name="area" class="form-control" step="0.01" min="0">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Habitaciones</label>
                        <input type="number" name="habitaciones" class="form-control" min="0">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Ba&ntilde;os</label>
                        <input type="number" name="banios" class="form-control" min="0">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Parqueaderos</label>
                        <input type="number" name="parqueaderos" class="form-control" min="0">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Ciudad *</label>
                        <select name="idCiudad" class="form-select" required>
                            <option value="">Seleccione...</option>
                            <% if (ciudades != null) for (Map<String, Object> c : ciudades) { %>
                                <option value="<%= c.get("id") %>"><%= c.get("nombre") %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Tipo de propiedad *</label>
                        <select name="idTipo" class="form-select" required>
                            <option value="">Seleccione...</option>
                            <% if (tipos != null) for (Map<String, Object> t : tipos) { %>
                                <option value="<%= t.get("id") %>"><%= t.get("nombre") %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-12">
                        <label class="form-label">Fotograf&iacute;as (JPG, PNG, WEBP; varias permitidas)</label>
                        <input type="file" name="imagen" class="form-control" accept=".jpg,.jpeg,.png,.gif,.webp" multiple>
                    </div>
                    <div class="col-12">
                        <label class="form-label">Caracter&iacute;sticas</label>
                        <% if (caracteristicas == null || caracteristicas.isEmpty()) { %>
                            <p class="text-muted small">No hay caracter&iacute;sticas registradas en el cat&aacute;logo.</p>
                        <% } else { %>
                            <div class="row g-2">
                                <% for (Caracteristica c : caracteristicas) { %>
                                    <div class="col-md-3 col-6">
                                        <div class="form-check">
                                            <input class="form-check-input" type="checkbox" name="caracteristicas" value="<%= c.getIdCaracteristica() %>" id="ca<%= c.getIdCaracteristica() %>">
                                            <label class="form-check-label" for="ca<%= c.getIdCaracteristica() %>"><%= c.getNombre() %></label>
                                        </div>
                                    </div>
                                <% } %>
                            </div>
                        <% } %>
                    </div>
                    <div class="col-12">
                        <button type="submit" class="btn btn-success">Guardar Propiedad</button>
                        <a href="<%= ctx %>/AdminPropiedadServlet" class="btn btn-outline-secondary">Cancelar</a>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>