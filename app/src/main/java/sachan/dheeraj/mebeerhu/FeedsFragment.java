package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.Vector;

import sachan.dheeraj.mebeerhu.localData.AppDbHelper;
import sachan.dheeraj.mebeerhu.model.AppLocation;
import sachan.dheeraj.mebeerhu.model.Feeds;
import sachan.dheeraj.mebeerhu.model.Post;
import sachan.dheeraj.mebeerhu.model.Tag;
import sachan.dheeraj.mebeerhu.model.User;
import sachan.dheeraj.mebeerhu.viewHolders.PostViewHolder;

import sachan.dheeraj.mebeerhu.localData.AppContract.PostEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.UserEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.TagEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.PostTagEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.PostAccompanyingUserEntry;
import sachan.dheeraj.mebeerhu.globalData.CommonData;

/**
 * Created by agarwalh on 9/3/2015.
 */

public class FeedsFragment extends Fragment{
    public static final int PLACE_PICKER_REQUEST = 100;

    private static final String LOG_TAG = FeedsFragment.class.getSimpleName();  
    private ProgressBar progressBar;

    private AppDbHelper mDbHelper;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FeedsAdapter mAdapter;
    private ArrayList<Post> posts;
    private Handler handler;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    //LoadInitialPosts loadInitialPosts;
    GetInitialPostsFromNetwork getInitialPostsFromNetwork;

    private View.OnLongClickListener TAG_LONG_CLICK_LISTENER = new View.OnLongClickListener() {
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
            mActivity.showTagDialog(tagName, description, isFollowed);

            return true;
        }
    };

    private View.OnLongClickListener LOCATION_LONG_CLICK_LISTENER = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            TextView tView = (TextView) view;
            Log.v(LOG_TAG, "Long Pressed Location with value = " + tView.getText());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            FeedsActivity mActivity =  (FeedsActivity)getActivity();
            String locationName = String.valueOf(tView.getText());
            AppLocation location = CommonData.locations.get(locationName);
            String description="";
            if ( location != null)
            {
                description = location.getLocDescription();
            }
            mActivity.showLocationDialog(locationName, description);

            return true;
        }
    };

    public FeedsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        mDbHelper =  new AppDbHelper(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume for FeedsFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy for FeedsFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "onPause for FeedsFragment");
    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView for FeedsFragment");
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.post_swipe_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);        
        progressBar = (ProgressBar) view.findViewById(R.id.loader);

        progressBar.setVisibility(View.VISIBLE);

        handler = new Handler();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //mSwipeRefreshLayout.setRefreshing(true);
        getInitialPostsFromNetwork = new GetInitialPostsFromNetwork();
        getInitialPostsFromNetwork.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getInitialPostsFromNetwork = new GetInitialPostsFromNetwork();
                        getInitialPostsFromNetwork.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2500);
            }
        });        

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FeedsActivity mActivity = (FeedsActivity)getActivity();
                    mActivity.showCreatePostDialog();
            }
        });

        return view;
    }

    class GetInitialPostsFromNetwork extends AsyncTask<Void, Void, ArrayList<Post>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Post> feeds) {
            try {
                posts = feeds;

                mAdapter = new FeedsAdapter(getActivity(), posts, mRecyclerView,
                        LOCATION_LONG_CLICK_LISTENER, TAG_LONG_CLICK_LISTENER);
                //mAdapter.enableFooter(true);

                mAdapter.setOnLoadMoreListener(new FeedsAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        //add progress item
                        posts.add(null);
                        mAdapter.notifyItemInserted(posts.size() - 1);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //remove progress item
                                new LoadMorePosts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                            }
                        }, 2000);
                        System.out.println("load");
                    }
                });

                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                //mSwipeRefreshLayout.setRefreshing(false);
            }catch (Exception e){
                Log.e(LOG_TAG, e.getMessage());
            }
        }

        @Override
        protected ArrayList<Post> doInBackground(Void... params) {

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

                    AppLocation thisLocation = new AppLocation(thisPost.getPostLocation());

                    /** TEMP. Need to get proper description from data fetched from server **/
                    thisLocation.createSetDescription("68 A, 4th Block", "Koramangala",
                            "Bangalore", "Karnataka", "India", "560034"  );
                    CommonData.locations.put(thisLocation.getLocationName(),thisLocation);

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
                                /* Store tag info into common data for quick access */
                            CommonData.tags.put(tag.getTagName(), tag);

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

            Log.v(LOG_TAG, "Total unique tags received = " + CommonData.tags.size());

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
            return feeds;
        }
    };

    class LoadInitialPosts extends AsyncTask<Void, Void, ArrayList<Post>>
    {
        private final String LOG_TAG = LoadInitialPosts.class.getSimpleName();

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
        protected void onPostExecute(ArrayList<Post> feeds) {
            try {
                posts = Feeds.feedsBuilder();

                mAdapter = new FeedsAdapter(getActivity(), posts, mRecyclerView,
                         LOCATION_LONG_CLICK_LISTENER, TAG_LONG_CLICK_LISTENER);
                //mAdapter.enableFooter(true);

                mAdapter.setOnLoadMoreListener(new FeedsAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        //add progress item
                        posts.add(null);
                        mAdapter.notifyItemInserted(posts.size() - 1);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //remove progress item
                                new LoadMorePosts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                            }
                        }, 2000);
                        System.out.println("load");
                    }
                });

                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                //mSwipeRefreshLayout.setRefreshing(false);
            }catch (Exception e){
                Log.e(LOG_TAG, e.getMessage());
            }

        }
    }

    public class LoadMorePosts extends AsyncTask<Void, Void, ArrayList<Post>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Post> feeds) {
            try {
                posts.remove(posts.size() - 1);
                mAdapter.notifyItemRemoved(posts.size());
                posts.addAll(feeds);
                mAdapter.notifyDataSetChanged();
                mAdapter.setLoaded();
            }catch (Exception e){
                Log.e(LOG_TAG, e.getMessage());
            }

        }

        @Override
        protected ArrayList<Post> doInBackground(Void... params) {

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

                    AppLocation thisLocation = new AppLocation(thisPost.getPostLocation());

                    /** TEMP. Need to get proper description from data fetched from server **/
                    thisLocation.createSetDescription("68 A, 4th Block", "Koramangala",
                            "Bangalore", "Karnataka", "India", "560034"  );
                    CommonData.locations.put(thisLocation.getLocationName(),thisLocation);

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
                                /* Store tag info into common data for quick access */
                            CommonData.tags.put(tag.getTagName(), tag);

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

            Log.v(LOG_TAG, "Total unique tags received = " + CommonData.tags.size());

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
            return feeds;
        }
    }
}
