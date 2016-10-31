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


public class AsyncP2 extends AsyncTask<Void, Void, String>{
    Context ct = null;
    RequestParams params = new RequestParams();
    String url = "";

    String[] names = null;
    String[] values = null;
    Activity act = null;
    public AsyncP2(Activity ct, String[] nm, String[] vl, String url) {
        this.ct = ct;
        this.names = nm;
        this.values = vl;
        this.url = url;
        this.act = ct;

    }
    boolean b= false;

    public AsyncP2(Activity ct, RequestParams params, String[] nm, String[] vl, String url, boolean bl) {
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
        // Put converted Image string into Async Http Post param 10:39
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

                            try {

                                Log.d("cp2====", response + "1");
                                JSONObject jsonObject = new JSONObject(response);
                                SharedPreferences preferences = PreferenceManager
                                        .getDefaultSharedPreferences(ct);
                                SharedPreferences.Editor editor = preferences.edit();

                                int duration = Toast.LENGTH_SHORT;
                                String message = "";
                                String cpp = "0";
                                int a = 0;

                                String temp = response +1;

                                if(jsonObject.getBoolean("status")==false){
                                    Log.d("cp1====", response);
                                    editor.putString("timer","0");
                                    Log.d("savebasyncptest====","0");
                                    editor.commit();
                                }else{
                                    Log.d("cp2====", response);
                                    editor.putString("timer", "1");
                                    Log.d("savebasyncptest====", "1");
                                    editor.commit();
                                }
                            } catch (JSONException e) {
                                //e.printStackTrace();
                                Log.e("ASYNCP2",e.getLocalizedMessage());
                            }

                            Log.d("savebasyncp2====", ""+response);
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
            Log.e("ASYNCP2",""+asyncp2.toString());
        }

    }
}
