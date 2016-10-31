package my.burger.now.app;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import my.burger.now.app.configuration.Configuration;
import my.burger.now.app.connexion.AsyncLoadAndroid;
import my.burger.now.app.connexion.AsyncP;
import my.burger.now.app.connexion.AsyncP2;
import my.burger.now.app.connexion.AsyncPostRefresh;
import my.burger.now.app.connexion.AsyncPostServ;
import my.burger.now.app.connexion.AsyncPostService;
import my.burger.now.app.connexion.LogOut;
import my.burger.now.app.connexion.MyTestReceiver;
import my.burger.now.app.enumeration.TYPE_LIVREUR;
import my.burger.now.app.services.MapService;
import my.burger.now.app.waypoint.HttpConnection;
import my.burger.now.app.waypoint.PathJSONParser;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LivActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {


    private LinearLayout layoutButton;
    private TextView buttonCommamde;
    private TextView buttonValider;
    private Button buttonRestaurent;
    private TextView textViewLivreur;
    private TextView textViewLivraison;
    AlarmManager alarm;

    private int ETAPE_LIVRAISON = 0;

    private String nomDeRestaurent = "";
    private String lienDeLivraison = "";
    Dialog dialogdem;

    private GoogleMap mMap;

    private Socket mSocket;
    private String typeLivreur;
    LatLng myLatLng = null;


    public static int FM_NOTIFICATION_ID = 5;
    public static final int MY_LOCATION_REQUEST_CODE = 1;
    public static String nomLivreur = "";
    public static String idLivreur = "7";
    PendingIntent pIntent;

    private int statutLivreur = 0;


    public static String prenomLivreur = "";
    public static String isLivreur = "";

    public boolean bipNot = false;
    public boolean zoom = true;
    public String serverKey = "AIzaSyC7HTmtCoKx5ey4NDo9g1Xxo_F7mndbmBI";


    private String nomFastfood = "aucun";
    private String distanceLiv = "";
    private String idRestaurent = "";
    private String idOperation = "";

    SupportMapFragment mapFragment;
    private boolean mTyping = false;
    private int connect;


    private double latitudeC = 0;
    private double latitudeL = 0;
    private double longitudeC = 0;
    private double longitudeL = 0;

    private boolean showingMarker = false;

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();

    Timer timerLiv;
    TimerTask timerTaskLiv;
    final Handler handlerLiv = new Handler();

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;


    private  boolean checkAndRequestPermissions() {
        int permissionInternet = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int locationCoasePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int callPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionInternet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }

        if (callPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (callPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("TAG", "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("TAG", "Internet & location services permission granted");
                        finish();
                        startActivity(new Intent(this, LivActivity.class));
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("TAG", "Internet & permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("Internet and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    })   ;
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }







    @Override
    protected void onResume() {
        super.onResume();
        testConnect();
        //startTimerMAp();
        loadBDDMessage();
        initMap();


    }

    public void startTimer() {
        //set a new Timer
        /*timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, 10000); //
        */
        scheduleSyncIn(5000);
    }


    public void startTimerMAp() {

        scheduleSyncInMap(5000);
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        /*if (timer != null) {
            timer.cancel();
            timer = null;
        }*/

        if (mRunnable != null && mHandler != null) {

        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp


                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                        final String strDate = simpleDateFormat.format(calendar.getTime());
                        //if(preferences.getString("id", "")!=""/* && preferences.getString("id_op_status", "")=="2"*/){
                        try {
                            AsyncP2 refresh = new AsyncP2(LivActivity.this, new String[0], new String[0], Configuration.IPWEB + "/check_distance_livreur_restaurant.php?id_livreur=" + URLEncoder.encode("" + idLivreur, "UTF-8"));
                            refresh.execute();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }



                    }
                });
            }
        };
    }


    Handler mHandler;
    Runnable mRunnable;

    void scheduleSyncIn(final int aSeconds) {
        mHandler = new Handler();

        mRunnable = new Runnable() {

            @Override
            public void run() {

                mHandler.postDelayed(mRunnable, aSeconds);
            }
        };
        mHandler.postDelayed(mRunnable, aSeconds);

    }


    Handler mHandlerMap;
    Runnable mRunnableMap;
    boolean updtMap = true;

    void scheduleSyncInMap(final int aSeconds) {
        mHandlerMap = new Handler();
        final SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        mRunnableMap = new Runnable() {

            @Override
            public void run() {
                updtMap = true;
                if (new Integer(preferences.getString("id_op_status", "0")).intValue() > 3 && new Integer(preferences.getString("id_op_status", "0")).intValue() < 8 && preferences.getString("id_op_status", "0")!=null){

                }else{
                    AsyncP2 refresh = new AsyncP2(LivActivity.this, new String[]{"action","id_livreur"}, new String[]{"check_distance_livreur_restaurant",""+idLivreur}, Configuration.IPWEB + "/webapp/f/fonctions2.php");
                    refresh.execute();
                }

                if(myLatLng!=null){
                    String[] names = new String[]{"action", "id", "lat", "lng", "locomotion"};

                    String[] values = new String[]{"maj_geo", "" + idLivreur, "" + myLatLng.latitude, "" + myLatLng.longitude, "" + preferences.getString("locomotion", "")};
                    AsyncPostService send = new AsyncPostService(LivActivity.this, names, values, Configuration.IPWEB + "/webapp/f/fonctions2.php");
                    send.execute();
                }

                loadBDD();
                loadGetLivreur();
                mHandlerMap.postDelayed(mRunnableMap, aSeconds);
            }
        };


        mHandlerMap.postDelayed(mRunnableMap, aSeconds);

    }
    public void stopTimerMap(){
        if(mHandlerMap!=null){
            mHandlerMap.removeCallbacks(mRunnableMap);
        }
    }


    public void startTimerLiv() {
        //set a new Timer
        timerLiv = new Timer();

        //initialize the TimerTask's job
        initializeTimerTaskLiv();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timerLiv.schedule(timerTaskLiv, 15000, 15000); //
    }

    public void stoptimertaskLiv() {
        //stop the timer, if it's not already null
        if (timerLiv != null) {
            timerLiv.cancel();
            timerLiv = null;
        }
    }

    public void initializeTimerTaskLiv() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp

                        SharedPreferences preferences = PreferenceManager
                                .getDefaultSharedPreferences(LivActivity.this);
                        AsyncP2 refresh = new AsyncP2(LivActivity.this, new String[]{"action", "id", "lat", "lng", "locomotion"}, new String[]{"maj_geo_update", "" + preferences.getString("id", ""), "" + preferences.getString("ltL", ""), "" + preferences.getString("lnL", ""), "" + preferences.getString("locomotion", "")}, Configuration.IPWEB + "/webapp/f/fonctions2.php");
                        refresh.execute();
                        notif = false;
                        delDialog();
                        stoptimertaskLiv();


                    }
                });
            }
        };
    }


    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liv);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setLogo(getResources().getDrawable(R.mipmap.ic_burgeur));
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        findViewById(R.id.ib_notif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog();
            }
        });
        loadSession();
        loadBDDMessage();
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor edt = preferences.edit();


        loadBDD();
        testConnect();


        if (Build.VERSION.SDK_INT >= 23) {
            if(checkAndRequestPermissions()) {
                //Toast.makeText(this,"Permission autorisé",Toast.LENGTH_LONG).show();
            }
        } else {

        }
    }

    public void stpServ(View v) {
        PackageManager pm = LivActivity.this.getPackageManager();
        ComponentName componentName = new ComponentName(LivActivity.this, MyTestReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }




    private Handler myHandler = new Handler();
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            // Code à éxécuter de façon périodique
            String[] name = new String[]{
                    "action",
                    "id_operation"
            };
            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(LivActivity.this);
            String[] value = new String[]{
                    "get_operation",
                    "" + preferences.getInt("id_op_status", 0)
            };
            AsyncPostRefresh refresh = new AsyncPostRefresh(LivActivity.this, name, value, Configuration.IPWEB + "/webapp/f/fonctions2.php");
            myHandler.postDelayed(this, 500);
        }
    };

    public static void unset_session_liv(Context ct) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(ct);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("livraison_recue", null);
        editor.putString("id_enseigne", null);
        editor.putString("nom_enseigne", null);
        editor.putString("id_operation", null);
        editor.putString("id_restaurant", null);
        editor.putString("id_client", null);
        editor.putString("lnR", "0");
        editor.putString("ltR", "0");
        editor.putString("lnC", "0");
        editor.putString("ltC", "0");
        editor.putString("lnL", "0");
        editor.putString("ltL", "0");
        editor.putString("nom_client", null);
        editor.putString("distance", null);
        editor.putString("mess_livraison", null);
        editor.commit();
    }

    // enchainement de bouton de livreur pendant la durer de livraison


    private void viewAttenteCommande() {
        ((TextView) findViewById(R.id.textStatus)).setText("Nous vous cherchons un client veuillez patienter");
        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        ((ImageButton) findViewById(R.id.ib_notif)).setVisibility(View.GONE);
    }


    private void viewAttenteConfirmation(String lieuDLivraison) {
        ((TextView) findViewById(R.id.textStatus)).setText("Veuillez patienter le client est en train d'effectuer son paiement");
        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        ((ImageButton) findViewById(R.id.ib_notif)).setVisibility(View.GONE);
    }

    private void viewConfirmationFaite(String lieuDLivraison) {
        ((TextView) findViewById(R.id.textStatus)).setText("Paiement effectué, vous pouvez aller chercher la commande");
        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.ib_notif)).setVisibility(View.VISIBLE);
    }

    private void viewVersResto(String nomRestaurent, String lieuDLivraison) {

        ((TextView) findViewById(R.id.textStatus)).setText("Veuillez effectuer la commande");
        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.ib_notif)).setVisibility(View.VISIBLE);
    }

    private void viewVersLivraison(String nomRestaurent, String lieuDLivraison) {

        ((TextView) findViewById(R.id.textStatus)).setText(LivActivity.this.getResources().getString(R.string.msg_vers_liv));
        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.ib_notif)).setVisibility(View.VISIBLE);
    }

    private void viewArriverDestination(String nomRestaurent, String lieuDLivraison) {
    }

    private void viewAttenteClient(String lieuDLivraison) {
        ((TextView) findViewById(R.id.textStatus)).setText(LivActivity.this.getResources().getString(R.string.msg_att_client));
        ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.ib_notif)).setVisibility(View.VISIBLE);


    }

    // les actions a chaque fois quon presse le boutton <<buttonValider>>

    //action commencer le trajet

    private void CommencerTrajet(String nomRestaurent, String lieuDLivraison) {
        viewVersLivraison(nomRestaurent, lieuDLivraison);
        //les specification du code//
        ETAPE_LIVRAISON = Configuration.EN_VERS_RESTAURANT;

        //-----------------------//

    }

    //action commande recue

    private void CommandeRecue(String nomRestaurent, String lieuDLivraison) {
        viewArriverDestination(nomRestaurent, lieuDLivraison);
        //les specification du code//
        ETAPE_LIVRAISON = Configuration.EN_VERS_CLIENT;

        //-----------------------//

    }

    //action arriver chez clients//

    private void ArriverAdestination(String lieuDLivraison) {
        viewAttenteClient(lieuDLivraison);
        //les specification du code//
        ETAPE_LIVRAISON = Configuration.ARRIVE_A_DESTINATION;

        //-----------------------//

    }

    //livraison effectuee
    private void livraisonEffectuee() {
        viewAttenteCommande();
        //les specification du code//
        ETAPE_LIVRAISON = Configuration.EN_ATTENTE;

        //-----------------------//

    }


    //test de view
    private void test() {

    }

    public void valider(View v) {
        switch (ETAPE_LIVRAISON) {

            case 2:
                viewVersResto(nomDeRestaurent, lienDeLivraison);
                break;
            case 3:
                viewVersLivraison(nomDeRestaurent, lienDeLivraison);
                break;
            case 4:
                viewArriverDestination(nomDeRestaurent, lienDeLivraison);
                break;

        }
    }

    //init map

    LatLng livcoordonner = new LatLng(0,0);

    private void initMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.d("permission", "refused");

                return;
            } else {
                Log.d("permission", "autorised");
                //mMap.setMyLocationEnabled(true);
            }


            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location arg0) {
                        // TODO Auto-generated method stub

                        SharedPreferences preferences = PreferenceManager
                                .getDefaultSharedPreferences(LivActivity.this);


                        /*loadBDD();
                        loadGetLivreur();*/
                        myLatLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                        if (updtMap) {
                            updtMap = false;

                            mMap.clear();

                            String serverKey = "AIzaSyAx4d7m0yZu26-lSsC7MJ_nu5ORsM7AtvQ";
                            loadMapInformation();
                            Log.d("TAGMAP","0");
                            if (showingMarker) {
                                LatLng latLngC = new LatLng(latitudeC, longitudeC);
                                LatLng latLngL = new LatLng(latitudeL, longitudeL);
                                String cli = null;
                                cli = PreferenceManager
                                        .getDefaultSharedPreferences(LivActivity.this).getString("ac", "aucun");
                                byte[] clitemp =null;
                                String resto = null;
                                byte[] restemp = null;
                                byte[] nctemp = null;
                                resto = PreferenceManager
                                        .getDefaultSharedPreferences(LivActivity.this).getString("e", "aucun");
                                String nclient = PreferenceManager
                                        .getDefaultSharedPreferences(LivActivity.this).getString("nc", "aucun");
                                try {
                                    clitemp = cli.getBytes("UTF-8");
                                    restemp = resto.getBytes("ISO-8859-1");
                                    nctemp = nclient.getBytes("ISO-8859-1");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }




                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngC)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_flag))
                                        .title(new String(nctemp))
                                        .snippet(""+new String(clitemp)));

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLngL)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_fastfood2))
                                        .title(new String(restemp))
                                        .snippet(""+PreferenceManager
                                                .getDefaultSharedPreferences(LivActivity.this).getString("adfast", "aucun")));

                                GoogleDirection.withServerKey(serverKey)
                                        .from(latLngL)
                                        .to(latLngC)
                                        .alternativeRoute(true)
                                        .execute(new DirectionCallback() {
                                            @Override
                                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                                // Do something here

                                                Log.d("DIRECTION", "true");
                                                if (direction.isOK()) {
                                                    // Do something
                                                    Route route = direction.getRouteList().get(0);
                                                    Leg leg = route.getLegList().get(0);
                                                    ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                                    PolylineOptions polylineOptions = DirectionConverter.createPolyline(LivActivity.this, directionPositionList, 5, Color.RED);
                                                    mMap.addPolyline(polylineOptions);
                                                    Log.d("DIRECTION", "isOK");
                                                }

                                            }

                                            @Override
                                            public void onDirectionFailure(Throwable t) {
                                                // Do something here
                                                Log.d("DIRECTION", "false");
                                            }
                                        });

                                GoogleDirection.withServerKey(serverKey)
                                        .from(myLatLng)
                                        .to(latLngL)
                                        .alternativeRoute(true)
                                        .execute(new DirectionCallback() {
                                            @Override
                                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                                // Do something here

                                                Log.d("DIRECTION", "true");
                                                if (direction.isOK()) {
                                                    // Do something
                                                    Route route = direction.getRouteList().get(0);
                                                    Leg leg = route.getLegList().get(0);
                                                    ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                                    PolylineOptions polylineOptions = DirectionConverter.createPolyline(LivActivity.this, directionPositionList, 5, Color.RED);
                                                    mMap.addPolyline(polylineOptions);
                                                    Log.d("DIRECTION", "isOK");
                                                }

                                            }

                                            @Override
                                            public void onDirectionFailure(Throwable t) {
                                                // Do something here
                                                Log.d("DIRECTION", "false");
                                            }
                                        });
                            }
                        }

                            Log.d("savestatop", "" + preferences.getInt("statu_livreur", 0));
                            //Toast.makeText(LivActivity.this,"lat:"+arg0.getLatitude()+" lg:" +arg0.getLongitude(),Toast.LENGTH_LONG).show()

                            CameraPosition myPosition = new CameraPosition.Builder()
                                    .target(myLatLng).zoom(14).bearing(90).tilt(30).build();
                            if (zoom) {
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));
                                zoom = false;
                            }
                            /*String[] names = new String[]{"action", "id", "lat", "lng", "locomotion"};

                            String[] values = new String[]{"maj_geo", "" + idLivreur, "" + arg0.getLatitude(), "" + arg0.getLongitude(), "" + preferences.getString("locomotion", "")};
                            AsyncPostService send = new AsyncPostService(LivActivity.this, names, values, Configuration.IPWEB + "/webapp/f/fonctions2.php");
                            send.execute();*/
                        }


                });

            }
        }

    }


    private void loadMapInformation() {
        SharedPreferences preferences = PreferenceManager

                .getDefaultSharedPreferences(this);
        latitudeC = new Double(preferences.getString("ltC", "0")).doubleValue();
        latitudeL = new Double(preferences.getString("ltR", "0")).doubleValue();
        longitudeC = new Double(preferences.getString("lnC", "0")).doubleValue();
        longitudeL = new Double(preferences.getString("lnR", "0")).doubleValue();

        if (new Integer(preferences.getString("id_op_status", "0")).intValue() > 2 && new Integer(preferences.getString("id_op_status", "0")).intValue() < 8) {


            showingMarker = true;
        } else {
            showingMarker = false;
        }
    }


    //les fonction concernant socketIO

    private void initSocketIO() {
        try {
            mSocket = IO.socket(Configuration.IPSAILS);
        } catch (URISyntaxException e) {
            Log.d("savee err", e.getLocalizedMessage());
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
            mSocket.off();
            /*if(idLivreur!=null){

                mSocket.off(idLivreur, onNewMessage);
            }else{
                idLivreur = PreferenceManager
                        .getDefaultSharedPreferences(LivActivity.this).getString("idtemp", "0");
                mSocket.off(idLivreur, onNewMessage);
            }*/

        }
    }

    private boolean notif = true;
    int cppp = 0;
    int cpConf = 0;
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            LivActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    loadBDD();
                    loadGetLivreur();
                    Log.d("responsesocket=", args[0].toString());
                    JSONObject data = null;
                    try {
                        data = new JSONObject(args[0].toString());
                        Log.d("response=", args[0].toString());
                    } catch (JSONException e) {
                        //e.printStackTrace();
                        Log.e("res", "err" + e.getLocalizedMessage());
                    }

                    try {


                        if (data.has("type")) {
                            Log.d("socket response", args[0].toString());
                            if (data.getInt("type") == 0) {
                                //bip();
                            } else if (data.getInt("type") == 4) {
                                //payement effectuez
                                loadBDD();
                                loadGetLivreur();
                                SharedPreferences preferences = PreferenceManager
                                        .getDefaultSharedPreferences(LivActivity.this);
                                SharedPreferences.Editor editor = preferences.edit();

                                editor.putInt("statu_livreur", 4);
                                editor.commit();
                                test();
                                testConnect();
                                loadGetLivreur();

                            }
                        } else {
                            String id_operation = "";
                            String id_restaurant = "";
                            String id_livreur = "";
                            String distance = "";
                            String idclient = "";
                            String em = "";

                            id_operation = data.getString("o");
                            id_restaurant = data.getString("r");
                            id_livreur = data.getString("l");
                            distance = data.getString("d");
                            idclient = data.getString("c");
                            em = data.getString("em");
                            SharedPreferences preferences = PreferenceManager
                                    .getDefaultSharedPreferences(LivActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();

                            editor.putString("o", id_operation);
                            editor.putString("r", id_restaurant);
                            editor.putString("d", distance);
                            editor.putString("e", data.getString("e"));
                            editor.putString("c", idclient);
                            editor.putString("ac", data.getString("ac"));
                            editor.putString("p", data.getString("p"));
                            editor.putString("em", em);
                            editor.commit();

                            distanceLiv = distance;
                            idOperation = id_operation;
                            idRestaurent = id_restaurant;

                            if (cppp==0) {
                                //notif = false;
                                bip();
                                /*Toast.makeText(LivActivity.this.getApplicationContext(),
                                        "new notification", Toast.LENGTH_LONG).show();*/
                                addNotification("Demande de livraison", id_operation, em);
                                callDialog("essai", id_restaurant);
                                // startTimerLiv();
                                cppp++;

                            }


                        }

                        if (data.getInt("type") == 6) {
                            delDialog();
                            closeNotification(FM_NOTIFICATION_ID);
                            cppp=0;
                        }else  if (data.getInt("type") == 4) {
                            if(cpConf==0){
                                bip();
                                cpConf++;
                            }
                            loadBDD();
                            loadGetLivreur();
                        } else if (data.getInt("type") == 22) {
                            loadGetLivreur();
                            Log.d("type", "22");
                            loadBDD();
                            loadGetLivreur();
                            cppp=0;
                            Toast.makeText(LivActivity.this, Html.fromHtml(LivActivity.this.getResources().getString(R.string.msg_payment_effectue)), Toast.LENGTH_LONG).show();
                            /*SharedPreferences preferences = PreferenceManager
                                    .getDefaultSharedPreferences(LivActivity.this);
                            new AlertDialog.Builder(LivActivity.this)
                                    .setTitle("Erreur")
                                    .setMessage(Html.fromHtml(preferences.getString("c_client", "")))
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                            loadGetLivreur();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();*/
                        }else if (data.getInt("type") == 0) {
                            bip();
                        }


                    } catch (Exception e) {
                        Log.e("res", "err" + e.getLocalizedMessage());

                    }


                }
            });
        }
    };


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
            LivActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   /* Toast.makeText(LivActivity.this.getApplicationContext(), "Echec de connexion", Toast.LENGTH_LONG).show();*/
                    Log.e("Echec de connection", "  sails");
                }
            });
        }
    };






    private void instantiateSocket() {
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(idLivreur, onNewMessage);
        mSocket.connect();
    }

    //Notification

    public void addNotification(String title, String id, String content) {


        android.support.v4.app.NotificationCompat.Builder builder =
                new android.support.v4.app.NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(content);

        Intent notificationIntent = new Intent(this, LivActivity.class);
        notificationIntent.putExtra(title, content);
        notificationIntent.putExtra("notif", 1);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(FM_NOTIFICATION_ID, builder.build());
    }


    //dialog concern
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            finish();
            closeSocketIO();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, LivActivity.this.getResources().getString(R.string.msg_back_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void callDialog(String fastfood, String destination) {
        dialogdem = new Dialog(this);

        dialogdem.setContentView(R.layout.layout_demande_de_livraison);
        dialogdem.setTitle("Livraison");


        String clitemp =null;
        String resto = null;
        String cli = null;
        byte[] restemp = null;
        resto = PreferenceManager
                .getDefaultSharedPreferences(LivActivity.this).getString("em", "aucun");
        cli = PreferenceManager
                .getDefaultSharedPreferences(LivActivity.this).getString("e", "aucun");
        try {
            clitemp = URLDecoder.decode(cli,"UTF-8");
            restemp = resto.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("compare",cli);

        TextView fastfoodLieu = (TextView) dialogdem.findViewById(R.id.fastFLieu);
        TextView fastfoodNam = (TextView) dialogdem.findViewById(R.id.restoName);
        TextView tvp = (TextView) dialogdem.findViewById(R.id.tvPrix);
        fastfoodLieu.setText(Html.fromHtml(resto));
        tvp.setText(PreferenceManager
                .getDefaultSharedPreferences(LivActivity.this).getString("p", "aucun") + "" + Html.fromHtml("&#128"), TextView.BufferType.SPANNABLE);
        TextView clientLieu = (TextView) dialogdem.findViewById(R.id.adClient);
        clientLieu.setText(PreferenceManager
                .getDefaultSharedPreferences(LivActivity.this).getString("ac", "aucun"));
        fastfoodNam.setText(Html.fromHtml(new String(clitemp.replace("Ã©","é"))));

        Button dialogButtonRefuse = (Button) dialogdem.findViewById(R.id.buttonRefuser);
        // if button is clicked, close the custom dialog
        dialogButtonRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                notif = true;
                cppp=0;
                loadBDD();
                loadGetLivreur();
                dialogdem.dismiss();
            }
        });

        Button dialogButtonAccepter = (Button) dialogdem.findViewById(R.id.buttonAccepter);
        // if button is clicked, close the custom dialog
        dialogButtonAccepter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //attendreConfirmation();

                //setMarker(-19.9131, 47.516963, "resto");
                //setMarker(-18.913127, 47.516963, "destination");
                //buttonRestaurent.setText("EN ATTENTE DE PAYEMENT");
                notif = false;
                String idd = null;
                JSONObject id = new JSONObject();
                try {
                    id.put("id_op_status", idOperation);
                    id.put("id_user_livreur", idRestaurent);
                    id.put("d_restaurant", idLivreur);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                idd = id.toString();
                String[] name = new String[]{
                        "action",
                        "i",
                        "r",
                        "l",
                        "d",
                        "c",
                        "e"
                };
                String em =
                        PreferenceManager
                                .getDefaultSharedPreferences(LivActivity.this).getString("em", "aucun");
                String tk = "";
                if (em.split(" km").length > 1) tk = em.split(" km")[0].split(" ")[1];
                String[] value = new String[]{
                        "l_validate",
                        "" + idOperation,
                        "" + idRestaurent,
                        "" + idLivreur,
                        "" + tk,
                        "" + PreferenceManager
                                .getDefaultSharedPreferences(LivActivity.this).getString("c", "22"),
                        PreferenceManager
                                .getDefaultSharedPreferences(LivActivity.this).getString("em", "aucun")
                };

                AsyncPostServ asyncPostServ = new AsyncPostServ(LivActivity.this, name, value, Configuration.IPSAILS + "/l_validate");
                asyncPostServ.execute();

                loadBDD();
                loadGetLivreur();
                //mapFragment.getMapAsync(LivActivity.this);


                dialogdem.dismiss();
            }
        });
        dialogdem.show();

    }

    public void delDialog() {

        notif = true;
        dialogdem.dismiss();
    }



    public void callDialog(Context c) {
        final Dialog dialog = new Dialog(c);

        dialog.setContentView(R.layout.layout_dialog_voir_commande);
        dialog.setTitle(LivActivity.this.getResources().getString(R.string.msg_commande));


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);

        TextView listC = (TextView) dialog.findViewById(R.id.commandeList);
        TextView prixx = (TextView) dialog.findViewById(R.id.prix);
        TextView tell = (TextView) dialog.findViewById(R.id.tel);
        tell.setText(PreferenceManager
                .getDefaultSharedPreferences(this).getString("t", "aucun"));
        prixx.setText(PreferenceManager
                .getDefaultSharedPreferences(this).getString("p", "aucun") + "" + Html.fromHtml("&#128"), TextView.BufferType.SPANNABLE);
        listC.setText(Html.fromHtml(preferences.getString("bundle", "")), TextView.BufferType.SPANNABLE);
        dialog.findViewById(R.id.callph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(PreferenceManager
                        .getDefaultSharedPreferences(LivActivity.this).getString("t", "aucun"));
            }
        });
        // if button is clicked, close the custom dialog

        Button dialogButtonAccepter = (Button) dialog.findViewById(R.id.buttonOk);
        // if button is clicked, close the custom dialog
        dialogButtonAccepter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();

            }
        });
        dialog.show();

    }

    public void call(String p) {
        boolean hasPhone = getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        if (hasPhone) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + p));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(callIntent);
        }

    }


    public void callDialog() {
        final Dialog dialog = new Dialog(this);
        loadBDD();
        dialog.setContentView(R.layout.layout_dialog_voir_etape);
        dialog.setTitle(LivActivity.this.getResources().getString(R.string.msg_title_etapes));
        Button buttonAuResto = (Button) dialog.findViewById(R.id.buttonAuResto);
        // if button is clicked, close the custom dialog
        buttonAuResto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(LivActivity.this);
                String[] name = new String[]{"action",
                        "idOp",
                        "ltL",
                        "lnL",
                        "ltR",
                        "lnR",
                        "d",
                        "c"
                };

                String[] value = new String[]{"au_restaurant",
                        "" + preferences.getString("id_operation", ""),
                        "" + myLatLng.latitude,
                        "" + myLatLng.longitude,
                        "" + PreferenceManager
                                .getDefaultSharedPreferences(LivActivity.this).getString("ltR", "0"),
                        "" + PreferenceManager
                                .getDefaultSharedPreferences(LivActivity.this).getString("lnR", "0"),
                        "" + PreferenceManager
                                .getDefaultSharedPreferences(LivActivity.this).getString("d", ""),
                        "" + preferences.getString("c", "")
                };
                Log.d("idOp", "err" + preferences.getString("id_operation", ""));
                AsyncPostServ asyncPostServ = new AsyncPostServ(LivActivity.this, name, value, Configuration.IPSAILS + "/au_restaurant");
                asyncPostServ.execute();

                test();
                testConnect();
                loadGetLivreur();


                loadBDD();
                loadGetLivreur();
                dialog.dismiss();
            }
        });

        Button buttonFixPrix = (Button) dialog.findViewById(R.id.buttonFixPrix);
        buttonFixPrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();

                callDialogFP();
            }
        });

        Button buttonCodeRencontre = (Button) dialog.findViewById(R.id.buttonCodeRencontre);
        // if button is clicked, close the custom dialog
        buttonCodeRencontre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();
                callDialogCR();
            }
        });


        if (new Integer(PreferenceManager
                .getDefaultSharedPreferences(LivActivity.this).getString("id_op_status", "2")).intValue() == 4) {


            buttonCodeRencontre.setEnabled(false);
            buttonFixPrix.setEnabled(false);
            buttonAuResto.setEnabled(true);

        } else if (new Integer(PreferenceManager
                .getDefaultSharedPreferences(LivActivity.this).getString("id_op_status", "2")).intValue() == 5) {


            buttonCodeRencontre.setEnabled(false);
            buttonFixPrix.setEnabled(true);
            buttonAuResto.setText("Fait");
            buttonAuResto.setBackgroundColor(Color.GREEN);

            buttonAuResto.setEnabled(false);

        } else if (new Integer(PreferenceManager
                .getDefaultSharedPreferences(LivActivity.this).getString("id_op_status", "2")).intValue() >= 6) {


            buttonCodeRencontre.setEnabled(true);
            buttonFixPrix.setEnabled(false);
            buttonAuResto.setEnabled(false);

            buttonAuResto.setText("Fait");
            buttonAuResto.setBackgroundColor(Color.GREEN);


            buttonFixPrix.setText("Fait");
            buttonFixPrix.setBackgroundColor(Color.GREEN);

        }


        dialog.show();

    }

    public void callDialogCR() {
        final Dialog dialoge = new Dialog(this);

        dialoge.setContentView(R.layout.layout_dialog_code_rencontre);
        dialoge.setTitle(LivActivity.this.getResources().getString(R.string.msg_code_de_renc));

        Button buttonCodeRencontre = (Button) dialoge.findViewById(R.id.buttonCodeRencontre2);
        Button buttonChezClient= (Button) dialoge.findViewById(R.id.buttonchezclient);
        buttonChezClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] name = new String[]{"action",
                        "idOp",
                        "id",
                        "id_op_status",
                        "reponse",
                        "prix_reel",
                        "p_panier",
                        "p_id",
                        "l_id",
                        "ltL",
                        "lnL",
                        "ltR",
                        "lnR",
                        "c",
                        "reponse",
                        "d",
                };
                String[] value = new String[0];
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(LivActivity.this);
                value = new String[]{"attente_client",
                        preferences.getString("id_operation", ""),
                        "" + idLivreur,
                        preferences.getString("idOpStatus", ""),
                        preferences.getString("reponse", ""),
                        preferences.getString("prix_reel", ""),
                        preferences.getString("panier_prix_total_ttc", ""),
                        preferences.getString("p_id", ""),
                        preferences.getString("l_id", ""),
                        "" + myLatLng.latitude,
                        "" + myLatLng.longitude,
                        preferences.getString("ltR", ""),
                        preferences.getString("lnR", ""),
                        preferences.getString("c", ""),
                        preferences.getString("r", ""),
                        preferences.getString("d", ""),
                };

                AsyncPostServ asyncPostServ = new AsyncPostServ(LivActivity.this, name, value, Configuration.IPSAILS + "/attente_client");
                asyncPostServ.execute();
                loadBDD();
                loadGetLivreur();
                dialoge.dismiss();
            }
        });
        // if button is clicked, close the custom dialog
        buttonCodeRencontre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] name = new String[]{"action",
                        "idOp",
                        "l",
                        "id_op_status",
                        "reponse",
                        "prix_reel",
                        "p_panier",
                        "p_id",
                        "l_id",
                        "ltL",
                        "lnL",
                        "ltR",
                        "lnR",
                        "c",
                        "cd",
                        "d",
                        "nl"
                };
                String[] value = new String[0];
                EditText code = (EditText) dialoge.findViewById(R.id.editTextCodeRencontre);
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(LivActivity.this);
                value = new String[]{"validation_code_rencontre",
                        preferences.getString("id_operation", ""),
                        "" + idLivreur,
                        preferences.getString("idOpStatus", ""),
                        preferences.getString("reponse", ""),
                        preferences.getString("prix_reel", ""),
                        preferences.getString("panier_prix_total_ttc", ""),
                        preferences.getString("p_id", ""),
                        preferences.getString("l_id", ""),
                        "" + myLatLng.latitude,
                        "" + myLatLng.longitude,
                        preferences.getString("ltR", ""),
                        preferences.getString("lnR", ""),
                        preferences.getString("c", ""),
                        code.getText().toString(),
                        preferences.getString("d", ""),
                        ""
                };

                Log.d(preferences.getString("pr", "") + "err:" + preferences.getString("p", ""), "berr");

                AsyncPostServ asyncPostServ = new AsyncPostServ(LivActivity.this, name, value, Configuration.IPSAILS + "/validation_code");
                asyncPostServ.execute();

                /*if (preferences.getString("p", "") != preferences.getString("pr", "")) {
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
                    String[] values = new String[]{
                            "" + preferences.getString("id_operation", ""),
                            "mp",
                            key,
                            "" + preferences.getString("pr", "")
                    };

                    Log.d("key=", "" + key + "pr=" + preferences.getString("pr", ""));
                    AsyncP asyncP = null;
                    try {
                        asyncP = new AsyncP(LivActivity.this, names, values, Configuration.IPWEB + "/paiement/webservices/php/ws-v4_modify.php?op=" + URLEncoder.encode(preferences.getString("id_operation", ""), "UTF-8") + "&amount=" + URLEncoder.encode(preferences.getString("pr", ""), "UTF-8") + "&key=" + URLEncoder.encode(key, "UTF-8") + "&action=mp");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    asyncP.execute();

                } else {*/




                //}
                loadBDD();
                loadGetLivreur();

                cppp = 0;
                dialoge.dismiss();
            }
        });
        if (new Integer(PreferenceManager
                .getDefaultSharedPreferences(LivActivity.this).getString("id_op_status", "100")).intValue() == 6) {


            buttonCodeRencontre.setEnabled(false);
            buttonChezClient.setEnabled(true);

        }else if (new Integer(PreferenceManager
                .getDefaultSharedPreferences(LivActivity.this).getString("id_op_status", "100")).intValue() == 7) {


            buttonCodeRencontre.setEnabled(true);
            buttonChezClient.setBackgroundColor(Color.GREEN);
            buttonChezClient.setEnabled(false);

        }

        dialoge.show();

    }

    public void callDialogFP() {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.layout_dialog_fix_prix);
        dialog.setTitle(LivActivity.this.getResources().getString(R.string.msg_question_fixmp));

        Button buttonFPO = (Button) dialog.findViewById(R.id.buttonOUI);
        final TextView prix = (TextView) dialog.findViewById(R.id.prxf);
        prix.setText(PreferenceManager
                .getDefaultSharedPreferences(LivActivity.this).getString("p", "aucun") + "" + Html.fromHtml("&#128"), TextView.BufferType.SPANNABLE);
        // if button is clicked, close the custom dialog
        buttonFPO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] name = new String[]{
                        "action",
                        "idOp",
                        "l",
                        "id_op_status",
                        "prix_reel",
                        "panier_prix_total_ttc",
                        "p_id",
                        "l_id",
                        "ltL",
                        "lnL",
                        "ltR",
                        "lnR",
                        "c",
                        "r",
                        "reponse",
                        "d"
                };
                String[] value = new String[0];
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(LivActivity.this);
                prix.setText(preferences.getString("p", "") + "" + Html.fromHtml("&#128"), TextView.BufferType.SPANNABLE);
                value = new String[]{
                        "commande_recue",
                        preferences.getString("id_operation", ""),
                        "" + idLivreur,
                        preferences.getString("idOpStatus", ""),
                        preferences.getString("p", ""),
                        preferences.getString("p", ""),
                        preferences.getString("p_id", ""),
                        preferences.getString("l_id", ""),
                        "" + myLatLng.latitude,
                        "" + myLatLng.longitude,
                        preferences.getString("ltR", ""),
                        preferences.getString("lnR", ""),
                        preferences.getString("c", ""),
                        preferences.getString("id_restaurant", ""),
                        "non",
                        preferences.getString("d", "")
                };

                Log.d("lien", "" + Configuration.IPSAILS + "/c_recue?l=" + idLivreur + "&ltR=" + preferences.getString("ltR", "") + "&lnR=" + preferences.getString("lnR", "") +
                        "&ltL=" + myLatLng.latitude + "&lnR=" + myLatLng.longitude + "&reponse=non" + "&prix_reel=" + preferences.getString("p", "") +
                        "&panier_prix_total_ttc=" + preferences.getString("p", "") + "&d=" + preferences.getString("d", ""));
                String lien = "" + Configuration.IPSAILS + "/c_recue?l=" + idLivreur + "&ltR=" + preferences.getString("ltR", "") + "&lnR=" + preferences.getString("lnR", "") +
                        "&ltL=" + myLatLng.latitude + "&lnL=" + myLatLng.longitude + "&reponse=non" + "&prix_reel=" + preferences.getString("p", "") +
                        "&panier_prix_total_ttc=" + preferences.getString("p", "") + "&d=" + preferences.getString("d", "") + "&p_id=" + preferences.getString("p_id", "") + "&idOp=" + preferences.getString("id_operation", "");
                AsyncPostServ login = new AsyncPostServ(LivActivity.this, name, value, "" + Configuration.IPSAILS + "/c_recue");
                login.execute();


                test();
                testConnect();

                loadBDD();
                loadGetLivreur();
                dialog.dismiss();
            }

        });

        Button buttonFPN = (Button) dialog.findViewById(R.id.buttonNON);
        final EditText realPrix = (EditText) dialog.findViewById(R.id.editTextRM);
        // if button is clicked, close the custom dialog
        buttonFPN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] name = new String[]{"action",
                        "idOp",
                        "id",
                        "id_op_status",
                        "reponse",
                        "prix_reel",
                        "panier_prix_total_ttc",
                        "p_id",
                        "l_id",
                        "ltL",
                        "lnL",
                        "ltR",
                        "lnR",
                        "c",
                        "r",
                        "reponse",
                        "d",
                };
                String[] value = new String[0];
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(LivActivity.this);
                value = new String[]{"commande_recue",
                        preferences.getString("id_operation", ""),
                        "" + idLivreur,
                        preferences.getString("idOpStatus", ""),
                        preferences.getString("reponse", ""),
                        realPrix.getText().toString(),
                        preferences.getString("p", ""),
                        preferences.getString("p_id", ""),
                        preferences.getString("l_id", ""),
                        "" + myLatLng.latitude,
                        "" + myLatLng.longitude,
                        preferences.getString("ltR", ""),
                        preferences.getString("lnR", ""),
                        preferences.getString("c", ""),
                        preferences.getString("id_restaurant", ""),
                        "non",
                        preferences.getString("d", ""),
                };
                if(realPrix.getText().toString()=="" || realPrix.getText().toString().isEmpty()){
                    Toast.makeText(LivActivity.this,LivActivity.this.getResources().getString(R.string.msg_fix_prix_vide),Toast.LENGTH_LONG).show();
                }else{
                    String lien=""+Configuration.IPSAILS + "/c_recue?l="+ idLivreur+"&ltR="+preferences.getString("ltR", "")+"&lnR="+preferences.getString("lnR", "")+
                            "&ltL="+ myLatLng.latitude+"&lnL="+ myLatLng.longitude+"&reponse=non"+ "&prix_reel="+realPrix.getText().toString()+
                            "&panier_prix_total_ttc="+preferences.getString("p", "")+"&d="+preferences.getString("d", "")+"&p_id="+preferences.getString("p_id", "")+"&idOp="+preferences.getString("id_operation", "");
                    AsyncPostServ login = new AsyncPostServ(LivActivity.this, name, value, ""+Configuration.IPSAILS + "/c_recue");
                    login.execute();


                    loadBDD();
                    loadGetLivreur();
                    dialog.dismiss();
                }

            }
        });

        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_liv, menu);
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        if (new Integer(preferences.getString("id_op_status", "0")).intValue() <= 3 || new Integer(preferences.getString("id_op_status", "0")).intValue() >= 8) {

            menu.getItem(0).setEnabled(false);
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setEnabled(false);
            menu.getItem(1).setVisible(false);
        } else {
            menu.getItem(0).setEnabled(true);
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setEnabled(true);
            menu.getItem(1).setVisible(true);
        }

        return true;
    }

    private void loadBDD() {
        String[] names = new String[]{"action", "id"};
        String[] values = new String[]{"load_bdd", "" + idLivreur};
        AsyncPostServ send = new AsyncPostServ(LivActivity.this, names, values, Configuration.IPWEB + "/webapp/f/fonctions2.php");
        send.execute();
    }

    private void loadBDDMessage() {
        String[] names = new String[]{"action"};
        String[] values = new String[]{"load_message_android"};
        AsyncLoadAndroid send = new AsyncLoadAndroid(LivActivity.this, names, values, Configuration.IPWEB + "/webapp/f/fonctions2.php");
        send.execute();
    }



    //menu option
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
           /* case android.R.id.home:
                finish();
                startActivity(new Intent(LivActivity.this,ChoixTypLivreurActivity.class));
                return true;*/

            case R.id.action_chat:
                finish();
                startActivity(new Intent(LivActivity.this, MessageActivity.class));
                return false;

            case R.id.action_panier:
                callDialog(this);
                return false;

            case R.id.action_disconect:

                new AlertDialog.Builder(LivActivity.this)
                        .setTitle(LivActivity.this.getResources().getString(R.string.msg_title_deco))
                        .setMessage(LivActivity.this.getResources().getString(R.string.msg_deco))
                        .setPositiveButton(LivActivity.this.getResources().getString(R.string.msg_oui), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                stpServ(null);
                                if(mSocket!=null){

                                    mSocket.disconnect();
                                    if(idLivreur!=null){

                                        mSocket.off(idLivreur, onNewMessage);
                                    }else{
                                        idLivreur = PreferenceManager
                                                .getDefaultSharedPreferences(LivActivity.this).getString("idtemp", "0");
                                        mSocket.off(idLivreur, onNewMessage);
                                    }

                                }

                                String[] name = new String[]{
                                        "action",
                                        "id",
                                };
                                String em =
                                        PreferenceManager
                                                .getDefaultSharedPreferences(LivActivity.this).getString("id", "0");
                                String[] value = new String[]{
                                        "logoutMap" ,
                                        ""+ em
                                };

                                AsyncPostServ asyncPostServ = new AsyncPostServ(LivActivity.this, name, value, Configuration.IPWEB + "/webapp/f/fonctions2.php");
                                asyncPostServ.execute();
                                closeSocketIO();
                                finish();
                                Intent intent = new Intent(LivActivity.this, MainActivity.class);

                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;

        }
        return super.onOptionsItemSelected(item);

    }


    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    // Map concern
    boolean direction = false;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        //LatLng sydney = new LatLng(-18.913127, 47.516963);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            /*if(showingMarker){
                LatLng latLngC = new LatLng(latitudeC, longitudeC);
                LatLng latLngL = new LatLng(latitudeL, longitudeL);
                mMap.addMarker(new MarkerOptions()
                        .position(latLngC)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_flag))
                        .title("Destination"));

                mMap.addMarker(new MarkerOptions()
                        .position(latLngL)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_burgeur))
                        .title("Restaurent"));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngC,12.0f));
            }*/
            mMap.setMyLocationEnabled(true);

            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude()),12.0f));

        } else {

        }
    }


    //concern load Session

    private void loadSession() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        connect = preferences.getInt("idClient", 0);
        nomLivreur = preferences.getString("nom", null);
        prenomLivreur = preferences.getString("prenom", null);
        idLivreur = preferences.getString("id", null);
        isLivreur = preferences.getString("is_livreur", null);
        statutLivreur = preferences.getInt("statu_livreur", 0);


    }
    //load type livreur  http://dev.myburgernow.com/webapp/f/?mail=mirah.ratahiry@gmail.com&mdp=$$ratahiry&locomotion=1

    private void loadGetLivreur() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor edt = preferences.edit();
        typeLivreur = preferences.getString("type_livreur", TYPE_LIVREUR.PIEDS.name());

        invalidateOptionsMenu();
        Log.d("cp2free"+new Integer(preferences.getString("id_op_status", "0")).intValue(),preferences.getString("timer", "2"));
        if (new Integer(preferences.getString("id_op_status", "0")).intValue() > 3 && new Integer(preferences.getString("id_op_status", "0")).intValue() < 8 && preferences.getString("id_op_status", "0")!=null) {
            //stoptimertask();
            if(new Integer(preferences.getString("id_op_status", "0")).intValue() == 4 ){
                ((TextView) findViewById(R.id.textStatus)).setText(Html.fromHtml(this.getResources().getString(R.string.msg_vers_resto)));
            }else  if(new Integer(preferences.getString("id_op_status", "0")).intValue() == 5 ){
                ((TextView) findViewById(R.id.textStatus)).setText(Html.fromHtml(preferences.getString("currentMessage", "")));
            }else if(new Integer(preferences.getString("id_op_status", "0")).intValue() == 6 ){
                ((TextView) findViewById(R.id.textStatus)).setText(Html.fromHtml(this.getResources().getString(R.string.msg_vers_liv)));
            }else if(new Integer(preferences.getString("id_op_status", "0")).intValue() == 7 ){
                ((TextView) findViewById(R.id.textStatus)).setText(Html.fromHtml(this.getResources().getString(R.string.msg_att_client)));
            }

            ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
            ((ImageButton) findViewById(R.id.ib_notif)).setVisibility(View.VISIBLE);
        } else {
            Log.d("cp2test",""+(Integer.parseInt(preferences.getString("timer", "2").toString()) ==2));
            Log.d("cp2test",""+(Integer.parseInt(preferences.getString("timer", "2").toString()) == 0));
            if (Integer.parseInt(preferences.getString("timer", "2").toString()) == 0) {

                ((TextView) findViewById(R.id.textStatus)).setText(Html.fromHtml(LivActivity.this.getResources().getString(R.string.msg_localisation_troploin)));
            } else if(Integer.parseInt(preferences.getString("timer", "2").toString()) == 2){
                ((TextView) findViewById(R.id.textStatus)).setText(Html.fromHtml(LivActivity.this.getResources().getString(R.string.msg_localisation_encours)));

            }else{
                if(new Integer(preferences.getString("id_op_status", "0")).intValue() == 3 ){
                    ((TextView) findViewById(R.id.textStatus)).setText(Html.fromHtml(this.getResources().getString(R.string.msg_attente_conf)));
                }else{
                    ((TextView) findViewById(R.id.textStatus)).setText(Html.fromHtml(this.getResources().getString(R.string.msg_attente_comande)));
                }

            }
            ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
            ((ImageButton) findViewById(R.id.ib_notif)).setVisibility(View.GONE);
        }


    }

    //test connection

    private void testConnect() {
        if (connect == 0) {
            stopTimerMap();
            stoploadMap();
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else {
            initSocketIO();
            instantiateSocket();
            loadMapInformation();
            loadGetLivreur();
            scheduleMap();
            startTimerMAp();
            initMap();

        }
    }


    //button click etape

    public void nextStape(View v) {

    }








    /*public void launchTestService() {
        // Construct our Intent specifying the Service
        Intent i = new Intent(getApplicationContext(), MapService.class);
        // Add extras to the bundle
        i.putExtra("foo", "bar");
        // Start the service
        startService(i);
    }*/

    public void scheduleMap() {
        /*PackageManager pm  = LivActivity.this.getPackageManager();
        ComponentName componentName = new ComponentName(LivActivity.this, MyTestReceiver.class);
        pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        //Toast.makeText(getApplicationContext(), "activated", Toast.LENGTH_LONG).show();
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), MyTestReceiver.class);
        intent.putExtra("id",""+preferences.getString("id",""));
        intent.putExtra("locomotion",""+preferences.getString("locomotion",""));
        // Create a PendingIntent to be triggered when the alarm goes off
        pIntent = PendingIntent.getBroadcast(this, MyTestReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setRepeating(AlarmManager.RTC_WAKEUP,
                10000, 10000,
                pIntent);*/
        /*SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        Intent intent = new Intent(this, MapService.class);

        intent.putExtra("id",""+preferences.getString("id",""));
        intent.putExtra("locomotion",""+preferences.getString("locomotion",""));
        startService(intent);*/
    }

    public void stoploadMap(){
        if(alarm!=null && pIntent!=null){
            alarm.cancel(pIntent);
        }
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

                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeNotification(int id){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }

}
