<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%
    request.setAttribute("titulo", "Recuperar Contrase\u00F1a");
    String ctx = request.getContextPath();
    boolean enviado = Boolean.TRUE.equals(request.getAttribute("enviado"));
    String correo = request.getAttribute("correo") != null ? String.valueOf(request.getAttribute("correo")) : "";
%>
<%@ include file="/WEB-INF/jspf/head.jspf" %>
<div class="container my-5 pt-4">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <% if (!enviado) { %>
                        <h3 class="text-center mb-2">Recuperar Contrase&#241;a</h3>
                        <p class="text-muted text-center small mb-4">Ingresa el correo de tu cuenta y te enviaremos un c&oacute;digo de 6 d&iacute;gitos para restablecer la contrase&#241;a.</p>
                        <form action="<%= ctx %>/RecuperarClaveServlet" method="POST">
                            <input type="hidden" name="accion" value="pedir">
                            <div class="mb-3">
                                <label class="form-label">Correo Electr&oacute;nico</label>
                                <input type="email" name="correo" class="form-control" required>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Enviar c&oacute;digo de recuperaci&oacute;n</button>
                        </form>
                        <div class="mt-3 text-center">
                            <a href="<%= ctx %>/login.jsp">&larr; Volver al inicio de sesi&oacute;n</a>
                        </div>
                    <% } else { %>
                        <h3 class="text-center mb-2">Verifica tu c&oacute;digo</h3>
                        <p class="text-muted text-center small mb-3">D&iacute;gita el c&oacute;digo de 6 d&iacute;gitos junto con tu nueva contrase&#241;a.</p>

                        <%
                            boolean correoRegistrado = Boolean.TRUE.equals(request.getAttribute("correoRegistrado"));
                            Integer resultadoEnvio = (Integer) request.getAttribute("resultadoEnvio");
                        %>
                        <% if (resultadoEnvio != null && resultadoEnvio == 1) { %>
                            <div class="alert alert-success">Hemos enviado un c&oacute;digo de 6 d&iacute;gitos a <strong><%= correo %></strong>. Revisa tu bandeja de entrada (si no aparece, revisa tambi&eacute;n la carpeta de spam).</div>
                        <% } else if (!correoRegistrado) { %>
                            <div class="alert alert-danger">El correo <strong><%= correo %></strong> no corresponde a ninguna cuenta registrada. Reg&iacute;strate primero o verifica que lo escribiste bien.</div>
                        <% } else if (resultadoEnvio != null && resultadoEnvio == 0) { %>
                            <div class="alert alert-warning">La cuenta existe, pero el env&iacute;o de correos no est&aacute; configurado. El administrador debe completar <em>WEB-INF/classes/smtp.properties</em> con un Gmail remitente y su contrase&ntilde;a de aplicaci&oacute;n, y reiniciar Tomcat.</div>
                        <% } else { %>
                            <div class="alert alert-danger">El servidor de correo rechaz&oacute; el env&iacute;o a <strong><%= correo %></strong>. Revisa que la contrase&ntilde;a de aplicaci&oacute;n en <em>WEB-INF/classes/smtp.properties</em> sea correcta o intenta m&aacute;s tarde.</div>
                        <% } %>

                        <% if (request.getAttribute("error") != null) { %>
                            <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                        <% } %>

                        <form action="<%= ctx %>/RecuperarClaveServlet" method="POST">
                            <input type="hidden" name="accion" value="reset">
                            <input type="hidden" name="correo" value="<%= correo %>">
                            <div class="mb-3">
                                <label class="form-label">C&oacute;digo de verificaci&oacute;n</label>
                                <input type="text" name="codigo" class="form-control" maxlength="6" pattern="[0-9]{6}" inputmode="numeric" placeholder="6 d&iacute;gitos" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Nueva contrase&#241;a</label>
                                <input type="password" name="password" class="form-control" minlength="6" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Confirmar contrase&#241;a</label>
                                <input type="password" name="password2" class="form-control" minlength="6" required>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Actualizar contrase&#241;a</button>
                        </form>
                        <div class="mt-3 text-center">
                            <a href="<%= ctx %>/RecuperarClaveServlet" class="text-danger small">&#191;El c&oacute;digo venci&oacute; o lleg&oacute; incorrecto? Solicitar uno nuevo</a>
                            &middot;
                            <a href="<%= ctx %>/login.jsp" class="small">&larr; Volver al inicio de sesi&oacute;n</a>
                        </div>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>