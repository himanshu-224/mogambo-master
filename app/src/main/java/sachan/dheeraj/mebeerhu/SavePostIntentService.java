package sachan.dheeraj.mebeerhu;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import sachan.dheeraj.mebeerhu.customFlowLayout.FlowLayout;
import sachan.dheeraj.mebeerhu.model.Tag;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SavePostIntentService extends IntentService {

    public static final String LOG_TAG = SavePostIntentService.class.getSimpleName();
    private static final String ACTION_SEND_POST_TO_SERVER = "SavePostServer";

    private static final String PARAM_LOCATIONID = "locationId";
    private static final String PARAM_LOCATIONDESC = "locationDesc";
    private static final String PARAM_IMAGEPATH = "imagePath";
    private static final String PARAM_TAGLIST = "tagList";

    private static Context mContext;
    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSavePostServer(Context context, String param1, String param2, String param3, ArrayList<Tag> tags) {
        mContext = context;
        Intent intent = new Intent(context, SavePostIntentService.class);
        intent.setAction(ACTION_SEND_POST_TO_SERVER);
        intent.putExtra(PARAM_LOCATIONID, param1);
        intent.putExtra(PARAM_LOCATIONDESC, param2);
        intent.putExtra(PARAM_IMAGEPATH, param3);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM_TAGLIST, tags);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    public SavePostIntentService() {
        super("SavePostIntentService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "Service destroyed");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(LOG_TAG,  "New Intent received by service");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_POST_TO_SERVER.equals(action)) {
                final String param1 = intent.getStringExtra(PARAM_LOCATIONID);
                final String param2 = intent.getStringExtra(PARAM_LOCATIONDESC);
                final String param3 = intent.getStringExtra(PARAM_IMAGEPATH);
                Bundle bundle = intent.getExtras();
                ArrayList<Tag> tags =
                        (ArrayList<Tag>)bundle.getSerializable(PARAM_TAGLIST);
                handleActionSavePostServer(param1, param2, param3, tags);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSavePostServer(String placeId, String placeDetails, String imagePath, ArrayList<Tag> tags) {
        Bitmap imageBitmap;
        imageBitmap = imageBitmap = BitmapFactory.decodeFile(imagePath);

        Log.v(LOG_TAG, String.format("Service data - PlaceId : %s, PlaceDetails: %s, ImagePath: %s",
                placeId, placeDetails, imagePath));
        for(Tag tag:tags)
        {
            Log.v(LOG_TAG, String.format("Tag name: %s, meaning: %s, type: %d, approved: %b ",
                    tag.getTagName(), tag.getTagMeaning(), tag.getTypeId(), tag.isApproved()));
        }
        //Toast.makeText(mContext, String.valueOf("Post saved to server"), Toast.LENGTH_SHORT).show();
    }
}
