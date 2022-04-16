package com.leancleaning.calidad.WS;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.leancleaning.calidad.BuildConfig;
import com.leancleaning.calidad.utils.AsyncListener;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LlamadaGetCalidad extends AsyncTask<String, String, String> {

    private final String TAG = "LlamadaGetCalidad";
    public static final String URLBASE = "http://141.95.160.200/leancleaning/web/index.php?r=api_calidad/";
    static final String USERWS = "leancleaning";
    static final String PASSWS = "G445y,xX$8V[76,b";
    public AsyncListener completionCode;
    private String metodo;
    private int timeout;
    private boolean loading;
    private String mensaje;
    private Context context;
    private String resultado;
    private int httpStatus;
    private ProgressDialog progresDialog;
    private boolean flagOffline;
    private String message = "no exception";

    public LlamadaGetCalidad(String metodo, int timeout, boolean loading, String mensaje, Context context) {
        this.metodo = metodo;
        this.timeout = timeout;
        this.mensaje = mensaje;
        this.loading = loading;
        this.context = context;
        this.flagOffline = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (this.loading) {
            mostrarProgressDialog();
        }
    }

    private String readFully(InputStream inputStream, String encoding) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString(encoding);
    }

    @Override
    protected String doInBackground(String... input) {
        resultado = "";
        try {
            String fullUri = URLBASE + this.metodo;
            Log.d("url", "url: " + fullUri);

            try {
                URL url = new URL(fullUri);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString((USERWS + ":" + PASSWS).getBytes(), Base64.NO_WRAP));
                conn.setConnectTimeout(this.timeout);

                // read the response
                httpStatus = conn.getResponseCode();
                Log.d(TAG, "httpStatus: " + httpStatus);

                InputStream in = new BufferedInputStream(conn.getInputStream());
                resultado = readFully(in, "UTF-8");//IOUtils.toString(in, "UTF-8");

            } catch (MalformedURLException e) {
                Log.d(TAG, "Uri malformed: " + fullUri);
                e.printStackTrace();
                message = e.getMessage();
            } catch (Exception e) {

                if (e.getMessage().contains("Unable to resolve host")) {
                    flagOffline = true;
                    Log.d(TAG, "SIN INTERNET");
                } else if (e.getMessage().contains("timed out")) {
                    flagOffline = true;
                    Log.d(TAG, "POCA COBERTURA INTERNET");
                } else {
                    flagOffline = false;
                }

                message = e.getMessage();

                Log.d(TAG, "Exception: " + fullUri);
                e.printStackTrace();
            }
        } catch (Exception e) {
            httpStatus = 500;
            resultado = null;
        }
        return resultado;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        completionCode.onComplete();
    }

    private void mostrarProgressDialog() {
        progresDialog = ProgressDialog.show(context, "", mensaje, true);
        progresDialog.setCancelable(false);
    }

    public void quitarProgressDialog() {
        try {
            progresDialog.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isLoading() {
        return loading;
    }

    public String getResultado() {
        return resultado;
    }

    public int gethttpStatus() {
        return httpStatus;
    }

    public boolean getFlagOffline() {
        return flagOffline;
    }

    public String getMessage() {
        return message;
    }

}
