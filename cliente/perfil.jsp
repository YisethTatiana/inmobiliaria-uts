<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.inmobiliaria.modelo.Perfil" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%
    request.setAttribute("titulo", "Mi Perfil");
    Usuario u = (Usuario) session.getAttribute("usuario");
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String ctx = request.getContextPath();
    Perfil perfil = (Perfil) request.getAttribute("perfil");
    if (perfil == null) perfil = new Perfil();
    String panel = u.getPanelSegunRol();
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Mi Perfil</h2>
        <a href="<%= ctx %><%= panel %>" class="btn btn-outline-secondary btn-sm">&larr; Volver a mi panel</a>
    </div>

    <% if (request.getParameter("guardado") != null) { %>
        <div class="alert alert-success">Perfil actualizado correctamente.</div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
    <% } %>

    <div class="row g-4">
        <div class="col-lg-4">
            <div class="card shadow-sm text-center h-100">
                <div class="card-body">
                    <div class="avatar-circle mx-auto mb-3"><%= u.getCorreo().charAt(0) %></div>
                    <h5 class="mb-1"><%= (perfil != null && perfil.getNombres() != null) ? perfil.getNombres() + " " + perfil.getApellidos() : u.getCorreo() %></h5>
                    <p class="text-muted small mb-2"><%= u.getCorreo() %></p>
                    <hr>
                    <div class="d-flex flex-wrap gap-2 justify-content-center">
                        <% for (String rol : u.getRoles()) {
                            String cls = "ADMINISTRADOR".equals(rol) ? "bg-danger"
                                       : "INMOBILIARIA".equals(rol) ? "bg-primary" : "bg-success"; %>
                            <span class="badge <%= cls %>"><%= rol %></span>
                        <% } %>
                    </div>
                    <% if (u.getRoles().contains("CLIENTE") && u.getRoles().size() == 1) { %>
                        <a href="<%= ctx %>/cliente/solicitudes.jsp" class="btn btn-outline-success btn-sm mt-3 w-100">Mis solicitudes</a>
                    <% } %>
                </div>
            </div>
        </div>
        <div class="col-lg-8">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="mb-3">Datos personales</h5>
                    <form action="<%= ctx %>/PerfilServlet" method="POST" class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Nombres *</label>
                            <input type="text" name="nombres" class="form-control" required
                                   value="<%= perfil != null && perfil.getNombres() != null ? perfil.getNombres() : "" %>">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Apellidos *</label>
                            <input type="text" name="apellidos" class="form-control" required
                                   value="<%= perfil != null && perfil.getApellidos() != null ? perfil.getApellidos() : "" %>">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">Documento *</label>
                            <input type="text" name="documento" class="form-control" required
                                   value="<%= perfil != null && perfil.getDocumento() != null ? perfil.getDocumento() : "" %>">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">Teléfono</label>
                            <input type="text" name="telefono" class="form-control"
                                   value="<%= perfil != null && perfil.getTelefono() != null ? perfil.getTelefono() : "" %>">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">Dirección</label>
                            <input type="text" name="direccion" class="form-control"
                                   value="<%= perfil != null && perfil.getDireccion() != null ? perfil.getDireccion() : "" %>">
                        </div>
                        <div class="col-12">
                            <button type="submit" class="btn btn-primary">Guardar cambios</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>