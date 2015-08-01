package sachan.dheeraj.mebeerhu.model;

import java.util.ArrayList;

/**
 * Created by naveen.goel on 01/08/15.
 */
public class Post {


    private String postId;
    private String username;
    private String parentUsername;
    private String postLocation;
    private Long timestamp;
    private String priceCurrency;
    private Long postPrice;
    private String postImageURL;
    private Long locationGeoCode;
    private Long aggregatedVoteCount;
    private Long retweetCount;
    private ArrayList<Tag> tagList;
    private ArrayList<User> accompaniedWith;
    private String city;
    private String state;
    private String country;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getParentUsername() {
        return parentUsername;
    }

    public void setParentUsername(String parentUsername) {
        this.parentUsername = parentUsername;
    }

    public String getPostLocation() {
        return postLocation;
    }

    public void setPostLocation(String postLocation) {
        this.postLocation = postLocation;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public Long getPostPrice() {
        return postPrice;
    }

    public void setPostPrice(Long postPrice) {
        this.postPrice = postPrice;
    }

    public String getPostImageURL() {
        return postImageURL;
    }

    public void setPostImageURL(String postImageURL) {
        this.postImageURL = postImageURL;
    }

    public Long getLocationGeoCode() {
        return locationGeoCode;
    }

    public void setLocationGeoCode(Long locationGeoCode) {
        this.locationGeoCode = locationGeoCode;
    }

    public Long getAggregatedVoteCount() {
        return aggregatedVoteCount;
    }

    public void setAggregatedVoteCount(Long aggregatedVoteCount) {
        this.aggregatedVoteCount = aggregatedVoteCount;
    }

    public Long getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(Long retweetCount) {
        this.retweetCount = retweetCount;
    }

    public ArrayList<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(ArrayList<Tag> tagList) {
        this.tagList = tagList;
    }

    public ArrayList<User> getAccompaniedWith() {
        return accompaniedWith;
    }

    public void setAccompaniedWith(ArrayList<User> accompaniedWith) {
        this.accompaniedWith = accompaniedWith;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
