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

import org.apmem.tools.layouts.FlowLayout;

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

            FlowLayout flowLayout = (FlowLayout) rootView.findViewById(R.id.flow_layout);

            for(String s : tags) {
                View view = inflater.inflate(R.layout.list_item_tag, null);
                TextView textView = (TextView) view.findViewById(R.id.tv);
                textView.setText(s);
                flowLayout.addView(view,0);
            }
            return rootView;
        }
    }
}
