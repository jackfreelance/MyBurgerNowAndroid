package my.burger.now.app.connexion;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import my.burger.now.app.LivActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieStore;


/**
 * Created by Jack on 28/12/2015.
 */
public class AsyncPostService extends AsyncTask<Void, Void, String> {

    Context ct = null;
    RequestParams params = new RequestParams();
    String url = "";

    String[] names = null;
    String[] values = null;
    Activity act = null;
    public AsyncPostService(Activity ct,String[] nm,String[] vl, String url) {
        this.ct = ct;
        this.names = nm;
        this.values = vl;
        this.url = url;
        this.act = ct;

    }
    boolean b= false;

    public AsyncPostService(Activity ct,  RequestParams params,String[] nm,String[] vl, String url,boolean bl) {
        this.ct = ct.getApplicationContext();
        this.params = params;
        this.names = nm;
        this.values = vl;
        this.url = url;
        this.act = ct;
        this.b = bl;

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();//prgDialog.show();

    }


    @Override
    protected String doInBackground(Void... paramse) {


        return "";
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        // Put converted Image string into Async Http Post param
        for(int i=0;i<names.length;i++){
            params.put(names[i], values[i]);
        }
        // Trigger Image upload
        triggerImageUpload();

    }

    public void triggerImageUpload() {
        makeHTTPCall();
    }


