<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="com.inmobiliaria.modelo.Solicitud" %>
<%@ page import="com.inmobiliaria.modelo.Propiedad" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%@ page import="java.util.List" %>
<%
    request.setAttribute("titulo", "Mis Solicitudes");
    Usuario u = (Usuario) session.getAttribute("usuario");
    if (u == null || !u.tieneRol("CLIENTE")) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String ctx = request.getContextPath();
    List<Solicitud> solicitudes = (List<Solicitud>) request.getAttribute("solicitudes");
    List<Propiedad> propiedades = (List<Propiedad>) request.getAttribute("propiedades");
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Mis Solicitudes</h2>
        <a href="<%= ctx %>/PerfilServlet" class="btn btn-outline-secondary btn-sm">&larr; Mi perfil</a>
    </div>

    <% if (request.getParameter("creada") != null) { %>
        <div class="alert alert-success">Solicitud radicada correctamente.</div>
    <% } else if (request.getParameter("anulada") != null) { %>
        <div class="alert alert-info">La solicitud fue anulada.</div>
    <% } else if (request.getParameter("docOk") != null) { %>
        <div class="alert alert-success">Documento cargado correctamente.</div>
    <% } else if (request.getParameter("docTipoError") != null) { %>
        <div class="alert alert-danger">El documento debe ser PDF, JPG o PNG (m&#225;x. 5 MB).</div>
    <% } else if (request.getParameter("docError") != null) { %>
        <div class="alert alert-danger">Debes seleccionar el archivo que deseas adjuntar.</div>
    <% } else if (request.getParameter("error") != null) { %>
        <div class="alert alert-danger">No se pudo procesar la solicitud. Int&eacute;ntalo nuevamente.</div>
    <% } %>

    <div class="row g-4">
        <div class="col-lg-7">
            <div class="card shadow-sm">
                <div class="card-header bg-white"><strong>Historial</strong></div>
                <div class="card-body">
                    <% if (solicitudes == null || solicitudes.isEmpty()) { %>
                        <p class="text-muted mb-0">A&uacute;n no has radicado solicitudes.</p>
                    <% } else { %>
                        <div class="table-responsive">
                            <table class="table table-striped table-hover align-middle small tabla-sm responsive-cards">
                                <thead>
                                    <tr>
                                        <th>#</th>
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
                                            <td data-label="Propiedad"><%= s.getTituloPropiedad() %></td>
                                            <td data-label="Tipo"><%= s.getTipoSolicitud() %></td>
                                            <td data-label="Fecha">
                                                <%= s.getFechaSolicitud() != null
                                                        ? new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a").format(s.getFechaSolicitud()) : "\u2014" %>
                                            </td>
                                            <td data-label="Estado"><span class="badge <%= badge %>"><%= s.getEstado() %></span></td>
                                            <td class="text-end">
                                                <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal"
                                                        data-bs-target="#modalDoc<%= s.getIdSolicitud() %>"
                                                        <%= "APROBADA".equals(s.getEstado()) || "RECHAZADA".equals(s.getEstado()) || "ANULADA".equals(s.getEstado()) ? "disabled" : "" %>>
                                                    Adjuntar documento
                                                </button>
                                                <form action="<%= ctx %>/SolicitudServlet" method="POST" class="d-inline">
                                                    <input type="hidden" name="accion" value="cancelar">
                                                    <input type="hidden" name="idSolicitud" value="<%= s.getIdSolicitud() %>">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger"
                                                            <%= "APROBADA".equals(s.getEstado()) || "RECHAZADA".equals(s.getEstado()) || "ANULADA".equals(s.getEstado()) ? "disabled" : "" %>
                                                            onclick="return confirm('&#191;Anular esta solicitud?');">Anular</button>
                                                </form>
                                            </td>
                                        </tr>

                                        <div class="modal fade" id="modalDoc<%= s.getIdSolicitud() %>" tabindex="-1" aria-hidden="true">
                                            <div class="modal-dialog">
                                                <div class="modal-content">
                                                    <form action="<%= ctx %>/SolicitudServlet" method="POST" enctype="multipart/form-data">
                                                        <div class="modal-header">
                                                            <h5 class="modal-title">Adjuntar documento &mdash; Solicitud #<%= s.getIdSolicitud() %></h5>
                                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                        </div>
                                                        <div class="modal-body">
                                                            <input type="hidden" name="accion" value="documento">
                                                            <input type="hidden" name="idSolicitud" value="<%= s.getIdSolicitud() %>">
                                                            <div class="mb-3">
                                                                <label class="form-label">Tipo de documento</label>
                                                                <select name="tipoDocumento" class="form-select">
                                                                    <option>C&eacute;dula</option>
                                                                    <option>Certificado de ingresos</option>
                                                                    <option>Acta de trabajo</option>
                                                                    <option>Otro</option>
                                                                </select>
                                                            </div>
                                                            <div class="mb-3">
                                                                <label class="form-label">Archivo (PDF, JPG o PNG &mdash; m&aacute;x. 5 MB)</label>
                                                                <input type="file" name="archivo" class="form-control" required>
                                                            </div>
                                                        </div>
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                                                            <button type="submit" class="btn btn-primary">Subir documento</button>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    <% } %>
                </div>
            </div>
        </div>

        <div class="col-lg-5">
            <div class="card shadow-sm">
                <div class="card-header bg-white"><strong>Nueva solicitud</strong></div>
                <div class="card-body">
                    <% if (propiedades == null || propiedades.isEmpty()) { %>
                        <p class="text-muted mb-0">No hay propiedades disponibles para solicitar.</p>
                    <% } else { %>
                        <form action="<%= ctx %>/SolicitudServlet" method="POST">
                            <input type="hidden" name="accion" value="crear">
                            <div class="mb-3">
                                <label class="form-label">Propiedad</label>
                                <select name="idPropiedad" class="form-select" required>
                                    <% for (Propiedad p : propiedades) { %>
                                        <option value="<%= p.getIdPropiedad() %>"><%= p.getTitulo() %> &mdash; $ <%= String.format("%,.0f", p.getPrecio()) %></option>
                                    <% } %>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Tipo de solicitud</label>
                                <select name="tipoSolicitud" class="form-select" required>
                                    <option value="COMPRA">Compra</option>
                                    <option value="ARRIENDO">Arriendo</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Observaciones</label>
                                <textarea name="observaciones" class="form-control" rows="3"></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Radicar solicitud</button>
                        </form>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>