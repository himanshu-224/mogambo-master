package sachan.dheeraj.mebeerhu.viewHolders;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sachan.dheeraj.mebeerhu.R;
import sachan.dheeraj.mebeerhu.cache.MemDiskCache;
import sachan.dheeraj.mebeerhu.utils.ImageUtils;
import sachan.dheeraj.mebeerhu.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by agarwalh on 9/24/2015.
 */
public class LikesViewHolder{

    CircleImageView mCircleImageView;
    TextView userName;

    private final static float PROFILE_IMAGE_WIDTH = 40.0f;
    private final static float PROFILE_IMAGE_HEIGHT = 40.0f;

    private static final String LOG_TAG = LikesViewHolder.class.getSimpleName();

    int profileImageH, profileImageW;
    private ImageLoaderAsyncTask imageLoaderAsyncTask;

    public LikesViewHolder(View view)
    {
        mCircleImageView = (CircleImageView) view.findViewById(R.id.circular_image);
        userName = (TextView) view.findViewById(R.id.like_user_name);
    }

    public TextView getUserName() {
        return userName;
    }

    public void setUserName(TextView userName) {
        this.userName = userName;
    }

    public void loadAndSetProfileImage(String userImageUrl, Context context) {
        if (imageLoaderAsyncTask != null) {
            if (!imageLoaderAsyncTask.getCurrentImageUrl().equals(userImageUrl)) {
                imageLoaderAsyncTask.cancel(true);
            }
        }

        profileImageH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PROFILE_IMAGE_HEIGHT, context.getResources().getDisplayMetrics());
        profileImageW = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PROFILE_IMAGE_WIDTH, context.getResources().getDisplayMetrics());

        Bitmap profileBitmap = null;

        MemDiskCache mCache = MemDiskCache.getInstance();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (mCache!= null){
                profileBitmap =  mCache.getBitmapFromMemCache(Utils.toHex(md.digest(userImageUrl.getBytes())));
            }
        }
        catch (NoSuchAlgorithmException e) {
            Log.e(LOG_TAG, "MD5 algorithm not recognized : " + e.getMessage());
        }

        try {
            if (profileBitmap != null) {
                mCircleImageView.setImageBitmap(profileBitmap);
            }
            else{
                imageLoaderAsyncTask = new ImageLoaderAsyncTask(userImageUrl);
                imageLoaderAsyncTask.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ImageLoaderAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        private String userProfileUrl;

        private ImageLoaderAsyncTask(String userProfileUrl) {
            this.userProfileUrl = userProfileUrl;
        }

        public String getCurrentImageUrl() {
            return userProfileUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap profileBitmap = null;
            if (!isCancelled()) {
                Log.v(LOG_TAG, "ProfileImage Url: " + userProfileUrl );
                profileBitmap = ImageUtils.getBitmapFromUrl(userProfileUrl, profileImageH, profileImageW);
            }
            return profileBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap profileBitmap) {
            if (!isCancelled()) {
                try {
                    mCircleImageView.setImageBitmap(profileBitmap);
                }
                catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
