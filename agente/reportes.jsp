<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%!
    private void renderTabla(javax.servlet.jsp.JspWriter out, List<Map<String, Object>> filas) throws Exception {
        if (filas == null || filas.isEmpty()) {
            out.print("<p class='text-muted small mb-0'>Sin registros.</p>");
            return;
        }
        out.print("<div class='table-responsive'><table class='table table-sm table-striped align-middle tabla-sm'><thead class='table-dark'><tr>");
        Map<String, Object> primera = filas.get(0);
        for (String col : primera.keySet()) {
            out.print("<th>" + col + "</th>");
        }
        out.print("</tr></thead><tbody>");
        for (Map<String, Object> fila : filas) {
            out.print("<tr>");
            for (Object val : fila.values()) {
                out.print("<td>" + (val != null ? val : "&mdash;") + "</td>");
            }
            out.print("</tr>");
        }
        out.print("</tbody></table></div>");
    }
%>
<%
    request.setAttribute("titulo", "Reportes");
    Usuario u = (Usuario) session.getAttribute("usuario");
    String ctx = request.getContextPath();
    if (u == null) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }
    if (!u.tieneRol("INMOBILIARIA") && !u.tieneRol("ADMINISTRADOR")) {
        response.sendRedirect(ctx + "/acceso_denegado.jsp");
        return;
    }
    List<Map<String, Object>> citas = (List<Map<String, Object>>) request.getAttribute("citas");
    List<Map<String, Object>> usuarios = (List<Map<String, Object>>) request.getAttribute("usuarios");
    List<Map<String, Object>> propiedades = (List<Map<String, Object>>) request.getAttribute("propiedades");
    List<Map<String, Object>> porCiudadEstado = (List<Map<String, Object>>) request.getAttribute("porCiudadEstado");
    List<Map<String, Object>> solicitudesInmobiliaria = (List<Map<String, Object>>) request.getAttribute("solicitudesInmobiliaria");
    List<Map<String, Object>> citasPorEstado = (List<Map<String, Object>>) request.getAttribute("citasPorEstado");
    List<Map<String, Object>> caracteristicas = (List<Map<String, Object>>) request.getAttribute("caracteristicasDePropiedad");
    List<Map<String, Object>> ventasArriendos = (List<Map<String, Object>>) request.getAttribute("ventasArriendos");
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Reportes</h2>
        <a href="<%= ctx %>/agente/dashboard_inmobiliaria.jsp" class="btn btn-outline-secondary btn-sm">&larr; Mi panel</a>
    </div>

    <div class="card shadow-sm border-danger mb-4">
        <div class="card-header bg-white text-danger"><strong>Ventas y arriendos</strong></div>
        <div class="card-body"><% renderTabla(out, ventasArriendos); %></div>
    </div>

    <div class="card shadow-sm mb-4">
        <div class="card-header bg-white"><strong>1. Citas con cliente, propiedad y ciudad</strong></div>
        <div class="card-body"><% renderTabla(out, citas); %></div>
    </div>

    <div class="card shadow-sm mb-4">
        <div class="card-header bg-white"><strong>2. Caracter&iacute;sticas de la propiedad #1</strong></div>
        <div class="card-body"><% renderTabla(out, caracteristicas); %></div>
    </div>

    <div class="card shadow-sm mb-4">
        <div class="card-header bg-white"><strong>3. Usuarios con su perfil y cantidad de citas</strong></div>
        <div class="card-body"><% renderTabla(out, usuarios); %></div>
    </div>

    <div class="card shadow-sm mb-4">
        <div class="card-header bg-white"><strong>4. Propiedades por ciudad y estado</strong></div>
        <div class="card-body"><% renderTabla(out, porCiudadEstado); %></div>
    </div>

    <div class="card shadow-sm mb-4">
        <div class="card-header bg-white"><strong>5. Propiedades m&aacute;s solicitadas (por n&uacute;mero de citas)</strong></div>
        <div class="card-body"><% renderTabla(out, propiedades); %></div>
    </div>

    <div class="card shadow-sm mb-4">
        <div class="card-header bg-white"><strong>6. Solicitudes por inmobiliaria, tipo y estado</strong></div>
        <div class="card-body"><% renderTabla(out, solicitudesInmobiliaria); %></div>
    </div>

    <div class="card shadow-sm mb-4">
        <div class="card-header bg-white"><strong>7. Citas por estado</strong></div>
        <div class="card-body"><% renderTabla(out, citasPorEstado); %></div>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>