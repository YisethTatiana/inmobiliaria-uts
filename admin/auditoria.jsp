<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="com.inmobiliaria.modelo.Auditoria" %>
<%@ page import="com.inmobiliaria.modelo.Perfil" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%@ page import="java.util.List" %>
<%
    request.setAttribute("titulo", "Auditoria");
    Usuario u = (Usuario) session.getAttribute("usuario");
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if (!u.tieneRol("ADMINISTRADOR")) {
        response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
        return;
    }
    String ctx = request.getContextPath();
    List<Auditoria> registros = (List<Auditoria>) request.getAttribute("registros");
    Perfil perfil = (Perfil) request.getAttribute("perfil");
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Bit&aacute;cora de auditor&iacute;a</h2>
        <% if (u.tieneRol("ADMINISTRADOR")) { %>
            <a href="<%= ctx %>/admin/dashboard_admin.jsp" class="btn btn-outline-secondary btn-sm">&larr; Panel admin</a>
        <% } else { %>
            <a href="<%= ctx %>/index.jsp" class="btn btn-outline-secondary btn-sm">&larr; Inicio</a>
        <% } %>
    </div>

    <% if (perfil != null && perfil.getNombres() != null) { %>
        <div class="alert alert-light border small">
            Sesion de auditor&iacute;a iniciada por <strong><%= perfil.getNombres() %> <%= perfil.getApellidos() %></strong> (<%= u.getCorreo() %>).
        </div>
    <% } %>

    <div class="card shadow-sm">
        <div class="card-body">
            <% if (registros == null || registros.isEmpty()) { %>
                <p class="text-muted mb-0">No hay registros de auditor&iacute;a.</p>
            <% } else { %>
                <div class="table-responsive">
                    <table class="table table-striped table-hover align-middle small tabla-sm responsive-cards">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Fecha</th>
                                <th>Usuario</th>
                                <th>Acci&oacute;n</th>
                                <th>Entidad</th>
                                <th>Detalle</th>
                                <th>IP</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Auditoria a : registros) { %>
                                <tr>
                                    <td data-label="N&ordm;">#<%= a.getIdAuditoria() %></td>
                                    <td data-label="Fecha"><%= new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(a.getFecha()) %></td>
                                    <td data-label="Usuario"><%= a.getCorreoUsuario() != null ? a.getCorreoUsuario() : "\u2014" %></td>
                                    <td data-label="Accion"><span class="badge bg-primary"><%= a.getAccion() %></span></td>
                                    <td data-label="Entidad"><%= a.getEntidad() %></td>
                                    <td data-label="Detalle" class="text-wrap"><%= a.getDetalle() %></td>
                                    <td data-label="IP"><%= a.getIp() %></td>
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