package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import java.util.HashSet;

import sachan.dheeraj.mebeerhu.globalData.CommonData;
import sachan.dheeraj.mebeerhu.model.Tag;

public class TagFillerFragment extends Fragment {

    public final String LOG_TAG = TagFillerFragment.class.getSimpleName();

    private HashSet<Tag> tags = new HashSet<Tag>();

    private AutoCompleteTextView autoCompleteTextView;
    private LinearLayout linearLayout;
    private HorizontalScrollView horizontalScrollView;

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
        return tags.add(new Tag(tag, "taste", Tag.TYPE_NOUN, true));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "On onCreateView for TagFillerFragment");
        View rootView = inflater.inflate(R.layout.fragment_tag_filler, container, false);
        autoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.auto_complete);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout);
        horizontalScrollView = (HorizontalScrollView) rootView.findViewById(R.id.hsv);
        horizontalScrollView.setVisibility(View.GONE);
        String[] countries = getResources().getStringArray(R.array.countries_array);
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, countries);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = adapter.getItem(position);

                /* Store the Tags locally, and also check if it has not already been entered
                 * by the user. If already entered, inform the user and return from here */
                if(!addTags(s.replace("\n", ""))) {
                    autoCompleteTextView.setText("");
                    Toast.makeText(getActivity(),"Tag Already Added", Toast.LENGTH_SHORT).show();
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
                    if(!addTags(s.toString().replace("\n", ""))) {
                        autoCompleteTextView.setText("");
                        Toast.makeText(getActivity(),"Tag Already Added", Toast.LENGTH_SHORT).show();
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
                    linearLayout.addView(view1);

                    view1.setTag("test");
                    view1.setOnLongClickListener(onLongClickListener);

                    autoCompleteTextView.setText("");
                }
            }
        });

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.v(LOG_TAG, "keyCode: " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.v(LOG_TAG, "onKey Back listener pressed");
                    getFragmentManager().popBackStack(getString(R.string.fragment_tag_filler), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                } else {
                    return false;
                }
            }
        });
        return rootView;
    }

    private static View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ClipData.Item item = new ClipData.Item((String) v.getTag());

            // Create a new ClipData using the tag as a label, the plain text MIME type, and
            // the already-created item. This will create a new ClipDescription object within the
            // ClipData, and set its MIME type entry to "text/plain"

            String[] MIMETYPES_TEXT_PLAIN = new String[]{
                    ClipDescription.MIMETYPE_TEXT_PLAIN};

            ClipData dragData = new ClipData((String) v.getTag(), MIMETYPES_TEXT_PLAIN, item);

            // Instantiates the drag shadow builder.
            View.DragShadowBuilder myShadow = new MyDragShadowBuilder(v);

            // Starts the drag

            v.startDrag(dragData,  // the data to be dragged
                    myShadow,  // the drag shadow builder
                    null,      // no need to use local data
                    0          // flags (not currently used, set to 0)
            );
            return true;
        }
    };

    private static class MyDragShadowBuilder extends View.DragShadowBuilder {

        // The drag shadow image, defined as a drawable thing
        private static Drawable shadow;

        // Defines the constructor for myDragShadowBuilder
        public MyDragShadowBuilder(View v) {

            // Stores the View parameter passed to myDragShadowBuilder.
            super(v);

            // Creates a draggable image that will fill the Canvas provided by the system.
            shadow = new ColorDrawable(Color.LTGRAY);
        }

        // Defines a callback that sends the drag shadow dimensions and touch point back to the
        // system.
        @Override
        public void onProvideShadowMetrics(Point size, Point touch) {
            // Defines local variables
            int width, height;

            // Sets the width of the shadow to half the width of the original View
            width = getView().getWidth() / 2;

            // Sets the height of the shadow to half the height of the original View
            height = getView().getHeight() / 2;

            // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
            // Canvas that the system will provide. As a result, the drag shadow will fill the
            // Canvas.
            shadow.setBounds(0, 0, width, height);

            // Sets the size parameter's width and height values. These get back to the system
            // through the size parameter.
            size.set(width, height);

            // Sets the touch point's position to be in the middle of the drag shadow
            touch.set(width / 2, height / 2);
        }

        // Defines a callback that draws the drag shadow in a Canvas that the system constructs
        // from the dimensions passed in onProvideShadowMetrics().
        @Override
        public void onDrawShadow(Canvas canvas) {

            // Draws the ColorDrawable in the Canvas passed in from the system.
            shadow.draw(canvas);
        }
    }
}

