<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" import="com.inmobiliaria.modelo.Usuario" %>
<%
    request.setAttribute("titulo", "Error interno");
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>
<div class="container my-5 text-center">
    <div class="display-1 text-danger fw-bold">500</div>
    <h2 class="my-3">Error interno del servidor</h2>
    <p class="text-muted">Ocurri&#243; un problema inesperado. Intenta nuevamente en unos minutos o contacta al administrador.</p>
    <a href="<%= request.getContextPath() %>/index.jsp" class="btn btn-primary">Volver al inicio</a>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
