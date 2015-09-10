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
public class TagDialogFragment extends DialogFragment {

    public static final int DISMISS_POPUP = 0;
    public static final int SEARCH_TAG = 1;
    public static final int FOLLOW_TAG = 2;

    public int userResponse = DISMISS_POPUP;
    public String searchTag = "";

    private static final String LOG_TAG = TagDialogFragment.class.getSimpleName();

    final View.OnClickListener DIALOG_TAG_CLICK_LISTENER = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView tv = (TextView) getDialog().findViewById(R.id.tag_title);
            String tagName = String.valueOf(tv.getText());
            int res = v.getId();
            Log.v(LOG_TAG, String.format("tagname %s, res_id %d", tagName, res));
            switch (res) {
                case R.id.search_tag_icon:
                case R.id.search_tag: {
                    Log.v(LOG_TAG, "Tag dialog: Clicked Search for Tag = " + tagName);
                    //tagCallBack.onTagSearch(tagName);
                   //Log.v(LOG_TAG, "Sent tag Search done event to main activity");
                    userResponse = SEARCH_TAG;
                    searchTag = tagName;
                    getDialog().dismiss();
                    Log.v(LOG_TAG, "Dismissed dialog: starting tag search");
                    break;
                }
                case R.id.follow_tag_icon:
                case R.id.follow_tag: {
                    Log.v(LOG_TAG, "Tag dialog: Clicked Follow for Tag = " + tagName);
                    userResponse = FOLLOW_TAG;
                    /* Todo : Add code to synchronize with database and server here */
                    String followText = String.valueOf(((TextView) getDialog().findViewById(R.id.follow_tag)).getText());
                    if (followText.equals(getString(R.string.follow))) {
                        Tag thisTag = CommonData.tags.get(tagName);
                        if (thisTag == null) {
                            /* Put a tag with empty description and default params */
                            thisTag = new Tag(tagName, "", thisTag.TYPE_ADJECTIVE, false);
                            Log.e(LOG_TAG, "Could not find unfollowed tag in tags cache: " + tagName);
                        }
                        CommonData.followedTags.put(tagName, thisTag);
                    } else if (followText.equals(getString(R.string.unfollow))) {
                        if (CommonData.followedTags.remove(tagName) == null)
                            Log.e(LOG_TAG, "Could not find Followed tag in user tags cache: " + tagName);
                    } else {
                        Log.e(LOG_TAG, "Error: Tag should either be followed/unfollowed: " + followText);
                    }
                    getDialog().dismiss();
                    break;
                }
                default:
                    Log.v(LOG_TAG, "Tag dialog: No action needs to taken");

            }
        }
    };

    private onTagSearchedListener tagCallBack;

    // Container Activity must implement this interface
    public interface onTagSearchedListener {
        public void onTagSearch(String tagName);
    }

    static TagDialogFragment newInstance(String tagName, String tagDescription, boolean isFollowed) {
        TagDialogFragment instance = new TagDialogFragment();
        Bundle args = new Bundle();
        args.putString("tagName", tagName);
        args.putString("tagDesc", tagDescription);
        args.putBoolean("isFollowed", isFollowed);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            tagCallBack = (onTagSearchedListener)activity;
            Log.v(LOG_TAG, "TagSearch Fragment attached to activity successfully");
        }
        catch(ClassCastException ex)
        {
            Log.e(LOG_TAG, "Container activity hasn't implemented TagSearch Fragment");
            throw new ClassCastException(activity.toString()
                    + " must implement OnTagSearchListener interface");
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        Log.v(LOG_TAG, "OnDismiss Tag popup dismiss, user selected: " + userResponse);
        if (userResponse == SEARCH_TAG) {
            tagCallBack.onTagSearch(searchTag);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        Bundle args = getArguments();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_window_tag);

        TextView title = (TextView) dialog.findViewById(R.id.tag_title);
        TextView description = (TextView) dialog.findViewById(R.id.tag_description);
        ImageView searchIcon = (ImageView) dialog.findViewById(R.id.search_tag_icon);
        TextView searchTV = (TextView) dialog.findViewById(R.id.search_tag);
        ImageView followIcon = (ImageView) dialog.findViewById(R.id.follow_tag_icon);
        TextView followTV = (TextView) dialog.findViewById(R.id.follow_tag);

        title.setText(args.getString("tagName"));
        description.setText(args.getString("tagDesc"));
        boolean isFollowed = args.getBoolean("isFollowed");
        if (isFollowed)
            followTV.setText(getString(R.string.unfollow));
        else
            followTV.setText(getString(R.string.follow));
        searchIcon.setOnClickListener(DIALOG_TAG_CLICK_LISTENER);
        searchTV.setOnClickListener(DIALOG_TAG_CLICK_LISTENER);
        followIcon.setOnClickListener(DIALOG_TAG_CLICK_LISTENER);
        followTV.setOnClickListener(DIALOG_TAG_CLICK_LISTENER);

        return dialog;
    }
}
