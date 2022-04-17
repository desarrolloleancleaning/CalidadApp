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

import com.leancleaning.calidad.WS.LlamadaGet;
import com.leancleaning.calidad.WS.LlamadaGetCalidad;
import com.leancleaning.calidad.WS.LlamadaPost;
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
import java.util.concurrent.Executors;

public class MainActivityFragment extends Fragment {

    Application application;

    public static String TAG = "MainActivity_fragment";
    private View fragmentView;
    private LoginActivity principal;
    private LlamadaGetCalidad llamadaGet, llamadaGet2;
    private String usuario_aux;
    private LlamadaPost llamadapost, llamadapost2, llamadapost3, llamadapost4;
    private TextView text_usuario,text_sede;

    ImageView image_estructura, image_procedimiento, image_datos_generales;

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




        image_estructura = fragmentView.findViewById(R.id.image_estructura);
        image_procedimiento = fragmentView.findViewById(R.id.image_procedimiento);
        image_datos_generales  = fragmentView.findViewById(R.id.image_datos_generales);

        text_usuario = fragmentView.findViewById(R.id.text_usuario);
        text_usuario.setText(Html.fromHtml("<b>"+getString(R.string.usuario_cabecera)+"</b>" + " " + LeancleaningUtils.getPreferencias("usuario_logueado", "", requireContext())));

        text_sede = fragmentView.findViewById(R.id.text_sede);


        if (LeancleaningUtils.getPreferencias("id_sede_seleccionada","",principal).equals(""))
            getusuariosedes();
        else
            text_sede.setText(Html.fromHtml("<b>"+getString(R.string.sede_cabecera)+"</b>" + " " + LeancleaningUtils.getPreferencias("nombre_sede_seleccionada", "", requireContext())));


        if (application.getRespuestas_estructura() != null && application.getRespuestas_estructura().size() > 0){
            image_estructura.setImageResource(R.drawable.jornadatrabajo_ok);
        }else{
            image_estructura.setImageResource(R.drawable.jornadatrabajo);
        }

        if (application.getRespuestas_procedimientos() != null && application.getRespuestas_procedimientos().size() > 0){
            image_procedimiento.setImageResource(R.drawable.comunicacion_ok);
        }else{
            image_procedimiento.setImageResource(R.drawable.comunicacion);
        }

        if (application.getCuestionario() != null && !application.getCuestionario().getCentro().equals("") ){
            image_datos_generales.setImageResource(R.drawable.registrotrabajo_ok);
        }else{
            image_datos_generales.setImageResource(R.drawable.registrotrabajo);
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



}
