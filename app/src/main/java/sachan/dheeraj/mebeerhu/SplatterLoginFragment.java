package sachan.dheeraj.mebeerhu;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        userNameEditText = (EditText) view.findViewById(R.id.username);
        passwordEditText = (EditText) view.findViewById(R.id.password);

        loginInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Login button clicked, attempting to login");
                loginInButton.setEnabled(false);
                final HashMap<String, String> stringStringHashMap = new HashMap<String, String>();
                stringStringHashMap.put("username", userNameEditText.getText().toString());
                stringStringHashMap.put("password", passwordEditText.getText().toString());
                new AsyncTask<Void, Void, Boolean>() {

                    @Override
                    protected Boolean doInBackground(Void... params) {
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
                            Log.d(LOG_TAG, "Log-in successful, starting subsequent activities");
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new PostListFragment()).commit();
                        } else {
                            Log.d(LOG_TAG, "Log-in unsuccessful");
                            Toast.makeText(getActivity(), "Something fucked up", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        return view;
    }


}
