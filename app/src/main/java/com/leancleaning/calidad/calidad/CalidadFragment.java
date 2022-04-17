package com.leancleaning.calidad.calidad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.leancleaning.calidad.Application;
import com.leancleaning.calidad.Clases.Area;
import com.leancleaning.calidad.Clases.Pregunta;
import com.leancleaning.calidad.Clases.Respuesta;
import com.leancleaning.calidad.R;
import com.leancleaning.calidad.WS.LlamadaGetCalidad;
import com.leancleaning.calidad.procedimientos.ProcedimientosFragment;
import com.leancleaning.calidad.utils.AsyncListener;
import com.leancleaning.calidad.utils.LeancleaningUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CalidadFragment extends Fragment {

    Application application;

    public static String TAG = "calidad_fragment";
    private View fragmentView;

    private TextView text_usuario,text_sede;
    private LlamadaGetCalidad llamadaGet;

    private ListView lista_preguntas;

    private ArrayList<Area> areas;

    private ArrayList<Pregunta> preguntas;

    private ArrayList<Respuesta> respuestas;

    ImageView boton_guardar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.calidad, container, false);

        application = (Application) getActivity().getApplication();

        text_usuario = fragmentView.findViewById(R.id.text_usuario);
        text_usuario.setText(Html.fromHtml("<b>"+getString(R.string.usuario_cabecera)+"</b>" + " " + LeancleaningUtils.getPreferencias("usuario_logueado", "", requireContext())));

        text_sede = fragmentView.findViewById(R.id.text_sede);
        text_sede.setText(Html.fromHtml("<b>"+getString(R.string.sede_cabecera)+"</b>" + " " + LeancleaningUtils.getPreferencias("nombre_sede_seleccionada", "", requireContext())));

        //EVITAR QUE SE HAGA CLICK EN EL FRAGMENT ANTERIOR
        fragmentView.setClickable(true);
        fragmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        lista_preguntas = fragmentView.findViewById(R.id.listaPreguntas);

        respuestas = new ArrayList<>();

        cargar_areas_sede();

        boton_guardar = fragmentView.findViewById(R.id.boton_guardar);
        boton_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Guardar","Guardar");
                guardar_datos();
            }
        });

        return fragmentView;
    }



    private void cargar_areas_sede(){

        String sede = LeancleaningUtils.getPreferencias("id_sede_seleccionada", "", requireContext());

        llamadaGet = new LlamadaGetCalidad("getareasporsede" + "&idSede=" + sede   , 10000, true, "Descargando sedes...", getContext());
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

                            areas = new ArrayList<>();
                            for (int i = 0; i< data.length(); i++){

                                JSONObject json_area = data.getJSONObject(i);

                                Area aux = new Area();
                                aux.setId_area(Integer.valueOf(json_area.getString("idArea")));
                                aux.setNombre(json_area.getString("nombre"));

                                areas.add(aux);
                            }

                            continuar_proceso();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error: problemas al descargar las areas", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Problemas al descargar las areas", Toast.LENGTH_SHORT).show();

                }
            }
        };


    }

    public void continuar_proceso(){
        Log.d("Llega","Llega");

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(getString(R.string.seleccion_area));
            builder.setMessage(getString(R.string.seleccion_area_text));
            builder.setCancelable(false);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20,0,20,0);

            Context context = builder.getContext();
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(layoutParams);

            for (int i = 0; i<areas.size(); i++){
                Area area = areas.get(i);

                final EditText tv_area = new EditText(getActivity());
                tv_area.setFocusable(false);
                tv_area.setText(area.getNombre());
                tv_area.setTag(area.getId_area());
                tv_area.setClickable(true);

                tv_area.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(),"Area seleccionada: "+ tv_area.getText() , Toast.LENGTH_LONG).show();

                    }
                });

                layout.addView(tv_area,layoutParams);
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


    public void guardar_datos(){

    }

}
