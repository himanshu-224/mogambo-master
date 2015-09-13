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

    public int userResponse = DISMISS_POPUP;

    private static final String LOG_TAG = LocationDialogFragment.class.getSimpleName();

    static LocationDialogFragment newInstance(String locName, String locDescription) {
        LocationDialogFragment instance = new LocationDialogFragment();
        Bundle args = new Bundle();
        args.putString("locationName", locName);
        args.putString("locationDescription", locDescription);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        Log.v(LOG_TAG, "OnDismiss Location popup dismiss, user selected: " + userResponse);
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

        title.setText(args.getString("locationName"));
        description.setText(args.getString("locationDescription"));

        return dialog;
    }
}
