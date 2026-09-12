<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="com.inmobiliaria.modelo.Solicitud" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%@ page import="java.util.List" %>
<%
    request.setAttribute("titulo", "Solicitudes");
    Usuario u = (Usuario) session.getAttribute("usuario");
    if (u == null || !u.tieneRol("INMOBILIARIA")) {
        response.sendRedirect(request.getContextPath() + "/agente/dashboard_inmobiliaria.jsp");
        return;
    }
    String ctx = request.getContextPath();
    List<Solicitud> solicitudes = (List<Solicitud>) request.getAttribute("solicitudes");
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Solicitudes de tr&aacute;mite</h2>
        <a href="<%= ctx %>/agente/dashboard_inmobiliaria.jsp" class="btn btn-outline-secondary btn-sm">&larr; Mi panel</a>
    </div>

    <div class="card shadow-sm">
        <div class="card-body">
            <% if (solicitudes == null || solicitudes.isEmpty()) { %>
                <p class="text-muted mb-0">No hay solicitudes para tus propiedades.</p>
            <% } else { %>
                <div class="table-responsive">
                    <table class="table table-striped table-hover align-middle small tabla-sm responsive-cards">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Cliente</th>
                                <th>Propiedad</th>
                                <th>Tipo</th>
                                <th>Fecha</th>
                                <th>Estado</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Solicitud s : solicitudes) {
                                String badge = "RADICADA".equals(s.getEstado()) ? "bg-primary"
                                             : "APROBADA".equals(s.getEstado()) ? "bg-success"
                                             : "RECHAZADA".equals(s.getEstado()) ? "bg-danger"
                                             : "bg-secondary"; %>
                                <tr>
                                    <td data-label="N&ordm;">#<%= s.getIdSolicitud() %></td>
                                    <td data-label="Cliente"><%= s.getCorreoCliente() %></td>
                                    <td data-label="Propiedad"><%= s.getTituloPropiedad() %></td>
                                    <td data-label="Tipo"><%= s.getTipoSolicitud() %></td>
                                    <td data-label="Fecha">
                                        <%= s.getFechaSolicitud() != null
                                                ? new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a").format(s.getFechaSolicitud()) : "\u2014" %>
                                    </td>
                                    <td data-label="Estado"><span class="badge <%= badge %>"><%= s.getEstado() %></span></td>
                                    <td class="text-end">
                                        <a href="<%= ctx %>/GestionSolicitudServlet?idSolicitud=<%= s.getIdSolicitud() %>" class="btn btn-sm btn-outline-primary">Revisar</a>
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