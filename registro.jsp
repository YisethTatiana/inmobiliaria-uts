<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%
    request.setAttribute("titulo", "Registro");
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<div class="container my-5 pt-4">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h3 class="text-center mb-4">Crear Cuenta</h3>

                    <% if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                    <% } %>

                    <form action="<%= request.getContextPath() %>/RegistroServlet" method="POST">
                        <div class="mb-3">
                            <label class="form-label">Correo Electr&#243;nico</label>
                            <input type="email" name="correo" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Nombres</label>
                            <input type="text" name="nombres" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Apellidos</label>
                            <input type="text" name="apellidos" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">N&#250;mero de documento</label>
                            <input type="text" name="documento" class="form-control" inputmode="numeric"
                                   pattern="[0-9]{6,12}" maxlength="12" title="Solo d&#237;gitos (6 a 12)"
                                   required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Tel&#233;fono</label>
                            <input type="tel" name="telefono" class="form-control">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Contrase&#241;a (m&#237;nimo 6 caracteres)</label>
                            <input type="password" name="password" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Confirmar Contrase&#241;a</label>
                            <input type="password" name="confirmar" class="form-control" required>
                        </div>
                        <button type="submit" class="btn btn-success w-100">Registrarse</button>
                    </form>
                    <div class="mt-3 text-center">
                        <a href="<%= request.getContextPath() %>/login.jsp">&#191;Ya tienes cuenta? Inicia sesi&#243;n</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>