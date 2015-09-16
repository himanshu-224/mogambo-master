package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import sachan.dheeraj.mebeerhu.globalData.CommonData;
import sachan.dheeraj.mebeerhu.localData.AppDbHelper;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * Created by agarwalh on 8/28/2015.
 */
public class CreatePostFragment extends Fragment {

    private final static String LOG_TAG = CreatePostFragment.class.getSimpleName();

    private static final int REQUEST_TAKE_PICTURE = 100;
    private static final int REQUEST_PICK_FROM_GALLERY = 101;
    private int picMethod = 0;
    private ImageView mainImageView;

    private Bitmap imageBitmap = null;
    private boolean picTaken = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "OnCreateView called for CreatePost Fragment");
        View view  = inflater.inflate(R.layout.fragment_create_post, container, false);
        mainImageView = (ImageView)view.findViewById(R.id.main_image);
        Button button_camera = (Button)view.findViewById(R.id.button_camera);
        Button button_gallery = (Button)view.findViewById(R.id.button_gallery);

        CreatePostActivity activity = (CreatePostActivity)getActivity();
        picMethod = activity.get_picMethod();

        button_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
                }
            }
        });

        button_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_PICK_FROM_GALLERY);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "On resume for CreatePost Fragment");
        if (!picTaken) {
            switch (picMethod) {
                case CommonData.TAKE_PICTURE: {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
                    }
                    break;
                }
                case CommonData.PICK_FROM_GALLERY: {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, REQUEST_PICK_FROM_GALLERY);
                    break;
                }
                default:
                    Log.v(LOG_TAG, "Something messed up, picture action code = " + picMethod);
            }
        }
        if (picTaken && imageBitmap != null) {
            Log.v(LOG_TAG, "OnResume called and setting image again");
            setImage();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, String.format("onActivityResult, requestCode = %d, result = %d",
                requestCode, resultCode));
        CreatePostActivity activity = (CreatePostActivity)getActivity();
        if (requestCode == REQUEST_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            Log.v(LOG_TAG, "IMAGE Captured successfully");
            picTaken = true;
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
        } else if (requestCode == REQUEST_PICK_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Log.v(LOG_TAG, "IMAGE Selected from Gallery successfully");
            picTaken = true;
            Uri galleryImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(galleryImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            imageBitmap = BitmapFactory.decodeFile(cursor.getString(columnIndex));
            cursor.close();
        }
        else {
            if (activity.get_origin_feeds()) {
                Log.v(LOG_TAG, "Failure in capturing image, ending activity");
                getActivity().finish();
            }
            else {
                Log.v(LOG_TAG, "Failure in capturing image, retaining activity");
            }
        }
        activity.set_origin_feeds(false);
    }

    public void setImage()
    {
        Log.v(LOG_TAG, "Applying image bitmap");
        mainImageView.setImageBitmap(imageBitmap);
    }
}