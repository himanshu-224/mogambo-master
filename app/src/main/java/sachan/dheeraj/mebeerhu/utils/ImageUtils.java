package sachan.dheeraj.mebeerhu.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Hashtable;

import sachan.dheeraj.mebeerhu.cache.MemDiskCache;

/**
 * Created by dheeraj on 8/4/2015.
 */
public class ImageUtils {
    private static final String LOG_TAG = ImageUtils.class.getSimpleName();

    public static Bitmap getBitmapFromUrl(String url, int targetH, int targetW) {
        try {
            /* Generate md5 hash to be used as key */
            String key = Utils.toHex(MessageDigest.getInstance("MD5").digest(url.getBytes()));
            Bitmap bitmap = null;
            MemDiskCache mCache = MemDiskCache.getInstance();

            if (mCache != null) {
                bitmap = mCache.getBitmapFromCache(key);
                if(bitmap != null){
                    return bitmap;
                }
            }

            InputStream in = new URL(url).openStream();
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();

            /*bmOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, bmOptions);
            int imageW = bmOptions.outWidth;
            int imageH = bmOptions.outHeight;

            int scaleFactor = 1;
            if ((targetW > 0) || (targetH > 0)) {
                scaleFactor = Math.min(imageW/targetW, imageH/targetH);
            }
            if (scaleFactor < 1)
                scaleFactor = 1;

            in.reset();
    		Set bitmap options to scale the image decode target
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true; */

            bitmap = BitmapFactory.decodeStream(in);

            if (bitmap != null) {
                int imageH = bitmap.getHeight();
                int imageW = bitmap.getWidth();

                Log.v(LOG_TAG, String.format("Image Width: %d, Height: %d", imageW, imageH));

                int scaleFactor = 1;
                if ((targetW > 0) || (targetH > 0)) {
                    scaleFactor = Math.min(imageW/targetW, imageH/targetH);
                }

                Log.v(LOG_TAG, String.format("Target Width: %d, Height: %d, scaleFactor: %d",
                        targetW, targetH, scaleFactor));

                if (scaleFactor > 1)
                {
                    imageW = imageW/scaleFactor;
                    imageH = imageH/scaleFactor;
                    bitmap = Bitmap.createScaledBitmap(bitmap, imageW, imageH, false);
                }

                if (mCache != null) {
                    mCache.addBitmapToCache(key, bitmap);
                }
            }
            return bitmap;
        }catch (Exception e){
            Log.e(LOG_TAG, "Exception in loading image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
