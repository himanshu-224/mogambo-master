package sachan.dheeraj.mebeerhu;


import android.content.Context;
import android.content.Intent;
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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.regex.Pattern;

import sachan.dheeraj.mebeerhu.model.SignUpReply;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(LOG_TAG, "Received Activity Result, passed to callback manager");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
