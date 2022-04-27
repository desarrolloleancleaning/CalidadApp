package com.leancleaning.calidad.Clases;

import java.io.Serializable;

public class Respuesta implements Cloneable {

    private int idCuestionarioDetalle;
    private int idCuestionario;
    private int idPregunta;
    private int idArea;
    private int nivel1;
    private int nivel2;
    private int nivel3;
    private int nivel4;
    private int nivel5;
    private int nivelSi;
    private int nivelNo;
    private boolean contestado;
    private int nivelK;

    public Respuesta() {
    }

    public Respuesta(int idCuestionarioDetalle, int idCuestionario, int idPregunta, int idArea, int nivel1, int nivel2, int nivel3, int nivel4, int nivel5, int nivelSi, int nivelNo) {
        this.idCuestionarioDetalle = idCuestionarioDetalle;
        this.idCuestionario = idCuestionario;
        this.idPregunta = idPregunta;
        this.idArea = idArea;
        this.nivel1 = nivel1;
        this.nivel2 = nivel2;
        this.nivel3 = nivel3;
        this.nivel4 = nivel4;
        this.nivel5 = nivel5;
        this.nivelSi = nivelSi;
        this.nivelNo = nivelNo;
    }

    public int getIdCuestionarioDetalle() {
        return idCuestionarioDetalle;
    }

    public void setIdCuestionarioDetalle(int idCuestionarioDetalle) {
        this.idCuestionarioDetalle = idCuestionarioDetalle;
    }

    public int getIdCuestionario() {
        return idCuestionario;
    }

    public void setIdCuestionario(int idCuestionario) {
        this.idCuestionario = idCuestionario;
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

    public int getNivel1() {
        return nivel1;
    }

    public void setNivel1(int nivel1) {
        this.nivel1 = nivel1;
    }

    public int getNivel2() {
        return nivel2;
    }

    public void setNivel2(int nivel2) {
        this.nivel2 = nivel2;
    }

    public int getNivel3() {
        return nivel3;
    }

    public void setNivel3(int nivel3) {
        this.nivel3 = nivel3;
    }

    public int getNivel4() {
        return nivel4;
    }

    public void setNivel4(int nivel4) {
        this.nivel4 = nivel4;
    }

    public int getNivel5() {
        return nivel5;
    }

    public void setNivel5(int nivel5) {
        this.nivel5 = nivel5;
    }

    public int getNivelSi() {
        return nivelSi;
    }

    public void setNivelSi(int nivelSi) {
        this.nivelSi = nivelSi;
    }

    public int getNivelNo() {
        return nivelNo;
    }

    public void setNivelNo(int nivelNo) {
        this.nivelNo = nivelNo;
    }

    public boolean isContestado() {
        return contestado;
    }

    public void setContestado(boolean contestado) {
        this.contestado = contestado;
    }

    public int getNivelK() {
        return nivelK;
    }

    public void setNivelK(int nivelK) {
        this.nivelK = nivelK;
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch( CloneNotSupportedException e )
        {
            return null;
        }
    }


}
