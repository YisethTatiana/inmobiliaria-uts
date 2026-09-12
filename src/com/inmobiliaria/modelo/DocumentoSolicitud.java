package com.inmobiliaria.modelo;

import java.sql.Timestamp;

public class DocumentoSolicitud {
    private int idDocumento;
    private int idSolicitud;
    private String nombreArchivo;
    private String ruta;
    private String tipoDocumento;
    private String estado;
    private Timestamp fechaCarga;

    public DocumentoSolicitud() {}

    public int getIdDocumento() { return idDocumento; }
    public void setIdDocumento(int idDocumento) { this.idDocumento = idDocumento; }

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getRuta() { return ruta; }
    public void setRuta(String ruta) { this.ruta = ruta; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Timestamp getFechaCarga() { return fechaCarga; }
    public void setFechaCarga(Timestamp fechaCarga) { this.fechaCarga = fechaCarga; }
}