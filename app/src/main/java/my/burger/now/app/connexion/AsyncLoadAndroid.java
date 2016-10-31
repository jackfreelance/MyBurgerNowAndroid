package my.burger.now.app.connexion;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;


public class AsyncLoadAndroid extends AsyncTask<Void, Void, String>{
    Context ct = null;
    RequestParams params = new RequestParams();
    String url = "";

    String[] names = null;
    String[] values = null;
    Activity act = null;
    public AsyncLoadAndroid(Activity ct, String[] nm, String[] vl, String url) {
        this.ct = ct;
        this.names = nm;
        this.values = vl;
        this.url = url;
        this.act = ct;

    }
    boolean b= false;

    public AsyncLoadAndroid(Activity ct, RequestParams params, String[] nm, String[] vl, String url, boolean bl) {
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

        for(int i=0;i<names.length;i++){
            params.put(names[i], values[i]);
        }
        // Trigger Image upload
        triggerImageUpload();
        return "";
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        // Put converted Image string into Async Http Post param

    }

    public void triggerImageUpload() {
        makeHTTPCall();
    }


    public void makeHTTPCall() {
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setBasicAuth("antoine", "antoine");
            client.post(url,
                    params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                                SharedPreferences preferences = PreferenceManager
                                        .getDefaultSharedPreferences(ct);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("attente_livraison", jsonObject.getString("attente_livraison"));
                                editor.putString("livraison_valide", jsonObject.getString("livraison_valide"));
                                editor.putString("payement_effectue", jsonObject.getString("payement_effectue"));
                                editor.putString("au_restaurant", jsonObject.getString("au_restaurant"));
                                editor.putString("depart_restaurant", jsonObject.getString("depart_restaurant"));
                                editor.putString("attente_client", jsonObject.getString("attente_client"));
                                editor.putString("attente_client", jsonObject.getString("attente_client"));
                                editor.putString("validation_code_rencontre", jsonObject.getString("validation_code_rencontre"));
                                editor.putString("distance_restaurant_error", jsonObject.getString("distance_restaurant_error"));
                                editor.putString("distance_restaurant", jsonObject.getString("distance_restaurant"));
                                editor.putString("error_ecart_prix", jsonObject.getString("error_ecart_prix"));
                                editor.putString("c_client", jsonObject.getString("c_client"));

                                editor.commit();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            Log.d("savebasyncpLoad====", response);
                        }
                        @Override
                        public void onFailure(int statusCode, Throwable error,
                                              String content) {
                            // Hide Progress Dialog
                            //prgDialog.hide();
                            if (statusCode == 404) {
                                if(!b) {

                                }

                                Log.d("error====", "error " + 404);



                            }
                            else if (statusCode == 500) {

                                if(!b) {

                                }
                                Log.d("error====","error "+500);
                            }
                            else {



                                if(!b) {

                                }
                                Log.d("error====","error inconue");

                            }
                            //Toast.makeText(ct, statusCode, Toast.LENGTH_LONG);
                            Log.d("error====","error");
                        }
                    });
        }catch (Exception asyncp2){
            Log.e("ASYNCLOADANDROID",""+asyncp2.toString());
        }

    }
}
