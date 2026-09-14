<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="com.inmobiliaria.modelo.Propiedad" %>
<%@ page import="com.inmobiliaria.modelo.ImagenPropiedad" %>
<%@ page import="com.inmobiliaria.modelo.Caracteristica" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%@ page import="java.util.List" %>
<%
    request.setAttribute("titulo", "Detalle de propiedad");
    Propiedad p = (Propiedad) request.getAttribute("propiedad");
    Usuario u = (Usuario) session.getAttribute("usuario");
    String ctx = request.getContextPath();
    if (p == null) {
        response.sendRedirect(ctx + "/PropiedadServlet");
        return;
    }
    boolean puedeAgendar = (u != null && u.tieneRol("CLIENTE"));
    boolean veContacto = (u != null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <% if (request.getParameter("citaOk") != null) { %>
        <div class="alert alert-success">Cita agendada correctamente.</div>
    <% } else if (request.getParameter("citaDuplicada") != null) { %>
        <div class="alert alert-warning">Ya tienes una cita programada para esta propiedad en la misma fecha y hora.</div>
    <% } else if (request.getParameter("citaError") != null) { %>
        <div class="alert alert-danger">No se pudo agendar la cita. Verifica los datos.</div>
    <% } else if (request.getParameter("favorito") != null) { %>
        <div class="alert alert-success">Favorito actualizado.</div>
    <% } %>

    <div class="d-flex justify-content-between align-items-center mb-2">
        <a href="javascript:history.back()" class="btn btn-outline-secondary btn-sm">&larr; Volver</a>
        <a href="<%= ctx %>/PropiedadServlet" class="btn btn-outline-primary btn-sm">Ver cat&aacute;logo</a>
    </div>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="<%= ctx %>/PropiedadServlet">Cat&#225;logo</a></li>
            <li class="breadcrumb-item active"><%= p.getTitulo() != null ? p.getTitulo() : "Ficha" %></li>
        </ol>
    </nav>

    <div class="card shadow-sm">
        <div class="card-body">
            <div class="row g-4">
                <div class="col-lg-7">
                    <img id="imagenPrincipal" class="img-detalle-principal w-100"
                         src="<%= !p.getImagenPrincipal().isEmpty() ? p.getImagenPrincipal() : "https://images.unsplash.com/photo-1568605114967-8130f3a36994?auto=format&fit=crop&w=1200&h=700&q=60" %>"
                         alt="<%= p.getTitulo() %>">
                    <div class="d-flex gap-2 mt-2 mt-3">
                        <% int imgCount = 0;
                           for (ImagenPropiedad img : p.getImagenes()) {
                               imgCount++; if (imgCount > 5) break; %>
                            <img class="img-sello <%= img.isPrincipal() || imgCount == 1 ? "activa" : "" %>"
                                 src="<%= img.getRuta() %>" alt="Miniatura"
                                 onclick="cambiarImagen(this, '<%= img.getRuta().replace("'", "\\'") %>')">
                        <% } %>
                    </div>
                </div>
                <div class="col-lg-5 d-flex flex-column">
                    <div class="d-flex justify-content-between align-items-start">
                        <h2 class="mb-1"><%= p.getTitulo() %></h2>
                        <div class="d-flex flex-column align-items-end gap-1">
                            <span class="badge <%= "ARRIENDO".equals(p.getOperacion()) ? "bg-info text-dark" : "bg-primary" %>"><%= "ARRIENDO".equals(p.getOperacion()) ? "En arriendo" : "En venta" %></span>
                            <span class="badge bg-<%= "DISPONIBLE".equals(p.getEstado()) ? "success" : "secondary" %> estado-badge"><%= p.getEstado() %></span>
                        </div>
                    </div>
                    <p class="text-muted mb-2">
                        <%= p.getNombreCiudad() %> &middot; <%= p.getNombreTipo() %><br>
                        <small><i class="bi bi-geo-alt"></i> <%= p.getDireccion() %></small>
                    </p>
                    <h3 class="text-primary fw-bold">$ <%= String.format("%,.0f", p.getPrecio()) %></h3>
                    <ul class="list-inline">
                        <% if (p.getHabitaciones() > 0) { %><li class="list-inline-item badge bg-secondary"><i class="bi bi-bed"></i> <%= p.getHabitaciones() %> hab.</li><% } %>
                        <% if (p.getBanios() > 0) { %><li class="list-inline-item badge bg-secondary"><i class="bi bi-droplet"></i> <%= p.getBanios() %> ba&#241;os</li><% } %>
                        <% if (p.getParqueaderos() > 0) { %><li class="list-inline-item badge bg-secondary"><i class="bi bi-car-front"></i> <%= p.getParqueaderos() %> parq.</li><% } %>
                        <% if (p.getArea() != null && p.getArea().doubleValue() > 0) { %><li class="list-inline-item badge bg-secondary"><i class="bi bi-rulers"></i> <%= String.format("%,.0f", p.getArea()) %> m&sup2;</li><% } %>
                    </ul>
                    <hr>
                    <h6>Descripci&#243;n</h6>
                    <p class="text-muted"><%= p.getDescripcion() %></p>

                    <h6>Caracter&#237;sticas</h6>
                    <% if (p.getCaracteristicas().isEmpty()) { %>
                        <p class="text-muted small">Sin caracter&#237;sticas registradas.</p>
                    <% } else { %>
                        <ul class="list-unstyled small">
                            <% for (Caracteristica c : p.getCaracteristicas()) { %>
                                <li><i class="bi bi-check-circle-fill text-success"></i>
                                    <%= c.getNombre() %><% if (c.getCantidad() > 1) { %> &times; <%= c.getCantidad() %><% } %>
                                </li>
                            <% } %>
                        </ul>
                    <% } %>

                    <hr>
                    <h6>Inmobiliaria gestora</h6>
                    <p class="mb-1">
                        <strong><i class="bi bi-building"></i> <%= p.getNombreInmobiliaria() %></strong><br>
                        <% if (veContacto) { %>
                            <% if (p.getTelefonoInmobiliaria() != null && !p.getTelefonoInmobiliaria().isEmpty()) { %>
                                <small><i class="bi bi-telephone"></i> <%= p.getTelefonoInmobiliaria() %></small><br>
                            <% } %>
                            <% if (p.getEmailInmobiliaria() != null && !p.getEmailInmobiliaria().isEmpty()) { %>
                                <small><i class="bi bi-envelope"></i> <%= p.getEmailInmobiliaria() %></small>
                            <% } %>
                        <% } else { %>
                            <small class="text-muted">Inicia sesi&#243;n para ver los datos de contacto completos.</small>
                        <% } %>
                    </p>

                    <div class="mt-auto pt-3 d-flex gap-2">
                        <% if (puedeAgendar) { %>
                            <button class="btn btn-success flex-fill" data-bs-toggle="modal" data-bs-target="#modalCitaDetalle">
                                <i class="bi bi-calendar-check"></i> Agendar visita
                            </button>
                            <a href="<%= ctx %>/FavoritoServlet?id=<%= p.getIdPropiedad() %>&origen=detalle"
                               class="btn btn-outline-warning"><i class="bi bi-star"></i> Favorito</a>
                        <% } else { %>
                            <a href="<%= ctx %>/login.jsp" class="btn btn-primary flex-fill">Inicia sesi&#243;n para agendar una visita</a>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<% if (puedeAgendar) { %>
<div class="modal fade" id="modalCitaDetalle" tabindex="-1" aria-hidden="true">
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
                        <textarea name="observaciones" class="form-control" rows="2"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Confirmar</button>
                </div>
            </form>
        </div>
    </div>
</div>
<% } %>

<script>
function cambiarImagen(el, ruta) {
    document.getElementById('imagenPrincipal').src = ruta;
    document.querySelectorAll('.img-sello').forEach(function (i) { i.classList.remove('activa'); });
    el.classList.add('activa');
}
</script>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>