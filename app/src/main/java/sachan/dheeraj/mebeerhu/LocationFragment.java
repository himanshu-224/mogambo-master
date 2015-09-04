package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    OnLocationSelectedListener lcallBack;

    // Container Activity must implement this interface
    public interface OnLocationSelectedListener {
        public void onLocationSelected(String location);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            lcallBack = (OnLocationSelectedListener)activity;
            Log.v(LOG_TAG, "Location Fragment attached to activity successfully");
        }
        catch(ClassCastException ex)
        {
            Log.e(LOG_TAG, "Container activity hasn't implemented OnLocationSelectedListener");
            throw new ClassCastException(activity.toString()
                     + " must implement OnLocationSelectedListener interface");
        }

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
        myLocation = new LatLng(location.getLatitude(),location.getLongitude());

        boundsMyLocation = findLocationBounds(myLocation, distance);

        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                rootView.findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), android.R.layout.simple_list_item_1,
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
        return rootView;
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
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            Log.v(LOG_TAG, "Sending location data to place holder fragment");
            lcallBack.onLocationSelected(String.valueOf(item.description));
            Log.v(LOG_TAG, "Sent location data to place holder fragment");

            Toast.makeText(getActivity(), "Clicked: " + item.description,
                    Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Called getPlaceById to get Place details for " + item.placeId);
            getFragmentManager().popBackStack(getString(R.string.fragment_location), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(LOG_TAG, "onConnected successful, setting up Adapter");
    }

    @Override
    public void onConnectionSuspended(int i)  {
        Log.e(LOG_TAG,"Google Connection suspended, attempting to re-connect");
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
