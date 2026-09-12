package com.inmobiliaria.modelo;

import java.sql.Timestamp;

public class Solicitud {
    private int idSolicitud;
    private int idUsuarioCliente;
    private int idPropiedad;
    private String tipoSolicitud;
    private String estado;
    private Timestamp fechaSolicitud;
    private String observaciones;

    private String correoCliente;
    private String tituloPropiedad;

    public Solicitud() {}

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }

    public int getIdUsuarioCliente() { return idUsuarioCliente; }
    public void setIdUsuarioCliente(int idUsuarioCliente) { this.idUsuarioCliente = idUsuarioCliente; }

    public int getIdPropiedad() { return idPropiedad; }
    public void setIdPropiedad(int idPropiedad) { this.idPropiedad = idPropiedad; }

    public String getTipoSolicitud() { return tipoSolicitud; }
    public void setTipoSolicitud(String tipoSolicitud) { this.tipoSolicitud = tipoSolicitud; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Timestamp getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Timestamp fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getCorreoCliente() { return correoCliente; }
    public void setCorreoCliente(String correoCliente) { this.correoCliente = correoCliente; }

    public String getTituloPropiedad() { return tituloPropiedad; }
    public void setTituloPropiedad(String tituloPropiedad) { this.tituloPropiedad = tituloPropiedad; }
}