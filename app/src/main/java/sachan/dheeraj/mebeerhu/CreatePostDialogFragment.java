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
public class CreatePostDialogFragment extends DialogFragment {

    public static final int DISMISS_POPUP = 0;
    public static final int TAKE_PICTURE = 1;
    public static final int PICK_FROM_GALLERY = 2;

    public int userResponse = DISMISS_POPUP;

    private static final String LOG_TAG = CreatePostDialogFragment.class.getSimpleName();

    final View.OnClickListener DIALOG_CREATE_POST_CLICK_LISTENER = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int res = v.getId();
            switch (res) {
                case R.id.picture_icon:
                case R.id.take_picture_text: {
                    Log.v(LOG_TAG, "Create post dialog: Clicked Take picture");
                    userResponse = TAKE_PICTURE;
                    getDialog().dismiss();
                    break;
                }
                case R.id.gallery_icon:
                case R.id.open_gallery_text: {
                    Log.v(LOG_TAG, "Create Post dialog: Clicked Pick from gallery");
                    userResponse = PICK_FROM_GALLERY;
                    getDialog().dismiss();
                    break;
                }
                default:
                    Log.v(LOG_TAG, "Create Post dialog: No action needs to taken");

            }
        }
    };

    private onCreatePostDialogListener mCallBack;

    // Container Activity must implement this interface
    public interface onCreatePostDialogListener {
        public void onTakePicture();
        public void onPickFromGallery();
    }

    static CreatePostDialogFragment newInstance() {
        CreatePostDialogFragment instance = new CreatePostDialogFragment();
        return instance;
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        Log.v(LOG_TAG, "OnDismiss CreatePost popup dismiss, user selected: " + userResponse);
        if (userResponse == TAKE_PICTURE) {
            userResponse = DISMISS_POPUP;
            mCallBack.onTakePicture();
        }
        else if(userResponse == PICK_FROM_GALLERY){
            userResponse = DISMISS_POPUP;
            mCallBack.onPickFromGallery();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            mCallBack = (onCreatePostDialogListener)activity;
            Log.v(LOG_TAG, "CreatePost Fragment attached to activity successfully");
        }
        catch(ClassCastException ex)
        {
            Log.e(LOG_TAG, "Container activity hasn't implemented CreatePost Fragment");
            throw new ClassCastException(activity.toString()
                    + " must implement onCreatePostDialogListener interface");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        Bundle args = getArguments();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_create_post);

        ImageView cameraIcon = (ImageView) dialog.findViewById(R.id.picture_icon);
        TextView cameraTV = (TextView) dialog.findViewById(R.id.take_picture_text);
        ImageView galleryIcon = (ImageView) dialog.findViewById(R.id.gallery_icon);
        TextView galleryTV = (TextView) dialog.findViewById(R.id.open_gallery_text);

        cameraIcon.setOnClickListener(DIALOG_CREATE_POST_CLICK_LISTENER);
        cameraTV.setOnClickListener(DIALOG_CREATE_POST_CLICK_LISTENER);
        galleryIcon.setOnClickListener(DIALOG_CREATE_POST_CLICK_LISTENER);
        galleryTV.setOnClickListener(DIALOG_CREATE_POST_CLICK_LISTENER);

        return dialog;
    }
}
