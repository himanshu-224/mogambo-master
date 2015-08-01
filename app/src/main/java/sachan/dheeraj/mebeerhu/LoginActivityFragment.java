package sachan.dheeraj.mebeerhu;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginFragment;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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
    private static final String TAG = LoginActivityFragment.class.getSimpleName();

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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new SignUpFragment()).commit();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new SplatterLoginFragment()).commit();
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),GoogleActivity.class);
                getActivity().startActivity(intent);
            }
        });

        loginButtonFaceBook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "success");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new SelectTagsFragment()).commit();


            }

            @Override
            public void onCancel() {
                Log.e(TAG, "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Exception exception1 = exception;
                Log.e(TAG, "caught exception", exception);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
