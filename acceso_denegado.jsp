<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="com.inmobiliaria.modelo.Usuario" %>
<%@ page import="java.util.List" %>
<%
    request.setAttribute("titulo", "Acceso denegado");
    String ctx = request.getContextPath();
    Usuario u = (Usuario) session.getAttribute("usuario");
    String panel = (u != null) ? u.getPanelSegunRol() : "/index.jsp";
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>
<div class="container my-5 my-5 text-center">
    <div class="display-1 text-danger fw-bold">403</div>
    <h2 class="my-3">Acceso denegado</h2>
    <p class="text-muted">No tienes permisos para acceder a este m&oacute;dulo.</p>
    <a href="<%= ctx %><%= panel %>" class="btn btn-primary">Volver a mi panel</a>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>