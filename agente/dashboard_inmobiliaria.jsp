<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%
    request.setAttribute("titulo", "Panel de la Inmobiliaria");
    Usuario u = (Usuario) session.getAttribute("usuario");
    String ctx = request.getContextPath();
    if (u == null) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }
    boolean esAgente = u.tieneRol("INMOBILIARIA");
    boolean esAdmin = u.tieneRol("ADMINISTRADOR");
    if (!esAgente && !esAdmin) {
        response.sendRedirect(ctx + "/acceso_denegado.jsp");
        return;
    }
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <% if (request.getParameter("sinInmobiliaria") != null) { %>
        <div class="alert alert-warning">Tu cuenta no tiene una inmobiliaria asociada. Solicita al administrador la asignaci&oacute;n.</div>
    <% } %>
    <h2 class="mb-1">Panel de la Inmobiliaria</h2>
    <p class="text-muted">Bienvenido, <strong><%= u.getCorreo() %></strong>. Gestiona tus propiedades publicadas.</p>

    <div class="row g-4 mt-1">
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-primary"><div class="card-body">
                <h5 class="card-title text-primary">Mis Propiedades</h5>
                <p class="card-text small text-muted">Publicar, editar, bajar o eliminar inmuebles.</p>
                <a href="<%= ctx %>/AgentePropiedadServlet" class="btn btn-primary btn-sm">Gestionar</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-success"><div class="card-body">
                <h5 class="card-title text-success">Publicar nueva</h5>
                <p class="card-text small text-muted">Registrar una propiedad con fotos y caracter&iacute;sticas.</p>
                <a href="<%= ctx %>/agente/crear_propiedad.jsp" class="btn btn-success btn-sm">Crear</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-info"><div class="card-body">
                <h5 class="card-title text-info">Solicitudes</h5>
                <p class="card-text small text-muted">Aprobar o rechazar tr&aacute;mites de clientes.</p>
                <a href="<%= ctx %>/GestionSolicitudServlet" class="btn btn-info btn-sm">Gestionar</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-danger"><div class="card-body">
                <h5 class="card-title text-danger">Reportes de ventas y arriendos</h5>
                <p class="card-text small text-muted">Reportes de ventas, arriendos y citas.</p>
                <a href="<%= ctx %>/ReporteServlet" class="btn btn-danger btn-sm">Generar reportes</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-warning"><div class="card-body">
                <h5 class="card-title text-warning">Citas</h5>
                <p class="card-text small text-muted">Confirmar o cancelar visitas de tus propiedades.</p>
                <a href="<%= ctx %>/AgenteCitaServlet" class="btn btn-warning btn-sm">Gestionar</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-primary"><div class="card-body">
                <h5 class="card-title">Cat&aacute;logo p&uacute;blico</h5>
                <p class="card-text small text-muted">Ver el portal como lo ve el p&uacute;blico.</p>
                <a href="<%= ctx %>/PropiedadServlet" class="btn btn-outline-primary btn-sm">Abrir</a>
            </div></div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>