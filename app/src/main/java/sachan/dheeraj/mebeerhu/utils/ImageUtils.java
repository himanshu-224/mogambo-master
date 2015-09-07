package sachan.dheeraj.mebeerhu.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

/**
 * Created by dheeraj on 8/4/2015.
 */
public class ImageUtils {
    private static final String LOG_TAG = ImageUtils.class.getSimpleName();

    private static Hashtable<String,Bitmap> stringBitmapHashtable = new Hashtable<String,Bitmap>();

    public static Bitmap getBitmapFromUrl(String url) {
        try {
            if(stringBitmapHashtable.get(url) != null){
                return stringBitmapHashtable.get(url);
            }

            InputStream in = new URL(url).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            stringBitmapHashtable.put(url,bitmap);
            return bitmap;
        }catch (Exception e){
            Log.e(LOG_TAG, "Exception in loading image: " + e.getMessage());
            return null;
        }
    }
}
