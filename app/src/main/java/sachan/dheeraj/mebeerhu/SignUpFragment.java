package sachan.dheeraj.mebeerhu;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;

import java.util.HashMap;

import sachan.dheeraj.mebeerhu.model.SignUpReply;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private final static String LOG_TAG = SignUpFragment.class.getSimpleName();

    private Button signUpButton;
    private EditText fullNameEditText,userNameEditText,emailEditText,passwordEditText;
    private String username, fullname, emailId;

    private LoginButton loginButtonFaceBook;
    private com.google.android.gms.common.SignInButton googleButton;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "OnCreateView for SignUp Fragment" );

        View view = inflater.inflate(R.layout.fragment_signup,container,false);
        signUpButton = (Button) view.findViewById(R.id.signup);

        fullNameEditText = (EditText) view.findViewById(R.id.name);
        userNameEditText = (EditText) view.findViewById(R.id.username);
        emailEditText = (EditText) view.findViewById(R.id.email);
        passwordEditText = (EditText) view.findViewById(R.id.password);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "SignUp button clicked, attempting to Sign-Up");
                final HashMap<String, String> stringStringHashMap = new HashMap<String, String>();
                username = userNameEditText.getText().toString();
                fullname = fullNameEditText.getText().toString();
                emailId = emailEditText.getText().toString();
                stringStringHashMap.put("username", fullname);
                stringStringHashMap.put("name", username);
                stringStringHashMap.put("emailId", emailId);
                stringStringHashMap.put("password", passwordEditText.getText().toString());
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        Log.v(LOG_TAG, "Submitting Sign-Up info to server");

                        String reply = HttpAgent.postGenericData(UrlConstants.SIGN_UP_URL, JsonHandler.stringifyNormal(stringStringHashMap), getActivity());
                        SignUpReply signUpReply = JsonHandler.parseNormal(reply, SignUpReply.class);
                        if (signUpReply != null) {
                            Log.v(LOG_TAG, "Sign-up reply received, getting authentication token");
                            HttpAgent.tokenValue = signUpReply.getToken();
                            return true;
                        }
                        /* Temp */
                        HttpAgent.tokenValue = "abcdxyz";
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
                            prefEdit.putString(getString(R.string.key_username), username);
                            prefEdit.putString(getString(R.string.key_fullname), fullname);
                            prefEdit.putString(getString(R.string.key_email), emailId);
                            prefEdit.putString(getString(R.string.access_token), HttpAgent.tokenValue);
                            prefEdit.commit();

                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new SelectTagsFragment()).commit();
                        } else {
                            Log.d(LOG_TAG, "Sign-up failed");
                            Toast.makeText(getActivity(), "Something fucked up", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        return view;
    }

}
