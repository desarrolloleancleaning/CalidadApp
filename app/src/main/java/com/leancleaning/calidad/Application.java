package com.leancleaning.calidad;

import com.leancleaning.calidad.Clases.Cuestionario;
import com.leancleaning.calidad.Clases.Respuesta;

import java.util.ArrayList;

public class Application extends android.app.Application{

    private ArrayList<Respuesta> respuestas_estructura;
    private ArrayList<Respuesta> respuestas_procedimientos;
    private Cuestionario cuestionario;

    @Override
    public void onCreate() {

        respuestas_estructura = new ArrayList<>();

        super.onCreate();

    }

    public ArrayList<Respuesta> getRespuestas_estructura() {
        return respuestas_estructura;
    }

    public void setRespuestas_estructura(ArrayList<Respuesta> respuestas_estructura) {
        this.respuestas_estructura = respuestas_estructura;
    }

    public ArrayList<Respuesta> getRespuestas_procedimientos() {
        return respuestas_procedimientos;
    }

    public void setRespuestas_procedimientos(ArrayList<Respuesta> respuestas_procedimientos) {
        this.respuestas_procedimientos = respuestas_procedimientos;
    }

    public Cuestionario getCuestionario() {
        return cuestionario;
    }

    public void setCuestionario(Cuestionario cuestionario) {
        this.cuestionario = cuestionario;
    }
}
