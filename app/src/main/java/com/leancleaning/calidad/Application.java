package com.leancleaning.calidad;

import com.leancleaning.calidad.Clases.Cuestionario;
import com.leancleaning.calidad.Clases.Respuesta;
import com.leancleaning.calidad.Clases.RespuestasCalidad;

import java.util.ArrayList;

public class Application extends android.app.Application{

    public ArrayList<Respuesta> respuestas_estructura;
    public ArrayList<Respuesta> respuestas_procedimientos;
    public ArrayList<RespuestasCalidad> respuestas_calidad;

    private Cuestionario cuestionario;

    @Override
    public void onCreate() {

        respuestas_estructura = new ArrayList<>();
        respuestas_procedimientos = new ArrayList<>();

        super.onCreate();

    }



    public Cuestionario getCuestionario() {
        return cuestionario;
    }

    public void setCuestionario(Cuestionario cuestionario) {
        this.cuestionario = cuestionario;
    }



}
