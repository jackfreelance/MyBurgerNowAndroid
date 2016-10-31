package my.burger.now.app.connexion;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by 8029 on 16/06/2016.
 */
public class LivreurGeoAsync extends AsyncTask<Void, Void, String> {

    Context ct = null;
    RequestParams params = new RequestParams();
    String url = "";

    String[] names = null;
    String[] values = null;
    Activity act = null;
    public LivreurGeoAsync(String[] nm,String[] vl, String url) {
        this.ct = ct;
        this.names = nm;
        this.values = vl;
        this.url = url;

    }
    boolean b= false;

    public LivreurGeoAsync(RequestParams params,String[] nm,String[] vl, String url,boolean bl) {
        this.ct = ct.getApplicationContext();
        this.params = params;
        this.names = nm;
        this.values = vl;
        this.url = url;
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
            if(names[0]=="action"){
                client.setBasicAuth("antoine", "antoine");
            }

            client.post(url,
                    params, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(String response) {



                            Log.d("saveMande====", response);
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

                                Log.d("error====", "error " + 404);


                            }
                            else if (statusCode == 500) {

                                if(!b) {

                                }
                                Log.d("error====", "error " + 500);
                            }
                            else {



                                if(!b) {

                                }
                                Log.d("error====", "error inconue" + statusCode);

                            }
                            Log.d("error====","error");
                            //Toast.makeText(ct,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
        }catch (Exception asyncp2){
            Log.e("ASYNCPOSTLIVGEO",""+asyncp2.toString());
        }

    }


}