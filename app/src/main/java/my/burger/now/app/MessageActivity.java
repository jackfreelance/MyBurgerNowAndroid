package my.burger.now.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import my.burger.now.app.Message.Message;
import my.burger.now.app.Message.MessageAdapter;
import my.burger.now.app.configuration.Configuration;
import my.burger.now.app.connexion.AsyncPostMessage;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MessageActivity extends AppCompatActivity {

    private ArrayList<Message> conversation = new ArrayList<>();
    private Socket mSocket;
    MessageAdapter messageAdapter;
    public static String nomLivreur = "";

    private boolean mTyping = false;
    private boolean bipNot = false;
    public static String idLivreur = "7";

    private int statutLivreur = 0;


    public static String prenomLivreur = "";
    public static String isLivreur = "";
    public ListView listMes = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        loadSession();
        initSocketIO();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(getResources().getDrawable(R.mipmap.ic_burgeur));
        // getSupportActionBar().setCustomView(logo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        listMes = (ListView)findViewById(R.id.messages_view);
        messageAdapter = new MessageAdapter(this,conversation);
        listMes.setAdapter(messageAdapter);
        loadMess();



    }

    public void loadMess(){
        String[] name = new String[]{
                "action",
                "id",
                "idc",
                "op"
        };

        String[] value = new String[]{
                "message_list",
                ""+idLivreur,
                ""+PreferenceManager
                        .getDefaultSharedPreferences(this).getString("id_client", idLivreur),
                ""+PreferenceManager
                        .getDefaultSharedPreferences(this).getString("id_operation", "")
        };
        AsyncPost asyncPostServ = new AsyncPost(MessageActivity.this, name, value, Configuration.IPWEB+"/webapp/f/fonctions2.php");
        asyncPostServ.execute();
    }

    public void sendMessage(View v){
        try {
            mSocket = IO.socket(Configuration.IPSAILS);
        } catch (URISyntaxException e) {
        }


        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        Message m = new Message();
        m.idEnvoyeur = 1;
        m.id = "1";
        m.name = prenomLivreur;
        m.text = ((EditText)findViewById(R.id.message_input)).getText().toString();
        ((EditText)findViewById(R.id.message_input)).setText("");
        m.time = Calendar.getInstance().getTime().getTime();
        if (TextUtils.isEmpty(m.text) || idLivreur==null) {
            return;
        }
        JSONObject data = new JSONObject();
        try {
            data.put("id",preferences.getString("c",idLivreur));
            data.put("from",idLivreur);
            data.put("type",0);
            data.put("msg",m.text);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String[] name = new String[]{"to","from","type","msg","op"};
        String[] value = new String[]{preferences.getString("id_client", idLivreur),idLivreur,"0",m.text,preferences.getString("id_operation", "")};

        AsyncPostMessage send = new AsyncPostMessage(this,name,value,Configuration.IPSAILS+"/message");
        send.execute();

        //messageAdapter.add(m);

        loadMess();



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                closeSocketIO();
                startActivity(new Intent(MessageActivity.this, LivActivity.class));
                return false;

        }
        return super.onOptionsItemSelected(item);

    }


    private void loadSession(){
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        nomLivreur = preferences.getString("nom", null);
        prenomLivreur = preferences.getString("prenom", null);
        idLivreur = preferences.getString("id",null);
        isLivreur = preferences.getString("is_livreur", null);


    }

    private void initSocketIO(){
        try {
            mSocket = IO.socket(Configuration.IPSAILS);
        } catch (URISyntaxException e) {
        }


        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(idLivreur, onNewMessage);
        mSocket.connect();
    }

    private void closeSocketIO(){
        /*if(mSocket!=null){
            mSocket.disconnect();
            mSocket.close();
        }*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mSocket!=null){

            mSocket.disconnect();
            mSocket.off(idLivreur, onNewMessage);
        }
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MessageActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = null;
                    try {
                        data = new JSONObject(args[0].toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("responsebb=", args[0].toString());
                    try {
                        //bip();
                        if (data.getInt("type") == 0) {
                            String to = "";
                            String from = "";
                            String type = "";
                            String msg = "";
                            String h = "";
                            String img = "";
                            msg = data.getString("msg");
                            h = data.getString("h");
                            SharedPreferences preferences = PreferenceManager
                                    .getDefaultSharedPreferences(MessageActivity.this);

                            Message m = new Message();
                            m.idEnvoyeur = 1;
                            m.id = from;
                            m.name = preferences.getString("nc", "aucun");
                            m.text = msg;
                            m.time = Calendar.getInstance().getTime().getTime();

                            Log.d("message", m.text);
                           /* messageAdapter.add(m);
                            scrollMyListViewToBottom();*/
                            loadMess();
                        } else if (data.getInt("type") == 5) {
                            //payement effectuez


                        } else if (data.getInt("type") == 6) {
                            //en attente de livraison


                        } else {
                            //sinon

                            String id_operation = "";
                            String id_restaurant = "";
                            String id_livreur = "";
                            String distance = "";
                            String idclient = "";

                            id_operation = data.getString("o");
                            id_restaurant = data.getString("r");
                            id_livreur = data.getString("l");
                            distance = data.getString("d");
                            idclient = data.getString("c");
                            SharedPreferences preferences = PreferenceManager
                                    .getDefaultSharedPreferences(MessageActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();

                            editor.putString("o", id_operation);
                            editor.putString("r", id_restaurant);
                            editor.putString("d", distance);
                            editor.putString("c", idclient);
                            editor.commit();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };


    @Override
    public void onBackPressed()
    {
        closeSocketIO();
        finish();
        startActivity(new Intent(MessageActivity.this,LivActivity.class));
    }

    public  void bip(){
        try {
            MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);
            if(!bipNot){
                bipNot = true;
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    public void onCompletion(MediaPlayer mp) {
                        bipNot = false;
                        Log.i("Completion Listener","Song Complete");
                        if(mp!=null){
                            mp.stop();
                            bipNot = false;
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            mSocket.emit("stop typing");
        }
    };


    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            MessageActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*Toast.makeText(MessageActivity.this.getApplicationContext(),"Echec de connexion", Toast.LENGTH_LONG).show();*/
                }
            });
        }
    };

    private void instantiateSocket(){
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(idLivreur, onNewMessage);
        mSocket.connect();
    }


    //async post

    private class AsyncPost extends AsyncTask<Void, Void, String> {

        Context ct = null;
        RequestParams params = new RequestParams();
        String url = "";

        String[] names = null;
        String[] values = null;
        Activity act = null;
        private ArrayList<Message> conversation = new ArrayList<>();
        public AsyncPost(Activity ct, String[] nm, String[] vl,String url) {
            this.ct = ct;
            this.names = nm;
            this.values = vl;
            this.url = url;
            this.act = ct;

        }
        boolean b= false;

        public AsyncPost(Activity ct, RequestParams params, String[] nm, String[] vl, String url, boolean bl) {
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
            AsyncHttpClient client = new AsyncHttpClient();
            if(names[0]=="action"){
                client.setBasicAuth("antoine", "antoine");
            }else{

            }
            client.post(url,
                    params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                            try {
                                ArrayList<Message> conver = new ArrayList<>();
                                JSONObject jsonObject2 = new JSONObject(response);
                                JSONArray jsonArray = jsonObject2.getJSONArray("messages");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Message mess = new Message();
                                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date date ;
                                    try {
                                         date = formatter.parse(jsonObject.getString("date_create"));
                                    }catch (Exception e){
                                        date = new Date();
                                    }

                                        mess.id = jsonObject.getString("to");
                                        mess.text = jsonObject.getString("message");
                                        mess.time = date.getTime();
                                        mess.idDestinateur = new Integer(jsonObject.getString("from")).intValue();
                                        mess.idEnvoyeur = new Integer(jsonObject.getString("to")).intValue();
                                        conver.add(mess);

                                    }

                                    MessageAdapter messageAdapter = new MessageAdapter(ct, conver);
                                    listMes.setAdapter(messageAdapter);
                                    scrollMyListViewToBottom();

                                }catch(JSONException e){
                                    e.printStackTrace();
                                    Log.e("json", e.getLocalizedMessage());
                                }


                                Log.d("save====", response);
                                //Toast.makeText(ct, response, Toast.LENGTH_LONG);
                            }
                            @Override
                            public void onFailure ( int statusCode, Throwable error,
                                    String content){
                                // Hide Progress Dialog
                                //prgDialog.hide();
                                if (statusCode == 404) {
                                    if (!b) {

                                    }

                                    Log.d("error====", "error " + 404);
                                    //Toast.makeText(ct, "error" + statusCode, Toast.LENGTH_LONG);


                                } else if (statusCode == 500) {

                                    if (!b) {

                                    }
                                    Log.d("error====", "error " + 500);
                                    //Toast.makeText(ct, "error" + statusCode, Toast.LENGTH_LONG);
                                } else {


                                    if (!b) {

                                    }
                                    Log.d("error====", "error inconue");
                                    //Toast.makeText(ct, "error inconue", Toast.LENGTH_LONG);

                                }
                                //Toast.makeText(ct, "errrrr", Toast.LENGTH_LONG);
                                Log.d("error====", "error");
                            }
                        }

                        );
                    }
        }


    private void scrollMyListViewToBottom() {
        listMes.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...

                listMes.setSelection(listMes.getAdapter().getCount() - 1);
            }
        });
    }




}
