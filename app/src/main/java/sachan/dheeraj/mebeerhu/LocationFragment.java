package sachan.dheeraj.mebeerhu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * Created by agarwalh on 8/28/2015.
 */
public class LocationFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private final static String LOG_TAG = LocationFragment.class.getSimpleName();

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private TextView mPlaceDetailsText;

    private TextView mPlaceDetailsAttribution;

    //Location of mindspace, hyderabad - LatLng(17.439200, 78.377240)
    private static LatLng myLocation;
    private static final double distance = 5.0;
    private static LatLngBounds boundsMyLocation;

    //private static final String KEY_PLACE_ID = "bitmap";
    //private static final String KEY_PLACE_DETAILS = "imageTaken";

    public String placeId;
    public String placeDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "OnCreateView called for Location Fragment");
        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
        else {
            /* Location of mindspace hyderabad */
            myLocation = new LatLng(17.439200, 78.377240);
            Log.e(LOG_TAG, "Could not obtain current location, using default values");
        }

        boundsMyLocation = findLocationBounds(myLocation, distance);

        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                rootView.findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), R.layout.list_item_place_suggestion,
                mGoogleApiClient, boundsMyLocation, null);
        mAutocompleteView.setAdapter(mAdapter);

        // Set up the 'clear text' button that clears the text in the autocomplete view
        Button clearButton = (Button) rootView.findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
            }
        });

        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file), Context.MODE_PRIVATE);
        placeId = sharedPref.getString(getString(R.string.post_location_id), null);
        placeDetails = sharedPref.getString(getString(R.string.post_location_description), null);
        if (placeDetails != null)
            mAutocompleteView.setText(placeDetails);

        return rootView;
    }

    /* @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "Saving the state for LocationFragment");
        if (placeDetails != null && placeId != null)
        {
            outState.putString(KEY_PLACE_ID, placeId);
            outState.putString(KEY_PLACE_DETAILS, placeDetails);
            super.onSaveInstanceState(outState);
        }
    } */

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "OnResume for Location fragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "OnPause for Location fragment");
        if (placeId != null && placeDetails != null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences(
                    getString(R.string.preference_file), Context.MODE_PRIVATE);
            SharedPreferences.Editor prefEdit = sharedPref.edit();
            prefEdit.putString(getString(R.string.post_location_id), placeId);
            prefEdit.putString(getString(R.string.post_location_description), placeDetails);
            prefEdit.commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "OnStart called, connecting with Google API");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        Log.i(LOG_TAG, "OnStop called, Disconnected from Google API");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy called, Disconnected from Google API");
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            placeId = String.valueOf(item.placeId);
            placeDetails = String.valueOf(item.description);
            Log.i(LOG_TAG, String.format("Selected Place with Id: %s, details: %s ", placeId, placeDetails));

            View focusView = getActivity().getCurrentFocus();
            if (focusView != null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
            mAutocompleteView.clearFocus();
            mAutocompleteView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAutocompleteView.scrollTo(0, 0);
                }
            }, 200);
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(LOG_TAG, "onConnected successful, setting up Adapter");
    }

    @Override
    public void onConnectionSuspended(int i)  {
        Log.e(LOG_TAG, "Google Connection suspended, attempting to re-connect");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(LOG_TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getActivity(),
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    public LatLngBounds findLocationBounds(LatLng loc, double distance)
    {
        double radius = 6378.137;
        double angDistance =  distance/radius;
        double radAngleNE = toRadians(45.0);
        double radAngleSW = toRadians(225);
        double radLat = toRadians(loc.latitude);
        double radLong = toRadians(loc.longitude);
        double latNE, longNE, latSW, longSW;

        LatLng boundNE, boundSW;
        LatLngBounds locationBounds;

        float results[] = new float[10];

        Log.v(LOG_TAG, "Calculating location bounds");

        /* Formulae for calculating the new latitude and longitude.
           $LatB = asin(sin(rLat) * cos(rAngDist) +
                        cos(rLat) * sin(rAngDist) * cos(rBearing));

           $LonB = $rLon + atan2(sin(rBearing) * sin(rAngDist) * cos(rLat),
                           cos(rAngDist) - sin(rLat) * sin(rLatB));
        */

        /* North East bound */
        latNE = asin(sin(radLat)*cos(angDistance) + cos(radLat)*sin(angDistance)*cos(radAngleNE));
        longNE = radLong + atan2( sin(radAngleNE)*sin(angDistance)* cos(radLat),
                cos(angDistance) - sin(radLat)*sin(latNE));
        /* South West bound */
        latSW = asin(sin(radLat)*cos(angDistance) + cos(radLat)*sin(angDistance)*cos(radAngleSW));
        longSW = radLong + atan2( sin(radAngleSW)*sin(angDistance)* cos(radLat),
                cos(angDistance) - sin(radLat)*sin(latSW));

        latNE = toDegrees(latNE);
        longNE = toDegrees(longNE);
        latSW = toDegrees(latSW);
        longSW = toDegrees(longSW);

        Log.v(LOG_TAG, "Current Location - lat: "+loc.latitude+", long: "+loc.longitude);
        Log.v(LOG_TAG, "Distance in kms in SW and NE directions : "+distance);

        Log.v(LOG_TAG, "North East bound - lat: "+latNE+", long: "+longNE);
        Log.v(LOG_TAG, "South West bound - lat: "+latSW+", long: "+longSW);

        boundNE = new LatLng(latNE, longNE);
        boundSW = new LatLng(latSW, longSW);

        locationBounds = new LatLngBounds(
                boundSW, boundNE);
        try {
            Location.distanceBetween(loc.latitude, loc.longitude, latNE, longNE, results);
            Log.v(LOG_TAG, "Distance between centre and NE Bound in metres: " + results[0]);

            Location.distanceBetween( latSW, longSW, loc.latitude, loc.longitude, results);
            Log.v(LOG_TAG, "Distance between centre and SW Bound in metres: " + results[0]);
        }
        catch (IllegalArgumentException	ex)
        {
            Log.v(LOG_TAG, "Exception while trying to calculate distance : " + ex.getMessage());
        }

        return locationBounds;
    }
}
