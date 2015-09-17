package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    private static  final int REQUEST_CROP_IMAGE = 200;
    private int picMethod = 0;
    private ImageView mainImageView;

    private String mCurrentPhotoPath;

    private String curImagePath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

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
                dispatchTakePictureIntent(REQUEST_TAKE_PICTURE);
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
                    dispatchTakePictureIntent(REQUEST_TAKE_PICTURE);
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
    public void onPause() {
        super.onPause();
        if (curImagePath != null) {
            Log.v(LOG_TAG, "OnPause called for CreatePost Fragment");
            SharedPreferences sharedPref = getActivity().getSharedPreferences(
                    getString(R.string.preference_file), Context.MODE_PRIVATE);
            SharedPreferences.Editor prefEdit = sharedPref.edit();
            prefEdit.putString(getString(R.string.post_image_path), curImagePath);
            prefEdit.commit();
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
            if (!cropImage(mCurrentPhotoPath, mCurrentPhotoPath))
            {
                getCameraImage();
            }

        } else if (requestCode == REQUEST_PICK_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Log.v(LOG_TAG, "IMAGE Selected from Gallery successfully");
            picTaken = true;
            Uri galleryImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(galleryImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            curImagePath = cursor.getString(columnIndex);
            cursor.close();
            Log.v(LOG_TAG, "Gallery image path: " + curImagePath);

            try {
                mCurrentPhotoPath = createImageFile().getAbsolutePath();
                Log.v(LOG_TAG, "Cropped image to be saved at: " + mCurrentPhotoPath);
            } catch (IOException e) {
                e.printStackTrace();
                setPic(curImagePath);
                return;
            }

            if (!cropImage(curImagePath, mCurrentPhotoPath ))
            {
                setPic(curImagePath);
            }

            //imageBitmap = BitmapFactory.decodeFile(curImagePath);
        }
        else if (requestCode == REQUEST_CROP_IMAGE && resultCode == Activity.RESULT_OK)
        {
            getCameraImage();
            Log.v(LOG_TAG, "Image cropped successfully");
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

    private boolean cropImage(String src_path, String dest_path)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        Log.v(LOG_TAG, "Created intent for crop app");
        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities( intent, 0 );
        int size = list.size();
        if (size == 0) {
            Log.e(LOG_TAG, "Could not find any image crop app, skipping crop");
            return false;
        }

        // this will open all images in the Gallery
        File f = new File(src_path);
        Uri srcUri = Uri.fromFile(f);
        intent.setData(srcUri);

        intent.putExtra("crop", "true");
        // this defines the aspect ratio
        intent.putExtra("aspectX", 5);
        intent.putExtra("aspectY", 3);
        intent.putExtra("scale", true);
        // true to return a Bitmap, false to directly save the cropped iamge
        intent.putExtra("return-data", false);

        //save output image in uri
        f = new File(dest_path);
        Uri destUri = Uri.fromFile(f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, destUri);

        Intent i        = new Intent(intent);
        ResolveInfo res = list.get(0);
        i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
        startActivityForResult(i, REQUEST_CROP_IMAGE);

        return true;
    }

    /* Dispatch Intent to capture image from camera */
    private void dispatchTakePictureIntent(int actionCode) {

        File f = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            f = createImageFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            Log.v(LOG_TAG,"Image to be saved at: " + mCurrentPhotoPath);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            mCurrentPhotoPath = null;
        }
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, actionCode);
        }
        else
        {
            Log.e(LOG_TAG, "Could not find camera app, unable to take picture");
        }
    }

    /* Take a picture from Camera and save it to SD card */
    private void getCameraImage()
    {
        if (mCurrentPhotoPath != null) {
            setPic(mCurrentPhotoPath);
            galleryAddPic(mCurrentPhotoPath);
            curImagePath = mCurrentPhotoPath;
            mCurrentPhotoPath = null;
        }
    }

    /*Helper functions to get and set images */
    private void setPic(String imagePath)
    {
		/* There isn't enough memory to open up more than a couple camera photos
		 * So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mainImageView.getWidth();
        int targetH = mainImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        imageBitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
    }

    private void galleryAddPic(String Imagepath) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(Imagepath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                    ),
                    getString(R.string.album_name)
            );

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(LOG_TAG, "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        /* String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File filePath = getAlbumDir()+
        return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, getAlbumDir()); */

        String timeStamp = "new_post_image"; /* TEMP to replace image files while testing */
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + JPEG_FILE_SUFFIX;
        File f = new File(getAlbumDir().getPath()+File.separator + imageFileName);
        Log.v(LOG_TAG, "Image File Path: " + f);

        boolean b = f.createNewFile();
        Log.v(LOG_TAG, "Is file created successfully: " + b);
        return f;
    }

    public void setImage()
    {
        Log.v(LOG_TAG, "Applying image bitmap");
        mainImageView.setImageBitmap(imageBitmap);
    }
}