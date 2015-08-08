package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import sachan.dheeraj.mebeerhu.customFlowLayout.FlowLayout;
import sachan.dheeraj.mebeerhu.model.Tag;


public class CreatePostActivityNew extends ActionBarActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_activity_new);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_post_activity_new, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private ImageView mainImageView;
        private EditText editText;

        private boolean picTaken = false;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_create_post_activity_new, container, false);
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
            if (!picTaken) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
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

        private AutoCompleteTextView autoCompleteTextView;
        private LinearLayout linearLayout;
        private HorizontalScrollView horizontalScrollView;

        public TagFillerFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
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
                        autoCompleteTextView.setText("");
                    }
                }
            });

            return rootView;
        }
    }

}
