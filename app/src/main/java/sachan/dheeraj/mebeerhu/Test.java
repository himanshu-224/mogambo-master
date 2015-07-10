package sachan.dheeraj.mebeerhu;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class Test extends ActionBarActivity {

    private static final String[] tags = {"qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq","Homemade", "Jumbo", "Vanilla", "Fruit Salad", "Spicy",
            "Fizzy", "Grilled", "Caramolized", "Nutty", "Burger",
            "Beer", "Non Veg", "Tentalizing", "IceCream", "Hot",
            "Low Calorie", "Pizza", "Mutton", "Chilled", "Biryani",
            "Mexican", "Punjabi", "Traditional", "Marinated", "Choot Slayer","Homemade", "Jumbo", "Vanilla", "Fruit Salad", "Spicy",
            "Fizzy", "Grilled", "Caramolized", "Nutty", "Burger",
            "Beer", "Non Veg", "Tentalizing", "IceCream", "Hot",
            "Low Calorie", "Pizza", "Mutton", "Chilled", "Biryani",
            "Mexican", "Punjabi", "Traditional", "Marinated"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_follow_tags, container, false);
          /*  LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.tags);

            Arrays.sort(tags, new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    if (lhs.length() == rhs.length()) {
                        return 0;
                    } else if (lhs.length() > rhs.length()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            ArrayList<FrameLayout> textViews = new ArrayList<FrameLayout>();
            for (String s : tags) {
                View view = inflater.inflate(R.layout.list_item_tag, null);
                TextView textView = (TextView) view.findViewById(R.id.tv);
                textView.setText(s);
                textViews.add((FrameLayout)view);
            }

            LinearLayout linearLayout1 = new LinearLayout(getActivity());
            linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout1.setPadding(0, 5, 0, 5);
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout1.addView(textViews.get(0));
            linearLayout1.addView(textViews.get(1));
            linearLayout1.addView(textViews.get(2));
            linearLayout1.addView(textViews.get(3));
            ((LinearLayout.LayoutParams)textViews.get(0).getLayoutParams()).setMargins(5,5,5,5);
            ((LinearLayout.LayoutParams)textViews.get(1).getLayoutParams()).setMargins(5,5,5,5);
            ((LinearLayout.LayoutParams)textViews.get(2).getLayoutParams()).setMargins(5,5,5,5);
            ((LinearLayout.LayoutParams)textViews.get(3).getLayoutParams()).setMargins(5,5,5,5);

            linearLayout.addView(linearLayout1);

            LinearLayout linearLayout2 = new LinearLayout(getActivity());
            linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout2.setPadding(0,5,0,5);
            linearLayout2.addView(textViews.get(4));
            linearLayout2.addView(textViews.get(5));
            linearLayout2.addView(textViews.get(6));
            linearLayout2.addView(textViews.get(7));
            linearLayout.addView(linearLayout2);

            int k = linearLayout2.getMeasuredWidth();
            int j = textViews.get(0).getMeasuredWidth();*/

            /*LinearLayout linearLayout3 = new LinearLayout(getActivity());
            linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout1.addView(textViews.get(8));
            linearLayout1.addView(textViews.get(9));
            linearLayout1.addView(textViews.get(10));
            linearLayout1.addView(textViews.get(11));
            linearLayout.addView(linearLayout3);

            LinearLayout linearLayout4 = new LinearLayout(getActivity());
            linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout1.addView(textViews.get(12));
            linearLayout1.addView(textViews.get(13));
            linearLayout1.addView(textViews.get(14));
            linearLayout1.addView(textViews.get(15));
            linearLayout.addView(linearLayout4);

            LinearLayout linearLayout5 = new LinearLayout(getActivity());
            linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout1.addView(textViews.get(16));
            linearLayout1.addView(textViews.get(17));
            linearLayout1.addView(textViews.get(18));
            linearLayout1.addView(textViews.get(19));
            linearLayout.addView(linearLayout5);*/

            GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
            gridView.setAdapter(new ArrayAdapter<String>(getActivity(),-1,tags){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = inflater.inflate(R.layout.list_item_tag, null);
                    TextView textView = (TextView) view.findViewById(R.id.tv);
                    textView.setText(getItem(position));
                    return textView;
                }
            });
            return rootView;
        }
    }
}
