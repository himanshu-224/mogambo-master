package sachan.dheeraj.mebeerhu.globalData;

import java.util.HashMap;
import java.util.Map;

import sachan.dheeraj.mebeerhu.model.AppLocation;
import sachan.dheeraj.mebeerhu.model.Tag;

/**
 * Created by agarwalh on 9/8/2015.
 */
public class CommonData {
    public static Map<String, Tag> tags = new HashMap<>();
    public static Map<String, Tag> followedTags = new HashMap<>();
    public static Map<String, AppLocation> locations = new HashMap<>();
    public static final int TAKE_PICTURE = 1;
    public static final int PICK_FROM_GALLERY = 2;
}
