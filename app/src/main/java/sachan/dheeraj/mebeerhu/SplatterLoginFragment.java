package sachan.dheeraj.mebeerhu;


import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import sachan.dheeraj.mebeerhu.model.AccessTokenCredentials;
import sachan.dheeraj.mebeerhu.model.SignUpReply;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class SplatterLoginFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private EditText userNameEditText, passwordEditText;
    private Button loginInButton;

    private LoginButton loginButtonFaceBook;
    private com.google.android.gms.common.SignInButton googleButton;
    private CallbackManager callbackManager;
    private TextView forgotPassword;

    private String username;

    private static final String LOG_TAG = SplatterLoginFragment.class.getSimpleName();
    private TextView err_username, err_pwd;

    public SplatterLoginFragment() {
        // Required empty public constructor
    }

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 245;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mIntentInProgress;

    private GoogleAsyncTask mGoogleAsyncTask;

    public boolean validateUsername(String text)
    {
        if (text==null || text.length()==0) {
            err_username.setText("Please enter your username");
            err_username.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }
    public boolean validatePassword(String text)
    {
        if (text==null || text.length()==0) {
            err_pwd.setText("Please enter your password");
            err_pwd.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView for SplatterLoginFragment");
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        loginInButton = (Button) view.findViewById(R.id.login_button);
        loginButtonFaceBook = (LoginButton) view.findViewById(R.id.fb_login_button);

        userNameEditText = (EditText) view.findViewById(R.id.username);
        passwordEditText = (EditText) view.findViewById(R.id.password);
        err_username = (TextView) view.findViewById(R.id.error_message_username);
        err_pwd = (TextView) view.findViewById(R.id.error_message_pwd);

        loginButtonFaceBook.setReadPermissions("user_friends");
        googleButton = (com.google.android.gms.common.SignInButton) view.findViewById(R.id.google);
        // If using in a fragment
        loginButtonFaceBook.setFragment(this);
        // Other app specific specialization
        callbackManager = CallbackManager.Factory.create();
        forgotPassword = (TextView) view.findViewById(R.id.forgot_pwd);

        loginInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Login button clicked, attempting to login");
                username = userNameEditText.getText().toString();

                boolean all_fields_ok = true;

                if(!validateUsername(username)) {
                    all_fields_ok = false;
                }
                else {
                    err_username.setText("");
                    err_username.setVisibility(View.GONE);
                }
                if(!validatePassword(passwordEditText.getText().toString())){
                    all_fields_ok = false;
                }
                else {
                    err_pwd.setText("");
                    err_pwd.setVisibility(View.GONE);
                }

                if(!all_fields_ok)
                    return;

                loginInButton.setEnabled(false);
                final HashMap<String, String> stringStringHashMap = new HashMap<String, String>();
                stringStringHashMap.put("username", username);
                stringStringHashMap.put("password", passwordEditText.getText().toString());

                new AsyncTask<Void, Void, Boolean>() {

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        /* Temp HttpAgent.tokenValue = "abcdxyz";
                        if (true) return true; */
                        Log.d(LOG_TAG, "Posted login information to server");
                        String reply = HttpAgent.postGenericData(UrlConstants.LOGIN_URL, JsonHandler.stringifyNormal(stringStringHashMap), getActivity());
                        AccessTokenCredentials accessTokenCredentials = JsonHandler.parseNormal(reply, AccessTokenCredentials.class);
                        if (accessTokenCredentials != null) {
                            HttpAgent.tokenValue = accessTokenCredentials.getToken();
                            Log.v(LOG_TAG, "Sign-in reply received, auth token = " + HttpAgent.tokenValue);
                            return true;
                        }
                        Log.v(LOG_TAG, "Access credentials returned as Null, login unsuccessful");
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if (aBoolean) {
                            Log.d(LOG_TAG, "Log-in successful, starting Feeds Activity");
                            SharedPreferences sharedPref = getActivity().getSharedPreferences(
                                    getString(R.string.preference_file), Context.MODE_PRIVATE);
                            SharedPreferences.Editor prefEdit = sharedPref.edit();
                            prefEdit.putString(getString(R.string.login_method), getString(R.string.app_login));
                            prefEdit.putString(getString(R.string.key_username), username);
                            prefEdit.putString(getString(R.string.access_token), HttpAgent.tokenValue);
                            prefEdit.apply();

                            Intent intent = new Intent(getActivity(),FeedsActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d(LOG_TAG, "Log-in unsuccessful");
                        }
                    }
                }.execute();
            }
        });

        loginButtonFaceBook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(LOG_TAG, "Sign-in with Facebook successful");
                final String username = loginResult.getAccessToken().getUserId();
                HttpAgent.tokenValue = loginResult.getAccessToken().getToken();

                Set<String> permissions = loginResult.getAccessToken().getPermissions();
                Log.d(LOG_TAG, "List of permissions obtained: " + permissions);

                Bundle parameters = new Bundle();

                if (permissions.contains("email")) {
                    Log.v(LOG_TAG, "Email permission given by user for FB login");
                    parameters.putString("fields", "id,name,email,picture.width(200).height(200)");
                } else {
                    Log.e(LOG_TAG, "Email permission NOT given by user for FB login");
                    parameters.putString("fields", "id,name,picture.width(200).height(200)");
                }

                Log.v(LOG_TAG, String.format("Username %s, AccessToken %s",
                        username, HttpAgent.tokenValue));

                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me",
                        parameters,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                String username = null, fullName = null, emailID = null, profileUri = null;
                                boolean fatal = false;
                                Log.v(LOG_TAG, "Response from FB graph: " + response.toString());
                                FacebookRequestError error = response.getError();

                                if (error == null) {
                                    JSONObject mObject = response.getJSONObject();
                                    try {
                                        if (mObject.has("id")) {
                                            username = mObject.getString("id");
                                        }
                                        if (mObject.has("name")) {
                                            fullName = mObject.getString("name");
                                        }
                                        if (mObject.has("email")) {
                                            emailID = mObject.getString("email");
                                        }
                                        if (mObject.has("picture")) {
                                            mObject = mObject.getJSONObject("picture");
                                            if (mObject.has("data")) {
                                                mObject = mObject.getJSONObject("data");
                                                if (mObject.has("url")) {
                                                    profileUri = mObject.getString("url");
                                                }
                                            }
                                        }

                                    } catch (JSONException e) {
                                        Log.e(LOG_TAG, "Exception in parsing JSON from facebook");
                                        fatal = true;
                                        e.printStackTrace();
                                    }
                                } else {
                                    fatal = true;
                                    Log.v(LOG_TAG, "Error in FB login " + error);
                                }
                                if (fatal || (username == null) || (fullName == null)) {
                                    Log.e(LOG_TAG, String.format("Mandatory FB params, id:%s or email:%s not received", username, emailID));
                                    Log.i(LOG_TAG, "Logging out from facebook");
                                    LoginManager.getInstance().logOut();
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Log.v(LOG_TAG, String.format("Name %s, Email %s, PicURL %s",
                                            fullName, emailID, profileUri));
                                    SharedPreferences sharedPref = getActivity().getSharedPreferences(
                                            getString(R.string.preference_file), Context.MODE_PRIVATE);
                                    SharedPreferences.Editor prefEdit = sharedPref.edit();
                                    prefEdit.putString(getString(R.string.login_method), getString(R.string.facebook_login));
                                    prefEdit.putString(getString(R.string.key_username), username);
                                    prefEdit.putString(getString(R.string.key_fullname), fullName);
                                    prefEdit.putString(getString(R.string.key_email), emailID);
                                    prefEdit.putString(getString(R.string.key_profile_image_uri), profileUri);

                                    prefEdit.putString(getString(R.string.access_token), HttpAgent.tokenValue);
                                    prefEdit.apply();

                                    //String k = JsonHandler.stringifyNormal(loginResult);
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new SelectTagsFragment()).commit();
                                }
                            }
                        }
                ).executeAsync();
            }

            @Override
            public void onCancel() {
                Log.i(LOG_TAG, "Facebook login operation cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(LOG_TAG, "In Facebook Sign-in, got exception: ", exception);
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Sign-in with Google clicked, signing-in");


                String SERVER_CLIENT_ID = getString(R.string.google_server_client_id);

                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                            .addConnectionCallbacks(SplatterLoginFragment.this)
                            .addOnConnectionFailedListener(SplatterLoginFragment.this)
                            .addApi(Plus.API)
                            .addScope(new Scope(Scopes.PROFILE))
                            .build();

                    GoogleHelper.setGoogleApiClient(mGoogleApiClient);
                }

                if (mGoogleAsyncTask != null) {
                    mGoogleAsyncTask.cancel(false);
                }

                if (mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.disconnect();
                }
                mGoogleApiClient.connect();

            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Clicked forgot password, switching to it");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new FragmentResetPassword()).commit();
            }
        });
        return view;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(LOG_TAG, "User connected with Google");

        mGoogleAsyncTask = new GoogleAsyncTask();
        mGoogleAsyncTask.execute();
    }


    @Override
    public void onConnectionSuspended(int i)  {
        Log.e(LOG_TAG, "Google Connection suspended, attempting to re-connect");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(LOG_TAG, "Google Connection failed");
        if (!mIntentInProgress && result.hasResolution()) {
            try {
                mIntentInProgress = true;
                getActivity().startIntentSenderForResult(result.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        if (mGoogleAsyncTask != null) {
            mGoogleAsyncTask.cancel(true);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(LOG_TAG, String.format("Activity Result, requestCode %d, responseCode %d",
                requestCode, resultCode));

        if (requestCode == RC_SIGN_IN)
        {
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
            Log.v(LOG_TAG, "Received Activity Result, passed to callback manager");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    class GoogleAsyncTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {

            if(mGoogleApiClient.isConnected()) {
                String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
                Log.v(LOG_TAG, "Account name = " + accountName);

                Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                //String scopes = "audience:server:client_id:" + SERVER_CLIENT_ID; // Not the app's client ID. Enable this with the CLIENT_ID generated for the server (WEB) */
                String scopes = "oauth2:profile";
                try {
                    String token = null;
                    if(!isCancelled()) {
                        token = GoogleAuthUtil.getToken(getActivity().getApplicationContext(), account, scopes);
                    }
                    Log.v(LOG_TAG, "Obtained google token = " + token);
                    return token;
                } catch (GoogleAuthException e) {
                    Log.e(LOG_TAG, "Error retrieving ID token.", e);
                    return null;
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error retrieving ID token.", e);
                    return null;
                }
            }
            else
            {
                Log.e(LOG_TAG, "Some race condition, GoogleAPIClient is not connected");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String token) {
            if(!isCancelled()) {
                if (token != null) {
                    Log.i(LOG_TAG, "Google auth token retrieved successfully, token = " + token);
                    if (mGoogleApiClient.isConnected() && Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null)
                    {
                        String username = null, fullName = null, emailID = null, profileUri = null;
                        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                        emailID = Plus.AccountApi.getAccountName(mGoogleApiClient);

                        if(currentPerson.hasName()){
                            fullName = currentPerson.getDisplayName();
                        }
                        else{
                            fullName = emailID.split("@")[0];
                        }
                        if (currentPerson.hasImage()){
                            if(currentPerson.getImage().hasUrl())
                            {
                                profileUri = currentPerson.getImage().getUrl();
                            }
                        }
                        username = emailID.split("@")[0];

                        if(profileUri != null)
                        {
                            int pos = profileUri.indexOf("?sz=");
                            if( pos != -1)
                            {
                                profileUri = profileUri.substring(0,pos).concat("?sz=200");
                            }
                            else{
                                profileUri = profileUri.concat("?sz=200");
                            }
                        }
                        Log.v(LOG_TAG, String.format("User details : name %s, email %s, username %s, pic_url %s",
                                fullName, emailID, username, profileUri));

                        HttpAgent.tokenValue = token;
                        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
                        SharedPreferences.Editor prefEdit = sharedPref.edit();
                        prefEdit.putString(getString(R.string.login_method), getString(R.string.google_login));
                        prefEdit.putString(getString(R.string.access_token), HttpAgent.tokenValue);
                        prefEdit.putString(getString(R.string.key_username), username);
                        prefEdit.putString(getString(R.string.key_fullname), fullName);
                        prefEdit.putString(getString(R.string.key_email), emailID);
                        prefEdit.putString(getString(R.string.key_profile_image_uri), profileUri);
                        prefEdit.apply();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new SelectTagsFragment()).commit();
                    } else {
                        Log.e(LOG_TAG, "Google Signed-in but could not retrieve user's details");
                    }
                } else {
                    Log.e(LOG_TAG, "Error retrieving Google auth token, token is null");
                }
            }
            else{
                Log.e(LOG_TAG, "Skipping post execute, thread cancelled");
            }

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
