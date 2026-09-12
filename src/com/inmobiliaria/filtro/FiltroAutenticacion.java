package com.inmobiliaria.filtro;

import com.inmobiliaria.modelo.Usuario;

import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns = {"/admin/*", "/agente/*", "/cliente/*"})
public class FiltroAutenticacion implements Filter {

    private static final String ROL_ADMIN = "ADMINISTRADOR";
    private static final String ROL_AGENTE = "INMOBILIARIA";
    private static final String ROL_CLIENTE = "CLIENTE";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String contexto = req.getContextPath();
        String uri = req.getRequestURI();
        String path = uri.substring(contexto.length());

        String rolRequerido = null;
        if (path.startsWith("/admin/")) {
            rolRequerido = ROL_ADMIN;
        } else if (path.startsWith("/agente/")) {
            rolRequerido = ROL_AGENTE;
        } else if (path.startsWith("/cliente/")) {
            rolRequerido = ROL_CLIENTE;
        }

        if (rolRequerido == null) {
            chain.doFilter(request, response);
            return;
        }

        // El administrador también puede operar como agente y ver el panel de cliente
        Usuario usuario = getUsuario(session);
        if (usuario == null) {
            res.sendRedirect(contexto + "/login.jsp");
            return;
        }

        // El rol AUDITOR solo puede acceder a la bitácora de auditoría
        boolean esAuditorEnAuditoria = usuario.tieneRol("AUDITOR")
                && path.equals("/admin/auditoria.jsp");

        if (!usuario.tieneRol(rolRequerido) && !usuario.tieneRol(ROL_ADMIN) && !esAuditorEnAuditoria) {
            res.sendRedirect(contexto + "/acceso_denegado.jsp");
            return;
        }

        chain.doFilter(request, response);
    }

    private Usuario getUsuario(HttpSession session) {
        if (session != null) {
            Object u = session.getAttribute("usuario");
            if (u instanceof Usuario) {
                return (Usuario) u;
            }
        }
        return null;
    }

    @Override
    public void destroy() {
    }
}