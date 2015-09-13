package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import sachan.dheeraj.mebeerhu.globalData.CommonData;
import sachan.dheeraj.mebeerhu.model.Tag;

/**
 * Created by agarwalh on 9/8/2015.
 */
public class LocationDialogFragment extends DialogFragment {

    public static final int DISMISS_POPUP = 0;
    public static final int SEARCH_LOCATION = 1;

    public int userResponse = DISMISS_POPUP;
    public String searchLocation = "";

    private static final String LOG_TAG = LocationDialogFragment.class.getSimpleName();

    final View.OnClickListener DIALOG_LOCATION_CLICK_LISTENER = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView tv = (TextView) getDialog().findViewById(R.id.location_title);
            String locationName = String.valueOf(tv.getText());
            int res = v.getId();
            Log.v(LOG_TAG, String.format("location %s, res_id %d", locationName, res));
            switch (res) {
                case R.id.search_location_icon:
                case R.id.search_location: {
                    Log.v(LOG_TAG, "Location dialog: Clicked Search for Location = " + locationName);
                    userResponse = SEARCH_LOCATION;
                    searchLocation = locationName;
                    getDialog().dismiss();
                    Log.v(LOG_TAG, "Dismissed dialog: starting location search");
                    break;
                }
                default:
                    Log.v(LOG_TAG, "Location dialog: No action needs to taken");

            }
        }
    };

    private onLocationSearchedListener locationCallBack;

    // Container Activity must implement this interface
    public interface onLocationSearchedListener {
        public void onLocationSearch(String locationName);
    }

    static LocationDialogFragment newInstance(String locName, String locDescription) {
        LocationDialogFragment instance = new LocationDialogFragment();
        Bundle args = new Bundle();
        args.putString("locationName", locName);
        args.putString("locationDescription", locDescription);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            locationCallBack = (onLocationSearchedListener)activity;
            Log.v(LOG_TAG, "LocationSearch Fragment attached to activity successfully");
        }
        catch(ClassCastException ex)
        {
            Log.e(LOG_TAG, "Container activity hasn't implemented LocationSearch Fragment");
            throw new ClassCastException(activity.toString()
                    + " must implement OnTagSearchListener interface");
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        Log.v(LOG_TAG, "OnDismiss Location popup dismiss, user selected: " + userResponse);
        if (userResponse == SEARCH_LOCATION) {
            locationCallBack.onLocationSearch(searchLocation);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        Bundle args = getArguments();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_window_location);

        TextView title = (TextView) dialog.findViewById(R.id.location_title);
        TextView description = (TextView) dialog.findViewById(R.id.location_description);
        ImageView searchIcon = (ImageView) dialog.findViewById(R.id.search_location_icon);
        TextView searchTV = (TextView) dialog.findViewById(R.id.search_location);

        title.setText(args.getString("locationName"));
        description.setText(args.getString("locationDescription"));
        searchIcon.setOnClickListener(DIALOG_LOCATION_CLICK_LISTENER);
        searchTV.setOnClickListener(DIALOG_LOCATION_CLICK_LISTENER);

        return dialog;
    }
}
