package sachan.dheeraj.mebeerhu;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import sachan.dheeraj.mebeerhu.customFlowLayout.FlowLayout;
import sachan.dheeraj.mebeerhu.globalData.CommonData;
import sachan.dheeraj.mebeerhu.model.Tag;
import sachan.dheeraj.mebeerhu.model.TagArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectTagsFragment extends Fragment {
    private final String LOG_TAG = SelectTagsFragment.class.getSimpleName();
    private HashSet<Tag> tagHashSet = new HashSet<Tag>();
    private TextView continueTextView;
    Typeface typeface;

    public SelectTagsFragment() {
        // Required empty public constructor
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
                        //mGoogleApiClient.connect(); /* Why is this needed? */
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

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView for SelectTags Fragment");
        typeface = Typeface.createFromAsset(getActivity().getBaseContext().getResources().getAssets(), "gothic.ttf");
        final View view = inflater.inflate(R.layout.fragment_follow_tags, container, false);
        ((TextView) view.findViewById(R.id.header)).setTypeface(typeface);
        ((TextView) view.findViewById(R.id.hook)).setTypeface(typeface);
        ((TextView) view.findViewById(R.id.fine)).setTypeface(typeface);

        final FlowLayout flowLayout = (FlowLayout) view.findViewById(R.id.flow_layout);
        continueTextView = (TextView) view.findViewById(R.id.continue_button);
        continueTextView.setTypeface(typeface);

        continueTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v(LOG_TAG, "Continue button clicked, no of tags selected = " + tagHashSet.size() );

                if(tagHashSet.size() <= 4){
                    Toast.makeText(getActivity(), "Select atleast 5 tags to continue", Toast.LENGTH_SHORT).show();
                    return;
                }

                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        ArrayList<String> stringArrayList = new ArrayList<String>();
                        for (Tag tag : tagHashSet) {
                            stringArrayList.add(tag.getTagName());
                            CommonData.followedTags.put(tag.getTagName(), tag);
                        }

                        Log.v(LOG_TAG, "Sending User Selected Tags to server");
                        /* String data = HttpAgent.postGenericData(UrlConstants.FOLLOW_TAGS_URL, JsonHandler.stringifyNormal(stringArrayList), getActivity());                        
                        if (data != null) {
                            Log.v(LOG_TAG, "Non-null response from server for saving tags");
                            return true;
                        }
                        Log.v(LOG_TAG, "Server response null, couldn't save tags at server");
                        return false; */
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if (aBoolean) {
                            //Log.d(LOG_TAG, "User Tags saved at server");
                            //Toast.makeText(getActivity(), "saved", Toast.LENGTH_LONG).show();
                            Log.d(LOG_TAG, "Minimum tags selected, continuing to show feeds");
                            Intent mIntent = new Intent(getActivity(), FeedsActivity.class);
                            startActivity(mIntent);
                        }
                    }

                }.execute();
            }

            ;
        });

        new AsyncTask<Void, Void, TagArrayList>() {
            @Override
            protected TagArrayList doInBackground(Void... params) {
                /* String data = HttpAgent.get(UrlConstants.GET_TRENDY_TAGS_URL, getActivity());
                 * TagArrayList tagArrayList = JsonHandler.parseNormal(data, TagArrayList.class); */
                Log.v(LOG_TAG, "Creating temporary TAGs");
                TagArrayList testTagList = new TagArrayList();
                testTagList.add(new Tag("Homemade", "DishType", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Jumbo", "Taste", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Marinated", "Taste", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Vanilla", "Ambiance", Tag.TYPE_NOUN, true));
                testTagList.add(new Tag("Fruit Salad", "City", Tag.TYPE_NOUN, true));
                testTagList.add(new Tag("Spicy", "DishType", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Fizzy", "DishType", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Grilled", "Taste", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Burger", "Taste", Tag.TYPE_NOUN, true));
                testTagList.add(new Tag("Caramelized", "Ambiance", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Nutty", "City", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Beer", "DishType", Tag.TYPE_NOUN, true));
                testTagList.add(new Tag("Non Veg", "Taste", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Tantalizing", "Taste", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Ice Cream", "Ambiance", Tag.TYPE_NOUN, true));
                testTagList.add(new Tag("Hot", "City", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Low Calorie", "Taste", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Pizza", "Ambiance", Tag.TYPE_NOUN, true));
                testTagList.add(new Tag("Mutton", "City", Tag.TYPE_NOUN, true));
                testTagList.add(new Tag("Delicious", "DishType", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Chocolaty", "Taste", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Brunch", "Taste", Tag.TYPE_NOUN, true));
                testTagList.add(new Tag("Fried", "Ambiance", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Superb", "City", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Natural", "City", Tag.TYPE_ADJECTIVE, true));
                testTagList.add(new Tag("Ale", "DishType", Tag.TYPE_NOUN, true));
                testTagList.add(new Tag("Sweet", "Taste", Tag.TYPE_ADJECTIVE, true));
                return testTagList;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(TagArrayList tagArrayList) {
                if (tagArrayList != null) {
                    flowLayout.removeAllViews();
                    flowLayout.setMaxLinesSupported(Integer.MAX_VALUE);
                    Log.v(LOG_TAG, "Temporary TAGs list size = " + tagArrayList.size());
                        for (Tag tag : tagArrayList) {
                            View view1 = inflater.inflate(R.layout.list_item_tag, null);
                            TextView textView = (TextView) view1.findViewById(R.id.tv);
                            FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout);
                            textView.setText(tag.getTagName());
                            Log.v(LOG_TAG, "Adding TAG : " + tag.getTagName());
                            textView.setTypeface(typeface);
                            view1.setOnClickListener(ON_CLICK_LISTENER);
                            view1.setTag(new TagHolder(tag, textView));
                            flowLayout.addView(view1);
                        }
                    Log.v(LOG_TAG, "Tags fetched from server, enabled continue button" );
                    continueTextView.setEnabled(true);
                }
            }
        }.execute();



        return view;
    }

    private final View.OnClickListener ON_CLICK_LISTENER = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TagHolder tagHolder = (TagHolder) v.getTag();
            if (tagHashSet.contains(tagHolder.tag)) {
                Log.v(LOG_TAG, String.format("Tag %s already selected, de-selecting it", tagHolder.tag.getTagName()) );
                tagHolder.textView.setTextColor(getActivity().getResources().getColor(R.color.purple));
                tagHashSet.remove(tagHolder.tag);
                tagHolder.textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            } else {
                Log.v(LOG_TAG, String.format("Selected tag: %s, type %d", tagHolder.tag.getTagName(), tagHolder.tag.getTypeId()));
                tagHolder.textView.setTextColor(getActivity().getResources().getColor(R.color.white));
                tagHashSet.add(tagHolder.tag);
                if (tagHolder.tag.getTypeId() == Tag.TYPE_NOUN) {
                    tagHolder.textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                } else {
                    tagHolder.textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
                }
            }
            if (tagHashSet.size() > 4) {
                Log.v(LOG_TAG, "Min req tags selected, enable continue button" );
                continueTextView.setBackground(getResources().getDrawable(R.drawable.button_smooth));
            } else {
                Log.v(LOG_TAG, "Min req tags not selected, num selected tags = " + tagHashSet.size());
                continueTextView.setBackground(getResources().getDrawable(R.drawable.button_smooth_tatti));
            }
        }
    };

    private static final class TagHolder {
        private Tag tag;
        private TextView textView;

        private TagHolder(Tag tag, TextView textView) {
            this.tag = tag;
            this.textView = textView;
        }

    }
}
