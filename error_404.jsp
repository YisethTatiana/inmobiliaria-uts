<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" import="com.inmobiliaria.modelo.Usuario" %>
<%
    request.setAttribute("titulo", "P&aacute;gina no encontrada");
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<%@ include file="/WEB-INF/jspf/menu.jspf" %>
<div class="container my-5 text-center">
    <div class="display-1 text-warning fw-bold">404</div>
    <h2 class="my-3">P&#225;gina no encontrada</h2>
    <p class="text-muted">El recurso que buscas no existe o fue movido. Revisa la direcci&#243;n o vuelve al inicio.</p>
    <a href="<%= request.getContextPath() %>/index.jsp" class="btn btn-primary">Volver al inicio</a>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
