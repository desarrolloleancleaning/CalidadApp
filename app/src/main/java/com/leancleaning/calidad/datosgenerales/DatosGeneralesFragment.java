package com.leancleaning.calidad.datosgenerales;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.leancleaning.calidad.Application;
import com.leancleaning.calidad.Clases.Cuestionario;
import com.leancleaning.calidad.Clases.Respuesta;
import com.leancleaning.calidad.R;
import com.leancleaning.calidad.utils.LeancleaningUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatosGeneralesFragment extends Fragment {

    Application application;

    public static String TAG = "datosgenerales_fragment";
    private View fragmentView;

    private TextView text_usuario,text_sede;

    ImageView boton_guardar, img_firma_evaluador, img_firma_responsable;

    TextView centro, fecha, evaluador;

    EditText departamento, responsable, supervisor, objetivo, observaciones;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.datos_generales, container, false);

        application = (Application) getActivity().getApplication();

        text_usuario = fragmentView.findViewById(R.id.text_usuario);
        text_usuario.setText(Html.fromHtml("<b>"+getString(R.string.usuario_cabecera)+"</b>" + " " + LeancleaningUtils.getPreferencias("usuario_logueado", "", requireContext())));

        text_sede = fragmentView.findViewById(R.id.text_sede);
        text_sede.setText(Html.fromHtml("<b>"+getString(R.string.sede_cabecera)+"</b>" + " " + LeancleaningUtils.getPreferencias("nombre_sede_seleccionada", "", requireContext())));


        centro = fragmentView.findViewById(R.id.centro);
        centro.setText(LeancleaningUtils.getPreferencias("nombre_sede_seleccionada", "", requireContext()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        fecha = fragmentView.findViewById(R.id.fecha);
        fecha.setText(sdf.format(new Date()));

        evaluador = fragmentView.findViewById(R.id.evaluador);
        evaluador.setText(LeancleaningUtils.getPreferencias("nombre_evaluador", "", requireContext()));


        departamento = fragmentView.findViewById(R.id.departamento);
        responsable = fragmentView.findViewById(R.id.responsable);
        supervisor = fragmentView.findViewById(R.id.supervisor);
        objetivo = fragmentView.findViewById(R.id.objetivo);
        observaciones = fragmentView.findViewById(R.id.observaciones);

        //EVITAR QUE SE HAGA CLICK EN EL FRAGMENT ANTERIOR
        fragmentView.setClickable(true);
        fragmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        boton_guardar = fragmentView.findViewById(R.id.boton_guardar);
        boton_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Guardar","Guardar");
                guardar_datos();
            }
        });


        img_firma_evaluador = fragmentView.findViewById(R.id.img_firma_evaluador);
        img_firma_evaluador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("firma_evaluador","firma_evaluador");

            }
        });

        img_firma_responsable = fragmentView.findViewById(R.id.img_firma_responsable);
        img_firma_responsable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("firma_responsable","firma_responsable");
            }
        });

        /* Miramos si ya tenemos respuestas para actualizar en lugar de crear */
        if (application.getCuestionario() != null && !application.getCuestionario().getCentro().equals("") ){

            Cuestionario aux = application.getCuestionario();

            departamento.setText(aux.getDepartamento());
            responsable.setText(aux.getResponsable());
            supervisor.setText(aux.getSupervisor());
            objetivo.setText(aux.getObjetivo());
            observaciones.setText(aux.getObservaciones());

        }


        return fragmentView;
    }



    public void guardar_datos(){

        boolean contestado_todo_cuestionario = true;


        if (departamento.getText().toString().equals("") ||
                responsable.getText().toString().equals("") ||
                objetivo.getText().toString().equals("") ){

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(getString(R.string.atencion));
            builder.setMessage(getString(R.string.error_datos_generales));

            builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }else{

            Cuestionario cuestionario = new Cuestionario();

            String sede = LeancleaningUtils.getPreferencias("id_sede_seleccionada", "", requireContext());


            String dtStart = fecha.getText().toString();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date date = null;
            try {
                date = format.parse(dtStart);
            } catch (Exception e) {
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            cuestionario.setCentro(centro.getText().toString());
            cuestionario.setIdSede(Integer.parseInt(sede));
            cuestionario.setDepartamento(departamento.getText().toString());
            cuestionario.setResponsable(responsable.getText().toString());
            cuestionario.setFecha(sdf.format(date));
            cuestionario.setObjetivo(objetivo.getText().toString());
            cuestionario.setSupervisor(supervisor.getText().toString());
            cuestionario.setEvaluador(evaluador.getText().toString());
            cuestionario.setObservaciones(observaciones.getText().toString());

            application.setCuestionario(cuestionario);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack();
        }

    }

}
