package sachan.dheeraj.mebeerhu;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import java.util.HashMap;

import sachan.dheeraj.mebeerhu.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class AutoLoginFragment extends Fragment {

    private static final String LOG_TAG = AutoLoginFragment.class.getSimpleName();
    private TextView errorMessage;
    private Button retry, relogin;
    private ProgressBar loader;

    private ServerLoginTask mServerLoginTask;

    private String login_method, access_token, fullName, userName, emailID, profileUri;

    public AutoLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file), Context.MODE_PRIVATE);
        access_token = sharedPref.getString(getString(R.string.access_token), null);
        login_method = sharedPref.getString(getString(R.string.login_method), null);
        fullName = sharedPref.getString(getString(R.string.key_fullname), null);
        userName = sharedPref.getString(getString(R.string.key_username), null);
        emailID = sharedPref.getString(getString(R.string.key_email), null);
        profileUri = sharedPref.getString(getString(R.string.key_profile_image_uri), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView for AutoLoginFragment");
        View view = inflater.inflate(R.layout.fragment_landing_auto_login, container, false);
        errorMessage = (TextView) view.findViewById(R.id.error_message);
        retry = (Button) view.findViewById(R.id.retry_button);
        relogin = (Button) view.findViewById(R.id.relogin_button);
        loader = (ProgressBar) view.findViewById(R.id.loader);

        mServerLoginTask = new ServerLoginTask();
        mServerLoginTask.execute();

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServerLoginTask.cancel(false);
                mServerLoginTask = new ServerLoginTask();
                mServerLoginTask.execute();
            }
        });

        relogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAgent.tokenValue = null;
                Log.i(LOG_TAG, "User selected re-login, going to landing page");
                SharedPreferences sharedPref = getActivity().getSharedPreferences(
                        getString(R.string.preference_file), Context.MODE_PRIVATE);
                SharedPreferences.Editor prefEdit = sharedPref.edit();
                prefEdit.clear();
                prefEdit.apply();
                if(getString(R.string.facebook_login).equals(login_method))
                {
                    LoginManager mLoginManager = LoginManager.getInstance();
                    if (mLoginManager != null)
                        mLoginManager.logOut();
                }
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new LoginActivityFragment()).commit();
            }
        });
        return view;
    }

    private HashMap<String,String> getHashMapData()
    {
        HashMap<String, String> stringStringHashMap = new HashMap<String, String>();

        stringStringHashMap.put("loginMethod", login_method);
        stringStringHashMap.put("accessToken", access_token);

        stringStringHashMap.put("username", userName);
        stringStringHashMap.put("fullname", fullName);
        stringStringHashMap.put("emailID", emailID);
        stringStringHashMap.put("profileUri", profileUri);

        return stringStringHashMap;
    }

    class ServerLoginTask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            errorMessage.setVisibility(View.GONE);
            retry.setVisibility(View.GONE);
            relogin.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params)
        {
            Log.d(LOG_TAG, "Posting login information to server for auto-login");
            String reply = null;
            if(getString(R.string.app_login).equals(login_method))
            {
                reply = HttpAgent.postGenericData(UrlConstants.AUTHENTICATE_URL, "", getActivity());
            }
            else if (getString(R.string.facebook_login).equals(login_method))
            {
                Log.v(LOG_TAG, "Sending JSON data for FB login: " + JsonHandler.stringifyNormal(getHashMapData()));
                reply = HttpAgent.postGenericData(UrlConstants.LOGIN_FACEBOOK, JsonHandler.stringifyNormal(getHashMapData()), getActivity());
            }
            else if (getString(R.string.google_login).equals(login_method))
            {
                Log.v(LOG_TAG, "Sending JSON data for Google login: " + JsonHandler.stringifyNormal(getHashMapData()));
                reply = HttpAgent.postGenericData(UrlConstants.LOGIN_GOOGLE, JsonHandler.stringifyNormal(getHashMapData()), getActivity());
            }
            else
            {
                Log.e(LOG_TAG, "Unidentified login method: " + login_method);
            }
            Log.v(LOG_TAG, "Reply from server for auto-login: " + reply);
            return reply;
        }

        @Override
        protected void onPostExecute(String reply)
        {
            loader.setVisibility(View.INVISIBLE);
            boolean failure = true;
            reply  = "allok"; /* Temp */

            if (reply == null) {
                errorMessage.setText("No Response from Server. Please retry");
            }
            else if (AppConstants.NO_INTERNET_ACCESS.equals(reply))
            {
                errorMessage.setText("No Internet access. Please check Internet settings.");
            }
            else if(AppConstants.CONNECTION_TIME_OUT.equals(reply))
            {
                errorMessage.setText("Could not connect to Server. Please retry");
            }
            else if(AppConstants.UNAUTHENTICATED.equals(reply))
            {
                Utils.deleteCredentials(LOG_TAG, getActivity());
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_layout, new LoginActivityFragment()).commit();
                return;
            }
            else if(AppConstants.API_RESOURCE_ERROR.equals(reply))
            {
                errorMessage.setText("Something went wrong.. Please Retry");
            }
            else {
                failure = false;
                Log.d(LOG_TAG, "Log-in successful, starting Feeds Activity");
                Intent intent = new Intent(getActivity(), FeedsActivity.class);
                startActivity(intent);
            }
            if(failure)
            {
                errorMessage.setVisibility(View.VISIBLE);
                retry.setVisibility(View.VISIBLE);
                relogin.setVisibility(View.VISIBLE);
            }
        }
    }
}
