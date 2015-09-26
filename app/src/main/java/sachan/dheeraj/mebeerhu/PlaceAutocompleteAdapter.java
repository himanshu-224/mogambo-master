
package sachan.dheeraj.mebeerhu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import sachan.dheeraj.mebeerhu.globalData.CommonData;

import static com.google.android.gms.location.places.PlacesStatusCodes.getStatusCodeString;


public class PlaceAutocompleteAdapter
        extends ArrayAdapter<PlaceAutocompleteAdapter.PlaceAutocomplete> implements Filterable {

    private static final String LOG_TAG = PlaceAutocompleteAdapter.class.getSimpleName();
    /**
     * Current results returned by this adapter.
     */
    private ArrayList<PlaceAutocomplete> mResultList;

    /**
     * Handles autocomplete requests.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * The bounds used for Places Geo Data autocomplete API requests.
     */
    private LatLngBounds mBounds;

    private Context context;

    /**
     * The autocomplete filter used to restrict queries to a specific set of place types.
     */
    private AutocompleteFilter mPlaceFilter;

    public static class ViewHolder {
        public TextView location_title;
        public TextView location_details;
        public ViewHolder(View v) {
            location_title = (TextView)v.findViewById(R.id.place_title);
            location_details = (TextView)v.findViewById(R.id.place_details);
        }
    }
    /**
     * Initializes with a resource for text rows and autocomplete query bounds.
     *
     * @see ArrayAdapter#ArrayAdapter(Context, int)
     */
    public PlaceAutocompleteAdapter(Context context, int resource, GoogleApiClient googleApiClient,
                                    LatLngBounds bounds, AutocompleteFilter filter) {
        super(context, resource);
        this.context = context;
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
        mPlaceFilter = filter;
    }
    /**
     * Sets the bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
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
    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
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

    private ArrayList<PlaceAutocomplete> getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {
            Log.v(LOG_TAG, "Starting autocomplete query for: " + constraint);

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, mPlaceFilter);

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(60, TimeUnit.SECONDS);

            // Confirm that the query completed successfully, otherwise return null
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Toast.makeText(getContext(), "Error contacting API: " + status.toString(),
                        Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Error getting autocomplete prediction API call: success = " +
                        status.isSuccess() + ", error_code = " +
                        getStatusCodeString(status.getStatusCode()));
                autocompletePredictions.release();
                return null;
            }

            Log.v(LOG_TAG, "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");

            // Copy the results into our own data structure, because we can't hold onto the buffer.
            // AutocompletePrediction objects encapsulate the API response (place ID and description).

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                        prediction.getDescription()));
            }

            // Release the buffer now that all data has been copied.
            autocompletePredictions.release();

            return resultList;
        }
        Log.e(LOG_TAG, "Google API client is not connected for autocomplete query.");
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlaceAutocomplete place = getItem(position);
        /* Log.v(LOG_TAG, String.format("Binding Tag to adapter, Name: %s, meaning: %s",
                tag.getTagName(),tag.getTagMeaning())); */
        ViewHolder mViewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_place_suggestion, parent, false);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder)convertView.getTag();
        }

        String title, desc;
        String placeDesc = String.valueOf(place.description);
        int sepPos = placeDesc.indexOf(",");
        if (sepPos != -1)
        {
            title = placeDesc.substring(0,sepPos);
            desc = placeDesc.substring(sepPos + 1);
            desc = desc.trim();
        }
        else
        {
            title = desc = placeDesc;
        }

        mViewHolder.location_title.setText(title);
        mViewHolder.location_details.setText(desc);

        return convertView;
    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;

        PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }
}
