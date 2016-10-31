package my.burger.now.app.services;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import my.burger.now.app.configuration.Configuration;
import my.burger.now.app.connexion.LivreurGeoAsync;

/**
 * Created by 8029 on 16/06/2016.
 */
public class MapService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    LocationManager lm = null;
    LocationListener gpsLocationListener = null;
    Location gpsLocation = null;
    public MapService() {
        super("map");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        getLocation();
        if(gpsLocation!=null){
            Log.i("MyTestService", "Service running lng=" + gpsLocation.getLongitude());
            send(intent.getStringExtra("id"),intent.getStringExtra("locomotion"));
        }else{
            Log.i("MyTestService", "Service running lng=null");
        }

    }

    public void send(String id,String locom){
        String[] names = new String[]{"action", "id", "lat", "lng", "locomotion"};

        String[] values = new String[]{"maj_geo", "" + id, "" + gpsLocation.getLatitude(), "" + gpsLocation.getLongitude(), "" + locom};
        LivreurGeoAsync send = new LivreurGeoAsync( names, values, Configuration.IPWEB + "/webapp/f/fonctions2.php");
        send.execute();


    }

    public void getLocation() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
        gpsLocationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLocationChanged(Location location) {
                //do something with the new location
                if (location != null)
                    gpsLocation = location;

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("PERMS","false");
            return;
        }
        gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, gpsLocationListener);


    }
}
