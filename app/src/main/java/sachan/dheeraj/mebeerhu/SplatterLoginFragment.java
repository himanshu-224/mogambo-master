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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.HashMap;

import sachan.dheeraj.mebeerhu.model.AccessTokenCredentials;
import sachan.dheeraj.mebeerhu.model.SignUpReply;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class SplatterLoginFragment extends Fragment {
    private EditText userNameEditText, passwordEditText;
    private Button loginInButton;

    private LoginButton loginButtonFaceBook;
    private com.google.android.gms.common.SignInButton googleButton;
    private CallbackManager callbackManager;
    private TextView forgotPassword;

    private String username;

    private static final String LOG_TAG = SplatterLoginFragment.class.getSimpleName();

    public SplatterLoginFragment() {
        // Required empty public constructor
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
                loginInButton.setEnabled(false);
                final HashMap<String, String> stringStringHashMap = new HashMap<String, String>();
                username = userNameEditText.getText().toString();
                stringStringHashMap.put("username", username);
                stringStringHashMap.put("password", passwordEditText.getText().toString());

                new AsyncTask<Void, Void, Boolean>() {

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        /* Temp */ HttpAgent.tokenValue = "abcdxyz";
                        if (true) return true;
                        Log.d(LOG_TAG, "Posted login information to server");
                        String reply = HttpAgent.postGenericData(UrlConstants.LOGIN_URL, JsonHandler.stringifyNormal(stringStringHashMap), getActivity());
                        AccessTokenCredentials accessTokenCredentials = JsonHandler.parseNormal(reply, AccessTokenCredentials.class);
                        if (accessTokenCredentials != null) {
                            Log.v(LOG_TAG, "Sign-in reply received, getting authentication token");
                            HttpAgent.tokenValue = accessTokenCredentials.getToken();
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
                            Toast.makeText(getActivity(), "Something fucked up", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Sign-in with Google clicked, switching to Google activity");
                Intent intent = new Intent(getActivity(),GoogleActivity.class);
                getActivity().startActivity(intent);
            }
        });

        loginButtonFaceBook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(LOG_TAG, "Sign-in with Facebook successful");
                String username = loginResult.getAccessToken().getUserId();
                HttpAgent.tokenValue = loginResult.getAccessToken().toString();

                Log.v(LOG_TAG, String.format("Username %s, AccessToken %s",
                        username, HttpAgent.tokenValue));
                SharedPreferences sharedPref = getActivity().getSharedPreferences(
                        getString(R.string.preference_file), Context.MODE_PRIVATE);
                SharedPreferences.Editor prefEdit = sharedPref.edit();
                prefEdit.putString(getString(R.string.login_method), getString(R.string.facebook_login));
                prefEdit.putString(getString(R.string.key_username), username);
                prefEdit.putString(getString(R.string.access_token), HttpAgent.tokenValue);
                prefEdit.apply();

                String k = JsonHandler.stringifyNormal(loginResult);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new SelectTagsFragment()).commit();
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

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(LOG_TAG, "Received Activity Result, passed to callback manager");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
