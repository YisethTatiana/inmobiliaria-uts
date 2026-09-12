<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.inmobiliaria.modelo.Cita" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%
    request.setAttribute("titulo", "Citas - Admin");
    Usuario u = (Usuario) session.getAttribute("usuario");
    String ctx = request.getContextPath();
    if (u == null || !u.tieneRol("ADMINISTRADOR")) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }
    List<Cita> citas = (List<Cita>) request.getAttribute("citas");
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Gesti&oacute;n de Citas</h2>
        <a href="<%= ctx %>/admin/dashboard_admin.jsp" class="btn btn-outline-secondary btn-sm">&larr; Panel admin</a>
    </div>

    <% if ("true".equals(request.getParameter("actualizado"))) { %>
        <div class="alert alert-success">Estado de la cita actualizado correctamente.</div>
    <% } %>

    <div class="card shadow-sm">
        <div class="card-body">
            <% if (citas == null || citas.isEmpty()) { %>
                <p class="text-muted text-center mb-0">No hay citas registradas.</p>
            <% } else { %>
                <div class="table-responsive">
                    <table class="table table-striped align-middle small tabla-sm responsive-cards">
                        <thead class="table-dark">
                            <tr>
                                <th>#</th>
                                <th>Cliente</th>
                                <th>Propiedad</th>
                                <th>Ciudad</th>
                                <th>Fecha y hora</th>
                                <th>Estado</th>
                                <th>Cambiar estado</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Cita c : citas) {
                                String badge = "PENDIENTE".equals(c.getEstado()) ? "bg-warning text-dark"
                                             : "APROBADA".equals(c.getEstado()) ? "bg-primary"
                                             : "COMPLETADA".equals(c.getEstado()) ? "bg-success" : "bg-secondary"; %>
                                <tr>
                                    <td data-label="ID">#<%= c.getIdCita() %></td>
                                    <td data-label="Cliente"><%= c.getCorreoCliente() != null ? c.getCorreoCliente() : "\u2014" %></td>
                                    <td data-label="Propiedad"><%= c.getTituloPropiedad() != null ? c.getTituloPropiedad() : "Propiedad #" + c.getIdPropiedad() %></td>
                                    <td data-label="Ciudad"><%= c.getNombreCiudad() != null ? c.getNombreCiudad() : "\u2014" %></td>
                                    <td data-label="Fecha"><%= new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a").format(c.getFechaCita()) %></td>
                                    <td data-label="Estado"><span class="badge <%= badge %>"><%= c.getEstado() %></span></td>
                                    <td data-label="Cambiar estado" class="text-nowrap">
                                        <form action="<%= ctx %>/AdminCitaServlet" method="post" class="d-inline">
                                            <input type="hidden" name="idCita" value="<%= c.getIdCita() %>">
                                            <select name="estado" class="form-select form-select-sm d-inline-block w-auto" onchange="this.form.submit()">
                                                <option value="PENDIENTE" <%= "PENDIENTE".equals(c.getEstado()) ? "selected" : "" %>>Pendiente</option>
                                                <option value="APROBADA" <%= "APROBADA".equals(c.getEstado()) ? "selected" : "" %>>Aprobada</option>
                                                <option value="COMPLETADA" <%= "COMPLETADA".equals(c.getEstado()) ? "selected" : "" %>>Completada</option>
                                                <option value="CANCELADA" <%= "CANCELADA".equals(c.getEstado()) ? "selected" : "" %>>Cancelada</option>
                                            </select>
                                        </form>
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