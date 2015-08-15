package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import sachan.dheeraj.mebeerhu.model.Feeds;
import sachan.dheeraj.mebeerhu.model.Post;
import sachan.dheeraj.mebeerhu.viewHolders.PostViewHolder;


public class PostListFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    public static final int PLACE_PICKER_REQUEST = 100;

    private ListView listView;
    private ProgressBar progressBar;

    public PostListFragment() {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                String dataa = JsonHandler.stringifyNormal(place);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }
    GoogleApiClient mGoogleApiClient;
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        listView = (ListView) view.findViewById(R.id.list_view);
        progressBar = (ProgressBar) view.findViewById(R.id.loader);

        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getActivity(),CreatePostActivityNew.class);
                startActivity(intent);*/
              /*  try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                }catch (Exception e){
                    Log.e("", "");
                }*/
                 mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .addApi(Places.GEO_DATA_API)
                        .enableAutoManage(getActivity(), 0, PostListFragment.this)
                        .addConnectionCallbacks(PostListFragment.this)
                        .build();
            }
        });

        new AsyncTask<Void, Void, Feeds>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Feeds feeds) {
                ArrayAdapter<Post> postArrayAdapter = new ArrayAdapter<Post>(getActivity(), 0, feeds) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        Post post = getItem(position);
                        PostViewHolder postViewHolder;
                        if (convertView == null) {
                            convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_post, null);
                            postViewHolder = PostViewHolder.getInstance(convertView, getActivity());
                            convertView.setTag(postViewHolder);
                        } else {
                            postViewHolder = (PostViewHolder) convertView.getTag();
                        }
                        postViewHolder.getPosterNameTextView().setText(post.getParentUsername());
                        if (post.getAccompaniedWith() != null && post.getAccompaniedWith().size() > 0) {
                            postViewHolder.getWithTextView().setText("with");
                            postViewHolder.getxOthersTextView().setText(post.getAccompaniedWith().size() + " others");
                        } else {
                            postViewHolder.getWithTextView().setVisibility(View.GONE);
                            postViewHolder.getxOthersTextView().setVisibility(View.GONE);
                        }

                        postViewHolder.getLocationTextView().setText(post.getPostLocation());
                        postViewHolder.getLikesTextView().setText(post.getAggregatedVoteCount() + " likes");
                        postViewHolder.setPost(post);
                        postViewHolder.loadTagsInThreeLines(getActivity());
                        postViewHolder.initAndLoadImages(getContext());
                        return convertView;
                    }
                };

                listView.setAdapter(postArrayAdapter);
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            protected Feeds doInBackground(Void... params) {
                if (true) return Feeds.feedsBuilder();
                String feedString = HttpAgent.get(UrlConstants.GET_FEED, getActivity());
                Feeds feeds = JsonHandler.parseNormal(feedString, Feeds.class);
                if (feeds == null) {
                    throw new RuntimeException("json parse failed");
                } else {
                    return feeds;
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.e("","");
        final PendingResult<AutocompletePredictionBuffer> results =
                Places.GeoDataApi
                        .getAutocompletePredictions(mGoogleApiClient, "paratha plaza koramangla",
                                new LatLngBounds(
                                        new LatLng(-90,-180), new LatLng(90, 180)), null);
        // Wait for predictions, set the timeout.
        new AsyncTask<Void,Void,Void>() {
            AutocompletePredictionBuffer autocompletePredictions;
            @Override
            protected Void doInBackground(Void... params) {
                 autocompletePredictions = results.await(60, TimeUnit.SECONDS);
            return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
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
                int k = autocompletePredictions.getCount();
                ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
                while (iterator.hasNext()) {
                    AutocompletePrediction prediction = iterator.next();
                    String s = prediction.getDescription();
                    Log.e("","");
                }
                // Buffer release
                autocompletePredictions.release();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("","");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("","");
    }
}
