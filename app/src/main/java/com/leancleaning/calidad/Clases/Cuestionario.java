package com.leancleaning.calidad.Clases;

public class Cuestionario {
    
    private int idCuestionario;
    private String centro ;
    private int idSede ;
    private String departamento;
    private String responsable ;
    private String fecha  ;
    private String objetivo ;
    private String supervisor;
    private double valoracion ;
    private double valoracionAct  ;
    private double valoracionAnt  ;
    private double desviacion  ;
    private String evaluador ;
    private String observaciones ;
    private int idFirmaEvaluador;
    private int idFirmaResponsable ;

    public Cuestionario() {
    }

    public int getIdCuestionario() {
        return idCuestionario;
    }

    public void setIdCuestionario(int idCuestionario) {
        this.idCuestionario = idCuestionario;
    }

    public String getCentro() {
        return centro;
    }

    public void setCentro(String centro) {
        this.centro = centro;
    }

    public int getIdSede() {
        return idSede;
    }

    public void setIdSede(int idSede) {
        this.idSede = idSede;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public double getValoracion() {
        return valoracion;
    }

    public void setValoracion(double valoracion) {
        this.valoracion = valoracion;
    }

    public double getValoracionAct() {
        return valoracionAct;
    }

    public void setValoracionAct(double valoracionAct) {
        this.valoracionAct = valoracionAct;
    }

    public double getValoracionAnt() {
        return valoracionAnt;
    }

    public void setValoracionAnt(double valoracionAnt) {
        this.valoracionAnt = valoracionAnt;
    }

    public double getDesviacion() {
        return desviacion;
    }

    public void setDesviacion(double desviacion) {
        this.desviacion = desviacion;
    }

    public String getEvaluador() {
        return evaluador;
    }

    public void setEvaluador(String evaluador) {
        this.evaluador = evaluador;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getIdFirmaEvaluador() {
        return idFirmaEvaluador;
    }

    public void setIdFirmaEvaluador(int idFirmaEvaluador) {
        this.idFirmaEvaluador = idFirmaEvaluador;
    }

    public int getIdFirmaResponsable() {
        return idFirmaResponsable;
    }

    public void setIdFirmaResponsable(int idFirmaResponsable) {
        this.idFirmaResponsable = idFirmaResponsable;
    }
}
