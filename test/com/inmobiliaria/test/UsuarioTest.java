package com.inmobiliaria.test;

import com.inmobiliaria.modelo.Usuario;
import java.util.Arrays;

public class UsuarioTest {

    public static void ejecutar() {
        Usuario admin = new Usuario();
        admin.setRoles(Arrays.asList("CLIENTE", "INMOBILIARIA", "ADMINISTRADOR"));
        if (!admin.tieneRol("administrador")) {
            throw new AssertionError("tieneRol debe ignorar mayusculas/minusculas");
        }
        if (!admin.tieneRol("INMOBILIARIA")) {
            throw new AssertionError("Un usuario puede tener varios roles");
        }
        if (admin.tieneRol("AUDITOR")) {
            throw new AssertionError("No debe tener un rol que no se le asigno");
        }
        if (!"/admin/dashboard_admin.jsp".equals(admin.getPanelSegunRol())) {
            throw new AssertionError("El panel del administrador debe tener prioridad");
        }

        Usuario agente = new Usuario();
        agente.setRoles(Arrays.asList("CLIENTE", "INMOBILIARIA"));
        if (!"/agente/dashboard_inmobiliaria.jsp".equals(agente.getPanelSegunRol())) {
            throw new AssertionError("El panel del agente inmobiliario es incorrecto");
        }

        Usuario cliente = new Usuario();
        cliente.setRoles(Arrays.asList("CLIENTE"));
        if (!"/cliente/dashboard_cliente.jsp".equals(cliente.getPanelSegunRol())) {
            throw new AssertionError("El panel del cliente es incorrecto");
        }

        Usuario visitante = new Usuario();
        if (!"/index.jsp".equals(visitante.getPanelSegunRol())) {
            throw new AssertionError("Sin roles debe redirigir al inicio");
        }
        if (visitante.tieneRol("CLIENTE")) {
            throw new AssertionError("Sin roles no debe tener rol alguno");
        }

        Usuario conRolSimple = new Usuario();
        conRolSimple.setRol("CLIENTE");
        if (!conRolSimple.tieneRol("cliente")) {
            throw new AssertionError("Debe considerar el campo rol como respaldo");
        }

        System.out.println("[OK] UsuarioTest: roles multiples, case-insensitive y panel por rol");
    }
}