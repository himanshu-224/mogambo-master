package sachan.dheeraj.mebeerhu;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import sachan.dheeraj.mebeerhu.model.SignUpReply;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private Button signUpButton;
    private EditText fullNameEditText,userNameEditText,emailEditText,passwordEditText;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup,container,false);
        signUpButton = (Button) view.findViewById(R.id.signup);

        fullNameEditText = (EditText) view.findViewById(R.id.name);
        userNameEditText = (EditText) view.findViewById(R.id.username);
        emailEditText = (EditText) view.findViewById(R.id.email);
        passwordEditText = (EditText) view.findViewById(R.id.password);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<String,String> stringStringHashMap = new HashMap<String, String>();
                stringStringHashMap.put("username",fullNameEditText.getText().toString());
                stringStringHashMap.put("name",userNameEditText.getText().toString());
                stringStringHashMap.put("emailId",userNameEditText.getText().toString());
                stringStringHashMap.put("password",passwordEditText.getText().toString());
                new AsyncTask<Void,Void,Boolean>(){
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        String reply = HttpAgent.postGenericData(UrlConstants.SIGNUP_URL, JsonHandler.stringifyNormal(stringStringHashMap), getActivity());
                        SignUpReply signUpReply = JsonHandler.parseNormal(reply,SignUpReply.class);
                        if(signUpReply != null){
                            HttpAgent.tokenValue = signUpReply.getToken();
                            return true;
                        }
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if(aBoolean){
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new SelectTagsFragment()).commit();
                        }else{
                            Toast.makeText(getActivity(),"Something fucked up",Toast.LENGTH_SHORT).show();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        return view;
    }


}
