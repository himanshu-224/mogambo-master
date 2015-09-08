package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.Vector;

import sachan.dheeraj.mebeerhu.localData.AppContract;
import sachan.dheeraj.mebeerhu.localData.AppDbHelper;
import sachan.dheeraj.mebeerhu.model.Feeds;
import sachan.dheeraj.mebeerhu.model.Post;
import sachan.dheeraj.mebeerhu.model.Tag;
import sachan.dheeraj.mebeerhu.model.User;
import sachan.dheeraj.mebeerhu.viewHolders.PostViewHolder;

import sachan.dheeraj.mebeerhu.localData.AppContract.PostEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.UserEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.TagEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.PostTagEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.UserTagEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.PostAccompanyingUserEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.UserFollowingEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.UserFollowerEntry;

/**
 * Created by agarwalh on 9/3/2015.
 */

public class FeedsFragment extends Fragment{
    public static final int PLACE_PICKER_REQUEST = 100;

    private static final String LOG_TAG = FeedsFragment.class.getSimpleName();
    private ListView listView;
    private ProgressBar progressBar;

    private AppDbHelper mDbHelper;

    private View.OnLongClickListener LONG_CLICK_LISTENER = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            TextView tView = (TextView) view;
            Log.v(LOG_TAG, "Long Pressed Tag with value = " + tView.getText());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            FeedsActivity mActivity =  (FeedsActivity)getActivity();
            mActivity.showDialog(String.valueOf(tView.getText()), "This is some description", true);

