package com.leancleaning.calidad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.leancleaning.calidad.WS.LlamadaGet;
import com.leancleaning.calidad.utils.LeancleaningUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public Integer contador = 0, contador2 = 0;

    public Fragment fragment;
    NfcAdapter nfcAdapter;
    String usuario;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public Bitmap fotoincidencia;

    public String nombreJornada;


    public boolean editar = true;



    public Map<String, Integer> tareasPendientes = new HashMap<>();

    public boolean flagNuevaTarea = false;

    private LlamadaGet lg;
    private String nombreapellidos = "";
    private String empresaprestadora = "";

    private boolean isUploading = false;

    private Timer bgTimer;
    private TimerTask bgTimerTask;

    private static final int UPLOAD_INTERVAL = 1;

    public boolean descargaInicialRealizada = false;

    public boolean notificacionActiva = false;


    public static final String CHANNEL_NAME = "Common";
    public static final String CHANNEL_DESCRIPTION = "Common";
    public static final String CHANNEL_ID = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        usuario = LeancleaningUtils.getPreferencias("usuario_logueado", "", getApplicationContext());

        /*if (usuario == null || usuario.matches("")) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {

        }*/

        transitionToFragmentA(MainActivity.class.getName(), MainActivityFragment.TAG, true, "");

        //limpiar sharedPreferences
        /*SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = this.getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString("nuevonfcentrada", "");
        editor.putString("nuevonfcsalida", "");
        editor.commit();*/

    }


    public void transitionToFragmentA(String className, String tag, boolean addtostack, String informacion) {
        Log.w("FragmentTransition", className);
        transitionToFragment(Fragment.instantiate(MainActivity.this, className), tag, addtostack, informacion);

    }


    public void transitionToFragment(Fragment fragment, String tag, boolean addtostack, String informacion) {

        Bundle parametro = new Bundle();
        parametro.putString("informacion", informacion);
        fragment.setArguments(parametro);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (addtostack) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(tag)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }


}