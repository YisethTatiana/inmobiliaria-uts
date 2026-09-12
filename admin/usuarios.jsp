<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%
    request.setAttribute("titulo", "Gestión de Usuarios");
    Usuario admin = (Usuario) session.getAttribute("usuario");
    String ctx = request.getContextPath();
    if (admin == null) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }
    if (!admin.tieneRol("ADMINISTRADOR")) {
        response.sendRedirect(ctx + "/acceso_denegado.jsp");
        return;
    }
    List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
    Map<Integer, String> roles = (Map<Integer, String>) request.getAttribute("roles");
    List<Map<String, Object>> inmobiliarias = (List<Map<String, Object>>) request.getAttribute("inmobiliarias");
    SimpleDateFormat fmtFecha = new SimpleDateFormat("dd/MM/yyyy");

    int idInmobiliariaRol = -1;
    Map<String, Integer> idRolPorNombre = new HashMap<String, Integer>();
    if (roles != null) {
        for (Map.Entry<Integer, String> e : roles.entrySet()) {
            idRolPorNombre.put(e.getValue().toUpperCase(), e.getKey());
            if ("INMOBILIARIA".equalsIgnoreCase(e.getValue())) {
                idInmobiliariaRol = e.getKey();
            }
        }
    }
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Gesti&oacute;n de Usuarios</h2>
        <a href="<%= ctx %>/admin/dashboard_admin.jsp" class="btn btn-outline-secondary btn-sm">&larr; Panel admin</a>
    </div>

    <% if ("true".equals(request.getParameter("actualizado"))) { %>
        <div class="alert alert-success">Usuario actualizado correctamente.</div>
    <% } else if ("true".equals(request.getParameter("error"))) { %>
        <div class="alert alert-danger">No se pudo actualizar el usuario.</div>
    <% } else if ("true".equals(request.getParameter("eliminado"))) { %>
        <div class="alert alert-success">Usuario eliminado correctamente.</div>
    <% } else if ("true".equals(request.getParameter("eliminarError"))) { %>
        <div class="alert alert-danger">No se pudo eliminar el usuario porque tiene registros asociados (citas, solicitudes, etc.).</div>
    <% } %>

    <div class="card shadow-sm">
        <div class="card-body">
            <% if (usuarios != null && !usuarios.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="table table-striped align-middle small tabla-sm responsive-cards">
                        <thead class="table-dark">
                            <tr>
                                <th>#</th>
                                <th>Correo</th>
                                <th>Rol</th>
                                <th>Roles actuales</th>
                                <th>Fecha</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Usuario u : usuarios) {
                                boolean esCuentaPropia = (u.getIdUsuario() == admin.getIdUsuario());
                            %>
                                <tr>
                                    <td data-label="ID"><%= u.getIdUsuario() %></td>
                                    <td data-label="Correo"><%= u.getCorreo() %></td>
                                    <td data-label="Rol">
                                        <% if (esCuentaPropia) { %>
                                            <span class="badge bg-primary">Cuenta propia</span>
                                        <% } else if (roles != null) { %>
                                            <form action="<%= ctx %>/AdminUsuarioServlet" method="post" class="row g-1">
                                                <input type="hidden" name="accion" value="rol">
                                                <input type="hidden" name="idUsuario" value="<%= u.getIdUsuario() %>">
                                                <div class="col-7">
                                                    <select name="idRol" class="form-select form-select-sm" onchange="mostrarInmobiliaria(this, 'inm-<%= u.getIdUsuario() %>')">
                                                        <% for (Map.Entry<Integer, String> e : roles.entrySet()) { %>
                                                            <option value="<%= e.getKey() %>" <%= u.getRoles().contains(e.getValue().toUpperCase()) ? "selected" : "" %>><%= e.getValue() %></option>
                                                        <% } %>
                                                    </select>
                                                </div>
                                                <div class="col-5 d-none" id="inm-<%= u.getIdUsuario() %>">
                                                    <select name="idInmobiliaria" class="form-select form-select-sm">
                                                        <% if (inmobiliarias != null) for (Map<String, Object> i : inmobiliarias) { %>
                                                            <option value="<%= i.get("id") %>"><%= i.get("nombre") %></option>
                                                        <% } %>
                                                    </select>
                                                </div>
                                                <div class="col-12">
                                                    <button type="submit" class="btn btn-sm btn-primary w-100">Asignar rol</button>
                                                </div>
                                            </form>
                                        <% } %>
                                    </td>
                                    <td data-label="Roles actuales">
                                        <% if (u.getRoles() != null && !u.getRoles().isEmpty()) {
                                            for (String rol : u.getRoles()) { %>
                                                <span class="badge bg-secondary d-inline-block mb-1"><%= rol %></span>
                                                <% Integer idRol = idRolPorNombre.get(rol);
                                                   if (!esCuentaPropia && idRol != null) { %>
                                                    <form action="<%= ctx %>/AdminUsuarioServlet" method="post" class="d-inline">
                                                        <input type="hidden" name="accion" value="quitarRol">
                                                        <input type="hidden" name="idUsuario" value="<%= u.getIdUsuario() %>">
                                                        <input type="hidden" name="idRol" value="<%= idRol %>">
                                                        <button type="submit" class="btn btn-sm btn-outline-danger mb-1" title="Quitar rol">&times;</button>
                                                    </form>
                                                <% } %>
                                        <% }
                                        } else { %>
                                            <span class="text-muted">Sin roles</span>
                                        <% } %>
                                    </td>
                                    <td data-label="Fecha"><%= u.getFechaCreacion() != null ? fmtFecha.format(u.getFechaCreacion()) : "-" %></td>
                                    <td data-label="Estado">
                                        <span class="badge bg-<%= u.isActivo() ? "success" : "secondary" %>"><%= u.isActivo() ? "Activo" : "Inactivo" %></span>
                                    </td>
                                    <td data-label="Acciones" class="text-nowrap">
                                        <% if (!esCuentaPropia) { %>
                                            <form action="<%= ctx %>/AdminUsuarioServlet" method="post" class="d-inline">
                                                <input type="hidden" name="accion" value="estado">
                                                <input type="hidden" name="idUsuario" value="<%= u.getIdUsuario() %>">
                                                <input type="hidden" name="activo" value="<%= u.isActivo() ? "false" : "true" %>">
                                                <button type="submit" class="btn btn-sm <%= u.isActivo() ? "btn-outline-secondary" : "btn-outline-success" %>">
                                                    <%= u.isActivo() ? "Desactivar" : "Activar" %>
                                                </button>
                                            </form>
                                            <a href="<%= ctx %>/EliminarUsuarioServlet?id=<%= u.getIdUsuario() %>"
                                               class="btn btn-sm btn-outline-danger"
                                               onclick="return confirm('¿Estás seguro de eliminar el usuario <%= u.getCorreo() %>?');">Eliminar</a>
                                        <% } else { %>
                                            <span class="text-muted small">-</span>
                                        <% } %>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <p class="text-muted text-center mb-0">No hay usuarios registrados en el sistema.</p>
            <% } %>
        </div>
    </div>
</div>
<script>
function mostrarInmobiliaria(select, idDiv) {
    var div = document.getElementById(idDiv);
    if (select.value === "<%= idInmobiliariaRol %>") {
        div.classList.remove('d-none');
    } else {
        div.classList.add('d-none');
    }
}
</script>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>