package com.inmobiliaria.modelo;

public class Caracteristica {
    private int idCaracteristica;
    private String nombre;
    private int cantidad;

    public Caracteristica() {}

    public int getIdCaracteristica() { return idCaracteristica; }
    public void setIdCaracteristica(int idCaracteristica) { this.idCaracteristica = idCaracteristica; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}