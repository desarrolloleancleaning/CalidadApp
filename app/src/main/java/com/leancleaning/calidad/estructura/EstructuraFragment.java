package com.leancleaning.calidad.estructura;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.leancleaning.calidad.Application;
import com.leancleaning.calidad.Clases.Pregunta;
import com.leancleaning.calidad.Clases.Respuesta;
import com.leancleaning.calidad.R;
import com.leancleaning.calidad.WS.LlamadaGetCalidad;
import com.leancleaning.calidad.utils.AsyncListener;
import com.leancleaning.calidad.utils.LeancleaningUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class EstructuraFragment extends Fragment {

    Application application;

    public static String TAG = "estructura_fragment";
    private View fragmentView;

    private TextView text_usuario,text_sede;
    private LlamadaGetCalidad llamadaGet;

    private ListView lista_preguntas;

    private ArrayList<Pregunta> preguntas;

    private ArrayList<Respuesta> respuestas;

    ImageView boton_guardar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.estructura, container, false);

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

        cargar_preguntas_estructura();

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


    private void cargar_preguntas_estructura(){

        String sede = LeancleaningUtils.getPreferencias("id_sede_seleccionada", "", requireContext());
        String tipo_pregunta = "2"; //Tipo de estructura

        llamadaGet = new LlamadaGetCalidad("getpreguntasportipo" + "&idSede=" + sede + "&tipo=" + tipo_pregunta  , 10000, true, "Descargando sedes...", getContext());
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

                            preguntas = new ArrayList<>();

                            JSONArray data = result.getJSONArray("data");

                            for (int i = 0; i< data.length(); i++){
                                JSONObject json_pregunta = data.getJSONObject(i);

                                /* Guardamos las preguntas que tenemos de estructura */

                                Pregunta pregunta = new Pregunta();
                                pregunta.setIdPregunta(Integer.valueOf(json_pregunta.getString("idPregunta")));
                                pregunta.setDetalle(json_pregunta.getJSONObject("idPregunta0").getString("detalle"));
                                pregunta.setIdArea(Integer.valueOf(json_pregunta.getString("idArea")));
                                try {
                                    pregunta.setNivelK(Integer.valueOf(json_pregunta.getJSONObject("idPregunta0").getString("nivelK")));
                                }catch (Exception e){
                                    pregunta.setNivelK(0);
                                }

                                try {
                                    pregunta.setIdTipoAuditoria(Integer.valueOf(json_pregunta.getJSONObject("idPregunta0").getString("idTipoAuditoria")));
                                }catch (Exception e){
                                    pregunta.setIdTipoAuditoria(0);
                                }

                                preguntas.add(pregunta);

                                /* Generamos el array con las respuestas de estructura para ir metiendo los valores que corresponden */

                                Respuesta respuesta = new Respuesta();
                                respuesta.setIdPregunta(pregunta.getIdPregunta());
                                respuesta.setIdArea(pregunta.getIdArea());
                                respuesta.setContestado(false);

                                respuestas.add(respuesta);
                            }


                            if ( preguntas != null && preguntas.size()>0){

                                /* Miramos si ya tenemos respuestas para actualizar en lugar de crear */
                                if (application.respuestas_estructura != null && application.respuestas_estructura.size() >0){
                                    respuestas.clear();
                                    respuestas = new ArrayList<>();
                                    for(Respuesta obj : application.respuestas_estructura) {
                                        respuestas.add((Respuesta) obj.clone());
                                    }

                                }

                                CustomAdapter adapter = new CustomAdapter(getActivity(), R.layout.row_pregunta_sino, preguntas);
                                lista_preguntas.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

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


    private class CustomAdapter extends ArrayAdapter<Pregunta> {
        private LayoutInflater inflater;
        private ArrayList<Pregunta> items;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<Pregunta> items) {
            super(context, textViewResourceId, items);
            this.items = items;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.row_pregunta_sino, null);
            try {

                final TextView nombre_tarea = convertView.findViewById(R.id.nombre_tarea);
                nombre_tarea.setText(items.get(position).getDetalle());

                final CheckBox checkbox_si =  convertView.findViewById(R.id.checkbox_si);
                final CheckBox checkbox_no =  convertView.findViewById(R.id.checkbox_no);
                final CheckBox checkbox_no_aplica =  convertView.findViewById(R.id.checkbox_no_aplica);

                checkbox_si.setTag(items.get(position).getIdPregunta());
                checkbox_no.setTag(items.get(position).getIdPregunta());
                checkbox_no_aplica.setTag(items.get(position).getIdPregunta());

                //Inicializar el checkbox segun los valores de las respuestas
                int id_pregunta_maestra = items.get(position).getIdPregunta();
                Respuesta respuesta_seleccionada = null;
                for (Respuesta resp :respuestas) {
                    if (resp.getIdPregunta() == id_pregunta_maestra){
                        respuesta_seleccionada = resp;
                        break;
                    }
                }

                if (respuesta_seleccionada.getNivelSi() == 1){
                    checkbox_si.setChecked(true);
                }

                if (respuesta_seleccionada.getNivelNo() == 1){
                    checkbox_no.setChecked(true);
                }

                if (respuesta_seleccionada.getNivelNoAplica() == 1){
                    checkbox_no_aplica.setChecked(true);
                }

                checkbox_si.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int id_pregunta_pulsada = Integer.parseInt(checkbox_si.getTag().toString());
                        Respuesta respuesta_seleccionada = null;
                        for (Respuesta resp :respuestas) {
                            if (resp.getIdPregunta() == id_pregunta_pulsada){
                                respuesta_seleccionada = resp;
                                break;
                            }
                        }

                        if(((CompoundButton) view).isChecked()){
                            System.out.println("checkbox_si Checked: "+ checkbox_si.getTag());
                            respuesta_seleccionada.setNivelSi(1);
                            respuesta_seleccionada.setNivelNo(0);
                            respuesta_seleccionada.setNivelNoAplica(0);
                            checkbox_no.setChecked(false);
                            checkbox_no_aplica.setChecked(false);
                            respuesta_seleccionada.setContestado(true);

                        } else {
                            System.out.println("checkbox_si Un-Checked: "+ checkbox_si.getTag());
                            respuesta_seleccionada.setNivelSi(0);
                            respuesta_seleccionada.setContestado(false);
                        }
                    }
                });

                checkbox_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int id_pregunta_pulsada = Integer.parseInt(checkbox_no.getTag().toString());
                        Respuesta respuesta_seleccionada = null;
                        for (Respuesta resp :respuestas) {
                            if (resp.getIdPregunta() == id_pregunta_pulsada){
                                respuesta_seleccionada = resp;
                                break;
                            }
                        }

                        if(((CompoundButton) view).isChecked()){
                            System.out.println("checkbox_no Checked: "+ checkbox_no.getTag());
                            respuesta_seleccionada.setNivelNo(1);
                            respuesta_seleccionada.setNivelSi(0);
                            respuesta_seleccionada.setNivelNoAplica(0);
                            checkbox_si.setChecked(false);
                            checkbox_no_aplica.setChecked(false);
                            respuesta_seleccionada.setContestado(true);
                        } else {
                            System.out.println("checkbox_no Un-Checked: "+ checkbox_no.getTag());
                            respuesta_seleccionada.setNivelNo(0);
                            respuesta_seleccionada.setContestado(false);
                        }
                    }
                });

                checkbox_no_aplica.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int id_pregunta_pulsada = Integer.parseInt(checkbox_no_aplica.getTag().toString());
                        Respuesta respuesta_seleccionada = null;
                        for (Respuesta resp :respuestas) {
                            if (resp.getIdPregunta() == id_pregunta_pulsada){
                                respuesta_seleccionada = resp;
                                break;
                            }
                        }

                        if(((CompoundButton) view).isChecked()){
                            System.out.println("checkbox_no Checked: "+ checkbox_no_aplica.getTag());
                            respuesta_seleccionada.setNivelNoAplica(1);
                            respuesta_seleccionada.setNivelNo(0);
                            respuesta_seleccionada.setNivelSi(0);

                            checkbox_si.setChecked(false);
                            checkbox_no.setChecked(false);
                            respuesta_seleccionada.setContestado(true);
                        } else {
                            System.out.println("checkbox_no Un-Checked: "+ checkbox_no_aplica.getTag());
                            respuesta_seleccionada.setNivelNoAplica(0);
                            respuesta_seleccionada.setContestado(false);
                        }
                    }
                });


                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }


    public void guardar_datos(){

        boolean contestado_todo_cuestionario = true;

        for (Respuesta res: respuestas) {
            Log.d("Respuesta","Respuesta");
            Log.d("getIdPregunta","getIdPregunta "+ res.getIdPregunta());
            Log.d("getNivelSi","getNivelSi "+ res.getNivelSi());
            Log.d("getNivelNo","getNivelNo "+ res.getNivelNo());
            Log.d("isContestado","isContestado "+ res.isContestado());

            if (!res.isContestado()){
                contestado_todo_cuestionario = false;
                break;
            }
        }

        if (!contestado_todo_cuestionario){
            Log.d("ERROR","ERROR");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(getString(R.string.atencion));
            builder.setMessage(getString(R.string.error_cuestionario));

            builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }else{
            application.respuestas_estructura.clear();
            application.respuestas_estructura = new ArrayList<Respuesta>();
            for(Respuesta obj : respuestas ) {
                application.respuestas_estructura.add((Respuesta) obj.clone());
            }


            respuestas.clear();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack();
        }

    }


}
