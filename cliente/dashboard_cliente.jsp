<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%
    request.setAttribute("titulo", "Panel del Cliente");
    Usuario u = (Usuario) session.getAttribute("usuario");
    String ctx = request.getContextPath();
    if (u == null) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }
    if (!u.tieneRol("CLIENTE") && !u.tieneRol("ADMINISTRADOR")) {
        response.sendRedirect(ctx + "/acceso_denegado.jsp");
        return;
    }
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <h2 class="mb-1">Panel del Cliente</h2>
    <p class="text-muted">Bienvenido, <strong><%= u.getCorreo() %></strong>. Explora propiedades y gestiona tus tr&aacute;mites.</p>

    <div class="row g-4 mt-1">
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-primary"><div class="card-body">
                <h5 class="card-title text-primary">Buscar Propiedades</h5>
                <p class="card-text small text-muted">Filtra por ciudad, tipo y precio.</p>
                <a href="<%= ctx %>/PropiedadServlet" class="btn btn-primary btn-sm">Ver Cat&aacute;logo</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-success"><div class="card-body">
                <h5 class="card-title text-success">Mis Citas</h5>
                <p class="card-text small text-muted">Revisa el estado de las visitas programadas.</p>
                <a href="<%= ctx %>/CitaServlet" class="btn btn-success btn-sm">Ver Mis Citas</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-warning"><div class="card-body">
                <h5 class="card-title text-warning">Mis Favoritos</h5>
                <p class="card-text small text-muted">Propiedades que marcaste con &quot;estrella&quot;.</p>
                <a href="<%= ctx %>/FavoritoServlet" class="btn btn-warning btn-sm">Ver Favoritos</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-info"><div class="card-body">
                <h5 class="card-title text-info">Mis Solicitudes</h5>
                <p class="card-text small text-muted">Radica solicitudes de compra o arriendo y adjunta documentos.</p>
                <a href="<%= ctx %>/SolicitudServlet" class="btn btn-info btn-sm">Gestionar</a>
            </div></div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 shadow-sm border-secondary"><div class="card-body">
                <h5 class="card-title">Mi Perfil</h5>
                <p class="card-text small text-muted">Completa tus datos personales.</p>
                <a href="<%= ctx %>/PerfilServlet" class="btn btn-outline-secondary btn-sm">Ver</a>
            </div></div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>