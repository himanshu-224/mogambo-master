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
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

import sachan.dheeraj.mebeerhu.globalData.CommonData;
import sachan.dheeraj.mebeerhu.localData.AppContract;
import sachan.dheeraj.mebeerhu.localData.AppDbHelper;
import sachan.dheeraj.mebeerhu.model.Tag;

public class TagFillerFragment extends Fragment {

    public final String LOG_TAG = TagFillerFragment.class.getSimpleName();

    private ArrayList<Tag> tags = new ArrayList<>();

    private AppDbHelper mDbHelper;

    private CustomAutoCompleteTextView autoCompleteTextView;
    private LinearLayout linearLayout;
    private HorizontalScrollView horizontalScrollView;
    private ActionMode mActionMode;
    View removedView;

    public TagFillerFragment() {
    }

    public boolean addTags(String tag)
    {
        /* Here we need to determine if that tag exists in our database or not.
         * If yes we need to populate the parameters for the TAG from our database.
         * Else we need to save the TAG in the database with some default
         * parameters and mark that it is not approved yet.
         * Someone needs to define the parameters for all the non-approved TAGs in
         * the database regularly. I guess this process has to be manual for now */
        Log.v(LOG_TAG, "Adding Tag " + tag);
        Tag thisTag = new Tag(tag, "taste", Tag.TYPE_NOUN, true);
        if (tags.contains(thisTag))
        {
            return false;
        }
        else
        {
            tags.add(thisTag);
            return true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mDbHelper =  new AppDbHelper(getActivity());
        Log.v(LOG_TAG, "On Create for TagFillerFragment, fetch any saved tags from database");

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder mGenericQueryBuilder = new SQLiteQueryBuilder();
        mGenericQueryBuilder.setTables(
                AppContract.SinglePostTagEntry.TABLE_NAME);

        String projection[] = new String[]{
                AppContract.SinglePostTagEntry._ID,
                AppContract.SinglePostTagEntry.COLUMN_TAG_NAME,
                AppContract.SinglePostTagEntry.COLUMN_TAG_MEANING,
                AppContract.SinglePostTagEntry.COLUMN_TYPE_ID,
                AppContract.SinglePostTagEntry.COLUMN_APPROVED
        };

        int COL_ID = 0;
        int COL_TAG_NAME = 1;
        int COL_TAG_MEANING = 2;
        int COL_TYPE_ID = 3;
        int COL_APPROVED = 4;

        String selection = "1";
        String sortOrder = AppContract.SinglePostTagEntry._ID + " ASC";

        Tag mTag;

        Cursor cursor = mGenericQueryBuilder.query(db,
                projection,
                selection,
                null,
                null,
                null,
                sortOrder
        );

        int numTags = cursor.getCount();

        Log.v(LOG_TAG, String.format("Fetched Tag records, num rows = %d", numTags));

        cursor.moveToFirst();
        for (int j=0; j<numTags; j++)
        {
            mTag = new Tag();
                    /* Mandatory parameters. None of them should be NULL */
            if( cursor.isNull(COL_TAG_NAME) ||
                    cursor.isNull(COL_TAG_MEANING) ||
                    cursor.isNull(COL_TYPE_ID) ||
                    cursor.isNull(COL_APPROVED)
                    )
            {
                Log.e(LOG_TAG, "Error retrieving tag from db, mandatory params null");
                Log.e(LOG_TAG, String.format("Is NULL : tag_name %b, tag_meaning %b",
                        cursor.isNull(COL_TAG_NAME),cursor.isNull(COL_TAG_MEANING)));
                Log.e(LOG_TAG, String.format("Is NULL : type_id %b, tag_aproved %b", cursor.isNull(COL_TYPE_ID),
                        cursor.isNull(COL_APPROVED)));
            }
            else
            {
                Log.v(LOG_TAG, "Fetch tag for ID: "+cursor.getString(COL_ID));
                mTag = new Tag(
                        cursor.getString(COL_TAG_NAME),
                        cursor.getString(COL_TAG_MEANING),
                        cursor.getInt(COL_TYPE_ID),
                        cursor.getInt(COL_APPROVED)!=0
                );
                tags.add(mTag);
            }
            cursor.moveToNext();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tagfiller_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done_button)
        {
            if (tags.size() == 0 )
            {
                Toast.makeText(getActivity(),"Please add tags to finish Post", Toast.LENGTH_SHORT).show();
                return true;
            }
            Log.v(LOG_TAG, "Done, getting user data for Post");
            /* Launch a service here to save the data to server */
            Intent intent = new Intent(getActivity(), FeedsActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.v(LOG_TAG, "On Destroy for TagFillerFragment, save tags to database");
        Vector<ContentValues> cValuesTag = new Vector<ContentValues>();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        /* Save the tags to database to be retrieved if user switches back to Tag filler activity */
        for (Tag tag:tags)
        {
            ContentValues cValue;
            if (tag.getTagName() == null ||
                    tag.getTagMeaning() == null ||
                    tag.getTypeId() == null ||
                    tag.isApproved() == null)
            {
                Log.e(LOG_TAG, "Error saving tagList, mandatory params null");
                Log.e(LOG_TAG, String.format("Tag fields : tagName %s, tagMeaning %s, typeId %d, approved %b",tag.getTagName(),
                        tag.getTagMeaning(), tag.getTypeId(), tag.isApproved()));
            }
            else
            {
                cValue = new ContentValues();
                cValue.put(AppContract.SinglePostTagEntry.COLUMN_TAG_NAME, tag.getTagName());
                cValue.put(AppContract.SinglePostTagEntry.COLUMN_TAG_MEANING, tag.getTagMeaning());
                cValue.put(AppContract.SinglePostTagEntry.COLUMN_TYPE_ID, tag.getTypeId());
                cValue.put(AppContract.SinglePostTagEntry.COLUMN_APPROVED, tag.isApproved());
                cValuesTag.add(cValue);
            }
        }
        Log.v(LOG_TAG, "Inserting Tags data, num rows = " + cValuesTag.size());
        db.beginTransaction();
        try
        {
            db.delete(AppContract.SinglePostTagEntry.TABLE_NAME, null, null);
            for(ContentValues mValue:cValuesTag)
            {
                db.replace(AppContract.SinglePostTagEntry.TABLE_NAME, null, mValue);
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "On onCreateView for TagFillerFragment");
        View rootView = inflater.inflate(R.layout.fragment_tag_filler, container, false);
        autoCompleteTextView = (CustomAutoCompleteTextView) rootView.findViewById(R.id.auto_complete);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout);
        horizontalScrollView = (HorizontalScrollView) rootView.findViewById(R.id.hsv);
        horizontalScrollView.setVisibility(View.GONE);
        final TagAutocompleteAdapter adapter =
                new TagAutocompleteAdapter(getActivity(), R.layout.list_item_tag_suggestion);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tag thisTag = adapter.getItem(position);
                String s = thisTag.getTagName();

                /* Store the Tags locally, and also check if it has not already been entered
                 * by the user. If already entered, inform the user and return from here */
                if (!addTags(s.replace("\n", ""))) {
                    autoCompleteTextView.setText("");
                    Toast.makeText(getActivity(), "Tag Already Added", Toast.LENGTH_SHORT).show();
                    return;
                }
                View view1 = getActivity().getLayoutInflater().inflate(R.layout.list_item_tag, null);
                TextView textView = (TextView) view1.findViewById(R.id.tv);
                textView.setText(s.replace("\n", ""));
                textView.setTextColor(getActivity().getResources().getColor(R.color.white));
                if (/*tag.getTypeId() == Tag.TYPE_NOUN*/s.length() % 2 == 0) {
                    textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                } else {
                    textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
                }
                if (horizontalScrollView.getVisibility() == View.GONE) {
                    horizontalScrollView.setVisibility(View.VISIBLE);
                }
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                view1.setTag("test");
                view1.setOnLongClickListener(LongClickListener);
                linearLayout.addView(view1);
                autoCompleteTextView.setText("");
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.toString().contains("\n")) {

                /* Store the Tags locally, and also check if it has not already been entered
                 * by the user. If already entered, inform the user and return from here */
                    if (!addTags(s.toString().replace("\n", ""))) {
                        autoCompleteTextView.setText("");
                        Toast.makeText(getActivity(), "Tag Already Added", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //move to flow layout
                    View view1 = getActivity().getLayoutInflater().inflate(R.layout.list_item_tag, null);
                    TextView textView = (TextView) view1.findViewById(R.id.tv);
                    textView.setText(s.toString().replace("\n", ""));
                    textView.setTextColor(getActivity().getResources().getColor(R.color.white));
                    if (/*tag.getTypeId() == Tag.TYPE_NOUN*/s.toString().length() % 2 == 0) {
                        textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                    } else {
                        textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
                    }
                    if (horizontalScrollView.getVisibility() == View.GONE) {
                        horizontalScrollView.setVisibility(View.VISIBLE);
                    }
                    horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    view1.setTag("test");
                    view1.setOnLongClickListener(LongClickListener);
                    linearLayout.addView(view1);
                    autoCompleteTextView.setText("");
                }
            }
        });

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        /* Restore the tags on returning back to this activity */
        for (Tag tag:tags)
        {
            String s = tag.getTagName();
            View view1 = getActivity().getLayoutInflater().inflate(R.layout.list_item_tag, null);
            TextView textView = (TextView) view1.findViewById(R.id.tv);
            textView.setText(s);
            textView.setTextColor(getActivity().getResources().getColor(R.color.white));
            if (s.length() % 2 == 0) { /*tag.getTypeId() == Tag.TYPE_NOUN*/
                textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
            } else {
                textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
            }
            if (horizontalScrollView.getVisibility() == View.GONE) {
                horizontalScrollView.setVisibility(View.VISIBLE);
            }
            horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            view1.setTag("test");
            view1.setOnLongClickListener(LongClickListener);
            linearLayout.addView(view1);
        }
        return rootView;
    }

    View.OnLongClickListener LongClickListener = new View.OnLongClickListener() {
        // Called when the user long-clicks on someView
        public boolean onLongClick(View view) {
            Log.v(LOG_TAG,"LongClick selected by user");
            if (mActionMode != null) {
                return false;
            }
            removedView = view;
            // Start the CAB using the ActionMode.Callback defined above
            mActionMode = getActivity().startActionMode(mActionModeCallback);
            mActionMode.setTitle("Delete Tag");
            view.setSelected(true);
            return true;
        }
    };

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // inflate contextual menu
            mode.getMenuInflater().inflate(R.menu.tag_context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_delete:
                    linearLayout.removeView(removedView);
                    TextView tv = (TextView)removedView.findViewById(R.id.tv);
                    int index = tags.indexOf(new Tag(String.valueOf(tv.getText()), "dummy", Tag.TYPE_ADJECTIVE, true));
                    if (index != -1)
                        tags.remove(index);
                    else
                        Log.e(LOG_TAG, "Error in deleting, could not find tag: " + String.valueOf(tv.getText()) );
                    mode.finish();
                    return true;
                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

}

