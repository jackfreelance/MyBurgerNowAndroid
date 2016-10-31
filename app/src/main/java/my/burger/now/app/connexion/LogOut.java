package my.burger.now.app.connexion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import my.burger.now.app.LivActivity;

/**
 * Created by Jack on 16/12/2015.
 */
public class LogOut {
    Context ct = null;

    public LogOut(Context c) {
        this.ct = c;
    }

    public void logout(){
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(ct);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("idClient", 0);
        String id = preferences.getString("id","0");
        String p = preferences.getString("p","0");
        String pr = preferences.getString("pr","0");
        editor.putInt("statu_livreur", 0);
        editor.putString("ltC", "0");
        editor.putString("lnC", "0");
        editor.putString("ltL", "0");
        editor.putString("lnL", "0");
        editor.clear();
        editor.commit();
        editor.putString("idtemp", id);
        editor.putString("p", p);
        editor.putString("pr", pr);
        editor.commit();

    }

}
