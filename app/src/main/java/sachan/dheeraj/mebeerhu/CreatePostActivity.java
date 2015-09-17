package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import sachan.dheeraj.mebeerhu.globalData.CommonData;
import sachan.dheeraj.mebeerhu.localData.AppContract;
import sachan.dheeraj.mebeerhu.localData.AppDbHelper;
import sachan.dheeraj.mebeerhu.model.Tag;
import java.util.HashSet;

public class CreatePostActivity extends AppCompatActivity{

    private int picMethod = 0;

    private AppDbHelper mDbHelper;
    private boolean origin_feeds = false;

    private final String LOG_TAG = CreatePostActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate for CreatePost Activity");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_create_post);

        picMethod = getIntent().getIntExtra("action", CommonData.TAKE_PICTURE);
        origin_feeds =  getIntent().getBooleanExtra("originFeeds", false);
        if(origin_feeds)
            Log.v(LOG_TAG, "Initiated from feeds, destroy on failure");

        Log.v(LOG_TAG, "For generating picture, got action = " + picMethod);

        /* Clear the location data which might be stored in shared
         * prefs for some previous post */
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEdit = sharedPref.edit();
        prefEdit.remove(getString(R.string.post_location_id));
        prefEdit.remove(getString(R.string.post_location_description));
        prefEdit.remove(getString(R.string.post_image_path));
        prefEdit.apply();

        /* Delete the table having any stored tags for previous post */
        mDbHelper =  new AppDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try
        {
            db.delete(AppContract.SinglePostTagEntry.TABLE_NAME, null, null);
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.create_post_activity_layout, new CreatePostFragment(), getString(R.string.fragment_create_post))
                    .commit();
        }
    }

    public boolean get_origin_feeds()
    {
        return origin_feeds;
    }

    public void set_origin_feeds(boolean value)
    {
        origin_feeds =  value;
    }

    public int get_picMethod()
    {
        return picMethod;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_post_activity, menu);
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
            startActivity(new Intent(this, LocationActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
