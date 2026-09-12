<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.inmobiliaria.modelo.Cita" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    String rol = (String) session.getAttribute("rol");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if (rol == null || !"ADMINISTRADOR".equalsIgnoreCase(rol)) {
        response.sendRedirect(request.getContextPath() + "/acceso_denegado.jsp");
        return;
    }
    List<Cita> citas = (List<Cita>) request.getAttribute("citas");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Citas - Inmobiliaria UTS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container my-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Administración de Citas</h2>
            <a href="${pageContext.request.contextPath}/admin/dashboard_admin.jsp" class="btn btn-secondary">Volver al Panel</a>
        </div>
        <div class="card shadow-sm p-4">
            <% if (citas != null && !citas.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="table table-striped align-middle">
                        <thead class="table-dark">
                            <tr>
                                <th># Cita</th>
                                <th>ID Cliente</th>
                                <th>ID Propiedad</th>
                                <th>Fecha y Hora</th>
                                <th>Estado Actual</th>
                                <th>Acción</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Cita c : citas) { 
                                String est = (c.getEstado() != null) ? c.getEstado().trim().toUpperCase() : "PENDIENTE";
                            %>
                                <tr>
                                    <td><%= c.getIdCita() %></td>
                                    <td><%= c.getIdUsuario() %></td>
                                    <td><%= c.getIdPropiedad() %></td>
                                    <td><%= c.getFechaCita() %></td>
                                    <td>
                                        <% 
                                            String badgeClass = "bg-warning text-dark";
                                            if ("APROBADA".equals(est)) badgeClass = "bg-success";
                                            else if ("CANCELADA".equals(est)) badgeClass = "bg-danger";
                                        %>
                                        <span class="badge <%= badgeClass %>"><%= est %></span>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/AdminCitaServlet" method="POST" class="d-flex gap-2">
                                            <input type="hidden" name="idCita" value="<%= c.getIdCita() %>">
                                            <select name="estado" class="form-select form-select-sm">
                                                <option value="PENDIENTE" <%= "PENDIENTE".equals(est) ? "selected" : "" %>>PENDIENTE</option>
                                                <option value="APROBADA" <%= "APROBADA".equals(est) ? "selected" : "" %>>APROBADA</option>
                                                <option value="CANCELADA" <%= "CANCELADA".equals(est) ? "selected" : "" %>>CANCELADA</option>
                                            </select>
                                            <button type="submit" class="btn btn-sm btn-primary">Guardar</button>
                                        </form>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <p class="text-muted text-center mb-0">No hay citas registradas en el sistema.</p>
            <% } %>
        </div>
    </div>
</body>
</html>