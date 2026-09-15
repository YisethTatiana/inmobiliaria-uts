<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.inmobiliaria.modelo.Cita" %>
<%@ page import="com.inmobiliaria.modelo.Propiedad" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%
    request.setAttribute("titulo", "Mis Citas");
    Usuario u = (Usuario) session.getAttribute("usuario");
    String ctx = request.getContextPath();
    if (u == null) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }
    List<Cita> citas = (List<Cita>) request.getAttribute("citas");
    List<Propiedad> propiedades = (List<Propiedad>) request.getAttribute("propiedades");
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Mis Citas Programadas</h2>
        <div>
            <button type="button" class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#modalAgendarCita">Agendar nueva cita</button>
            <a href="<%= ctx %>/cliente/dashboard_cliente.jsp" class="btn btn-outline-secondary btn-sm">Panel</a>
        </div>
    </div>

    <% if ("true".equals(request.getParameter("agendada"))) { %>
        <div class="alert alert-success">Cita agendada correctamente.</div>
    <% } else if ("true".equals(request.getParameter("cancelada"))) { %>
        <div class="alert alert-info">Cita cancelada.</div>
    <% } else if ("true".equals(request.getParameter("duplicada"))) { %>
        <div class="alert alert-warning">Ya tienes una cita para esa propiedad en la misma fecha y hora.</div>
    <% } else if ("true".equals(request.getParameter("fechaInvalida"))) { %>
        <div class="alert alert-danger">La fecha y hora no son v&aacute;lidas.</div>
    <% } else if ("true".equals(request.getParameter("fechaPasada"))) { %>
        <div class="alert alert-danger">La fecha y hora deben ser futuras; elige un horario disponible.</div>
    <% } else if ("true".equals(request.getParameter("error"))) { %>
        <div class="alert alert-danger">No se pudo procesar la cita. Int&eacute;ntalo nuevamente.</div>
    <% } %>

    <div class="card shadow-sm">
        <div class="card-body">
            <% if (citas == null || citas.isEmpty()) { %>
                <p class="text-muted text-center mb-0">No tienes citas agendadas por el momento.</p>
            <% } else { %>
                <div class="table-responsive">
                    <table class="table table-hover align-middle small tabla-sm responsive-cards">
                        <thead class="table-primary">
                            <tr>
                                <th>#</th>
                                <th>Propiedad</th>
                                <th>Ciudad</th>
                                <th>Fecha y hora</th>
                                <th>Observaciones</th>
                                <th>Estado</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Cita c : citas) {
                                String badge = "PENDIENTE".equals(c.getEstado()) ? "bg-warning text-dark"
                                             : "CONFIRMADA".equals(c.getEstado()) ? "bg-primary"
                                             : "COMPLETADA".equals(c.getEstado()) ? "bg-success" : "bg-secondary"; %>
                                <tr>
                                    <td data-label="ID">#<%= c.getIdCita() %></td>
                                    <td data-label="Propiedad"><%= c.getTituloPropiedad() != null ? c.getTituloPropiedad() : "Propiedad #" + c.getIdPropiedad() %></td>
                                    <td data-label="Ciudad"><%= c.getNombreCiudad() != null ? c.getNombreCiudad() : "\u2014" %></td>
                                    <td data-label="Fecha"><%= new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a").format(c.getFechaCita()) %></td>
                                    <td data-label="Observaciones" class="text-wrap"><%= c.getObservaciones() != null && !c.getObservaciones().isEmpty() ? c.getObservaciones() : "\u2014" %></td>
                                    <td data-label="Estado"><span class="badge <%= badge %>"><%= c.getEstado() %></span></td>
                                    <td data-label="Acciones" class="text-end">
                                        <% if (!"CANCELADA".equals(c.getEstado()) && !"COMPLETADA".equals(c.getEstado())) { %>
                                            <form action="<%= ctx %>/CitaServlet" method="post" class="d-inline">
                                                <input type="hidden" name="accion" value="cancelar">
                                                <input type="hidden" name="idCita" value="<%= c.getIdCita() %>">
                                                <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('&#191;Cancelar esta cita?');">Cancelar</button>
                                            </form>
                                        <% } %>
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

<div class="modal fade" id="modalAgendarCita" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="<%= ctx %>/CitaServlet" method="post">
                <div class="modal-header">
                    <h5 class="modal-title">Agendar Nueva Cita</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Propiedad disponible</label>
                        <select name="idPropiedad" class="form-select" required>
                            <option value="">Seleccione una propiedad</option>
                            <% if (propiedades != null) for (Propiedad pp : propiedades) { %>
                                <option value="<%= pp.getIdPropiedad() %>"><%= pp.getTitulo() %> &mdash; <%= pp.getNombreCiudad() %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Fecha y hora</label>
                        <input type="datetime-local" name="fechaHora" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Observaciones (opcional)</label>
                        <textarea name="observaciones" class="form-control" rows="2"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Agendar</button>
                </div>
            </form>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>