package com.leancleaning.calidad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.leancleaning.calidad.utils.LeancleaningUtils;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        // OJO!!! Estas dos lineas las pongo aqui pero esto habrá que hacerlo al acabar
        LeancleaningUtils.setPreferencias("id_sede_seleccionada","",this);
        LeancleaningUtils.setPreferencias("nombre_sede_seleccionada","",this);


        transitionToFragment(LoginFragment.class.getName(), LoginFragment.TAG, false, "");
    }

    public void transitionToFragment(String className, String tag, boolean addtostack, String informacion) {
        Log.w("FragmentTransition", className);
        transitionToFragment(Fragment.instantiate(this, className), tag, addtostack, informacion);
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

    public void alert(String titulo, String texto, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titulo)
                .setMessage(texto)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }








    String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

}
