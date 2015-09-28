package sachan.dheeraj.mebeerhu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.login.LoginManager;

import java.math.BigInteger;
import java.util.ArrayList;

import sachan.dheeraj.mebeerhu.LoginActivity;
import sachan.dheeraj.mebeerhu.R;
import sachan.dheeraj.mebeerhu.model.User;

/**
 * Created by agarwalh on 9/19/2015.
 */
public class Utils {

    public static void deleteCredentials(String LOG_TAG, Activity activity)
    {
        Log.d(LOG_TAG, "Deleting credentials");
        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
        }

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEdit = sharedPref.edit();
        prefEdit.clear();
        prefEdit.apply();

        /* Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish(); */
    }

    public static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
    }

    public static String getKeyFromDrawable(int resId)
    {
        String key = "app_icon_";
        key += String.valueOf(resId);
        return key;
    }

    public static ArrayList<User> generateUsers()
    {
        ArrayList<User> users =  new ArrayList<User>();

        for(int i=0; i<10; i++)
        {
            User user = new User();
            switch( i%4)
            {
                case 0:
                    user.setName("Himanshu Agarwal");
                    break;
                case 1:
                    user.setName("Naveen Goel");
                    break;
                case 2:
                    user.setName("Random Guy");
                    break;
                case 3:
                    user.setName("Jane Doe");
                    break;
                default:
                    user.setName("Unexpected");
            }
            user.setProfileImageURL("http://i.ebayimg.com/00/s/NTAwWDUwMA==/z/4KEAAOSwv0tVFNSo/$_35.JPG");
            users.add(user);
        }

        return users;
    }
}
