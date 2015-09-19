package sachan.dheeraj.mebeerhu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginFragment;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.w3c.dom.Text;
/*
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
*/

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends Fragment {
    //ga0RGNYHvNM5d0SLGQfpQWAPGJ8=
    private static final String LOG_TAG = LoginActivityFragment.class.getSimpleName();
    private CallbackManager callbackManager;
    private Button signButton;
    private Button loginButton;

    private LoginButton loginButtonFaceBook;
    private com.google.android.gms.common.SignInButton googleButton;

    public LoginActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        Log.v(LOG_TAG, "OnCreateView for LoginActivity Fragment");
        View view = inflater.inflate(R.layout.fragment_landing, container, false);
        loginButtonFaceBook = (LoginButton) view.findViewById(R.id.login_button);
        signButton = (Button) view.findViewById(R.id.signup);

        loginButton = (Button) view.findViewById(R.id.login);
        loginButtonFaceBook.setReadPermissions("user_friends");
        googleButton = (com.google.android.gms.common.SignInButton) view.findViewById(R.id.google);
        // If using in a fragment
        loginButtonFaceBook.setFragment(this);
        // Other app specific specialization
        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Signup button clicked, switching to SignUp fragment");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new SignUpFragment()).commit();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Login button clicked, switching to SplatterLogin fragment");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new SplatterLoginFragment()).commit();
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

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(LOG_TAG, "Received Activity Result, passed to callback manager");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
