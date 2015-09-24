package sachan.dheeraj.mebeerhu.model;

import java.util.ArrayList;

/**
 * Created by agarwalh on 9/24/2015.
 */
public class ServerFeed {
    private String postId;
    private String username;
    private String parentUsername;
    private int timestamp;  /* Need to check the data type */
    private String priceCurrency;
    private String postPrice;
    private String postImageURL;
    private String locationGeoCode;
    private int aggregatedVoteCount;
    private int retweetCount;
    private ArrayList<Tag> tagList;
    private ArrayList<String> accompaniedWith;
    private String postLocation;
    private Location location;
    private LatLong latLong;
}
