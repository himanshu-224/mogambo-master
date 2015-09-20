package sachan.dheeraj.mebeerhu.utils;

import java.math.BigInteger;

/**
 * Created by agarwalh on 9/19/2015.
 */
public class Utils {

    public static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
    }

    public static String getKeyFromDrawable(int resId)
    {
        String key = "app_icon_";
        key += String.valueOf(resId);
        return key;
    }
}