            return true;
        }
    };

    void deleteTheDatabase()
    {
        boolean success = getActivity().deleteDatabase(mDbHelper.DATABASE_NAME);
        Log.v(LOG_TAG,"Deleted the database to start afresh, success = " + success);
    }

    public FeedsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        deleteTheDatabase();
        mDbHelper =  new AppDbHelper(getActivity());
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
                Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                startActivity(intent);
              /*  try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                }catch (Exception e){
                    Log.e("", "");
                }*/
            }
        });

        class DBLoaderTask extends AsyncTask<Void, Void, ArrayList<Post>>
        {
            private final String LOG_TAG = DBLoaderTask.class.getSimpleName();

            @Override
            protected ArrayList<Post> doInBackground(Void... params) {
                Cursor cursor;
                String selection, sortOrder;
                String projection[], selectionArgs[];
                int limit;
                ArrayList<Post> feeds;

                SQLiteDatabase db = mDbHelper.getReadableDatabase();

                SQLiteQueryBuilder mGenericQueryBuilder = new SQLiteQueryBuilder();

                Log.v(LOG_TAG, "Loading feeds in background");

                projection = new String[] {
                        PostEntry.COLUMN_POST_ID,
                        PostEntry.TABLE_NAME + "." + PostEntry.COLUMN_USERNAME,
                        PostEntry.COLUMN_PARENT_USERNAME,
                        PostEntry.COLUMN_POST_LOCATION,
                        PostEntry.COLUMN_POST_IMAGE_URL,
                        PostEntry.COLUMN_AGGREGATED_VOTE_COUNT,
                        UserEntry.COLUMN_NAME,
                        UserEntry.COLUMN_PROFILE_IMAGE_URL
                };

                int COL_POST_ID = 0;
                int COL_USERNAME = 1;
                int COL_PARENT_USERNAME = 2;
                int COL_POST_LOCATION = 3;
                int COL_POST_IMAGE_URL = 4;
                int COL_AGGREGATED_VOTE_COUNT = 5;
                int COL_NAME = 6;
                int COL_PROFILE_IMAGE_URL = 7;

                selection = "1";
                limit = 10;

                //This is an inner join which looks like
                //Post INNER JOIN User ON Post.username = User.username
                mGenericQueryBuilder.setTables(
                        PostEntry.TABLE_NAME + " INNER JOIN " +
                                UserEntry.TABLE_NAME +
                                " ON " + PostEntry.TABLE_NAME +
                                "." + PostEntry.COLUMN_USERNAME +
                                " = " + UserEntry.TABLE_NAME +
                                "." + UserEntry.COLUMN_USERNAME);


                cursor = mGenericQueryBuilder.query(db,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        null,
                        String.valueOf(limit));

                int numRecords = cursor.getCount();

                Log.v(LOG_TAG, "Fetched Post table records, num rows = " + numRecords);
                cursor.moveToFirst();
                feeds = new ArrayList<Post>();
                Post mPost;

                for (int i=0;i<numRecords;i++)
                {
                    /* Mandatory parameters. None of them should be NULL */
                    if(cursor.isNull(COL_POST_ID) ||
                            cursor.isNull(COL_USERNAME) ||
                            cursor.isNull(COL_PARENT_USERNAME) ||
                            cursor.isNull(COL_POST_LOCATION) ||
                            cursor.isNull(COL_POST_IMAGE_URL) ||
                            cursor.isNull(COL_NAME)||
                            cursor.isNull(COL_AGGREGATED_VOTE_COUNT)
                            )
                    {
                        Log.e(LOG_TAG, "Error retrieving post from db, mandatory params null");
                        Log.e(LOG_TAG, String.format("Is NULL : Post_id %b, username %b, parent_username %b", cursor.isNull(COL_POST_ID),
                                cursor.isNull(COL_USERNAME),cursor.isNull(COL_PARENT_USERNAME)));
                        Log.e(LOG_TAG, String.format("Is NULL : Post_location %b, post_image_url %b", cursor.isNull(COL_POST_LOCATION),
                                cursor.isNull(COL_POST_IMAGE_URL)));
                        Log.e(LOG_TAG, String.format("Is NULL : Post Creator Name %b, vote count %b", cursor.isNull(COL_NAME),
                                cursor.isNull(COL_AGGREGATED_VOTE_COUNT)));
                    }
                    else
                    {
                        mPost = new Post();

                        mPost.setPostId(cursor.getString(COL_POST_ID));
                        mPost.setUsername(cursor.getString(COL_USERNAME));
                        mPost.setParentUsername(cursor.getString(COL_PARENT_USERNAME));
                        mPost.setPostLocation(cursor.getString(COL_POST_LOCATION));
                        mPost.setPostImageURL(cursor.getString(COL_POST_IMAGE_URL));
                        mPost.setName(cursor.getString(COL_NAME));
                        mPost.setAggregatedVoteCount(cursor.getLong(COL_AGGREGATED_VOTE_COUNT));

                        /* Optional params and this can be null */
                        mPost.setUserImageURL(cursor.isNull(COL_PROFILE_IMAGE_URL) ? null : cursor.getString(COL_PROFILE_IMAGE_URL));

                        feeds.add(mPost);
                    }
                    cursor.moveToNext();
                }

                /* Fetch tag list for the posts */
                mGenericQueryBuilder.setTables(
                        PostTagEntry.TABLE_NAME + " INNER JOIN " +
                                TagEntry.TABLE_NAME +
                                " ON " + PostTagEntry.TABLE_NAME +
                                "." + PostTagEntry.COLUMN_TAG_NAME +
                                " = " + TagEntry.TABLE_NAME +
                                "." + TagEntry.COLUMN_TAG_NAME);

                projection = new String[]{
                        PostTagEntry.COLUMN_POST_ID,
                        TagEntry.TABLE_NAME + "." + TagEntry.COLUMN_TAG_NAME,
                        TagEntry.COLUMN_TAG_MEANING,
                        TagEntry.COLUMN_TYPE_ID,
                        TagEntry.COLUMN_APPROVED
                };

                COL_POST_ID = 0;
                int COL_TAG_NAME = 1;
                int COL_TAG_MEANING = 2;
                int COL_TYPE_ID = 3;
                int COL_APPROVED = 4;

                selection = PostTagEntry.TABLE_NAME + "." + PostTagEntry.COLUMN_POST_ID + " = ? ";

                Tag mTag;
                ArrayList<Tag> mTagList;
                for (int i = 0; i<feeds.size(); i++)
                {
                    selectionArgs = new String[]{feeds.get(i).getPostId()};
                    cursor = mGenericQueryBuilder.query(db,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null
                    );
                    int numTags = cursor.getCount();
                    mTagList = new ArrayList<Tag>();

                    Log.v(LOG_TAG, String.format("Fetched Tag records for Post Id %s, num rows = %d", selectionArgs[0], numTags));

                    cursor.moveToFirst();
                    for (int j=0; j<numTags; j++)
                    {
                        mTag = new Tag();
                        /* Mandatory parameters. None of them should be NULL */
                        if(cursor.isNull(COL_POST_ID) ||
                                cursor.isNull(COL_TAG_NAME) ||
                                cursor.isNull(COL_TAG_MEANING) ||
                                cursor.isNull(COL_TYPE_ID) ||
                                cursor.isNull(COL_APPROVED)
                                )
                        {
                            Log.e(LOG_TAG, "Error retrieving tag from db, mandatory params null");
                            Log.e(LOG_TAG, String.format("Is NULL : Post_id %b, tag_name %b, tag_meaning %b", cursor.isNull(COL_POST_ID),
                                    cursor.isNull(COL_TAG_NAME),cursor.isNull(COL_TAG_MEANING)));
                            Log.e(LOG_TAG, String.format("Is NULL : type_id %b, tag_aproved %b", cursor.isNull(COL_TYPE_ID),
                                    cursor.isNull(COL_APPROVED)));
                        }
                        else
                        {
                            mTag = new Tag(
                                    cursor.getString(COL_TAG_NAME),
                                    cursor.getString(COL_TAG_MEANING),
                                    cursor.getInt(COL_TYPE_ID),
                                    cursor.getInt(COL_APPROVED)!=0
                            );
                            mTagList.add(mTag);
                        }
                        cursor.moveToNext();
                    }
                    feeds.get(i).setTagList(mTagList);
                }

                /* Fetch the users accompanying the Post Provider */
                mGenericQueryBuilder.setTables(
                        PostAccompanyingUserEntry.TABLE_NAME + " INNER JOIN " +
                                UserEntry.TABLE_NAME +
                                " ON " + PostAccompanyingUserEntry.TABLE_NAME +
                                "." + PostAccompanyingUserEntry.COLUMN_ACCOMPANYING_USERNAME +
                                " = " + UserEntry.TABLE_NAME +
                                "." + UserEntry.COLUMN_USERNAME);

                projection = new String[]{
                        PostAccompanyingUserEntry.COLUMN_POST_ID,
                        UserEntry.TABLE_NAME + "." + UserEntry.COLUMN_USERNAME,
                        UserEntry.COLUMN_NAME
                };

                COL_POST_ID = 0;
                int COL_ACCOMPANYING_USERNAME = 1;
                COL_NAME = 2;

                selection = PostAccompanyingUserEntry.TABLE_NAME + "." + PostAccompanyingUserEntry.COLUMN_POST_ID + " = ? ";

                User mUser;
                ArrayList<User> mUserList;
                for (int i = 0; i<feeds.size(); i++)
                {
                    selectionArgs = new String[]{feeds.get(i).getPostId()};
                    cursor = mGenericQueryBuilder.query(db,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null
                    );
                    int numUsers = cursor.getCount();
                    mUserList = new ArrayList<User>();

                    Log.v(LOG_TAG, String.format("Fetched UserAccompanying records for Post Id %s, num rows = %d", selectionArgs[0], numUsers));

                    cursor.moveToFirst();
                    for (int j=0; j<numUsers; j++)
                    {
                        mUser = new User();
                        /* Mandatory parameters. None of them should be NULL */
                        if(cursor.isNull(COL_POST_ID) ||
                                cursor.isNull(COL_ACCOMPANYING_USERNAME) ||
                                cursor.isNull(COL_NAME)
                                )
                        {
                            Log.e(LOG_TAG, "Error retrieving Accompanying User from db, mandatory params null");
                            Log.e(LOG_TAG, String.format("Is NULL : Post_id %b, accompany_user_name %b, name %b", cursor.isNull(COL_POST_ID),
                                    cursor.isNull(COL_ACCOMPANYING_USERNAME),cursor.isNull(COL_NAME)));
                        }
                        else
                        {
                            mUser = new User();
                            mUser.setUsername(cursor.getString(COL_ACCOMPANYING_USERNAME));
                            mUser.setName(cursor.getString(COL_NAME));

                            mUserList.add(mUser);
                        }
                        cursor.moveToNext();
                    }
                    feeds.get(i).setAccompaniedWith(mUserList);
                }

                cursor.close();
                return feeds;
            }

            @Override
            protected void onPostExecute(ArrayList<Post> posts) {
                try {
                    Log.v(LOG_TAG, "PostExecute in task for loading feeds");
                    ArrayAdapter<Post> postArrayAdapter = new ArrayAdapter<Post>(getActivity(), 0, posts) {
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
                            postViewHolder.loadTagsInThreeLines(getActivity(), LONG_CLICK_LISTENER);
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
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Void params) {
                Log.v(LOG_TAG, "PostExecute in task for inserting feeds in db");
                DBLoaderTask dbTask = new DBLoaderTask();
                dbTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }

            @Override
            protected Void doInBackground(Void... params) {

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
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                Vector<ContentValues> cValues = new Vector<ContentValues>(feeds.size());
                ContentValues cValue;

                Log.v(LOG_TAG, "doInBackground in task for inserting feeds in db");

                int numFeeds = feeds.size();

                ArrayList<Post> validPosts = new ArrayList<Post>();

                for(Post thisPost:feeds)
                {
                    if (thisPost.getPostId()== null ||
                            thisPost.getUsername() == null ||
                            thisPost.getParentUsername() == null ||
                            thisPost.getPostLocation() == null ||
                            thisPost.getPostImageURL() == null ||
                            thisPost.getName() == null ||
                            thisPost.getAggregatedVoteCount() == null
                            )
                    {
                        Log.e(LOG_TAG, "Error building post, mandatory params null");
                        Log.e(LOG_TAG, String.format("Post fields : postId %s, username %s, parent_username %s",thisPost.getPostId(),
                                thisPost.getUsername(), thisPost.getParentUsername()));
                        Log.e(LOG_TAG, String.format("Post fields : post_location %s, post_image_url %s", thisPost.getPostLocation(),
                                thisPost.getPostImageURL() ));
                        Log.e(LOG_TAG, String.format("Post fields : name %s, vote count %d", thisPost.getName(),
                                 thisPost.getAggregatedVoteCount() ));
                    }
                    else
                    {
                        validPosts.add(thisPost);
                        cValue = new ContentValues();

                        /* Put mandatory params first */
                        cValue.put(PostEntry.COLUMN_POST_ID, thisPost.getPostId());
                        cValue.put(PostEntry.COLUMN_USERNAME, thisPost.getUsername());
                        cValue.put(PostEntry.COLUMN_PARENT_USERNAME, thisPost.getParentUsername());
                        cValue.put(PostEntry.COLUMN_POST_LOCATION, thisPost.getPostLocation());
                        cValue.put(PostEntry.COLUMN_POST_IMAGE_URL, thisPost.getPostImageURL());
                        cValue.put(PostEntry.COLUMN_AGGREGATED_VOTE_COUNT, thisPost.getAggregatedVoteCount());

                        /* Non-mandatory params */
                        if (thisPost.getTimestamp() != null)
                            cValue.put(PostEntry.COLUMN_TIMESTAMP, thisPost.getTimestamp());
                        if (thisPost.getPriceCurrency() != null)
                            cValue.put(PostEntry.COLUMN_PRICE_CURRENCY, thisPost.getPriceCurrency());
                        if (thisPost.getPostPrice() != null )
                            cValue.put(PostEntry.COLUMN_POST_PRICE, thisPost.getPostPrice());
                        if (thisPost.getLocationGeoCode() != null )
                            cValue.put(PostEntry.COLUMN_LOCATION_GEOCODE, thisPost.getLocationGeoCode());
                        if (thisPost.getRetweetCount() != null )
                            cValue.put(PostEntry.COLUMN_RETWEET_COUNT, thisPost.getRetweetCount());
                        if (thisPost.getCity() != null )
                            cValue.put(PostEntry.COLUMN_CITY, thisPost.getCity());
                        if (thisPost.getState() != null )
                            cValue.put(PostEntry.COLUMN_STATE, thisPost.getState());
                        if (thisPost.getCountry() != null )
                            cValue.put(PostEntry.COLUMN_COUNTRY, thisPost.getCountry());

                        cValues.add(cValue);
                    }
                }

                Log.v(LOG_TAG, "Inserting Post data, num rows = " + cValues.size());
                        db.beginTransaction();
                try
                {
                    for(ContentValues mValue:cValues)
                    {
                        db.replace(PostEntry.TABLE_NAME, null, mValue);
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                cValues.clear();

                Vector<ContentValues> cValuesTag = new Vector<ContentValues>();
                Vector<ContentValues> cValuesUser = new Vector<ContentValues>();

                Vector<ContentValues> cValuesPostTag = new Vector<ContentValues>();
                Vector<ContentValues> cValuesUserAccompany = new Vector<ContentValues>();

                for(Post thisPost:validPosts)
                {
                    ArrayList<Tag> tagList = thisPost.getTagList();

                    ContentValues cValueUser = new ContentValues();
                    /* We had already verified that Username and Name are not null */
                    cValueUser.put(UserEntry.COLUMN_USERNAME, thisPost.getUsername());
                    cValueUser.put(UserEntry.COLUMN_NAME, thisPost.getName());
                    if (thisPost.getUserImageURL() != null)
                        cValueUser.put(UserEntry.COLUMN_PROFILE_IMAGE_URL, thisPost.getUserImageURL());
                    cValuesUser.add(cValueUser);

                    if (tagList != null && tagList.size()>0)
                    {
                        for (Tag tag:tagList)
                        {
                            if (tag.getTagName() == null ||
                                    tag.getTagMeaning() == null ||
                                    tag.getTypeId() == null ||
                                    tag.isApproved() == null)
                            {
                                Log.e(LOG_TAG, "Error building tagList for post_id: " + thisPost.getPostId() +", mandatory params null");
                                Log.e(LOG_TAG, String.format("Tag fields : tagName %s, tagMeaning %s, typeId %d, approved %b",tag.getTagName(),
                                    tag.getTagMeaning(), tag.getTypeId(), tag.isApproved()));
                            }
                            else
                            {
                                cValue = new ContentValues();
                                cValue.put(TagEntry.COLUMN_TAG_NAME, tag.getTagName());
                                cValue.put(TagEntry.COLUMN_TAG_MEANING, tag.getTagMeaning());
                                cValue.put(TagEntry.COLUMN_TYPE_ID, tag.getTypeId());
                                cValue.put(TagEntry.COLUMN_APPROVED, tag.isApproved());
                                cValuesTag.add(cValue);

                                ContentValues cValuePostTag = new ContentValues();
                                cValuePostTag.put(PostTagEntry.COLUMN_POST_ID, thisPost.getPostId());
                                cValuePostTag.put(PostTagEntry.COLUMN_TAG_NAME, tag.getTagName());
                                cValuesPostTag.add(cValuePostTag);
                            }
                        }
                    }

                    ArrayList<User> userAccompanyList = thisPost.getAccompaniedWith();
                    if (userAccompanyList != null && userAccompanyList.size()>0)
                    {
                        for (User mUser:userAccompanyList)
                        {
                            if (mUser.getUsername() == null ||
                                    mUser.getName() == null)
                            {
                                Log.e(LOG_TAG, "Error building UserAccompanyList for post_id: " + thisPost.getPostId() + ", mandatory params null");
                                Log.e(LOG_TAG, String.format("Tag fields : userName %s, name %s",mUser.getUsername(),
                                        mUser.getName()));
                            }
                            else
                            {
                                cValue = new ContentValues();
                                cValue.put(UserEntry.COLUMN_USERNAME, mUser.getUsername() );
                                cValue.put(UserEntry.COLUMN_NAME, mUser.getName() );
                                cValuesUser.add(cValue);

                                ContentValues cValueUserAccompany = new ContentValues();
                                cValueUserAccompany.put(PostAccompanyingUserEntry.COLUMN_POST_ID, thisPost.getPostId());
                                cValueUserAccompany.put(PostAccompanyingUserEntry.COLUMN_ACCOMPANYING_USERNAME, mUser.getUsername());
                                cValuesUserAccompany.add(cValue);
                            }
                        }
                    }
                }

                Log.v(LOG_TAG, "Inserting User data, num rows = " + cValuesUser.size());
                /* Insert all the user records created */
                db.beginTransaction();
                try
                {
                    for(ContentValues mValue:cValuesUser)
                    {
                        db.replace(UserEntry.TABLE_NAME, null, mValue);
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }

                Log.v(LOG_TAG, "Inserting Tags data, num rows = " + cValuesTag.size());
                /* Insert all the tags created */
                        db.beginTransaction();
                try
                {
                    for(ContentValues mValue:cValuesTag)
                    {
                        db.replace(TagEntry.TABLE_NAME, null, mValue);
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }

                Log.v(LOG_TAG, "Inserting (Post,Tag) pair data, num rows = " + cValuesPostTag.size());
                /* Insert all the PostTags pairs created */
                db.beginTransaction();
                try
                {
                    for(ContentValues mValue:cValuesPostTag)
                    {
                        db.replace(PostTagEntry.TABLE_NAME, null, mValue);
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }

                Log.v(LOG_TAG, "Inserting (Post,AccompanyingUsers) pair data, num rows = " + cValuesUserAccompany.size());
                /* Insert all the Accompanying User pairs created */
                        db.beginTransaction();
                try
                {
                    for(ContentValues mValue:cValuesUserAccompany)
                    {
                        db.replace(PostAccompanyingUserEntry.TABLE_NAME, null, mValue);
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                return null;
            }
        }.execute();

        return view;
    }
}
