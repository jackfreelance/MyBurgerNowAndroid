package my.burger.now.app.connexion;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import my.burger.now.app.LivActivity;
import my.burger.now.app.MainActivity;
import my.burger.now.app.R;
import my.burger.now.app.configuration.Configuration;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;


/**
 * Created by Jack on 28/12/2015.
 */
public class AsyncPostServ extends AsyncTask<Void, Void, String> {

    Context ct = null;
    RequestParams params = new RequestParams();
    String url = "";


    String[] names = null;
    String[] values = new String[12];
    Activity act = null;
    public AsyncPostServ(Activity ct, String[] nm, String[] vl, String url) {
        this.ct = ct;
        this.names = nm;
        this.values = vl;
        this.url = url;
        this.act = ct;

    }
    boolean b= false;

    public AsyncPostServ(Activity ct, RequestParams params, String[] nm, String[] vl, String url, boolean bl) {
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
            AsyncHttpClient client = new AsyncHttpClient();
            if(names[0]=="action"){
                client.setBasicAuth("antoine", "antoine");
            }else{

            }
            client.post(url,
                    params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                        /*Toast.makeText(ct, response, Toast.LENGTH_LONG);*/

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if (values.length != 0 && values[0] == "au_restaurant") {
                                    if (jsonObject.has("type")) {
                                        SharedPreferences preferences = PreferenceManager
                                                .getDefaultSharedPreferences(ct);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        if (jsonObject.getInt("type") == 5) {
                                            if (jsonObject.getString("value").isEmpty()) {

                                                editor.putInt("statu_livreur", 5);
                                                editor.commit();
                                                act.finish();
                                                act.startActivity(new Intent(act,LivActivity.class));
                                            } else {
                                                new AlertDialog.Builder(ct)
                                                        .setTitle("Avertissement")
                                                        .setMessage(jsonObject.getString("value"))
                                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // continue with delete

                                                            }
                                                        })
                                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                                        .show();
                                            }

                                        } else {
                                        }

                                    } else {

                                    /*Toast.makeText(ct, response, Toast.LENGTH_LONG);*/
                                    }
                                } else if (values.length != 0 && values[0] == "commande_recue") {
                                    if (jsonObject.has("type")) {
                                        SharedPreferences preferences = PreferenceManager
                                                .getDefaultSharedPreferences(ct);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        if (jsonObject.getInt("type") == 6) {
                                            if (jsonObject.getString("value").isEmpty()) {

                                                editor.putInt("statu_livreur", 6);
                                                editor.putString("pr", values[5]);
                                                editor.commit();
                                                String[] names = new String[]{
                                                        "op",
                                                        "action",
                                                        "key",
                                                        "amout"
                                                };
                                                String key = "";
                                                try {
                                                    key = MainActivity.SHA1("" + preferences.getString("id_operation", "") + "-1formatik");
                                                } catch (NoSuchAlgorithmException e) {
                                                    e.printStackTrace();
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }
                                                String[] value = new String[]{
                                                        "" + preferences.getString("id_operation", ""),
                                                        "mp",
                                                        key,
                                                        "" + values[5]
                                                };

                                                Log.d("key=", "" + key + "pr=" + preferences.getString("pr", ""));
                                                AsyncP asyncP = null;
                                                try {
                                                    asyncP = new AsyncP(act, names, value, Configuration.IPWEB + "/paiement/webservices/php/ws-v4_modify.php?op=" + URLEncoder.encode(preferences.getString("id_operation", ""), "UTF-8") + "&amount=" + URLEncoder.encode(values[5], "UTF-8") + "&key=" + URLEncoder.encode(key, "UTF-8") + "&action=mp");
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }
                                                //if(values[5]!=preferences.getString("p", "")){

                                                    asyncP.execute();
                                                //}
                                                act.finish();
                                                act.startActivity(new Intent(act, LivActivity.class));
                                            } else {
                                                new AlertDialog.Builder(ct)
                                                        .setTitle("Avertissement")
                                                        .setMessage(jsonObject.getString("value"))
                                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // continue with delete
                                                            }
                                                        })
                                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                                        .show();
                                            }

                                        } else {
                                        }

                                    } else {

                                   /* Toast.makeText(ct, response, Toast.LENGTH_LONG);*/
                                    }

                                } else if (values.length != 0 && values[0] == "attente_client") {
                                    if (jsonObject.has("type")) {
                                        SharedPreferences preferences = PreferenceManager
                                                .getDefaultSharedPreferences(ct);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        if (jsonObject.getInt("type") == 7) {
                                            if (jsonObject.getString("value").isEmpty()) {

                                                editor.putInt("statu_livreur", 7);
                                                editor.commit();

                                                act.finish();
                                                act.startActivity(new Intent(act, LivActivity.class));
                                            } else {
                                                new AlertDialog.Builder(ct)
                                                        .setTitle("Avertissement")
                                                        .setMessage(jsonObject.getString("value"))
                                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // continue with delete

                                                            }
                                                        })
                                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                                        .show();
                                            }

                                        } else {

                                        }

                                    } else {

                                    /*Toast.makeText(ct, response, Toast.LENGTH_LONG);*/
                                    }

                                } else if (values.length != 0 && values[0] == "validation_code_rencontre") {
                                    if (jsonObject.has("type")) {
                                        SharedPreferences preferences = PreferenceManager
                                                .getDefaultSharedPreferences(ct);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        if (jsonObject.getInt("type") == 9) {
                                            if (jsonObject.getString("value").isEmpty()) {
                                                boolean attente = preferences.getBoolean("", true);
                                                String prenom_livreur = preferences.getString("prenom_livreur", "");
                                                String id = preferences.getString("id", "");
                                                String secret = preferences.getString("secret", "");
                                                String op = preferences.getString("o", "");
                                                String locomotion = preferences.getString("locomotion", "");
                                                int idClient = preferences.getInt("idClient", 1);

                                                //vidage de session
                                                /*String[] names = new String[]{
                                                        "op",
                                                        "action",
                                                        "key",
                                                        "amout"
                                                };
                                                String key = "";
                                                try {
                                                    key = MainActivity.SHA1("" + PreferenceManager
                                                            .getDefaultSharedPreferences(act).getString("id_operation", "") + "-1formatik");
                                                } catch (NoSuchAlgorithmException e) {
                                                    e.printStackTrace();
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }
                                                String[] values = new String[]{
                                                        "" + PreferenceManager
                                                                .getDefaultSharedPreferences(act).getString("id_operation", ""),
                                                        "mv",
                                                        key,
                                                        "bdd"
                                                };

                                                Log.d("key=", "" + key);

                                                AsyncP asyncP = null;
                                                try {
                                                    asyncP = new AsyncP(act, names, values, Configuration.IPWEB + "/paiement/webservices/php/ws-v4_modify.php?op=" + URLEncoder.encode(preferences.getString("id_operation", ""), "UTF-8") + "&amount=bdd&key=" + URLEncoder.encode(key, "UTF-8") + "&action=mv");
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }
                                                asyncP.execute();*/

                                                new AlertDialog.Builder(ct)
                                                        .setTitle("Avertissement")
                                                        .setCancelable(false)
                                                        .setMessage(ct.getResources().getString(R.string.msg_livraison_effectue))
                                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // continue with delete

                                                                act.finish();
                                                                act.startActivity(new Intent(act, LivActivity.class));
                                                            }
                                                        })
                                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                                        .show();
                                            } else {
                                                new AlertDialog.Builder(ct)
                                                        .setTitle("Avertissement")
                                                        .setMessage(jsonObject.getString("value"))
                                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // continue with delete

                                                            }
                                                        })
                                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                                        .show();
                                            }

                                        } else {

                                        }

                                    } else {
                                    /*Toast.makeText(ct, response, Toast.LENGTH_LONG);*/
                                    }

                                } else if (values.length != 0 && values[0] == "load_bdd") {
                                    SharedPreferences preferences = PreferenceManager
                                            .getDefaultSharedPreferences(ct);
                                    SharedPreferences.Editor editor = preferences.edit();


                                    editor.putString("d", jsonObject.getString("d"));
                                    editor.putString("id_operation", jsonObject.getString("id_operation"));
                                    editor.putString("ac", jsonObject.getString("ac"));
                                    editor.putString("map", jsonObject.getString("apimap"));
                                    editor.putString("direction", jsonObject.getString("apiserv"));
                                    editor.putInt("timeout", jsonObject.getInt("timeout"));

                                    editor.putString("id_op_status", "1");
                                    if (jsonObject.has("id_op_status") && new Integer(jsonObject.getString("id_op_status")).intValue() > 2) {

                                        if (jsonObject.getString("id_op_status")=="4" && jsonObject.getString("id_op_status")!= preferences.getString("id_op_status","0")){
                                            //bip(ct);
                                        }
                                        editor.putString("id_op_status", jsonObject.getString("id_op_status"));
                                        editor.putString("id_client", jsonObject.getString("id_client"));
                                        editor.putString("c", jsonObject.getString("id_client"));
                                        editor.putString("ltC", jsonObject.getString("ltC"));
                                        editor.putString("lnC", jsonObject.getString("lnC"));
                                        editor.putString("code_rencontre", jsonObject.getString("code_rencontre"));
                                        editor.putString("bundle", jsonObject.getJSONObject("panier").getString("bundle"));
                                        editor.putString("catalogue", jsonObject.getJSONObject("panier").getString("catalogue"));
                                        editor.putString("prix", jsonObject.getJSONObject("panier").getString("prix"));
                                        editor.putString("p", jsonObject.getJSONObject("panier").getString("prixttc"));
                                        editor.putString("p_id", jsonObject.getJSONObject("panier").getString("p_id"));
                                        editor.putString("nc", jsonObject.getJSONObject("client").getString("nc"));
                                        editor.putString("t", jsonObject.getJSONObject("client").getString("tel"));
                                        editor.putString("ltR", jsonObject.getJSONObject("restaurant").getString("ltR"));
                                        editor.putString("lnR", jsonObject.getJSONObject("restaurant").getString("lnR"));
                                        editor.putString("id_restaurant", jsonObject.getJSONObject("restaurant").getString("id_restaurant"));
                                        editor.putString("nom_enseigne", jsonObject.getJSONObject("restaurant").getString("nom_enseigne"));
                                        editor.putString("adfast", jsonObject.getJSONObject("restaurant").getString("adfast"));
                                        editor.putString("e", jsonObject.getJSONObject("restaurant").getString("nom_enseigne"));
                                    }
                                    editor.putString("current_page", jsonObject.getString("current_page"));
                                    editor.putString("currentMessage", jsonObject.getString("currentMessage"));
                                    Log.d("huawai", "mande");

                                    editor.commit();


                                }else if (values.length != 0 && values[0] == "l_validate") {
                                    SharedPreferences preferences = PreferenceManager
                                            .getDefaultSharedPreferences(ct);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("t", jsonObject.getString("t"));


                                    editor.commit();


                                }




                            } catch (JSONException e) {
                                Log.d("InputStream", e.getLocalizedMessage());
                                Log.d("savegfgfgf====", response);

                                //Toast.makeText(ct, response, Toast.LENGTH_LONG);
                            }


                            Log.d("saveb====", response);
                        }

                        @Override
                        public void onFailure(int statusCode, Throwable error,
                                              String content) {
                            // Hide Progress Dialog
                            //prgDialog.hide();
                            if (statusCode == 404) {
                                if (!b) {

                                }

                                Log.d("error====", "error " + 404);


                            } else if (statusCode == 500) {

                                if (!b) {

                                }
                                Log.d("error====", "error " + 500);
                            } else {


                                if (!b) {

                                }
                                Log.d("error====", "error inconue");

                            }
                            //Toast.makeText(ct, statusCode, Toast.LENGTH_LONG);
                            Log.d("error====", "error");
                        }
                    });
            client.clearBasicAuth();
        }catch (Exception asyncp2){
            Log.e("ASYNCPOSTSERV",""+asyncp2.toString());
        }

    }

}
