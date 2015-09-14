package sachan.dheeraj.mebeerhu.localData;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import sachan.dheeraj.mebeerhu.LoginActivity;
import sachan.dheeraj.mebeerhu.model.Tag;
import sachan.dheeraj.mebeerhu.model.User;

/**
 * Created by agarwalh on 9/5/2015.
 */
public class AppContract {

    /* Represents a Post */
    public static final String PATH_POST = "post";
    /* Represents a User */
    public static final String PATH_USER = "user";
    /* Represents a tag */
    public static final String PATH_TAG = "tag";
    /* All the tags associated with a given post */
    public static final String PATH_POST_TAG = "post_tag";
    /* All the tags a user is following */
    public static final String PATH_USER_TAG = "user_tag";
    /* Users who accompanied the post creator */
    public static final String PATH_POST_ACCOMPANYING_USER = "post_accompanying_user";
    /* Users whom a given user is following */
    public static final String PATH_USER_FOLLOWING = "post_following";
    /* Followers of a given user */
    public static final String PATH_USER_FOLLOWER = "post_follower";


    /* Inner class that defines the table contents of the User table */
    public static final class UserEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "user";

        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL_ID ="email_id";
        public static final String COLUMN_PROFILE_IMAGE_URL = "profile_image_url";
        public static final String COLUMN_PROFILE_IMAGE_FILENAME = "profile_image_filename";
        public static final String COLUMN_HASH_PASSWORD = "hash_password";
        public static final String COLUMN_USER_SCORE = "user_score";
        public static final String COLUMN_FOLLOWERS_COUNT = "followers_count";
        public static final String COLUMN_FOLLOWING_COUNT = "following_count";
        public static final String COLUMN_TAG_FOLLOW_COUNT = "tag_follow_count";
        public static final String COLUMN_POST_COUNT = "post_count";
        public static final String COLUMN_USER_VERIFIED = "user_verified";
        public static final String COLUMN_BLOCKED = "blocked";
        public static final String COLUMN_USER_EMAIL_VERIFIED = "user_email_verified";
    }

    /* Inner class that defines the table contents of the Tag table */
    public static final class TagEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "tag";

        public static final String COLUMN_TAG_NAME = "tag_name";
        public static final String COLUMN_TAG_MEANING = "tag_meaning";
        public static final String COLUMN_TYPE_ID ="type_id";
        public static final String COLUMN_APPROVED = "approved";
    }

    /* Inner class that defines the table contents of the Post table */
    public static final class PostEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "post";

        public static final String COLUMN_POST_ID = "post_id";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PARENT_USERNAME = "parent_username";
        public static final String COLUMN_POST_LOCATION = "post_location";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_PRICE_CURRENCY = "price_currency";
        public static final String COLUMN_POST_PRICE = "post_price";
        public static final String COLUMN_POST_IMAGE_URL = "post_image_url";
        public static final String COLUMN_LOCATION_GEOCODE = "location_geocode";
        public static final String COLUMN_AGGREGATED_VOTE_COUNT ="aggregated_vote_count";
        public static final String COLUMN_RETWEET_COUNT = "retweet_count";

        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_COUNTRY = "country";
    }

    public static final class PostTagEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "post_tag";

        public static final String COLUMN_POST_ID = "post_id";
        public static final String COLUMN_TAG_NAME = "tag_name";
    }

    public static final class UserTagEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "user_tag";

        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_TAG_NAME = "tag_name";
    }

    public static final class PostAccompanyingUserEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "post_accompanying_user";

        public static final String COLUMN_POST_ID = "post_id";
        public static final String COLUMN_ACCOMPANYING_USERNAME = "accompanying_username";

    }

    public static final class UserFollowingEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "user_following";

        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_USERNAME_FOLLOWING = "username_following";

    }

    public static final class UserFollowerEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "user_follower";

        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_USERNAME_FOLLOWER = "username_follower";

    }

    /* Inner class that defines the table contents of the Tag table */
    public static final class SinglePostTagEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "new_post_tag";

        public static final String COLUMN_TAG_NAME = "tag_name";
        public static final String COLUMN_TAG_MEANING = "tag_meaning";
        public static final String COLUMN_TYPE_ID ="type_id";
        public static final String COLUMN_APPROVED = "approved";
    }

}
