
package sachan.dheeraj.mebeerhu;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import sachan.dheeraj.mebeerhu.model.Post;
import sachan.dheeraj.mebeerhu.model.Tag;
import sachan.dheeraj.mebeerhu.viewHolders.PostViewHolder;

import static com.google.android.gms.location.places.PlacesStatusCodes.getStatusCodeString;


public class TagAutocompleteAdapter
        extends ArrayAdapter<Tag> implements Filterable {

    private static final String LOG_TAG = TagAutocompleteAdapter.class.getSimpleName();

    private Context context;
    /**
     * Current results returned by this adapter.
     */
    private ArrayList<Tag> mResultList;

    public static class ViewHolder {
        public TextView tag_title;
        public TextView tag_meaning;
        public ViewHolder(View v) {
            tag_title = (TextView)v.findViewById(R.id.tag_title);
            tag_meaning = (TextView)v.findViewById(R.id.tag_meaning);
        }
    }

    public TagAutocompleteAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    /**
     * Returns the number of results received in the last autocomplete query.
     */
    @Override
    public int getCount() {
        return mResultList.size();
    }

    /**
     * Returns an item from the last autocomplete query.
     */
    @Override
    public Tag getItem(int position) {
        return mResultList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tag tag = getItem(position);
        /* Log.v(LOG_TAG, String.format("Binding Tag to adapter, Name: %s, meaning: %s",
                tag.getTagName(),tag.getTagMeaning())); */

        ViewHolder mViewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_tag_suggestion, parent, false);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder)convertView.getTag();
        }

        mViewHolder.tag_title.setText(tag.getTagName());
        mViewHolder.tag_meaning.setText(tag.getTagMeaning());

        return convertView;
    }
    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getAutocomplete(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private ArrayList<Tag> getAutocomplete(CharSequence constraint)
    {
        Log.v(LOG_TAG, "Starting autocomplete query for: " + constraint);
        ArrayList<Tag> autocompletePredictions = new ArrayList<>();
        autocompletePredictions.add(new Tag("Vegetarian", "Made of vegetables", Tag.TYPE_ADJECTIVE, true ));
        autocompletePredictions.add(new Tag("Non Vegetarian", "Made of animals", Tag.TYPE_ADJECTIVE, true ));
        autocompletePredictions.add(new Tag("Burger", "Bun, cutlet, cheese and vegetables", Tag.TYPE_NOUN, true ));
        autocompletePredictions.add(new Tag("Pizza", "Flour base, cheese, vegetables and sauce", Tag.TYPE_NOUN, false ));
        autocompletePredictions.add(new Tag("Idli", "South Indian dish made of rice", Tag.TYPE_NOUN, true ));
        autocompletePredictions.add(new Tag("Homemade", "Made at home", Tag.TYPE_ADJECTIVE, true));
        autocompletePredictions.add(new Tag("Marinated", "A particular taste", Tag.TYPE_ADJECTIVE, true));
        autocompletePredictions.add(new Tag("Vanilla", "A flavor used in ice creams", Tag.TYPE_NOUN, true));
        autocompletePredictions.add(new Tag("Fruit Salad", "A variety of fruits mixed up", Tag.TYPE_NOUN, true));
        autocompletePredictions.add(new Tag("Spicy", "Flavor due to heavy amount of spices", Tag.TYPE_ADJECTIVE, true));
        autocompletePredictions.add(new Tag("Grilled", "Roasted on fire for a long time", Tag.TYPE_ADJECTIVE, true));
        autocompletePredictions.add(new Tag("Burger", "A western snack made of bread", Tag.TYPE_NOUN, true));

        // This method should have been called off the main UI thread. Block and wait for at most 60s
        // for a result from the API.
        Iterator<Tag> iterator = autocompletePredictions.iterator();
        ArrayList<Tag> resultList = new ArrayList<>(autocompletePredictions.size());
        while (iterator.hasNext()) {
            Tag prediction = iterator.next();
            resultList.add(prediction);
        }
        return resultList;
    }
}
