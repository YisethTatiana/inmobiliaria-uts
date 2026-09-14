package com.inmobiliaria.modelo;

public class ResultadoLogin {

    public static final int OK = 0;
    public static final int INACTIVA = 1;
    public static final int BLOQUEADO = 2;
    public static final int CREDENCIALES = 3;
    public static final int ERROR = 4;

    private int estado;
    private Usuario usuario;
    private int minutosRestantes;

    public ResultadoLogin(int estado) {
        this.estado = estado;
    }

    public ResultadoLogin(int estado, Usuario usuario) {
        this.estado = estado;
        this.usuario = usuario;
    }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public int getMinutosRestantes() { return minutosRestantes; }
    public void setMinutosRestantes(int minutosRestantes) { this.minutosRestantes = minutosRestantes; }
}