package com.leancleaning.calidad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.leancleaning.calidad.Clases.Respuesta;
import com.leancleaning.calidad.Clases.RespuestasCalidad;
import com.leancleaning.calidad.WS.LlamadaGet;
import com.leancleaning.calidad.WS.LlamadaGetCalidad;
import com.leancleaning.calidad.WS.LlamadaPost;
import com.leancleaning.calidad.calidad.CalidadFragment;
import com.leancleaning.calidad.datosgenerales.DatosGeneralesFragment;
import com.leancleaning.calidad.estructura.EstructuraFragment;
import com.leancleaning.calidad.procedimientos.ProcedimientosFragment;
import com.leancleaning.calidad.utils.AsyncListener;
import com.leancleaning.calidad.utils.LeancleaningUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Executors;

public class MainActivityFragment extends Fragment {

    Application application;

    public static String TAG = "MainActivity_fragment";
    private View fragmentView;
    private LoginActivity principal;
    private LlamadaGetCalidad llamadaGet;
    private LlamadaPost llamadaPost;
    private TextView text_usuario,text_sede;

    ImageView image_estructura, image_procedimiento, image_datos_generales, image_calidad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.dashboard, container, false);

        application = (Application) getActivity().getApplication();

        principal = (LoginActivity) getActivity();

        LinearLayout estructura = fragmentView.findViewById(R.id.estructura);
        estructura.setClickable(true);
        estructura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                principal.transitionToFragment(EstructuraFragment.class.getName(), EstructuraFragment.TAG, true, null);
            }
        });

        LinearLayout procedimiento = fragmentView.findViewById(R.id.procedimiento);
        procedimiento.setClickable(true);
        procedimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                principal.transitionToFragment(ProcedimientosFragment.class.getName(), ProcedimientosFragment.TAG, true, null);
            }
        });

        LinearLayout datos_generales = fragmentView.findViewById(R.id.datos_generales);
        datos_generales.setClickable(true);
        datos_generales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                principal.transitionToFragment(DatosGeneralesFragment.class.getName(), DatosGeneralesFragment.TAG, true, null);
            }
        });

        LinearLayout calidad = fragmentView.findViewById(R.id.calidad);
        calidad.setClickable(true);
        calidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                principal.transitionToFragment(CalidadFragment.class.getName(), CalidadFragment.TAG, true, null);
            }
        });


        LinearLayout linear_img_envio = fragmentView.findViewById(R.id.linear_img_envio);
        linear_img_envio.setVisibility(View.GONE);
        linear_img_envio.setClickable(true);
        linear_img_envio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviar_cuestionario();
            }
        });



        image_estructura = fragmentView.findViewById(R.id.image_estructura);
        image_procedimiento = fragmentView.findViewById(R.id.image_procedimiento);
        image_datos_generales  = fragmentView.findViewById(R.id.image_datos_generales);
        image_calidad  = fragmentView.findViewById(R.id.image_calidad);

        text_usuario = fragmentView.findViewById(R.id.text_usuario);
        text_usuario.setText(Html.fromHtml("<b>"+getString(R.string.usuario_cabecera)+"</b>" + " " + LeancleaningUtils.getPreferencias("usuario_logueado", "", requireContext())));

        text_sede = fragmentView.findViewById(R.id.text_sede);


        if (LeancleaningUtils.getPreferencias("id_sede_seleccionada","",principal).equals(""))
            getusuariosedes();
        else
            text_sede.setText(Html.fromHtml("<b>"+getString(R.string.sede_cabecera)+"</b>" + " " + LeancleaningUtils.getPreferencias("nombre_sede_seleccionada", "", requireContext())));


        if (application.respuestas_estructura != null && application.respuestas_estructura.size() > 0){
            image_estructura.setImageResource(R.drawable.jornadatrabajo_ok);
        }else{
            image_estructura.setImageResource(R.drawable.jornadatrabajo);
        }

        if (application.respuestas_procedimientos != null && application.respuestas_procedimientos.size() > 0){
            image_procedimiento.setImageResource(R.drawable.comunicacion_ok);
        }else{
            image_procedimiento.setImageResource(R.drawable.comunicacion);
        }

        if (application.getCuestionario() != null && !application.getCuestionario().getCentro().equals("") ){
            image_datos_generales.setImageResource(R.drawable.registrotrabajo_ok);
        }else{
            image_datos_generales.setImageResource(R.drawable.registrotrabajo);
        }

        if (application.respuestas_calidad != null && application.respuestas_calidad.size() >0 ){
            image_calidad.setImageResource(R.drawable.iconopresencia_ok);
        }else{
            image_calidad.setImageResource(R.drawable.iconopresencia);
        }

        /*if (application.getCuestionario() != null && !application.getCuestionario().getCentro().equals("")
        && (application.respuestas_calidad != null && application.respuestas_calidad.size() >0 )){
            linear_img_envio.setVisibility(View.VISIBLE);
        }else{
            linear_img_envio.setVisibility(View.GONE);
        }*/

        if (application.getCuestionario() != null && !application.getCuestionario().getCentro().equals("")){
            if ((application.respuestas_calidad != null && application.respuestas_calidad.size() >0)
                || (application.respuestas_procedimientos != null && application.respuestas_procedimientos.size() >0)
                    || (application.respuestas_estructura != null && application.respuestas_estructura.size() >0))  {
                linear_img_envio.setVisibility(View.VISIBLE);
            }else{
                linear_img_envio.setVisibility(View.GONE);
            }
        }else{
            linear_img_envio.setVisibility(View.GONE);
        }



        return fragmentView;
    }



    private void getusuariosedes(){

        llamadaGet = new LlamadaGetCalidad("getusuariosedes" + "&usuario=" + LeancleaningUtils.getPreferencias("id_usuario_logueado", "", requireContext()) , 10000, true, "Descargando sedes...", getContext());
        llamadaGet.execute("");

        llamadaGet.completionCode = new AsyncListener() {
            @Override
            public void onComplete() {
                if (llamadaGet.isLoading())
                    llamadaGet.quitarProgressDialog();

                if (llamadaGet.gethttpStatus() == 200) {
                    try {

                        JSONObject result = new JSONObject(llamadaGet.getResultado());

                        Log.d("RES","RES  "+llamadaGet.getResultado());

                        if (!result.getBoolean("success")) {
                            Toast.makeText(getActivity(), result.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {

                                JSONArray data = result.getJSONArray("data");
                                if (data.length()==1){
                                    JSONObject json_sede = data.getJSONObject(0);
                                    LeancleaningUtils.setPreferencias("id_sede_seleccionada",json_sede.getString("id_sede"),getContext());
                                    LeancleaningUtils.setPreferencias("nombre_sede_seleccionada",json_sede.getJSONObject("idSede").getString("nombre"),getContext());

                                    text_sede.setText(Html.fromHtml("<b>"+getString(R.string.sede_cabecera)+"</b>" + " " + LeancleaningUtils.getPreferencias("nombre_sede_seleccionada", "", requireContext())));

                                }else{
                                    if (data.length()>0){
                                        seleccion_sede(data);
                                    }else{
                                        Toast.makeText(getActivity(), getString(R.string.error_sedes_usuario), Toast.LENGTH_SHORT).show();
                                    }
                                }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error: problemas al descargar las sedes", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Problemas al descargar las sedes", Toast.LENGTH_SHORT).show();

                }
            }
        };


    }


    public void seleccion_sede(JSONArray data ){

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(principal);

            builder.setTitle(getString(R.string.seleccion_sede));
            builder.setMessage(getString(R.string.seleccion_sede_text));
            builder.setCancelable(false);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20,0,20,0);

            Context context = builder.getContext();
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(layoutParams);

            for (int i = 0; i<data.length(); i++){
                JSONObject sede_object = data.getJSONObject(i).getJSONObject("idSede");

                final EditText tv_sede = new EditText(principal);
                tv_sede.setFocusable(false);
                tv_sede.setText(sede_object.getString("nombre"));
                tv_sede.setTag(sede_object.getString("idSede"));
                tv_sede.setClickable(true);

                tv_sede.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(),"Sede seleccionada: "+ tv_sede.getText() , Toast.LENGTH_LONG).show();

                        LeancleaningUtils.setPreferencias("id_sede_seleccionada",tv_sede.getTag().toString(),getContext());
                        LeancleaningUtils.setPreferencias("nombre_sede_seleccionada", tv_sede.getText().toString(),getContext());

                        text_sede.setText(Html.fromHtml("<b>"+getString(R.string.sede_cabecera)+"</b>" + " " + LeancleaningUtils.getPreferencias("nombre_sede_seleccionada", "", requireContext())));

                    }
                });

                layout.addView(tv_sede,layoutParams);
            }

            builder.setView(layout);

            builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {


                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (text_sede.getText().equals(getString(R.string.sede_cabecera))){
                        Toast.makeText(getActivity(),"Debe seleccionar una sede para continuar" , Toast.LENGTH_LONG).show();
                    }else{
                        // Canceled.
                        dialog.dismiss();
                    }
                }
            });

        }catch (Exception e){

        }

    }



    private void enviar_cuestionario(){


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.atencion));
        builder.setMessage(getString(R.string.seguro_enviar));

        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                envio_datos();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private double calcular_nota_cuestionario(){
        boolean calidad = false;
        double nota_calidad = 0;

        boolean procedimientos = false;
        double nota_procedimientos = 0;

        boolean estructura = false;
        double nota_estructura = 0;

        if (application.respuestas_calidad != null && application.respuestas_calidad.size() >0 ){
            calidad = true;

            int cont_parcial = 0;
            int cont_total = 0;
            for (RespuestasCalidad resp:application.respuestas_calidad) {

                for (Respuesta res: resp.getRespuestas()){
                    int puntuacion_respuesta = 0;
                    if (res.getNivel1() == 1){
                        puntuacion_respuesta = 1;
                    }else if (res.getNivel2() == 1){
                        puntuacion_respuesta = 2;
                    }else if (res.getNivel3() == 1){
                        puntuacion_respuesta = 3;
                    }else if (res.getNivel4() == 1){
                        puntuacion_respuesta = 4;
                    }else if (res.getNivel5() == 1){
                        puntuacion_respuesta = 5;
                    }
                    puntuacion_respuesta = puntuacion_respuesta * res.getNivelK();
                    cont_parcial += puntuacion_respuesta;
                    cont_total += 5 * res.getNivelK();
                }
            }

            nota_calidad = ((double)cont_parcial/(double)cont_total)*100;

        }

        if (application.respuestas_estructura != null && application.respuestas_estructura.size() > 0){
            estructura = true;

            int cont_si = 0;
            for (Respuesta res:application.respuestas_estructura) {
                if (res.getNivelSi()==1)cont_si++;
            }
            nota_estructura = ((double)cont_si/(double)application.respuestas_estructura.size())*100;

        }

        if (application.respuestas_procedimientos != null && application.respuestas_procedimientos.size() > 0){
            procedimientos = true;

            int cont_si = 0;
            for (Respuesta res:application.respuestas_procedimientos) {
                if (res.getNivelSi()==1)cont_si++;
            }
            nota_procedimientos = ((double)cont_si/(double)application.respuestas_procedimientos.size())*100;
            Log.d("RES","RES: "+(double)cont_si/(double)application.respuestas_procedimientos.size());
        }

        if (procedimientos && estructura && calidad){
            return ((nota_procedimientos *20)+(nota_estructura*20)+(nota_calidad*60))/100;
        }

        if (procedimientos && estructura ){
            return ((nota_procedimientos *50)+(nota_estructura*50))/100;
        }

        if (procedimientos && calidad ){
            return ((nota_procedimientos *30)+(nota_calidad*70))/100;
        }

        if (estructura && calidad ){
            return ((nota_estructura *30)+(nota_calidad*70))/100;
        }

        if (estructura) return nota_estructura;
        if (calidad) return nota_calidad;
        if (procedimientos) return nota_procedimientos;

        return 0.0;
    }

    private void envio_datos(){
        Log.d("ENVIO","ENVIO");

        calcular_nota_cuestionario();

        JSONObject json_cuestionario = new JSONObject();

        try {
            json_cuestionario = new JSONObject();
            json_cuestionario.put("centro", application.getCuestionario().getCentro());
            json_cuestionario.put("idSede", application.getCuestionario().getIdSede());
            json_cuestionario.put("departamento", application.getCuestionario().getDepartamento());
            json_cuestionario.put("responsable", application.getCuestionario().getResponsable());
            json_cuestionario.put("fecha", application.getCuestionario().getFecha());
            json_cuestionario.put("objetivo", application.getCuestionario().getObjetivo());
            json_cuestionario.put("supervisor", application.getCuestionario().getSupervisor());
            json_cuestionario.put("valoracion", calcular_nota_cuestionario());
            json_cuestionario.put("desviacion", application.getCuestionario().getDesviacion());
            json_cuestionario.put("evaluador", application.getCuestionario().getEvaluador());
            json_cuestionario.put("observaciones", application.getCuestionario().getObservaciones());
            json_cuestionario.put("idFirmaEvaluador", "");
            json_cuestionario.put("idFirmaResponsable", "");

            JSONArray array_res = new JSONArray();
            for (int t = 0; t < application.respuestas_calidad.size(); t++) {
                RespuestasCalidad respuestasCalidad = application.respuestas_calidad.get(t);

                for (Respuesta res_cues:respuestasCalidad.getRespuestas()){
                    JSONObject respuesas = new JSONObject();
                    respuesas.put("idPregunta", res_cues.getIdPregunta());
                    respuesas.put("idArea",res_cues.getIdArea() );
                    respuesas.put("nivel1",res_cues.getNivel1());
                    respuesas.put("nivel2", res_cues.getNivel2());
                    respuesas.put("nivel3", res_cues.getNivel3());
                    respuesas.put("nivel4", res_cues.getNivel4());
                    respuesas.put("nivel5", res_cues.getNivel5());
                    respuesas.put("nivelSi", res_cues.getNivelSi());
                    respuesas.put("nivelNo", res_cues.getNivelNo());

                    array_res.put(respuesas);
                }

            }

            if (application.respuestas_estructura != null && application.respuestas_estructura.size() > 0){
                for (Respuesta res_cues:application.respuestas_estructura){
                    JSONObject respuesas = new JSONObject();
                    respuesas.put("idPregunta", res_cues.getIdPregunta());
                    respuesas.put("idArea",res_cues.getIdArea() );
                    respuesas.put("nivel1",res_cues.getNivel1());
                    respuesas.put("nivel2", res_cues.getNivel2());
                    respuesas.put("nivel3", res_cues.getNivel3());
                    respuesas.put("nivel4", res_cues.getNivel4());
                    respuesas.put("nivel5", res_cues.getNivel5());
                    respuesas.put("nivelSi", res_cues.getNivelSi());
                    respuesas.put("nivelNo", res_cues.getNivelNo());

                    array_res.put(respuesas);
                }
            }

            if (application.respuestas_procedimientos != null && application.respuestas_procedimientos.size() > 0){
                for (Respuesta res_cues:application.respuestas_procedimientos){
                    JSONObject respuesas = new JSONObject();
                    respuesas.put("idPregunta", res_cues.getIdPregunta());
                    respuesas.put("idArea",res_cues.getIdArea() );
                    respuesas.put("nivel1",res_cues.getNivel1());
                    respuesas.put("nivel2", res_cues.getNivel2());
                    respuesas.put("nivel3", res_cues.getNivel3());
                    respuesas.put("nivel4", res_cues.getNivel4());
                    respuesas.put("nivel5", res_cues.getNivel5());
                    respuesas.put("nivelSi", res_cues.getNivelSi());
                    respuesas.put("nivelNo", res_cues.getNivelNo());

                    array_res.put(respuesas);
                }
            }

            json_cuestionario.put("detalles", array_res);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("Datos envio", "Datos envio: "+ json_cuestionario.toString());

        llamadaPost = new LlamadaPost("registrarcuestionario", json_cuestionario.toString(), 10000, true, "Cargando...", null, null, requireContext());
        llamadaPost.execute("");
        llamadaPost.completionCode = new AsyncListener() {
            @Override
            public void onComplete() {
                String resultado = llamadaPost.getResultado();

                if (llamadaPost.isLoading())
                    llamadaPost.quitarProgressDialog();

                Log.d("RES","RES: "+ resultado);

                if (resultado == null) {
                    Toast.makeText(getActivity(), "Error al enviar el cuestionario, inténtalo de nuevo", Toast.LENGTH_SHORT).show();
                } else if (resultado.equals("")) {
                    Toast.makeText(getActivity(), "Error al enviar el cuestionario, inténtalo de nuevo", Toast.LENGTH_SHORT).show();
                }  else {

                    try {
                        JSONObject result = new JSONObject(llamadaPost.getResultado());
                        if (result.getBoolean("success")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                            builder.setTitle(getString(R.string.atencion));
                            builder.setCancelable(false);
                            builder.setMessage(getString(R.string.formulario_ok_enviado));

                            builder.setNegativeButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    LeancleaningUtils.setPreferencias("usuario_logueado","",getContext());
                                    LeancleaningUtils.setPreferencias("pass","",getContext());
                                    LeancleaningUtils.setPreferencias("rol","",getContext());
                                    LeancleaningUtils.setPreferencias("id_empresa","",getContext());
                                    LeancleaningUtils.setPreferencias("id_usuario_logueado","",getContext());
                                    LeancleaningUtils.setPreferencias("nombre_evaluador","" ,getContext());

                                    principal.finishAffinity();
                                    System.exit(0);

                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }else{
                            Toast.makeText(getActivity(), "Error al enviar el cuestionario, inténtalo de nuevo", Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e){
                        Toast.makeText(getActivity(), "Error al enviar el cuestionario, inténtalo de nuevo", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        };

    }

}
