package sachan.dheeraj.mebeerhu.model;

/**
 * Created by agarwalh on 9/24/2015.
 */

public class TrendyTag {
    private String tagName;
    private int typeId;
    private String tagMeaning;
    private int requestCount;
    private int followedCount;
    private boolean approved;

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public int getFollowedCount() {
        return followedCount;
    }

    public void setFollowedCount(int followedCount) {
        this.followedCount = followedCount;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public String getTagMeaning() {
        return tagMeaning;
    }

    public void setTagMeaning(String tagMeaning) {
        this.tagMeaning = tagMeaning;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("***** Tag Details *****\n");
        sb.append("Name="+getTagName()+"\n");
        sb.append("Id="+getTypeId()+"\n");
        sb.append("Meaning="+getTagMeaning()+"\n");
        sb.append("Count="+getRequestCount()+"\n");
        sb.append("FollowedCount="+getFollowedCount()+"\n");
        sb.append("Approved="+isApproved()+"\n");
        sb.append("*****************************");

        return sb.toString();
    }
}
