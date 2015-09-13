package sachan.dheeraj.mebeerhu;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class TagsAddActivity extends ActionBarActivity {

    private static final String LOG_TAG = TagsAddActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tags);

        Log.v(LOG_TAG, "onCreate for Tags Add Activity");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_add_tags);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tags_add_activity_layout, new TagFillerFragment(), getString(R.string.fragment_tag_filler))
                    .addToBackStack(getString(R.string.fragment_tag_filler))
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
        else if(id == R.id.done_button){
            //To do : save this post to server and direct user back to the feeds.
            Log.v(LOG_TAG, "User clicked done for the post");
            //To do .. keep on popping activities till FeedsActivity is found */
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
