package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import sachan.dheeraj.mebeerhu.model.Feeds;
import sachan.dheeraj.mebeerhu.model.Post;
import sachan.dheeraj.mebeerhu.viewHolders.PostViewHolder;

/**
 * Created by agarwalh on 9/3/2015.
 */
public class FeedsActivity extends ActionBarActivity {
    public static final int PLACE_PICKER_REQUEST = 100;

    private static final String LOG_TAG = FeedsActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate for FeedsActivity");
        setContentView(R.layout.activity_feeds);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.feeds_frame_layout, new FeedsFragment()).addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feeds, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout_menu_item)
        {
            Log.v(LOG_TAG, "Logout menu item selected, clearing cached user credentials");
            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.preference_file), Context.MODE_PRIVATE);
            SharedPreferences.Editor prefEdit = sharedPref.edit();
            prefEdit.clear();
            prefEdit.commit();

            Log.v(LOG_TAG, "Jumping to landing screen for login");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
