package sachan.dheeraj.mebeerhu;
//https://github.com/googleplus/gplus-quickstart-android.git
import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.provider.Contacts;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.io.IOException;

public class GoogleActivity extends ActionBarActivity  implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
        /*,ResultCallback<People.LoadPeopleResult>*/ {

    private static String SERVER_CLIENT_ID;

    private static final String LOG_TAG = GoogleActivity.class.getSimpleName();

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mIntentInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG_TAG, "onCreate for Google activity");
        SERVER_CLIENT_ID = getString(R.string.google_server_client_id);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        GoogleHelper.setGoogleApiClient(mGoogleApiClient);
        setContentView(R.layout.activity_google);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_google, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


   /* @Override
    public void onResult(People.LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    Log.d(TAG, "Display name: " + personBuffer.get(i).getDisplayName());
                }
            } finally {
                personBuffer.close();
            }
        } else {
            Log.e(TAG, "Error requesting people data: " + peopleData.getStatus());
        }
    }*/

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

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(LOG_TAG,"User connected with Google");

        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.v(LOG_TAG,"Account name = " + accountName);

                Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                //String scopes = "audience:server:client_id:" + SERVER_CLIENT_ID; // Not the app's client ID. Enable this with the CLIENT_ID generated for the server (WEB) */
                String scopes = "oauth2:profile";
                try {
                    String token = GoogleAuthUtil.getToken(getApplicationContext(), account, scopes);
                    Log.v(LOG_TAG,"Obtained google token = " + token);
                    return token;
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error retrieving ID token.", e);
                    return null;
                } catch (GoogleAuthException e) {
                    Log.e(LOG_TAG, "Error retrieving ID token.", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String token) {
                if (token != null)
                {
                    Log.i(LOG_TAG, "Google auth token retrieved successfully, token = " + token);
                    HttpAgent.tokenValue = token;
                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
                    SharedPreferences.Editor prefEdit = sharedPref.edit();
                    prefEdit.putString(getString(R.string.login_method), getString(R.string.google_login));
                    prefEdit.putString(getString(R.string.access_token), HttpAgent.tokenValue);

                    if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null)
                    {
                        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                        String personName = currentPerson.getDisplayName();
                        Person.Image personPhoto = currentPerson.getImage();
                        String personGooglePlusProfile = currentPerson.getUrl();
                        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                        Log.v(LOG_TAG, String.format("User details : name %s, email %s", personName, email));

                        prefEdit.putString(getString(R.string.key_username), email);
                        prefEdit.putString(getString(R.string.key_fullname), personName);
                        prefEdit.putString(getString(R.string.key_email), email);
                    }
                    else
                    {
                        Log.e(LOG_TAG,"Google Signed-in but could not retrieve user's details");
                    }

                    prefEdit.apply();
                }
                else
                {
                    Log.i(LOG_TAG, "Error retrieving Google auth token, token is null");
                }
                getSupportFragmentManager().beginTransaction().add(R.id.google_frame_layout, new SelectTagsFragment()).commit();
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
        }.execute();
    }


    @Override
    public void onConnectionSuspended(int i)  {
        Log.e(LOG_TAG,"Google Connection suspended, attempting to re-connect");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(LOG_TAG,"Google Connection failed");
        if (!mIntentInProgress && result.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(result.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }else{
            Log.e(LOG_TAG, String.format( "Cannot sign in with Google, requestCode %d, responseCode %d",
                                          requestCode, responseCode));
        }
    }
}
