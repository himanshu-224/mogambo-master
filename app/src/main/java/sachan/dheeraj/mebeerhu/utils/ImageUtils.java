package sachan.dheeraj.mebeerhu.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by dheeraj on 8/4/2015.
 */
public class ImageUtils {
    private static final String TAG = ImageUtils.class.getSimpleName();

    private static Hashtable<String,Bitmap> stringBitmapHashtable = new Hashtable<>();

    public static Bitmap getBitmapFromUrl(String url) {
        try {
            if(stringBitmapHashtable.contains(url)){
                return stringBitmapHashtable.get(url);
            }
            InputStream in = new URL(url).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            stringBitmapHashtable.put(url,bitmap);
            return bitmap;
        }catch (Exception e){
            return null;
        }
    }
}
