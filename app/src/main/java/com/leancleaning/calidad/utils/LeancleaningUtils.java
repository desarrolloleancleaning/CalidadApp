package com.leancleaning.calidad.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

public class LeancleaningUtils {

    public static void setPreferencias(String key, String value, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("LeanCleaning", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPreferencias(String key, String pordefecto, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("LeanCleaning", Context.MODE_PRIVATE);
        return sharedPref.getString(key, pordefecto);
    }

    public static void setPreferencias(String key, Integer value, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("LeanCleaning", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static Integer getPreferencias(String key, Integer pordefecto, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("LeanCleaning", Context.MODE_PRIVATE);
        return sharedPref.getInt(key, pordefecto);
    }

    public static void askDialog(Context context, int title, int message, int yesMessage, int noMessage, final OnYesNoDialogAceptCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(yesMessage, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                callback.onDialogYes();
                dialog.dismiss();
            }

        });

        builder.setNegativeButton(noMessage, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                callback.onDialogNo();
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();

        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);

        alert.show();
    }

    public interface OnYesNoDialogAceptCallback {
        void onDialogYes();

        void onDialogNo();
    }

}
