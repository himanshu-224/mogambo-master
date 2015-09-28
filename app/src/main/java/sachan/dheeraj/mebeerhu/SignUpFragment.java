package sachan.dheeraj.mebeerhu;


import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

import sachan.dheeraj.mebeerhu.model.SignUpReply;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final static String LOG_TAG = SignUpFragment.class.getSimpleName();

    private static final String NAME_PATTERN = "^[\\p{L} .'-]+$";
    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,25}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
    /*
     ^                # start-of-string
    (?=.*[0-9])       # a digit must occur at least once
    (?=.*[a-z])       # a lower case letter must occur at least once
    (?=.*[A-Z])       # an upper case letter must occur at least once
    (?=.*[@#$%^&+=])  # a special character must occur at least once
    (?=\S+$)          # no whitespace allowed in the entire string
    .{8,}             # anything, at least eight places though
    $                 # end-of-string */

    private Pattern userNamePattern;
    private Pattern namePattern;
    private Pattern pwdPattern;

    private Button signUpButton;

    private EditText fullNameEditText,userNameEditText,emailEditText,passwordEditText;
    private String username, fullname, emailId;

    private LoginButton loginButtonFaceBook;
    private com.google.android.gms.common.SignInButton googleButton;
    private CallbackManager callbackManager;
    private TextView loginView;

    private TextView err_name, err_username, err_email, err_pwd;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 245;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mIntentInProgress;

    private GoogleAsyncTask mGoogleAsyncTask;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public boolean validateName(String text)
    {
        if (text==null || text.length()==0) {
            err_name.setText("Please enter your full name");
            err_name.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            err_name.setText("Please enter a valid name");
            err_name.setVisibility(View.VISIBLE);
            return namePattern.matcher(text).matches();
        }
    }
    public boolean validateUsername(String text)
    {
        if (text==null || text.length()==0) {
            err_username.setText("Please enter your desired username");
            err_username.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            err_username.setText("Username should be 3 to 25 characters with only a-z0-9_- characters");
            err_username.setVisibility(View.VISIBLE);
            return userNamePattern.matcher(text).matches();
        }
    }
    public boolean validateEmail(String text)
    {
        if (text==null || text.length()==0){
            err_email.setText("Please enter your email address");
            err_email.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            err_email.setText("Please enter a valid email address");
            err_email.setVisibility(View.VISIBLE);
            return android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches();
        }
    }
    public boolean validatePassword(String text)
    {
        if (text==null || text.length()==0) {
            err_pwd.setText("Please enter your desired password");
            err_pwd.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            err_pwd.setText("Password should be min 8 characters with at least 1 small letter, capital letter and digit");
            err_pwd.setVisibility(View.VISIBLE);
            return pwdPattern.matcher(text).matches();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userNamePattern = Pattern.compile(USERNAME_PATTERN);
        namePattern = Pattern.compile(NAME_PATTERN);
        pwdPattern = Pattern.compile(PASSWORD_PATTERN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "OnCreateView for SignUp Fragment" );

        View view = inflater.inflate(R.layout.fragment_signup,container,false);
        signUpButton = (Button) view.findViewById(R.id.signup);

        loginButtonFaceBook = (LoginButton) view.findViewById(R.id.fb_login_button);
        loginButtonFaceBook.setReadPermissions("user_friends");
        googleButton = (com.google.android.gms.common.SignInButton) view.findViewById(R.id.google);
        // If using in a fragment
        loginButtonFaceBook.setFragment(this);
        callbackManager = CallbackManager.Factory.create();

        loginView = (TextView) view.findViewById(R.id.login_button);

        fullNameEditText = (EditText) view.findViewById(R.id.name);
        userNameEditText = (EditText) view.findViewById(R.id.username);
        emailEditText = (EditText) view.findViewById(R.id.email);
        passwordEditText = (EditText) view.findViewById(R.id.password);

        err_name = (TextView) view.findViewById(R.id.error_message_name);
        err_username = (TextView) view.findViewById(R.id.error_message_username);
        err_email = (TextView) view.findViewById(R.id.error_message_email);
        err_pwd = (TextView) view.findViewById(R.id.error_message_pwd);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "SignUp button clicked, attempting to Sign-Up");
                final HashMap<String, String> stringStringHashMap = new HashMap<String, String>();
                username = userNameEditText.getText().toString();
                fullname = fullNameEditText.getText().toString();
                emailId = emailEditText.getText().toString();

                boolean all_fields_ok = true;

                if(!validateName(fullname)) {
                    all_fields_ok = false;
                }
                else {
                    err_name.setText("");
                    err_name.setVisibility(View.GONE);
                }
                if(!validateUsername(username)) {
                    all_fields_ok = false;
                }
                else {
                    err_username.setText("");
                    err_username.setVisibility(View.GONE);
                }

                if (!validateEmail(emailId)) {
                    all_fields_ok = false;
                }
                else {
                    err_email.setText("");
                    err_email.setVisibility(View.GONE);
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

                stringStringHashMap.put("username", username);
                stringStringHashMap.put("name", fullname);
                stringStringHashMap.put("emailId", emailId);
                stringStringHashMap.put("password", passwordEditText.getText().toString());
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        Log.v(LOG_TAG, "Submitting Sign-Up info to server");
                        String reply = HttpAgent.postGenericData(UrlConstants.SIGN_UP_URL, JsonHandler.stringifyNormal(stringStringHashMap), getActivity());
                        SignUpReply signUpReply = JsonHandler.parseNormal(reply, SignUpReply.class);
                        if (signUpReply != null) {
                            HttpAgent.tokenValue = signUpReply.getToken();
                            Log.v(LOG_TAG, "Sign-up auth token = " + HttpAgent.tokenValue);
                            return true;
                        }
                        /* Temp */
                        Log.v(LOG_TAG, "SignUp reply received null, signup failure");
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if (aBoolean) {
                            Log.d(LOG_TAG, "Sign-up successful, starting SelectTags Fragment");
                            SharedPreferences sharedPref = getActivity().getSharedPreferences(
                                    getString(R.string.preference_file), Context.MODE_PRIVATE);
                            SharedPreferences.Editor prefEdit = sharedPref.edit();
                            prefEdit.putString(getString(R.string.login_method), getString(R.string.app_login));
                            prefEdit.putString(getString(R.string.key_username), username);
                            prefEdit.putString(getString(R.string.key_fullname), fullname);
                            prefEdit.putString(getString(R.string.key_email), emailId);
                            prefEdit.putString(getString(R.string.access_token), HttpAgent.tokenValue);
                            prefEdit.apply();

                            View view = getActivity().getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }

                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new SelectTagsFragment()).commit();
                        } else {
                            Log.d(LOG_TAG, "Sign-up failed");
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
                            .addConnectionCallbacks(SignUpFragment.this)
                            .addOnConnectionFailedListener(SignUpFragment.this)
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

        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Login button clicked, switching to SplatterLogin fragment");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new SplatterLoginFragment()).commit();
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
