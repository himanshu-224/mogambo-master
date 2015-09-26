package sachan.dheeraj.mebeerhu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class TagsAddActivity extends AppCompatActivity {

    private static final String LOG_TAG = TagsAddActivity.class.getSimpleName();
    private String locationId;
    private String locationDetails;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tags);

        Intent thisIntent = getIntent();
        locationId = thisIntent.getStringExtra("locationId");
        locationDetails = thisIntent.getStringExtra("locationDetails");

        Log.v(LOG_TAG, "onCreate for Tags Add Activity");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_add_tags);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tags_add_activity_layout, new TagFillerFragment(), getString(R.string.fragment_tag_filler))
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_tags, menu);
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
        if (id == R.id.done_button)
        {
            TagFillerFragment thisFragment = (TagFillerFragment)getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_tag_filler));
            if (thisFragment.getTags().size() == 0 )
            {
                Toast.makeText(this, "Please add tags to finish Post", Toast.LENGTH_SHORT).show();
                return true;
            }
            Log.v(LOG_TAG, "Done, getting user data for Post, saving tags to db");
            thisFragment.saveTagsInDB();
            /* Launch a service here to save the data to server */
            Intent thisIntent = new Intent(this, ShowPreviewActivity.class);
            startActivity(thisIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return true;
        }
        else if(id == android.R.id.home)
        {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
