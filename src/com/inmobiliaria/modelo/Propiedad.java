package com.inmobiliaria.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Propiedad {
    private int idPropiedad;
    private String matriculaInmobiliaria;
    private String titulo;
    private String descripcion;
    private BigDecimal precio;
    private String direccion;
    private String ubicacion;
    private BigDecimal area;
    private int habitaciones;
    private int banios;
    private int parqueaderos;
    private String estado;
    private int idCiudad;
    private int idTipo;
    private int idInmobiliaria;

    private String nombreCiudad;
    private String nombreTipo;
    private String nombreInmobiliaria;
    private String telefonoInmobiliaria;
    private String emailInmobiliaria;
    private List<ImagenPropiedad> imagenes = new ArrayList<>();
    private List<Caracteristica> caracteristicas = new ArrayList<>();

    public Propiedad() {}

    public int getIdPropiedad() { return idPropiedad; }
    public void setIdPropiedad(int idPropiedad) { this.idPropiedad = idPropiedad; }

    public String getMatriculaInmobiliaria() { return matriculaInmobiliaria; }
    public void setMatriculaInmobiliaria(String matriculaInmobiliaria) { this.matriculaInmobiliaria = matriculaInmobiliaria; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }

    public int getHabitaciones() { return habitaciones; }
    public void setHabitaciones(int habitaciones) { this.habitaciones = habitaciones; }

    public int getBanios() { return banios; }
    public void setBanios(int banios) { this.banios = banios; }

    public int getParqueaderos() { return parqueaderos; }
    public void setParqueaderos(int parqueaderos) { this.parqueaderos = parqueaderos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdCiudad() { return idCiudad; }
    public void setIdCiudad(int idCiudad) { this.idCiudad = idCiudad; }

    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }

    public int getIdInmobiliaria() { return idInmobiliaria; }
    public void setIdInmobiliaria(int idInmobiliaria) { this.idInmobiliaria = idInmobiliaria; }

    public String getNombreCiudad() { return nombreCiudad; }
    public void setNombreCiudad(String nombreCiudad) { this.nombreCiudad = nombreCiudad; }

    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }

    public String getNombreInmobiliaria() { return nombreInmobiliaria; }
    public void setNombreInmobiliaria(String nombreInmobiliaria) { this.nombreInmobiliaria = nombreInmobiliaria; }

    public String getTelefonoInmobiliaria() { return telefonoInmobiliaria; }
    public void setTelefonoInmobiliaria(String telefonoInmobiliaria) { this.telefonoInmobiliaria = telefonoInmobiliaria; }

    public String getEmailInmobiliaria() { return emailInmobiliaria; }
    public void setEmailInmobiliaria(String emailInmobiliaria) { this.emailInmobiliaria = emailInmobiliaria; }

    public List<ImagenPropiedad> getImagenes() { return imagenes; }
    public void setImagenes(List<ImagenPropiedad> imagenes) { this.imagenes = imagenes; }

    public List<Caracteristica> getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(List<Caracteristica> caracteristicas) { this.caracteristicas = caracteristicas; }

    public String getImagenPrincipal() {
        for (ImagenPropiedad img : imagenes) {
            if (img.isPrincipal()) {
                return img.getRuta();
            }
        }
        if (!imagenes.isEmpty()) {
            return imagenes.get(0).getRuta();
        }
        return "";
    }
}