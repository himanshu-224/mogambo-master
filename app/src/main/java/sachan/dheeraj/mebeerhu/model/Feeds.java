package sachan.dheeraj.mebeerhu.model;

        import java.util.ArrayList;

/**
 * Created by naveen.goel on 01/08/15.
 */
public class Feeds extends ArrayList<Post> {
    public static Feeds feedsBuilder() {
        Feeds feeds = new Feeds();
        for (int k = 0; k < 10; k++) {
            TagArrayList tagArrayList = new TagArrayList();
            for (int j = 0; j < 20; j++) {
                tagArrayList.add(new Tag("tag" + j, "meaning" + j, j % 2 == 0 ? Tag.TYPE_NOUN : Tag.TYPE_ADJECTIVE, true));
            }
            Post post = new Post(String.valueOf(k), "dheeraj", "sachan", "Dheeraj Sachan", "domlur", System.currentTimeMillis(), "Rs", 100.0f, "https://secure.static.tumblr.com/90b30b74c5d4c98ab35024137993f1b0/f9ylzrf/qIonnmkiq/tumblr_static_tumblr_static_etrbfs8y6w0k48kccg0oo0g08_640.jpg", 23434234L, 2L, 2L, tagArrayList, null, "bangalore", "karnataka", "india", "http://i.ebayimg.com/00/s/NTAwWDUwMA==/z/4KEAAOSwv0tVFNSo/$_35.JPG");
            feeds.add(post);
        }
        return feeds;
    }
}
