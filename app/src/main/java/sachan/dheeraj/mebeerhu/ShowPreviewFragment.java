package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import sachan.dheeraj.mebeerhu.cache.MemDiskCache;
import sachan.dheeraj.mebeerhu.customFlowLayout.FlowLayout;
import sachan.dheeraj.mebeerhu.localData.AppContract;
import sachan.dheeraj.mebeerhu.localData.AppDbHelper;
import sachan.dheeraj.mebeerhu.model.Tag;
import sachan.dheeraj.mebeerhu.utils.Utils;

public class ShowPreviewFragment extends Fragment {

    public final String LOG_TAG = ShowPreviewFragment.class.getSimpleName();
    private AppDbHelper mDbHelper;
    private ArrayList<Tag> tags = new ArrayList<>();
    String placeId, placeDetails, curImagePath;

    TextView location;
    ImageView mainImageView;
    FlowLayout flowLayout;
    private TextView moreTextView;
    private Button doneButton;

    private Bitmap imageBitmap = null;
    private static final MemDiskCache mCache = MemDiskCache.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);

        mDbHelper =  new AppDbHelper(getActivity());
        Log.v(LOG_TAG, "On Create for ShowPreviewFragment, fetch tags from db");

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

        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file), Context.MODE_PRIVATE);
        placeId = sharedPref.getString(getString(R.string.post_location_id), null);
        placeDetails = sharedPref.getString(getString(R.string.post_location_description), null);
        curImagePath = sharedPref.getString(getString(R.string.post_image_path),null);

        Log.v(LOG_TAG, String.format("place_id %s, place_details %s", placeId, placeDetails));
        Log.v(LOG_TAG, "Image path: " + curImagePath );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "On onCreateView for ShowPreviewFragment");
        View rootView = inflater.inflate(R.layout.fragment_show_preview, container, false);
        location = (TextView)rootView.findViewById(R.id.location);
        flowLayout = (FlowLayout) rootView.findViewById(R.id.flow_layout);
        mainImageView = (ImageView) rootView.findViewById(R.id.main_image);
        moreTextView = (TextView) rootView.findViewById(R.id.more);
        doneButton = (Button) rootView.findViewById(R.id.done_button);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FeedsActivity.class);
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        imageBitmap = null;
        MessageDigest md;
        String key="post_image";

        if(curImagePath != null) {

            if (mCache != null) {
                try {
                    md = MessageDigest.getInstance("MD5");
                    key = Utils.toHex(md.digest(curImagePath.getBytes()));
                    imageBitmap =  mCache.getBitmapFromCache(key);
                }
                catch (NoSuchAlgorithmException e)
                {
                    Log.e(LOG_TAG, "MD5 algorithm not recognized : " + e.getMessage());
                }
            }

            if(imageBitmap == null ) {
                setPic(curImagePath);
                if (mCache!= null)
                {
                    mCache.addBitmapToCache(key, imageBitmap);
                }
            }
            if (imageBitmap != null)
                mainImageView.setImageBitmap(imageBitmap);
        }

        /* Display the tags */
        displayTags(2);
        ViewTreeObserver vto = flowLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                flowLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (flowLayout.isSomeThingHidden()) {
                    moreTextView.setVisibility(View.VISIBLE);
                } else {
                    moreTextView.setVisibility(View.GONE);
                }
            }
        });

        moreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTags(Integer.MAX_VALUE);
            }
        });

        if (placeDetails != null)
            location.setText(placeDetails);

        return rootView;
    }

    /*Helper function to display tags based on MAX lines supported */
    private void displayTags(int max_lines)
    {
        moreTextView.setVisibility(View.GONE);
        flowLayout.removeAllViews();
        flowLayout.setMaxLinesSupported(max_lines);
        if (tags != null && tags.size() > 0) {
            for (Tag tag : tags) {
                View view1 = getActivity().getLayoutInflater().inflate(R.layout.list_item_tag, null);
                TextView textView = (TextView) view1.findViewById(R.id.tv);
                textView.setText(tag.getTagName());
                textView.setTextColor(getActivity().getResources().getColor(R.color.white));
                if (tag.getTypeId() == Tag.TYPE_NOUN) {
                    textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                } else {
                    textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
                }
                flowLayout.addView(view1);
            }
        }
    }

    /*Helper function to get and set images */
    private void setPic(String imagePath)
    {
		/* There isn't enough memory to open up more than a couple camera photos
		 * So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mainImageView.getWidth();
        int targetH = mainImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        imageBitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
    }
}

