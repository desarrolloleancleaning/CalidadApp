package com.leancleaning.calidad.Clases;

public class Pregunta {

    private int idPregunta;
    private int idArea;
    private String detalle;
    private int idTipoAuditoria;
    private int nivelK;

    public Pregunta() {
    }

    public Pregunta(int idPregunta, int idArea, String detalle, int idTipoAuditoria, int nivelK) {
        this.idPregunta = idPregunta;
        this.idArea = idArea;
        this.detalle = detalle;
        this.idTipoAuditoria = idTipoAuditoria;
        this.nivelK = nivelK;
    }

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    public int getIdArea() {
        return idArea;
    }

    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public int getIdTipoAuditoria() {
        return idTipoAuditoria;
    }

    public void setIdTipoAuditoria(int idTipoAuditoria) {
        this.idTipoAuditoria = idTipoAuditoria;
    }

    public int getNivelK() {
        return nivelK;
    }

    public void setNivelK(int nivelK) {
        this.nivelK = nivelK;
    }
}
