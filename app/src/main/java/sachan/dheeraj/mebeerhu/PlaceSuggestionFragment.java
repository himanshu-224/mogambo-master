package sachan.dheeraj.mebeerhu;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class PlaceSuggestionFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private EditText editText;
    private GoogleApiClient mGoogleApiClient;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    public void onConnected(Bundle bundle) {
        editText.addTextChangedListener(new TextWatcher() {
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
                final PendingResult<AutocompletePredictionBuffer> results =
                        Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, s.toString(),
                                new LatLngBounds(/*new LatLng(-90, -180), new LatLng(90, 180)*/getLatLongFromCurrentWithDistanceMinus(100),getLatLongFromCurrentWithDistancePlus(100)), null);
                // Wait for predictions, set the timeout.
                new AsyncTask<Void, Void, Void>() {
                    AutocompletePredictionBuffer autocompletePredictions;

                    @Override
                    protected Void doInBackground(Void... params) {
                        autocompletePredictions = results.await(60, TimeUnit.SECONDS);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        final com.google.android.gms.common.api.Status status = autocompletePredictions.getStatus();
                        if (!status.isSuccess()) {
                            Toast.makeText(getActivity(), "Error: " + status.toString(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e("", "Error getting place predictions: " + status
                                    .toString());
                            autocompletePredictions.release();
                            return;
                        }
                        Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
                        ArrayList<String> strings = new ArrayList<String>();
                        while (iterator.hasNext()) {
                            AutocompletePrediction prediction = iterator.next();
                            String s = prediction.getDescription();
                            strings.add(s);
                            Log.e("", "");
                        }
                        // Buffer release
                        autocompletePredictions.release();
                        adapter.clear();
                        adapter.addAll(strings);
                        adapter.notifyDataSetChanged();
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });

        editText.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        Log.e("", "");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("", "");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("", "");
    }


    public static PlaceSuggestionFragment newInstance() {
        PlaceSuggestionFragment fragment = new PlaceSuggestionFragment();
        return fragment;
    }

    public PlaceSuggestionFragment() {
    }

    private void findPlace() {
        try {
            GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyB_PosD0J7DDLoNCbk8Gjgxba4iq1kTGvU");
            GeocodingResult[] results = GeocodingApi.geocode(context,
                    "paratha plaza").await();
            System.out.println(results[0].formattedAddress);
        } catch (Exception e) {
            Log.e("", "", e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupClient();
        /*findPlace();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place_suggestion, container, false);
        editText = (EditText) view.findViewById(R.id.edit_text);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(adapter);

        editText.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        return view;
    }

    private volatile boolean done = false;

    private synchronized void setupClient() {
        if (!done) {
            done = true;
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Places.GEO_DATA_API)
                    .enableAutoManage(getActivity(), 0, PlaceSuggestionFragment.this)
                    .addConnectionCallbacks(PlaceSuggestionFragment.this)
                    .addOnConnectionFailedListener(PlaceSuggestionFragment.this)
                    .build();
        }
    }


    private LatLng getLatLongFromCurrentWithDistancePlus(int distance) {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();



     /*   Latitude: 1 deg = 110.574 km
        Longitude: 1 deg = 111.320*cos(latitude) km*/

        double latNew, longNew;

        latNew = latitude + latitude * distance / 110.574;
        longNew = longitude + distance / (11.32 * Math.cos(latNew));

        return new LatLng(latNew, longNew);
    }


    private LatLng getLatLongFromCurrentWithDistanceMinus(int distance) {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();



     /*   Latitude: 1 deg = 110.574 km
        Longitude: 1 deg = 111.320*cos(latitude) km*/

        double latNew, longNew;

        latNew = latitude - latitude * distance / 110.574;
        longNew = longitude - distance / (11.32 * Math.cos(latNew));

        return new LatLng(latNew, longNew);
    }

}
