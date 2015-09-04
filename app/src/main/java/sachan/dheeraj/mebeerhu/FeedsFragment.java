package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.facebook.login.LoginManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.plus.Plus;

import sachan.dheeraj.mebeerhu.model.Feeds;
import sachan.dheeraj.mebeerhu.model.Post;
import sachan.dheeraj.mebeerhu.viewHolders.PostViewHolder;

/**
 * Created by agarwalh on 9/3/2015.
 */

public class FeedsFragment extends Fragment {
    public static final int PLACE_PICKER_REQUEST = 100;

    private static final String LOG_TAG = FeedsFragment.class.getSimpleName();
    private ListView listView;
    private ProgressBar progressBar;

    public FeedsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_logout, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout_menu_item)
        {
            Log.v(LOG_TAG, "Logout menu item selected, clearing cached user credentials");

            SharedPreferences sharedPref = getActivity().getSharedPreferences(
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
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, String.format("onActivityResult, requestCode = %d, result = %d",
                requestCode, resultCode));
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                String dataa = JsonHandler.stringifyNormal(place);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView for FeedsFragment");
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        listView = (ListView) view.findViewById(R.id.list_view);
        progressBar = (ProgressBar) view.findViewById(R.id.loader);

        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CreatePostActivity.class);
                startActivity(intent);
              /*  try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                }catch (Exception e){
                    Log.e("", "");
                }*/
            }
        });

        new AsyncTask<Void, Void, Feeds>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Feeds feeds) {
                try {
                    ArrayAdapter<Post> postArrayAdapter = new ArrayAdapter<Post>(getActivity(), 0, feeds) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Post post = getItem(position);
                            PostViewHolder postViewHolder;
                            if (convertView == null) {
                                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_post, null);
                                postViewHolder = PostViewHolder.getInstance(convertView, getActivity());
                                convertView.setTag(postViewHolder);
                            } else {
                                postViewHolder = (PostViewHolder) convertView.getTag();
                            }
                            postViewHolder.getPosterNameTextView().setText(post.getParentUsername());
                            if (post.getAccompaniedWith() != null && post.getAccompaniedWith().size() > 0) {
                                postViewHolder.getWithTextView().setText("with");
                                postViewHolder.getxOthersTextView().setText(post.getAccompaniedWith().size() + " others");
                            } else {
                                postViewHolder.getWithTextView().setVisibility(View.GONE);
                                postViewHolder.getxOthersTextView().setVisibility(View.GONE);
                            }

                            postViewHolder.getLocationTextView().setText(post.getPostLocation());
                            postViewHolder.getLikesTextView().setText(post.getAggregatedVoteCount() + " likes");
                            postViewHolder.setPost(post);
                            postViewHolder.loadTagsInThreeLines(getActivity());
                            postViewHolder.initAndLoadImages(getContext());
                            return convertView;
                        }
                    };

                    listView.setAdapter(postArrayAdapter);
                    listView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }catch (Exception e){
                    Log.e(LOG_TAG,"While displaying Feeds, Exception: ",e);
                }

            }

            @Override
            protected Feeds doInBackground(Void... params) {
                if (true) return Feeds.feedsBuilder();
                String feedString = HttpAgent.get(UrlConstants.GET_FEED, getActivity());
                Feeds feeds = JsonHandler.parseNormal(feedString, Feeds.class);
                if (feeds == null) {
                    throw new RuntimeException("json parse failed");
                } else {
                    return feeds;
                }
            }
        }.execute();

        return view;
    }

}
