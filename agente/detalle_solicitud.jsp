<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.inmobiliaria.modelo.Solicitud" %>
<%@ page import="com.inmobiliaria.modelo.DocumentoSolicitud" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%@ page import="java.util.List" %>
<%
    request.setAttribute("titulo", "Detalle de solicitud");
    Usuario u = (Usuario) session.getAttribute("usuario");
    if (u == null || !u.tieneRol("INMOBILIARIA")) {
        response.sendRedirect(request.getContextPath() + "/agente/dashboard_inmobiliaria.jsp");
        return;
    }
    String ctx = request.getContextPath();
    Solicitud s = (Solicitud) request.getAttribute("solicitud");
    List<DocumentoSolicitud> documentos = (List<DocumentoSolicitud>) request.getAttribute("documentos");
    if (s == null) {
        response.sendRedirect(ctx + "/GestionSolicitudServlet");
        return;
    }
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Solicitud #<%= s.getIdSolicitud() %></h2>
        <a href="<%= ctx %>/GestionSolicitudServlet" class="btn btn-outline-secondary btn-sm">&larr; Volver a solicitudes</a>
    </div>

    <% if (request.getParameter("actualizado") != null) { %>
        <div class="alert alert-success">Estado actualizado.</div>
    <% } else if (request.getParameter("docActualizado") != null) { %>
        <div class="alert alert-success">Documento revisado.</div>
    <% } else if (request.getParameter("error") != null) { %>
        <div class="alert alert-danger">Ocurri&oacute; un error. Int&eacute;ntalo nuevamente.</div>
    <% } %>

    <div class="row g-4">
        <div class="col-lg-5">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-white"><strong>Informaci&oacute;n</strong></div>
                <div class="card-body">
                    <table class="table table-sm mb-3">
                        <tr><th class="text-muted">Cliente</th><td><%= s.getCorreoCliente() %></td></tr>
                        <tr><th class="text-muted">Propiedad</th><td><%= s.getTituloPropiedad() %></td></tr>
                        <tr><th class="text-muted">Tipo</th><td><%= s.getTipoSolicitud() %></td></tr>
                        <tr><th class="text-muted">Fecha</th><td><%= new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a").format(s.getFechaSolicitud()) %></td></tr>
                        <tr><th class="text-muted">Estado</th>
                            <td><span class="badge bg-<%= "APROBADA".equals(s.getEstado()) ? "success" : "RECHAZADA".equals(s.getEstado()) ? "danger" : "RADICADA".equals(s.getEstado()) ? "primary" : "secondary" %>"><%= s.getEstado() %></span></td>
                        </tr>
                        <tr><th class="text-muted">Observaciones</th><td class="text-wrap"><%= s.getObservaciones() != null && !s.getObservaciones().isEmpty() ? s.getObservaciones() : "Sin observaciones." %></td></tr>
                    </table>

                    <h6>Cambiar estado</h6>
                    <form action="<%= ctx %>/GestionSolicitudServlet" method="POST" class="d-flex gap-2 flex-wrap">
                        <input type="hidden" name="accion" value="estado">
                        <input type="hidden" name="idSolicitud" value="<%= s.getIdSolicitud() %>">
                        <button class="btn btn-sm btn-success" name="estado" value="APROBADA">Aprobar</button>
                        <button class="btn btn-sm btn-danger" name="estado" value="RECHAZADA">Rechazar</button>
                        <button class="btn btn-sm btn-secondary" name="estado" value="ANULADA">Anular</button>
                    </form>
                </div>
            </div>
        </div>

        <div class="col-lg-7">
            <div class="card shadow-sm">
                <div class="card-header bg-white"><strong>Documentos radicados</strong></div>
                <div class="card-body">
                    <% if (documentos == null || documentos.isEmpty()) { %>
                        <p class="text-muted mb-0">El cliente a&uacute;n no ha adjuntado documentos.</p>
                    <% } else { %>
                        <div class="table-responsive">
                            <table class="table table-sm align-middle tabla-sm">
                                <thead>
                                    <tr><th>Archivo</th><th>Tipo</th><th>Fecha</th><th>Estado</th><th>Acciones</th></tr>
                                </thead>
                                <tbody>
                                    <% for (DocumentoSolicitud d : documentos) {
                                        String badge = "APROBADO".equals(d.getEstado()) ? "bg-success"
                                                     : "RECHAZADO".equals(d.getEstado()) ? "bg-danger"
                                                     : "REVISADO".equals(d.getEstado()) ? "bg-primary" : "bg-secondary"; %>
                                        <tr>
                                            <td>
                                                <a href="<%= ctx %>/<%= d.getRuta() %>" target="_blank"><i class="bi bi-file-earmark"></i> <%= d.getNombreArchivo() %></a>
                                            </td>
                                            <td><%= d.getTipoDocumento() %></td>
                                            <td><%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(d.getFechaCarga()) %></td>
                                            <td><span class="badge <%= badge %>"><%= d.getEstado() %></span></td>
                                            <td class="text-end text-nowrap">
                                                <form action="<%= ctx %>/GestionSolicitudServlet" method="POST" class="d-inline">
                                                    <input type="hidden" name="accion" value="estadoDocumento">
                                                    <input type="hidden" name="idDocumento" value="<%= d.getIdDocumento() %>">
                                                    <input type="hidden" name="idSolicitud" value="<%= s.getIdSolicitud() %>">
                                                    <button class="btn btn-sm btn-outline-success" name="estado" value="APROBADO">Aprobar</button>
                                                    <button class="btn btn-sm btn-outline-warning" name="estado" value="REVISADO">Revisado</button>
                                                    <button class="btn btn-sm btn-outline-danger" name="estado" value="RECHAZADO">Rechazar</button>
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
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>