package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import sachan.dheeraj.mebeerhu.globalData.CommonData;
import sachan.dheeraj.mebeerhu.localData.AppContract;
import sachan.dheeraj.mebeerhu.localData.AppDbHelper;
import sachan.dheeraj.mebeerhu.model.Feeds;
import sachan.dheeraj.mebeerhu.model.Post;
import sachan.dheeraj.mebeerhu.model.Tag;
import sachan.dheeraj.mebeerhu.model.User;
import sachan.dheeraj.mebeerhu.viewHolders.PostViewHolder;

/**
 * A placeholder fragment containing a simple view.
 */
public class TagSearchFragment extends Fragment {

    public TagSearchFragment() {
    }

    private static final String LOG_TAG = TagSearchFragment.class.getSimpleName();
    private ListView listView;
    private ProgressBar progressBar;

    private String SearchTag;

    private AppDbHelper mDbHelper;

    private View.OnLongClickListener LONG_CLICK_LISTENER = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            TextView tView = (TextView) view;
            Log.v(LOG_TAG, "Long Pressed Tag with value = " + tView.getText());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            FeedsActivity mActivity =  (FeedsActivity)getActivity();
            String tagName = String.valueOf(tView.getText());
            Tag tag = CommonData.tags.get(tagName);
            String description="";
            boolean isFollowed = false;
            if ( tag != null)
            {
                description = tag.getTagMeaning();
            }
            tag = CommonData.followedTags.get(tagName);
            if (tag != null )
                isFollowed = true;
            mActivity.showDialog(tagName, description, isFollowed);

            return true;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView for TagSearchFragment");

        Bundle args = getArguments();
        SearchTag = args.getString("tagName");

        Log.v(LOG_TAG, "Tag to Search: " + SearchTag);

        View view = inflater.inflate(R.layout.fragment_tag_search, container, false);
        listView = (ListView) view.findViewById(R.id.tag_list_view);
        progressBar = (ProgressBar) view.findViewById(R.id.loader);

        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        new AsyncTask<Void, Void, ArrayList<Post>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(ArrayList<Post> posts) {
                try {
                    Log.v(LOG_TAG, "PostExecute in task for loading feeds");

                    /* Cache the tag list for quick reference regarding tag descriptions */
                    if (posts != null)
                    {
                        for (Post post:posts)
                        {
                            ArrayList<Tag> tagList = post.getTagList();
                            if (tagList != null)
                            {
                                for(Tag tag: tagList)
                                {
                                    CommonData.tags.put(tag.getTagName(),tag);
                                }
                            }

                        }
                    }
                    Log.v(LOG_TAG, "No of unique tags cached = " + CommonData.tags.size());

                    ArrayAdapter<Post> postArrayAdapter = new ArrayAdapter<Post>(getActivity(), 0, posts) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Log.v(LOG_TAG, "Trying to bind view for array adapter, pos = " + position);
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
                            postViewHolder.loadTagsInThreeLines(getActivity(), LONG_CLICK_LISTENER);
                            //postViewHolder.initAndLoadImages(getContext());
                            return convertView;
                        }
                    };

                    listView.setAdapter(postArrayAdapter);
                    listView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);


                    Log.v(LOG_TAG, "Done Setting arrayadapter ");

                }catch (Exception e){
                    Log.e(LOG_TAG,"While displaying Feeds, Exception: ",e);
                }
            }

            @Override
            protected ArrayList<Post> doInBackground(Void... params)
            {
                Feeds feeds;
                /*
                String feedString = HttpAgent.get(UrlConstants.GET_FEED, getActivity());
                feeds = JsonHandler.parseNormal(feedString, Feeds.class);
                if (feeds == null) {
                    throw new RuntimeException("json parse failed");
                }
                */
                feeds = Feeds.feedsBuilder();
                /* Put the feeds in the database */
                return feeds;
            }
        }.execute();

        Log.v(LOG_TAG, "exiting oncreateView for tagSearch ");
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy for tagSearch ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "onPause for tagSearch ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume for tagSearch ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(LOG_TAG, "onStop for tagSearch ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "onStart for tagSearch ");
    }
}
