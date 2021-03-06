package sachan.dheeraj.mebeerhu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import sachan.dheeraj.mebeerhu.globalData.CommonData;
import sachan.dheeraj.mebeerhu.localData.AppDbHelper;

/**
 * Created by agarwalh on 9/3/2015.
 */
public class FeedsActivity extends AppCompatActivity implements CreatePostDialogFragment.onCreatePostDialogListener
{
    private static final String LOG_TAG = FeedsActivity.class.getSimpleName();

    void deleteTheDatabase()
    {
        boolean success = deleteDatabase(AppDbHelper.DATABASE_NAME);
        Log.v(LOG_TAG, "Deleted the database to start afresh, success = " + success);
    }


    @Override
    public void onTakePicture() {
        Log.v(LOG_TAG, "onTakePicture callback called");
        Intent picIntent = new Intent(this, CreatePostActivity.class);
        picIntent.putExtra("action", CommonData.TAKE_PICTURE);
        picIntent.putExtra("originFeeds", true);
        startActivity(picIntent);
    }

    @Override
    public void onPickFromGallery() {
        Log.v(LOG_TAG, "onPickFromGallery callback called");
        Intent picIntent = new Intent(this, CreatePostActivity.class);
        picIntent.putExtra("action", CommonData.PICK_FROM_GALLERY);
        picIntent.putExtra("originFeeds", true);
        startActivity(picIntent);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate for FeedsActivity");
        setContentView(R.layout.activity_feeds);
        deleteTheDatabase();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.feeds_frame_layout, new FeedsFragment(), getString(R.string.fragment_feeds))
                    .commit();
        }
    }

     @Override
    public void onResume()
    {
        super.onResume();

        //Remove all pop-up dialogs when activity is resumed */
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("locationDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        prev = getSupportFragmentManager().findFragmentByTag("tagDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        prev = getSupportFragmentManager().findFragmentByTag("createPostDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feeds, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        if (id == R.id.logout_menu_item)
        {
            Log.v(LOG_TAG, "Logout menu item selected, clearing cached user credentials");
            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.preference_file), Context.MODE_PRIVATE);

            /* If we are logged-in through facebook/google, logout from facebook/google
             * along with clearing locally stored access credentials */
            if( (getString(R.string.facebook_login)).
                    equals(sharedPref.getString(getString(R.string.login_method),null )) )
            {
                Log.i(LOG_TAG, "Logging out from facebook");
                LoginManager.getInstance().logOut();
            }
            else if ( (getString(R.string.google_login)).
                    equals(sharedPref.getString(getString(R.string.login_method),null )) )
            {
                Log.i(LOG_TAG, "Logging out from google");
                GoogleApiClient mGoogleApiClient = GoogleHelper.getGoogleApiClient();
                if (mGoogleApiClient != null)
                {
                    if (mGoogleApiClient.isConnected()) {
                        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                        mGoogleApiClient.disconnect();
                        mGoogleApiClient.connect(); /* Why is this needed? */
                    }
                }
                else{
                    Log.e(LOG_TAG, "Can't logout from Google, googleApiClient instance null");
                }
            }
            SharedPreferences.Editor prefEdit = sharedPref.edit();
            prefEdit.clear();
            prefEdit.apply();

            Log.v(LOG_TAG, "Jumping to landing screen for login");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showTagDialog(String tagName, String tagDescription, boolean isFollowed)
    {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("tagDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = TagDialogFragment.newInstance(tagName, tagDescription, isFollowed);
        newFragment.show(ft, "tagDialog");
    }

    public void showLocationDialog(String locName, String locDescription)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("locationDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = LocationDialogFragment.newInstance(locName, locDescription);
        newFragment.show(ft, "locationDialog");
    }
    public void showCreatePostDialog()
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("createPostDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);

        // Create and show the dialog.
        CreatePostDialogFragment newFragment = CreatePostDialogFragment.newInstance();
        newFragment.show(ft, "createPostDialog");
    }
}
