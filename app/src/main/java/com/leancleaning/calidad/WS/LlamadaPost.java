package com.leancleaning.calidad.WS;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.leancleaning.calidad.utils.AsyncListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.net.URI;

public class LlamadaPost extends AsyncTask<String, String, String> {

    private final String TAG = "LlamadaPost";
    private String metodo;
    private String params;
    private int timeout;
    private boolean loading;
    private String mensaje;
    private Context context;
    private String resultado;
    private boolean flagOffline;
    private String message = "no exception";
    private ProgressDialog progresDialog;
    public AsyncListener completionCode;
    private int httpStatusCode;

    public LlamadaPost(String metodo, String params, int timeout, boolean loading, String mensaje, String user, String pass, Context context) {
        this.metodo = metodo;
        this.params = params;
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

    @Override
    protected String doInBackground(String... input) {
        try {

            HttpParams my_httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(my_httpParams, timeout);

            HttpClient client = new DefaultHttpClient(my_httpParams);

            String url = LlamadaGetCalidad.URLBASE;

            if (metodo.contains("gtodoareas") || metodo.contains("multirdt")) {
                url = url.replace("r=api", "r=api_offline");
            }

            String fullUri = url + this.metodo;
            Log.d("url", "url: " + fullUri);

            URI website = new URI(url + metodo);

            final HttpPost request = new HttpPost();
            request.setEntity(new StringEntity(params, HTTP.UTF_8));
            request.addHeader("content-type", "application/json");
            request.addHeader("Accept", "application/json");

            String authorizationString = "Basic " + Base64.encodeToString((LlamadaGet.USERWS + ":" + LlamadaGet.PASSWS).getBytes(), Base64.NO_WRAP);
            authorizationString.replace("\n", "");
            request.setHeader("Authorization", authorizationString);


            request.setURI(website);
            try {
                HttpResponse httpResponse = client.execute(request);
                httpStatusCode = httpResponse.getStatusLine().getStatusCode();

                HttpEntity entity = httpResponse.getEntity();
                if (httpStatusCode == 200 && entity != null) {
                    String response = EntityUtils.toString(entity, HTTP.UTF_8);
                    resultado = response;
                } else {
                    Log.d("errorrrrr", "errorrrrr");
                    String response = EntityUtils.toString(entity, HTTP.UTF_8);
                    logLargeString("response code error " + httpStatusCode + "    " + response);
                    if (response.contains("No existe jornada de trabajo para ese usuario y esa sede.")) {
                        resultado = "No existe jornada de trabajo para ese usuario y esa sede.";
                    } else {
                        resultado = "";
                    }
                }

            } catch (Exception e) {
                client.getConnectionManager().shutdown();

                if (e.getMessage().contains("Unable to resolve host")) {
                    Log.d(TAG, "SIN INTERNET");
                    flagOffline = true;
                } else if (e.getMessage().contains("timed out")) {
                    Log.d(TAG, "POCA COBERTURA INTERNET");
                    flagOffline = true;
                } else {
                    flagOffline = false;
                }

                message = e.getMessage();

                e.printStackTrace();
                resultado = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultado = null;
        }
        return resultado;
    }

    private void logLargeString(String str) {
        if (str.length() > 3000) {
            Log.i(TAG, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(TAG, str);
        }
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
        progresDialog.dismiss();
    }

    public boolean isLoading() {
        return loading;
    }

    public String getResultado() {
        return resultado;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public boolean getFlagOffline() {
        return flagOffline;
    }

    public String getMessage() {
        return message;
    }

    public static boolean versionGN() {
        return LlamadaGet.URLBASE.contains("cfnavarra");
    }
}
