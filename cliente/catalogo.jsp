<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="com.inmobiliaria.modelo.Propiedad" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%
    request.setAttribute("titulo", "Cat\u00E1logo");
    List<Propiedad> propiedades = (List<Propiedad>) request.getAttribute("propiedades");
    List<Map<String, Object>> ciudades = (List<Map<String, Object>>) request.getAttribute("ciudades");
    List<Map<String, Object>> tipos = (List<Map<String, Object>>) request.getAttribute("tipos");
    List<Integer> idsFavoritos = (List<Integer>) request.getAttribute("idsFavoritos");
    Set<Integer> misFavs = new HashSet<Integer>();
    if (idsFavoritos != null) misFavs.addAll(idsFavoritos);

    Usuario cUsuario = (Usuario) session.getAttribute("usuario");
    boolean puedeAgendar = (cUsuario != null && cUsuario.tieneRol("CLIENTE"));
    String ctx = request.getContextPath();
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <% if (request.getParameter("favorito") != null) { %>
        <div class="alert alert-success">Favorito actualizado.</div>
    <% } if (request.getParameter("citaOk") != null) { %>
        <div class="alert alert-success">Cita agendada correctamente.</div>
    <% } if (request.getParameter("citaDuplicada") != null) { %>
        <div class="alert alert-warning">Ya tienes una cita programada para esa propiedad en la misma fecha y hora.</div>
    <% } else if (request.getParameter("citaError") != null) { %>
        <div class="alert alert-danger">No se pudo agendar la cita. Int&#233;ntalo nuevamente.</div>
    <% } %>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="mb-0">Cat&#225;logo de Inmuebles</h2>
        <% if (cUsuario != null) { %>
            <a href="<%= ctx %>/PerfilServlet" class="btn btn-secondary btn-sm">Mi Perfil</a>
        <% } %>
    </div>

    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <form action="<%= ctx %>/PropiedadServlet" method="get" class="row g-2">
                <div class="col-md-4">
                    <input type="text" name="texto" class="form-control"
                           value="<%= request.getAttribute("filtroTexto") != null ? request.getAttribute("filtroTexto") : "" %>"
                           placeholder="Nombre o direcci&#243;n...">
                </div>
                <div class="col-md-2">
                    <select name="ciudad" class="form-select">
                        <option value="">Todas las ciudades</option>
                        <% if (ciudades != null) for (Map<String, Object> c : ciudades) {
                            String sel = c.get("id").toString().equals(String.valueOf(request.getAttribute("filtroCiudad"))) ? "selected" : ""; %>
                            <option value="<%= c.get("id") %>" <%= sel %>><%= c.get("nombre") %></option>
                        <% } %>
                    </select>
                </div>
                <div class="col-md-2">
                    <select name="tipo" class="form-select">
                        <option value="">Todo tipo</option>
                        <% if (tipos != null) for (Map<String, Object> t : tipos) {
                            String sel = t.get("id").toString().equals(String.valueOf(request.getAttribute("filtroTipo"))) ? "selected" : ""; %>
                            <option value="<%= t.get("id") %>" <%= sel %>><%= t.get("nombre") %></option>
                        <% } %>
                    </select>
                </div>
                <div class="col-md-2">
                    <input type="number" name="precioMin" class="form-control" min="0" step="100000"
                           value="<%= request.getAttribute("filtroPrecioMin") != null ? request.getAttribute("filtroPrecioMin") : "" %>"
                           placeholder="Precio m&#237;n.">
                </div>
                <div class="col-md-2">
                    <input type="number" name="precioMax" class="form-control" min="0" step="100000"
                           value="<%= request.getAttribute("filtroPrecioMax") != null ? request.getAttribute("filtroPrecioMax") : "" %>"
                           placeholder="Precio m&#225;x.">
                </div>
                <div class="col-12 d-grid d-md-flex justify-content-md-end gap-2 mt-2">
                    <a href="<%= ctx %>/PropiedadServlet" class="btn btn-outline-secondary btn-sm">Limpiar</a>
                    <button type="submit" class="btn btn-primary btn-sm px-4">Filtrar</button>
                </div>
            </form>
        </div>
    </div>

    <% if (cUsuario != null && (cUsuario.tieneRol("INMOBILIARIA") || cUsuario.tieneRol("CLIENTE"))) { %>
        <div class="text-muted small mb-3"><strong><%= propiedades != null ? propiedades.size() : 0 %></strong> resultado(s)</div>
    <% } %>

    <div class="row g-4">
        <% if (propiedades == null || propiedades.isEmpty()) { %>
            <div class="col-12 text-center py-5">
                <p class="lead text-muted">No hay propiedades que coincidan con los filtros aplicados.</p>
            </div>
        <% } else {
            for (Propiedad p : propiedades) { %>
            <div class="col-md-6 col-lg-4">
                <div class="card card-propiedad h-100 shadow-sm">
                    <img class="card-img-top" alt="<%= p.getTitulo() %>"
                         src="<%= !p.getImagenPrincipal().isEmpty()
                                ? p.getImagenPrincipal()
                                : "https://picsum.photos/seed/prop" + p.getIdPropiedad() + "/800/500" %>">
                    <div class="card-body d-flex flex-column">
                        <div class="d-flex justify-content-between align-items-start">
                            <h5 class="card-title mb-1"><%= p.getTitulo() %></h5>
                            <span class="badge bg-<%= "DISPONIBLE".equals(p.getEstado()) ? "success" : "secondary" %>"><%= p.getEstado() %></span>
                        </div>
                        <p class="text-muted small mb-1">
                            <%= p.getNombreCiudad() %> &middot; <%= p.getNombreTipo() %>
                        </p>
                        <p class="card-text text-muted small flex-grow-1">
                            <%= p.getDescripcion() != null && p.getDescripcion().length() > 120
                                    ? p.getDescripcion().substring(0, 120) + "\u2026" : p.getDescripcion() %>
                        </p>
                        <h6 class="fw-bold text-primary">$ <%= String.format("%,.0f", p.getPrecio()) %></h6>
                        <p class="small text-secondary mb-2">
                            <% if (p.getHabitaciones() > 0) { %><span class="me-2"><i class="bi bi-bed"></i> <%= p.getHabitaciones() %> hab.</span><% } %>
                            <% if (p.getBanios() > 0) { %><span class="me-2"><i class="bi bi-droplet"></i> <%= p.getBanios() %> ba&#241;os</span><% } %>
                            <% if (p.getArea() != null && p.getArea().doubleValue() > 0) { %><span><i class="bi bi-rulers"></i> <%= String.format("%,.0f", p.getArea()) %> m&sup2;</span><% } %>
                        </p>
                        <div class="d-flex gap-2 mt-auto">
                            <a href="<%= ctx %>/DetallePropiedadServlet?id=<%= p.getIdPropiedad() %>" class="btn btn-outline-primary btn-sm flex-fill">Ver ficha</a>
                            <% if (puedeAgendar) { %>
                                <button type="button" class="btn btn-outline-success btn-sm" data-bs-toggle="modal" data-bs-target="#modalCita<%= p.getIdPropiedad() %>">Cita</button>
                            <% } %>
                            <% if (cUsuario != null && cUsuario.tieneRol("CLIENTE")) { %>
                                <a href="<%= ctx %>/FavoritoServlet?id=<%= p.getIdPropiedad() %>&origen=catalogo"
                                   class="btn btn-sm <%= misFavs.contains(p.getIdPropiedad()) ? "btn-warning" : "btn-outline-warning" %>" title="Favorito">
                                    <%= misFavs.contains(p.getIdPropiedad()) ? "\u2605" : "\u2606" %>
                                </a>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>

            <div class="modal fade" id="modalCita<%= p.getIdPropiedad() %>" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <form action="<%= ctx %>/CitaServlet" method="POST">
                            <div class="modal-header">
                                <h5 class="modal-title">Agendar Visita: <%= p.getTitulo() %></h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <input type="hidden" name="idPropiedad" value="<%= p.getIdPropiedad() %>">
                                <div class="mb-3">
                                    <label class="form-label fw-bold">Fecha y hora:</label>
                                    <input type="datetime-local" name="fechaHora" class="form-control" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Observaciones (opcional):</label>
                                    <textarea name="observaciones" class="form-control" rows="2" placeholder="Ej: Me interesa el inmueble por las tardes"></textarea>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                                <button type="submit" class="btn btn-primary">Confirmar Cita</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        <% } } %>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>