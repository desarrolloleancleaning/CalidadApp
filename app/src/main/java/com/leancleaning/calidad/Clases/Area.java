package com.leancleaning.calidad.Clases;

import java.util.ArrayList;

public class Area {

    private int id_area;
    private String nombre;
    private String nfcentrada;
    private String nfcsalida;
    private int orden;

    public Area() {
    }

    public int getId_area() {
        return id_area;
    }

    public void setId_area(int id_area) {
        this.id_area = id_area;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNfcentrada() {
        return nfcentrada;
    }

    public void setNfcentrada(String nfcentrada) {
        this.nfcentrada = nfcentrada;
    }

    public String getNfcsalida() {
        return nfcsalida;
    }

    public void setNfcsalida(String nfcsalida) {
        this.nfcsalida = nfcsalida;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}
