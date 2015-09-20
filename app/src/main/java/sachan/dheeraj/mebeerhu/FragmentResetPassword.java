package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import sachan.dheeraj.mebeerhu.model.AccessTokenCredentials;


public class FragmentResetPassword extends Fragment {

    private static final String LOG_TAG = FragmentResetPassword.class.getSimpleName();

    private Button resetPassword;
    private EditText emailIdEditText;
    private TextView helpText;
    private TextView loginView;
    private int defaultTextColor;

    private String emailId;
    private TextView err_email;

    public FragmentResetPassword() {
        // Required empty public constructor
    }

    public boolean validateEmail(String text)
    {
        if (text==null || text.length()==0){
            err_email.setText("Please enter email address");
            err_email.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            err_email.setText("Please enter a valid email address");
            err_email.setVisibility(View.VISIBLE);
            return android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        resetPassword = (Button) view.findViewById(R.id.reset_password_button);
        emailIdEditText = (EditText) view.findViewById(R.id.email_id);
        loginView = (TextView) view.findViewById(R.id.login_button);
        helpText = (TextView) view.findViewById(R.id.help_message);
        defaultTextColor = helpText.getTextColors().getDefaultColor();

        err_email = (TextView) view.findViewById(R.id.error_message_email);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailId = emailIdEditText.getText().toString();
                helpText.setText(getString(R.string.reset_password_default_text));
                helpText.setTextColor(defaultTextColor);

                if (!validateEmail(emailId)) {
                    return;
                }
                else {
                    err_email.setText("");
                    err_email.setVisibility(View.GONE);
                }

                resetPassword.setEnabled(false);
                final HashMap<String, String> stringStringHashMap = new HashMap<String, String>();
                stringStringHashMap.put("emailId", emailId);

                new AsyncTask<Void, Void, Boolean>() {

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        Log.d(LOG_TAG, "Posted reset password information to server");
                        String reply = HttpAgent.postGenericData(UrlConstants.RESET_PASSWORD_URL, JsonHandler.stringifyNormal(stringStringHashMap), getActivity());
                        Log.v(LOG_TAG, "Reply received from server" + reply);
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if (aBoolean) {
                            Log.d(LOG_TAG, "Password reset success reply received from server");
                            resetPassword.setEnabled(false);
                            helpText.setTextColor(getResources().getColor(R.color.green));
                            helpText.setText("An email has been sent to your registered email address with the instructions to reset your password");
                        } else {
                            Log.d(LOG_TAG, "Password Reset unsuccessful");
                            helpText.setTextColor(getResources().getColor(R.color.red));
                            helpText.setText("Sorry, the email address entered by you is not registered with us");
                            resetPassword.setEnabled(true);
                        }
                    }
                }.execute();
            }
        });

        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Login button clicked, switching to SplatterLogin fragment");

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new SplatterLoginFragment()).commit();
            }
        });

        return view;
    }



}
