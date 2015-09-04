package sachan.dheeraj.mebeerhu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/*
import com.facebook.FacebookSdk;
*/
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends ActionBarActivity {
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "Initializing app, OnCreate for LoginActivity called");

        //getSupportActionBar().hide();
        FacebookSdk.sdkInitialize(getApplicationContext());
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "sachan.dheeraj.mebeerhu",PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG,"Caught Package Manager exception: ",e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(LOG_TAG,"Caught Algorithm exception: ",e);
        }
        setContentView(R.layout.activity_login);

        /* Check if auto login can be performed */
        boolean login_req = true;
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file), Context.MODE_PRIVATE);
        String access_token = sharedPref.getString(getString(R.string.access_token), null);
        String login_method = sharedPref.getString(getString(R.string.login_method), null);
        if (access_token != null && login_method != null)
        {
            Log.v(LOG_TAG, String.format("Cached token found %s, method = %s",
                    access_token, login_method ));
            if (login_method.equals(getString(R.string.facebook_login)))
            {
                AccessToken fbToken = AccessToken.getCurrentAccessToken();
                if (fbToken != null)
                {
                    Log.v(LOG_TAG, "FB access token valid, token = "+ fbToken.toString());
                    login_req = false;
                    HttpAgent.tokenValue = access_token.toString();
                }
            }
            else if (login_method.equals(getString(R.string.google_login)))
            {
                Log.v(LOG_TAG, "Auto-login through Google, token = ");
                login_req = false;
                HttpAgent.tokenValue = access_token;
            }
            else if (login_method.equals(getString(R.string.app_login)))
            {
                login_req = false;
                HttpAgent.tokenValue = access_token;
            }
            else
            {
                Log.e(LOG_TAG, String.format("Invalid Login Method %s from cached preferences", login_method));
                HttpAgent.tokenValue = null;
            }
        }

        if(login_req) {
            HttpAgent.tokenValue = null;
            Log.i(LOG_TAG, "User not logged in, opening login landing page");
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new LoginActivityFragment()).commit();
        }
        else
        {
            Log.i(LOG_TAG, String.format("User already logged in through %s, token = %s",
                    login_method, access_token ));
            Intent intent = new Intent(this, FeedsActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
