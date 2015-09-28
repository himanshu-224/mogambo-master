package sachan.dheeraj.mebeerhu;

/**
 * Created by naveen.goel on 12/07/15.
 */
public class UrlConstants {
    //private static final String BASE_URL = "http://ec2-52-3-131-55.compute-1.amazonaws.com:39112";
    private static final String BASE_URL = "http://52.3.131.55:39112/";
    public static final String SIGN_UP_URL = BASE_URL + "auth/signup";
    public static final String LOGIN_URL = BASE_URL + "auth/login";
    public static final String RESET_PASSWORD_URL = BASE_URL + "auth/reset";
    public static final String AUTHENTICATE_URL = BASE_URL + "auth/verifyToken";
    public static final String LOGIN_FACEBOOK = BASE_URL + "auth/loginFacebook";
    public static final String LOGIN_GOOGLE = BASE_URL + "auth/loginGoogle";

    public static final String GET_TRENDY_TAGS_URL = BASE_URL + "tag/getTrendyTags";

    public static final String FOLLOW_TAGS_URL = BASE_URL + "tag/followTag";
    public static final String GET_FEEDS_URL = BASE_URL + "feed/getFeed";

    public static final String GET_EPOCH_TIME = BASE_URL + "checkServer/epochTime";

    /* URLs associated with creating a new post */
    /* This will get the suggestions from server for the next tags to suggest the user to based on the
     * current tag list and the characters entered by user for this tag */
    public static final String GET_TAG_SUGGESTION_URL = BASE_URL + "newPost/getTagSuggestions";
    /* This needs to be called every time the user updates the current Tag list either through
     * adding a tag or deleting a tag.
     * The information will be sent incrementally to the server with say 2 query params, add and remove
     * which will indicate which tag(s) have been added/removed in the latest operation */
    public static final String USER_SELECTED_TAG_URL = BASE_URL + "newPost/tagFeedback";

    /* Send all the data for the post to server including image, location, tags and other supplementary information */
    public static final String CREATE_POST_URL = BASE_URL + "newPost/createPost";



}
