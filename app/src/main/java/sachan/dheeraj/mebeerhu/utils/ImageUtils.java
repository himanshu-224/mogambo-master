package sachan.dheeraj.mebeerhu.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by dheeraj on 8/4/2015.
 */
public class ImageUtils {
    private static final String TAG = ImageUtils.class.getSimpleName();

    public static Bitmap getBitmapFromUrl(Context context,String url) {
        try {
            InputStream in = new URL(url).openStream();
            return BitmapFactory.decodeStream(in);
        }catch (Exception e){
            return null;
        }
    }
}
