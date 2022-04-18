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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.leancleaning.calidad.Clases.RespuestasCalidad;
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

    Area area_seleccionada;

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

                            pop_up_seleccion_area();

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

    public void pop_up_seleccion_area(){
        Log.d("Llega","Llega");

        area_seleccionada = null;

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
                        Toast.makeText(getActivity(),"Área seleccionada: "+ tv_area.getText() , Toast.LENGTH_LONG).show();

                        text_sede.setText(Html.fromHtml("<b>"+getString(R.string.sede_cabecera)+"</b>" + " " + LeancleaningUtils.getPreferencias("nombre_sede_seleccionada", "", requireContext())
                                +" (" + tv_area.getText() +")"
                        ));

                        area_seleccionada = new Area();
                        area_seleccionada.setNombre(tv_area.getText().toString());
                        area_seleccionada.setId_area(Integer.parseInt(tv_area.getTag().toString()));
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
                    if (area_seleccionada == null){
                        Toast.makeText(getActivity(),"Debe seleccionar un area para continuar" , Toast.LENGTH_LONG).show();
                    }else{
                        // Canceled.
                        dialog.dismiss();
                        descargar_datos_area();
                    }
                }
            });

        }catch (Exception e){

        }

    }


    private void descargar_datos_area(){

        llamadaGet = new LlamadaGetCalidad("getpreguntasdecalidadporarea" + "&idArea=" + area_seleccionada.getId_area()   , 10000, true, "Descargando sedes...", getContext());
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
                            preguntas = new ArrayList<>();

                            for (int i = 0; i< data.length(); i++){
                                JSONObject json_pregunta = data.getJSONObject(i);

                                /* Guardamos las preguntas que tenemos de procedimientos */

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

                                /* Generamos el array con las respuestas de procedimientos para ir metiendo los valores que corresponden */

                                Respuesta respuesta = new Respuesta();
                                respuesta.setIdPregunta(pregunta.getIdPregunta());
                                respuesta.setIdArea(pregunta.getIdArea());
                                respuesta.setContestado(false);

                                respuestas.add(respuesta);
                            }


                            if ( preguntas != null && preguntas.size()>0){

                                /* Miramos si ya tenemos respuestas para actualizar en lugar de crear */
                                if (application.respuestas_calidad != null && application.respuestas_calidad.size() >0){
                                    for (RespuestasCalidad res_calidad: application.respuestas_calidad) {
                                        if (res_calidad.getArea().getId_area() == area_seleccionada.getId_area()){
                                            respuestas.clear();
                                            respuestas = new ArrayList<>(res_calidad.getRespuestas());
                                            break;
                                        }
                                    }
                                }


                                CustomAdapter adapter = new CustomAdapter(getActivity(), R.layout.row_pregunta, preguntas);
                                lista_preguntas.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            }

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
            convertView = inflater.inflate(R.layout.row_pregunta, null);
            try {

                final TextView nombre_tarea = convertView.findViewById(R.id.nombre_tarea);
                nombre_tarea.setText(items.get(position).getDetalle());

                final TextView prioridad = convertView.findViewById(R.id.prioridad);
                prioridad.setText(items.get(position).getNivelK()+"");

                final CheckBox checkbox_1 =  convertView.findViewById(R.id.checkbox_1);
                final CheckBox checkbox_2 =  convertView.findViewById(R.id.checkbox_2);
                final CheckBox checkbox_3 =  convertView.findViewById(R.id.checkbox_3);
                final CheckBox checkbox_4 =  convertView.findViewById(R.id.checkbox_4);
                final CheckBox checkbox_5 =  convertView.findViewById(R.id.checkbox_5);

                checkbox_1.setTag(items.get(position).getIdPregunta());
                checkbox_2.setTag(items.get(position).getIdPregunta());
                checkbox_3.setTag(items.get(position).getIdPregunta());
                checkbox_4.setTag(items.get(position).getIdPregunta());
                checkbox_5.setTag(items.get(position).getIdPregunta());

                //Inicializar el checkbox segun los valores de las respuestas
                int id_pregunta_maestra = items.get(position).getIdPregunta();
                Respuesta respuesta_seleccionada = null;
                for (Respuesta resp :respuestas) {
                    if (resp.getIdPregunta() == id_pregunta_maestra){
                        respuesta_seleccionada = resp;
                        break;
                    }
                }

                if (respuesta_seleccionada.getNivel1() == 1){
                    checkbox_1.setChecked(true);
                }

                if (respuesta_seleccionada.getNivel2() == 1){
                    checkbox_2.setChecked(true);
                }

                if (respuesta_seleccionada.getNivel3() == 1){
                    checkbox_3.setChecked(true);
                }

                if (respuesta_seleccionada.getNivel4() == 1){
                    checkbox_4.setChecked(true);
                }

                if (respuesta_seleccionada.getNivel5() == 1){
                    checkbox_5.setChecked(true);
                }



                checkbox_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int id_pregunta_pulsada = Integer.parseInt(checkbox_1.getTag().toString());
                        Respuesta respuesta_seleccionada = null;
                        for (Respuesta resp :respuestas) {
                            if (resp.getIdPregunta() == id_pregunta_pulsada){
                                respuesta_seleccionada = resp;
                                break;
                            }
                        }

                        if(((CompoundButton) view).isChecked()){
                            System.out.println("checkbox_1 Checked: "+ checkbox_1.getTag());
                            respuesta_seleccionada.setNivel1(1);
                            respuesta_seleccionada.setNivel2(0);
                            respuesta_seleccionada.setNivel3(0);
                            respuesta_seleccionada.setNivel4(0);
                            respuesta_seleccionada.setNivel5(0);
                            checkbox_2.setChecked(false);
                            checkbox_3.setChecked(false);
                            checkbox_4.setChecked(false);
                            checkbox_5.setChecked(false);
                            respuesta_seleccionada.setContestado(true);

                        } else {
                            System.out.println("checkbox_si Un-Checked: "+ checkbox_1.getTag());
                            respuesta_seleccionada.setNivel1(0);
                            respuesta_seleccionada.setContestado(false);
                        }
                    }
                });

                checkbox_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int id_pregunta_pulsada = Integer.parseInt(checkbox_2.getTag().toString());
                        Respuesta respuesta_seleccionada = null;
                        for (Respuesta resp :respuestas) {
                            if (resp.getIdPregunta() == id_pregunta_pulsada){
                                respuesta_seleccionada = resp;
                                break;
                            }
                        }

                        if(((CompoundButton) view).isChecked()){
                            System.out.println("checkbox_no Checked: "+ checkbox_2.getTag());
                            respuesta_seleccionada.setNivel2(1);
                            respuesta_seleccionada.setNivel1(0);
                            respuesta_seleccionada.setNivel3(0);
                            respuesta_seleccionada.setNivel4(0);
                            respuesta_seleccionada.setNivel5(0);
                            checkbox_1.setChecked(false);
                            checkbox_3.setChecked(false);
                            checkbox_4.setChecked(false);
                            checkbox_5.setChecked(false);
                            respuesta_seleccionada.setContestado(true);
                        } else {
                            System.out.println("checkbox_no Un-Checked: "+ checkbox_2.getTag());
                            respuesta_seleccionada.setNivel2(0);
                            respuesta_seleccionada.setContestado(false);
                        }
                    }
                });

                checkbox_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int id_pregunta_pulsada = Integer.parseInt(checkbox_3.getTag().toString());
                        Respuesta respuesta_seleccionada = null;
                        for (Respuesta resp :respuestas) {
                            if (resp.getIdPregunta() == id_pregunta_pulsada){
                                respuesta_seleccionada = resp;
                                break;
                            }
                        }

                        if(((CompoundButton) view).isChecked()){
                            System.out.println("checkbox_no Checked: "+ checkbox_3.getTag());
                            respuesta_seleccionada.setNivel3(1);
                            respuesta_seleccionada.setNivel1(0);
                            respuesta_seleccionada.setNivel2(0);
                            respuesta_seleccionada.setNivel4(0);
                            respuesta_seleccionada.setNivel5(0);
                            checkbox_1.setChecked(false);
                            checkbox_2.setChecked(false);
                            checkbox_4.setChecked(false);
                            checkbox_5.setChecked(false);
                            respuesta_seleccionada.setContestado(true);
                        } else {
                            System.out.println("checkbox_no Un-Checked: "+ checkbox_3.getTag());
                            respuesta_seleccionada.setNivel3(0);
                            respuesta_seleccionada.setContestado(false);
                        }
                    }
                });

                checkbox_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int id_pregunta_pulsada = Integer.parseInt(checkbox_4.getTag().toString());
                        Respuesta respuesta_seleccionada = null;
                        for (Respuesta resp :respuestas) {
                            if (resp.getIdPregunta() == id_pregunta_pulsada){
                                respuesta_seleccionada = resp;
                                break;
                            }
                        }

                        if(((CompoundButton) view).isChecked()){
                            System.out.println("checkbox_no Checked: "+ checkbox_4.getTag());
                            respuesta_seleccionada.setNivel4(1);
                            respuesta_seleccionada.setNivel1(0);
                            respuesta_seleccionada.setNivel2(0);
                            respuesta_seleccionada.setNivel3(0);
                            respuesta_seleccionada.setNivel5(0);
                            checkbox_1.setChecked(false);
                            checkbox_2.setChecked(false);
                            checkbox_3.setChecked(false);
                            checkbox_5.setChecked(false);
                            respuesta_seleccionada.setContestado(true);
                        } else {
                            System.out.println("checkbox_no Un-Checked: "+ checkbox_4.getTag());
                            respuesta_seleccionada.setNivel4(0);
                            respuesta_seleccionada.setContestado(false);
                        }
                    }
                });

                checkbox_5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int id_pregunta_pulsada = Integer.parseInt(checkbox_5.getTag().toString());
                        Respuesta respuesta_seleccionada = null;
                        for (Respuesta resp :respuestas) {
                            if (resp.getIdPregunta() == id_pregunta_pulsada){
                                respuesta_seleccionada = resp;
                                break;
                            }
                        }

                        if(((CompoundButton) view).isChecked()){
                            System.out.println("checkbox_no Checked: "+ checkbox_5.getTag());
                            respuesta_seleccionada.setNivel5(1);
                            respuesta_seleccionada.setNivel1(0);
                            respuesta_seleccionada.setNivel2(0);
                            respuesta_seleccionada.setNivel3(0);
                            respuesta_seleccionada.setNivel4(0);
                            checkbox_1.setChecked(false);
                            checkbox_2.setChecked(false);
                            checkbox_3.setChecked(false);
                            checkbox_4.setChecked(false);
                            respuesta_seleccionada.setContestado(true);
                        } else {
                            System.out.println("checkbox_no Un-Checked: "+ checkbox_5.getTag());
                            respuesta_seleccionada.setNivel5(0);
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
            Log.d("getNivel1","getNivel1 "+ res.getNivel1());
            Log.d("getNivel2","getNivel2 "+ res.getNivel2());
            Log.d("getNivel3","getNivel3 "+ res.getNivel3());
            Log.d("getNivel4","getNivel4 "+ res.getNivel4());
            Log.d("getNivel5","getNivel5 "+ res.getNivel5());
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
            Log.d("GUARDAR","GUARDAR");

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack();
        }



    }

}
