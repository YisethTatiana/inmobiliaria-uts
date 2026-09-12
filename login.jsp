<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%
    request.setAttribute("titulo", "Iniciar Sesi\u00F3n");
    String correoPref = "";
    Object attr = request.getAttribute("correoPrecargado");
    if (attr != null) correoPref = attr.toString();
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<div class="container my-5 pt-4">
    <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h3 class="text-center mb-4">Iniciar Sesi&#243;n</h3>

                    <% if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                    <% } %>
                    <% if ("exito".equals(request.getParameter("registro"))) { %>
                        <div class="alert alert-success">Cuenta creada correctamente. Inicia sesi&#243;n.</div>
                    <% } %>

                    <form action="<%= request.getContextPath() %>/LoginServlet" method="POST">
                        <div class="mb-3">
                            <label class="form-label">Correo Electr&#243;nico</label>
                            <input type="email" name="correo" class="form-control" value="<%= correoPref %>" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Contrase&#241;a</label>
                            <input type="password" name="password" class="form-control" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Ingresar</button>
                    </form>
                    <div class="mt-3 text-center">
                        <a href="<%= request.getContextPath() %>/registro.jsp">&#191;No tienes cuenta? Reg&#237;strate</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>