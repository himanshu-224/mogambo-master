package sachan.dheeraj.mebeerhu.model;

import java.util.List;

/**
 * Created by naveen.goel on 01/08/15.
 */
public class User {

    private String username;
    private String name;

    private String emailId;

    private String profileImageURL;
    private String profileImageFileName;
    private String hashPassword;
    private double userScore;
    private int followersCount;
    private int followingCount;
    private int tagFollowCount;
    private int postCount;
    private boolean userVerified;
    private boolean blocked;
    private boolean userEmailVerfied;

    private List<String> followersList;
    private List<String> followingList;
    private List<String> tagFollowingList;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getProfileImageFileName() {
        return profileImageFileName;
    }

    public void setProfileImageFileName(String profileImageFileName) {
        this.profileImageFileName = profileImageFileName;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public double getUserScore() {
        return userScore;
    }

    public void setUserScore(double userScore) {
        this.userScore = userScore;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getTagFollowCount() {
        return tagFollowCount;
    }

    public void setTagFollowCount(int tagFollowCount) {
        this.tagFollowCount = tagFollowCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public boolean isUserVerified() {
        return userVerified;
    }

    public void setUserVerified(boolean userVerified) {
        this.userVerified = userVerified;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isUserEmailVerfied() {
        return userEmailVerfied;
    }

    public void setUserEmailVerfied(boolean userEmailVerfied) {
        this.userEmailVerfied = userEmailVerfied;
    }

    public List<String> getFollowersList() {
        return followersList;
    }

    public void setFollowersList(List<String> followersList) {
        this.followersList = followersList;
    }

    public List<String> getFollowingList() {
        return followingList;
    }

    public void setFollowingList(List<String> followingList) {
        this.followingList = followingList;
    }

    public List<String> getTagFollowingList() {
        return tagFollowingList;
    }

    public void setTagFollowingList(List<String> tagFollowingList) {
        this.tagFollowingList = tagFollowingList;
    }
}
