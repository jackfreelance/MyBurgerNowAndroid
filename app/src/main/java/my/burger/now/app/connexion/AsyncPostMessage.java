package my.burger.now.app.connexion;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


/**
 * Created by Jack on 28/12/2015.
 */
public class AsyncPostMessage extends AsyncTask<Void, Void, String> {

    Context ct = null;
    RequestParams params = new RequestParams();
    String url = "";

    String[] names = null;
    String[] values = null;
    Activity act = null;

    public AsyncPostMessage(Activity ct, String[] nm, String[] vl, String url) {
        this.ct = ct;
        this.names = nm;
        this.values = vl;
        this.url = url;
        this.act = ct;

    }
    boolean b= false;

    public AsyncPostMessage(Activity ct, RequestParams params, String[] nm, String[] vl, String url, boolean bl) {
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
            }
            client.post(url,
                    params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {





                            Log.d("save====",response);
                        /*Toast.makeText(ct,response,Toast.LENGTH_LONG);*/
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
                                //Toast.makeText(ct, "error" + statusCode, Toast.LENGTH_LONG);



                            }
                            else if (statusCode == 500) {

                                if(!b) {

                                }
                                Log.d("error====","error "+500);
                                //Toast.makeText(ct, "error"+statusCode, Toast.LENGTH_LONG);
                            }
                            else {



                                if(!b) {

                                }
                                Log.d("error====","error inconue");
                                //Toast.makeText(ct, "error inconue", Toast.LENGTH_LONG);

                            }
                            //Toast.makeText(ct, "errrrr", Toast.LENGTH_LONG);
                            Log.d("error====","error");
                        }
                    });
        }catch (Exception asyncp2){
            Log.e("ASYNCPOSTMESSAGE",""+asyncp2.toString());
        }

    }
}
