package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
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


public class CreatePostActivity extends ActionBarActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 100;

    private final String LOG_TAG = ActionBarActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG_TAG, "onCreate for CreatePost Activity");
        setContentView(R.layout.activity_create_post);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
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
/*
            getSupportFragmentManager().beginTransaction().replace(R.id.container, PlaceSuggestionFragment.newInstance()).commit();
*/
            startActivity(new Intent(CreatePostActivity.this, PlaceSuggestionActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public final String LOG_TAG = PlaceholderFragment.class.getSimpleName();

        private ImageView mainImageView;
        private EditText editText;

        private boolean picTaken = false;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.v(LOG_TAG, "On onCreateView for PlaceHolder Fragment ");
            View rootView = inflater.inflate(R.layout.fragment_create_post_activity, container, false);
            mainImageView = (ImageView) rootView.findViewById(R.id.main_image);
            editText = (EditText) rootView.findViewById(R.id.edit_text);

            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new TagFillerFragment()).commit();
                    return true;
                }
            });

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.v(LOG_TAG, "On resume for PlaceHolder Fragment");
            if (!picTaken) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.v(LOG_TAG, String.format("onActivityResult, requestCode = %d, result = %d",
                    requestCode, resultCode));
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                Log.v(LOG_TAG, "IMAGE CAPTURE successful");
                picTaken = true;
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                mainImageView.setImageBitmap(imageBitmap);
                ViewGroup.LayoutParams layoutParams = mainImageView.getLayoutParams();
                WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics dimension = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(dimension);
                layoutParams.height = dimension.widthPixels * imageBitmap.getHeight() / imageBitmap.getWidth();
                mainImageView.setLayoutParams(layoutParams);
            } else {
                getActivity().finish();
            }
        }
    }


    public static class TagFillerFragment extends Fragment {

        public final String LOG_TAG = TagFillerFragment.class.getSimpleName();

        private AutoCompleteTextView autoCompleteTextView;
        private LinearLayout linearLayout;
        private HorizontalScrollView horizontalScrollView;

        public TagFillerFragment() {
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

   /* protected class myDragEventListener implements View.OnDragListener {

        // This is the method that the system calls when it dispatches a drag event to the
        // listener.
        public boolean onDrag(View v, DragEvent event) {

            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();

            // Handles each of the expected events
            switch(action) {

                case DragEvent.ACTION_DRAG_STARTED:

                    // Determines if this View can accept the dragged data
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                        // As an example of what your application might do,
                        // applies a blue color tint to the View to indicate that it can accept
                        // data.
                        v.setColorFilter(Color.BLUE);

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();

                        // returns true to indicate that the View can accept the dragged data.
                        return true;

                    }

                    // Returns false. During the current drag and drop operation, this View will
                    // not receive events again until ACTION_DRAG_ENDED is sent.
                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:

                    // Applies a green tint to the View. Return true; the return value is ignored.

                    v.setColorFilter(Color.GREEN);

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:

                    // Ignore the event
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:

                    // Re-sets the color tint to blue. Returns true; the return value is ignored.
                    v.setColorFilter(Color.BLUE);

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DROP:

                    // Gets the item containing the dragged data
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    // Gets the text data from the item.
                    dragData = item.getText();

                    // Displays a message containing the dragged data.
                    Toast.makeText(this, "Dragged data is " + dragData, Toast.LENGTH_LONG);

                    // Turns off any color tints
                    v.clearColorFilter();

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // Returns true. DragEvent.getResult() will return true.
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:

                    // Turns off any color tinting
                    v.clearColorFilter();

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // Does a getResult(), and displays what happened.
                    if (event.getResult()) {
                        Toast.makeText(this, "The drop was handled.", Toast.LENGTH_LONG);

                    } else {
                        Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_LONG);

                    }

                    // returns true; the value is ignored.
                    return true;

                // An unknown action type was received.
                default:
                    Log.e("DragDrop Example","Unknown action type received by OnDragListener.");
                    break;
            }

            return false;
        }
    };*/

}
