package my.burger.now.app.connexion;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import my.burger.now.app.MainActivity;
import my.burger.now.app.configuration.Configuration;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 8029 on 10/06/2016.
 */
public class AsyncP extends AsyncTask<Void, Void, String>{
    Context ct = null;
    RequestParams params = new RequestParams();
    String url = "";

    String[] names = null;
    String[] values = null;
    Activity act = null;
    public AsyncP(Activity ct, String[] nm, String[] vl, String url) {
        this.ct = ct;
        this.names = nm;
        this.values = vl;
        this.url = url;
        this.act = ct;

    }
    boolean b= false;

    public AsyncP(Activity ct, RequestParams params, String[] nm, String[] vl, String url, boolean bl) {
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
            client.setBasicAuth("antoine", "antoine");
            client.post(url,
                    params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                            SharedPreferences preferences = PreferenceManager
                                    .getDefaultSharedPreferences(ct);
                            String key = "";
                            /*if(values[1]=="mp"){
                                String[] names1 = new String[]{
                                        "op",
                                        "action",
                                        "key",
                                        "amout"
                                };
                                try {
                                    key = MainActivity.SHA1("" + PreferenceManager
                                            .getDefaultSharedPreferences(ct).getString("id_operation", "") + "-1formatik");
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                String[] values1 = new String[]{
                                        ""+PreferenceManager
                                                .getDefaultSharedPreferences(ct).getString("id_operation", ""),
                                        "mv",
                                        key,
                                        "bdd"
                                };

                                Log.d("key=",""+key);

                                AsyncP asyncP1 = null;
                                try {
                                    asyncP1 = new AsyncP(act, names1, values1, Configuration.IPWEB + "/paiement/webservices/php/ws-v4_modify.php?op="+ URLEncoder.encode(preferences.getString("id_operation", ""), "UTF-8")+"&amount=bdd&key="+ URLEncoder.encode(key, "UTF-8")+"&action=mv");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                asyncP1.execute();
                            }*/


                            Log.d("savebasyncp====", "ato");
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
            Log.e("ASYNCP",""+asyncp2.toString());
        }

    }
}
