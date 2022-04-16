package com.leancleaning.calidad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.leancleaning.calidad.WS.LlamadaGet;
import com.leancleaning.calidad.utils.AsyncListener;
import com.leancleaning.calidad.utils.LeancleaningUtils;

import org.json.JSONObject;

public class LoginFragment extends Fragment {

    public static String TAG = "main_fragment";
    private View fragmentView;
    private LoginActivity principal;
    private EditText dni,contrasena;
    private String user,pass,res;
    private LlamadaGet llamadaGet,llamadaGet2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.activity_main, container, false);


        principal = (LoginActivity) getActivity();

        principal.getSupportActionBar().hide();

        dni = fragmentView.findViewById(R.id.dni);
        contrasena = fragmentView.findViewById(R.id.contrasena);

        //Typeface fuente = Typeface.createFromAsset(getContext().getAssets(), "fonts/Palanquin-Bold.ttf");

        TextView version = fragmentView.findViewById(R.id.tv_version);
        version.setText("V. "+ BuildConfig.VERSION_NAME);

        Button boton = fragmentView.findViewById(R.id.login);
        boton.setClickable(true);
        //boton.setTypeface(fuente);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("login","login");

                user = dni.getText().toString();
                pass = contrasena.getText().toString();
                if (user.equals("") || pass.equals("")) {
                    Toast.makeText(getActivity(), "Debes rellenar ambos campos, usuario y contraseña", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        //OJO!! Aqui iban los id de las notifiaciones push de firebase
                        String token = "";

                        llamadaGet = new LlamadaGet("login"+"&usuario="+user+"&password="+pass+"&token="+token, 10000, true, "Comprobando credenciales", getContext());
                        llamadaGet.execute("");

                        llamadaGet.completionCode = new AsyncListener() {
                            @Override
                            public void onComplete() {
                                if (llamadaGet.isLoading())
                                    llamadaGet.quitarProgressDialog();

                                if (llamadaGet.gethttpStatus() == 200) {
                                    try {
                                        JSONObject result = new JSONObject(llamadaGet.getResultado());
                                        if (result.getBoolean("success")) {

                                            LeancleaningUtils.setPreferencias("usuario_logueado",user,getContext());
                                            LeancleaningUtils.setPreferencias("pass",pass,getContext());
                                            LeancleaningUtils.setPreferencias("rol",result.getString("rol"),getContext());
                                            LeancleaningUtils.setPreferencias("id_empresa",result.getString("id_empresa"),getContext());
                                            LeancleaningUtils.setPreferencias("id_usuario_logueado",result.getString("id_usuario"),getContext());

                                            View view = getActivity().getCurrentFocus();
                                            if (view != null) {
                                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                            }


                                            principal.transitionToFragment(MainActivityFragment.class.getName(), MainActivityFragment.TAG, false, "");

                                        } else {
                                            String micliente = result.getString("inactivo");

                                            if (micliente.matches("usuario")) {
                                                llamadaGet2 = new LlamadaGet("vaciartoken"+"&usuario="+user, 10000, true, " ", getContext());
                                                llamadaGet2.execute("");
                                                llamadaGet2.completionCode = new AsyncListener() {
                                                    @Override
                                                    public void onComplete() {
                                                        if (llamadaGet2.isLoading())
                                                            llamadaGet2.quitarProgressDialog();
                                                    }
                                                };
                                                principal.alert("Error","Usuario no activo, consulte con el responsable",getContext());

                                            } else {
                                                llamadaGet2 = new LlamadaGet("vaciartoken"+"&usuario="+user, 10000, true, " ", getContext());
                                                llamadaGet2.execute("");
                                                llamadaGet2.completionCode = new AsyncListener() {
                                                    @Override
                                                    public void onComplete() {
                                                        if (llamadaGet2.isLoading())
                                                            llamadaGet2.quitarProgressDialog();
                                                    }
                                                };
                                                principal.alert("Error","Empresa no activa, consulte con el responsable",getContext());

                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (llamadaGet.gethttpStatus() == 400) {
                                    principal.alert("Error","Usuario o contraseña incorrectos",getContext());
                                } else {
                                    principal.alert("Error","Imposible conectar, vuelva a intentarlo con conexión a internet",getContext());
                                }
                            }
                        };
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return fragmentView;
    }
}
