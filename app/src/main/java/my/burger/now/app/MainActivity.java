package my.burger.now.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import my.burger.now.app.configuration.Configuration;
import my.burger.now.app.connexion.AsyncPostService;
import my.burger.now.app.connexion.LogOut;
import my.burger.now.app.connexion.MyTestReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    EditText _emailText;
    EditText _passwordText;
    AlertDialog.Builder dialog = null;
    RadioGroup rg;
    int loco = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.mipmap.ic_logo);
        rg = (RadioGroup) findViewById(R.id.radiog);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(getResources().getDrawable(R.mipmap.ic_burgeur));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        stpServ(null);


        //–––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––//

        _emailText = (EditText)findViewById(R.id.editText_email);
        _passwordText = (EditText)findViewById(R.id.editText_password);
        dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", null);
        new LogOut(MainActivity.this).logout();
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            dialog.setTitle(getResources().getString(R.string.lb_tt_erreur_email));
            dialog.setMessage(getResources().getString(R.string.lb_tt_instruction_email_err));

            valid = false;
        } else {
            _emailText.setError(null);
            if (password.isEmpty() || password.length() < 5 || password.length() > 20) {
                dialog.setTitle(getResources().getString(R.string.lb_tt_erreur_mdp));
                dialog.setMessage(password+""+getResources().getString(R.string.lb_tt_instruction_mdp_err));
                valid = false;
            } else {
                _passwordText.setError(null);
            }
        }



        return valid;
    }

    public void toAceuil(View v){

        if (!validate()) {
            onLoginFailed();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);

    }
    public void stpServ(View v){
        PackageManager pm  = MainActivity.this.getPackageManager();
        ComponentName componentName = new ComponentName(MainActivity.this, MyTestReceiver.class);
        pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void onLoginSuccess() {


       String usr = null;

        try {
            JSONObject user = new JSONObject();
            user.put("mail", _emailText.getText().toString());
            try {
                user.put("pwd", SHA1(_passwordText.getText().toString()));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            usr = user.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //String[] name = new String[]{"mail","pwd"};
        String[] name = new String[]{"action","login","password","locomotion"};
        String[] value = new String[0];
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", _emailText.getText().toString());
        int selectedId=rg.getCheckedRadioButtonId();
        if(selectedId==R.id.radioButtonApied){
            loco = 1;
        }else if(selectedId==R.id.radioButtonAroulette){
            loco = 2;
        }else{
            loco = 3;
        }

        try {
            editor.putString("pwd", SHA1(_passwordText.getText().toString()));
            editor.commit();
            value = new String[]{"login",_emailText.getText().toString(),SHA1(_passwordText.getText().toString()),""+loco};
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        AsyncPostService login = new AsyncPostService(this,name,value, Configuration.IPWEB+"/webapp/f/fonctions2.php");
        login.execute();

    }

    public void onLoginFailed() {
        dialog.show();


    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                }
                else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }


    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

}
