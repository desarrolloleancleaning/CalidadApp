package com.leancleaning.calidad.Clases;

import java.util.ArrayList;

public class RespuestasCalidad {

    private Area area;
    private ArrayList<Respuesta> respuestas;

    public RespuestasCalidad() {
    }

    public RespuestasCalidad(Area area, ArrayList<Respuesta> respuestas) {
        this.area = area;
        this.respuestas = respuestas;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public ArrayList<Respuesta> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(ArrayList<Respuesta> respuestas) {
        this.respuestas = respuestas;
    }
}
