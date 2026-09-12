package com.inmobiliaria.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private int idUsuario;
    private String correo;
    private String passwordHash;
    private boolean activo;
    private Timestamp fechaCreacion;
    private String nombre;
    private String rol;
    private List<String> roles = new ArrayList<>();

    public Usuario() {}

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public boolean tieneRol(String rolBuscado) {
        if (roles != null) {
            for (String r : roles) {
                if (r.equalsIgnoreCase(rolBuscado)) {
                    return true;
                }
            }
        }
        return rol != null && rol.equalsIgnoreCase(rolBuscado);
    }

    public String getPanelSegunRol() {
        if (tieneRol("ADMINISTRADOR")) {
            return "/admin/dashboard_admin.jsp";
        }
        if (tieneRol("INMOBILIARIA")) {
            return "/agente/dashboard_inmobiliaria.jsp";
        }
        if (tieneRol("CLIENTE")) {
            return "/cliente/dashboard_cliente.jsp";
        }
        return "/index.jsp";
    }
}