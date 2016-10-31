package my.burger.now.app.services;

import android.app.Application;
import android.content.Intent;

/**
 * Created by 8029 on 21/06/2016.
 */
public class AppControlleur extends Application {
    private static AppControlleur mInstance;

    public static synchronized AppControlleur getInstance() {
        return mInstance;
    }

    public void stopService() {
        stopService(new Intent(getBaseContext(), MapService.class));
    }
}