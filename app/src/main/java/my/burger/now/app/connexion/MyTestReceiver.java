package my.burger.now.app.connexion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import my.burger.now.app.services.MapService;

/**
 * Created by 8029 on 16/06/2016.
 */
public class MyTestReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "MapService";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MapService.class);
        i.putExtra("id", ""+intent.getStringExtra("id"));
        i.putExtra("locomotion", ""+intent.getStringExtra("locomotion"));
        context.startService(i);
    }



}
