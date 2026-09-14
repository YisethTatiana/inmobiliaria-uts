<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%
    request.setAttribute("titulo", "Panel de Administracion");
    Usuario u = (Usuario) session.getAttribute("usuario");
    String ctx = request.getContextPath();
    if (u == null) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }
    if (!u.tieneRol("ADMINISTRADOR")) {
        response.sendRedirect(ctx + "/acceso_denegado.jsp");
        return;
    }
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <h2 class="mb-1">Panel de Administraci&oacute;n</h2>
    <p class="text-muted">Bienvenido, <strong><%= u.getCorreo() %></strong>. Administra el sistema completo.</p>

    <div class="row g-4 mt-1">
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-primary"><div class="card-body">
                <h5 class="card-title text-primary">Propiedades</h5>
                <p class="card-text small text-muted">Crear, editar, dar de baja y eliminar inmuebles.</p>
                <a href="<%= ctx %>/AdminPropiedadServlet" class="btn btn-primary btn-sm">Gestionar</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-success"><div class="card-body">
                <h5 class="card-title text-success">Usuarios</h5>
                <p class="card-text small text-muted">Asignar o revocar roles y cambiar estados de cuenta.</p>
                <a href="<%= ctx %>/AdminUsuarioServlet" class="btn btn-success btn-sm">Gestionar</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-info"><div class="card-body">
                <h5 class="card-title text-info">Cat&aacute;logos</h5>
                <p class="card-text small text-muted">Tipos, ciudades y caracter&iacute;sticas.</p>
                <a href="<%= ctx %>/AdminCatalogoServlet" class="btn btn-info btn-sm">Gestionar</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-secondary"><div class="card-body">
                <h5 class="card-title text-secondary">Reportes</h5>
                <p class="card-text small text-muted">Indicadores, agrupaciones y ranking de la inmobiliaria.</p>
                <a href="<%= ctx %>/ReporteServlet" class="btn btn-secondary btn-sm">Ver</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-danger"><div class="card-body">
                <h5 class="card-title text-danger">Auditor&iacute;a</h5>
                <p class="card-text small text-muted">Bit&aacute;cora de acciones de los usuarios.</p>
                <a href="<%= ctx %>/AuditoriaServlet" class="btn btn-danger btn-sm">Ver</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-primary"><div class="card-body">
                <h5 class="card-title">Cat&aacute;logo p&uacute;blico</h5>
                <p class="card-text small text-muted">Ver el portal como lo ve el p&uacute;blico.</p>
                <a href="<%= ctx %>/PropiedadServlet" class="btn btn-outline-primary btn-sm">Abrir</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-info"><div class="card-body">
                <h5 class="card-title">Mi perfil</h5>
                <p class="card-text small text-muted">Actualizar datos personales.</p>
                <a href="<%= ctx %>/PerfilServlet" class="btn btn-outline-info btn-sm">Ver</a>
            </div></div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>