    public void makeHTTPCall() {
        try{
            final AsyncHttpClient client = new AsyncHttpClient();
            if(names[0]=="action"){
                client.setBasicAuth("antoine", "antoine");
            }

            client.post(url,
                    params, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(String response) {


                            try {
                                if (values[0] == "panier") {
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ct);
                                    ;
                                    SharedPreferences.Editor editor = preferences.edit();
                                    JSONObject jsonObject = new JSONObject(response);
                                    editor.putString("panier", jsonObject.getString("bundle") + jsonObject.getString("catalogue") + jsonObject.getString("prix"));
                                    editor.commit();
                                    Log.d("panier=" + values[1], response);
                                    // Toast.makeText(ct, "" + response, Toast.LENGTH_LONG).show();
                                } else {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Log.d("save>>>>>", values[0]);
                                    if (values[0] == "login") {
                                        if (jsonObject.getBoolean("connection") == true) {

                                            Intent intent = new Intent(ct, LivActivity.class);
                                            intent.putExtra("prenom_livreur", jsonObject.getString("prenom_livreur"));
                                            intent.putExtra("id", jsonObject.getString("id"));
                                            intent.putExtra("prenom_livreur", jsonObject.getString("prenom_livreur"));
                                            intent.putExtra("secret", jsonObject.getString("secret"));
                                            intent.putExtra("locomotion", jsonObject.getString("locomotion"));
                                            intent.putExtra("attente_livraison", jsonObject.getBoolean("attente_livraison"));
                                            intent.putExtra("idClient", 1);

                                            SharedPreferences preferences = PreferenceManager
                                                    .getDefaultSharedPreferences(ct);
                                            SharedPreferences.Editor editor = preferences.edit();

                                            editor.putString("prenom_livreur", jsonObject.getString("prenom_livreur"));
                                            editor.putString("id", jsonObject.getString("id"));
                                            editor.putString("prenom_livreur", jsonObject.getString("prenom_livreur"));
                                            editor.putString("secret", jsonObject.getString("secret"));
                                            editor.putString("locomotion", jsonObject.getString("locomotion"));
                                            editor.putBoolean("attente_livraison", jsonObject.getBoolean("attente_livraison"));
                                            editor.putInt("idClient", 1);
                                            editor.commit();

                                            act.startActivity(intent);
                                            act.finish();


                                        }else {
                                            new AlertDialog.Builder(ct)
                                                    .setTitle("Erreur")
                                                    .setMessage("Login ou Mdp Incorect?")
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // continue with delete

                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                        }
                                    } else if (values[0] == "au_restaurant") {
                                        SharedPreferences preferences = PreferenceManager
                                                .getDefaultSharedPreferences(ct);
                                        SharedPreferences.Editor editor = preferences.edit();

                                        editor.putString("current_page", jsonObject.getString("current_page"));
                                        editor.putBoolean("status_changed", jsonObject.getBoolean("status_changed"));
                                        editor.commit();

                                    } else if (values[0] == "livraison") {
                                        SharedPreferences preferences = PreferenceManager
                                                .getDefaultSharedPreferences(ct);
                                        SharedPreferences.Editor editor = preferences.edit();

                                        editor.putString("id_enseigne", jsonObject.getString("id_enseigne"));
                                        editor.putString("nom_enseigne", jsonObject.getString("nom_enseigne"));
                                        editor.putString("id_operation", jsonObject.getString("id_operation"));
                                        editor.putString("id_restaurant", jsonObject.getString("id_restaurant"));
                                        editor.putString("id_client", jsonObject.getString("id_client"));
                                        editor.putString("nom_client", jsonObject.getString("nom_client"));
                                        editor.putString("distance", jsonObject.getString("distance"));
                                        editor.putString("mess_livraison", jsonObject.getString("mess_livraison"));
                                        editor.putBoolean("livraison_recue", jsonObject.getBoolean("livraison_recue"));
                                        editor.commit();

                                    } else if (values[0] == "validation_livraison") {


                                    } else if (values[0] == "payement_effectue") {
                                        SharedPreferences preferences = PreferenceManager
                                                .getDefaultSharedPreferences(ct);
                                        SharedPreferences.Editor editor = preferences.edit();

                                        editor.putString("current_page", jsonObject.getString("current_page"));
                                        editor.putBoolean("status_changed", jsonObject.getBoolean("status_changed"));
                                        editor.commit();

                                    } else if (values[0] == "commande_recue") {
                                        SharedPreferences preferences = PreferenceManager
                                                .getDefaultSharedPreferences(ct);
                                        SharedPreferences.Editor editor = preferences.edit();

                                        editor.putString("current_page", jsonObject.getString("current_page"));
                                        editor.putInt("id_op_status", jsonObject.getInt("id_op_status"));
                                        editor.putBoolean("status_changed", jsonObject.getBoolean("status_changed"));
                                        editor.commit();

                                    } else if (values[0] == "validation_code_rencontre") {
                                        SharedPreferences preferences = PreferenceManager
                                                .getDefaultSharedPreferences(ct);
                                        if (jsonObject.getBoolean("statut")) {
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putInt("statu_livreur", 1);
                                            editor.commit();
                                        } else {
                                            // Toast.makeText(ct, "" + jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }

                            }catch(JSONException e){
                                // Toast.makeText(ct, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                Log.d("InputStream", e.getLocalizedMessage());
                            }
                            Log.d("save====", response);
                            //Toast.makeText(ct, response, Toast.LENGTH_LONG).show();

                        }
                        @Override
                        public void onFailure(int statusCode, Throwable error,
                                              String content) {
                            // Hide Progress Dialog
                            //prgDialog.hide();
                            if (statusCode == 404) {
                                if(!b) {

                                }

                                Log.d("error====","error "+404);
                                //Toast.makeText(ct,"404",Toast.LENGTH_LONG).show();


                            }
                            else if (statusCode == 500) {

                                if(!b) {

                                }
                                Log.d("error====","error "+500);
                                //Toast.makeText(ct,"500",Toast.LENGTH_LONG).show();
                            }
                            else {



                                if(!b) {

                                }
                                Log.d("error====","error inconue"+statusCode);
                                //Toast.makeText(ct,"error inconue"+statusCode,Toast.LENGTH_LONG).show();

                            }
                            Log.d("error====","error");
                            //Toast.makeText(ct,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
        }catch (Exception asyncp2){
            Log.e("ASYNCPOSTSERVICE",""+asyncp2.toString());
        }

    }


}
