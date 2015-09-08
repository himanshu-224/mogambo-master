package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
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
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialog(String tagName, String tagDescription, boolean isFollowed)
    {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = TagDialogFragment.newInstance(tagName, tagDescription, isFollowed);
        newFragment.show(ft, "dialog");
    }

    public static class TagDialogFragment extends DialogFragment {

        final View.OnClickListener DIALOG_TAG_CLICK_LISTENER = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                TextView tv = (TextView)getDialog().findViewById(R.id.tag_title);
                String tagName = String.valueOf(tv.getText());
                int res = v.getId();
                Log.v(LOG_TAG, String.format("tagname %s, res_id %d", tagName, res) );
                switch(res)
                {
                    case R.id.search_tag_icon:
                    case R.id.search_tag:
                        Log.v(LOG_TAG, "Tag dialog: Clicked Search for Tag = " + tagName);
                        break;
                    case R.id.follow_tag_icon:
                    case R.id.follow_tag:
                        Log.v(LOG_TAG, "Tag dialog: Clicked Follow for Tag = " + tagName);
                        break;
                    default:
                        Log.v(LOG_TAG, "Tag dialog: No action needs to taken");

                }
            }
        };

        static TagDialogFragment newInstance(String tagName, String tagDescription, boolean isFollowed) {
            TagDialogFragment instance = new TagDialogFragment();
            Bundle args = new Bundle();
            args.putString("tagName", tagName);
            args.putString("tagDesc", tagDescription);
            args.putBoolean("isFollowed", isFollowed);
            instance.setArguments(args);
            return instance;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the layout inflater
            Bundle args = getArguments();
            LayoutInflater inflater = getActivity().getLayoutInflater();
            Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_window_tag);

            TextView title = (TextView)dialog.findViewById(R.id.tag_title);
            TextView description = (TextView)dialog.findViewById(R.id.tag_description);
            ImageView searchIcon = (ImageView)dialog.findViewById(R.id.search_tag_icon);
            TextView searchTV = (TextView)dialog.findViewById(R.id.search_tag);
            ImageView followIcon = (ImageView)dialog.findViewById(R.id.follow_tag_icon);
            TextView followTV = (TextView)dialog.findViewById(R.id.follow_tag);

            title.setText(args.getString("tagName"));
            description.setText(args.getString("tagDesc"));
            boolean isFollowed = args.getBoolean("isFollowed");
            if (isFollowed)
                followTV.setText("Unfollow");
            else
                followTV.setText("Follow");
            searchIcon.setOnClickListener(DIALOG_TAG_CLICK_LISTENER);
            searchTV.setOnClickListener(DIALOG_TAG_CLICK_LISTENER);
            followIcon.setOnClickListener(DIALOG_TAG_CLICK_LISTENER);
            followTV.setOnClickListener(DIALOG_TAG_CLICK_LISTENER);

            return dialog;
        }
    }

}
