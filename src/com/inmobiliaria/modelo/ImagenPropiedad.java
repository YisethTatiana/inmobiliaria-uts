package com.inmobiliaria.modelo;

public class ImagenPropiedad {
    private int idImagen;
    private int idPropiedad;
    private String ruta;
    private String titulo;
    private boolean principal;

    public ImagenPropiedad() {}

    public int getIdImagen() { return idImagen; }
    public void setIdImagen(int idImagen) { this.idImagen = idImagen; }

    public int getIdPropiedad() { return idPropiedad; }
    public void setIdPropiedad(int idPropiedad) { this.idPropiedad = idPropiedad; }

    public String getRuta() { return ruta; }
    public void setRuta(String ruta) { this.ruta = ruta; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public boolean isPrincipal() { return principal; }
    public void setPrincipal(boolean principal) { this.principal = principal; }
}