package sachan.dheeraj.mebeerhu;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class LocationActivity extends AppCompatActivity {

    private static final String LOG_TAG = LocationActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG_TAG, "onCreate for Location Activity");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_location);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.location_activity_layout, new LocationFragment(), getString(R.string.fragment_location))
                    .addToBackStack(getString(R.string.fragment_location))
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.next_button)
        {
            LocationFragment thisFragment = (LocationFragment)getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_location));

            /*if (thisFragment.placeId == null || thisFragment.placeDetails == null)
            {
                Toast.makeText(this, "Please select location to continue", Toast.LENGTH_SHORT).show();
                return true;
            }*/

            Log.v(LOG_TAG, String.format("Going to tags activity, placeId = %s, details = %s",
                    thisFragment.placeId, thisFragment.placeDetails));
            Intent thisIntent = new Intent(this, TagsAddActivity.class);
            thisIntent.putExtra("locationId", thisFragment.placeId);
            thisIntent.putExtra("locationDetails", thisFragment.placeDetails);
            startActivity(thisIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
