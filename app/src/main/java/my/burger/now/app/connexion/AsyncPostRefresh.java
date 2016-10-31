package my.burger.now.app.connexion;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Jack on 28/12/2015.
 */
public class AsyncPostRefresh extends AsyncTask<Void, Void, String> {

    Context ct = null;
    RequestParams params = new RequestParams();
    String url = "";

    String[] names = null;
    String[] values = null;
    Activity act = null;
    public AsyncPostRefresh(Activity ct, String[] nm, String[] vl, String url) {
        this.ct = ct;
        this.names = nm;
        this.values = vl;
        this.url = url;
        this.act = ct;

    }
    boolean b= false;

    public AsyncPostRefresh(Activity ct, RequestParams params, String[] nm, String[] vl, String url, boolean bl) {
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
    protected String doInBackground(Void... params) {
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
            }

            client.post(url,
                    params, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(String response) {

                            Log.d("refresh","ok");
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d("save>>>>>",values[0]);
                                if(jsonObject.getBoolean("status")==true){
                                    SharedPreferences preferences = PreferenceManager
                                            .getDefaultSharedPreferences(ct);
                                    SharedPreferences.Editor editor = preferences.edit();

                                    editor.putString("id_op_status", jsonObject.getString("id_op_status"));
                                    editor.commit();


                                }

                            } catch (JSONException e) {
                                // Toast.makeText(ct,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                Log.d("InputStream", e.getLocalizedMessage());
                            }
                            Log.d("save====",response);
                            Toast.makeText(ct,response,Toast.LENGTH_LONG).show();
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
            Log.e("ASYNCPOSTREFRESH",""+asyncp2.toString());
        }

    }


}
