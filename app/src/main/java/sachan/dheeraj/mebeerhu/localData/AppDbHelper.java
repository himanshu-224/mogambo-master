/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sachan.dheeraj.mebeerhu.localData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import sachan.dheeraj.mebeerhu.localData.AppContract.PostEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.UserEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.TagEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.PostTagEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.UserTagEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.PostAccompanyingUserEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.UserFollowingEntry;
import sachan.dheeraj.mebeerhu.localData.AppContract.UserFollowerEntry;

import sachan.dheeraj.mebeerhu.model.Post;
import sachan.dheeraj.mebeerhu.model.Tag;

/**
 * Manages a local database for weather data.
 */
public class AppDbHelper extends SQLiteOpenHelper {

    private final static String LOG_TAG = AppDbHelper.class.getSimpleName();
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "splatter.db";

    public AppDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.v(LOG_TAG, "onCreate for appDbHelper");

        /* Create a table to store a user's details */
        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                PostEntry._ID + " INTEGER PRIMARY KEY," +
                UserEntry.COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                UserEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                UserEntry.COLUMN_EMAIL_ID + " TEXT, " +
                UserEntry.COLUMN_PROFILE_IMAGE_URL + " TEXT, " +
                UserEntry.COLUMN_PROFILE_IMAGE_FILENAME + " TEXT, " +
                UserEntry.COLUMN_HASH_PASSWORD + " TEXT, " +
                UserEntry.COLUMN_USER_SCORE + " INTEGER, " +
                UserEntry.COLUMN_FOLLOWERS_COUNT + " INTEGER, " +
                UserEntry.COLUMN_FOLLOWING_COUNT + " INTEGER, " +
                UserEntry.COLUMN_TAG_FOLLOW_COUNT + " INTEGER, " +
                UserEntry.COLUMN_POST_COUNT + " INTEGER, " +
                UserEntry.COLUMN_USER_VERIFIED + " INTEGER, " +
                UserEntry.COLUMN_BLOCKED + " INTEGER, " +
                UserEntry.COLUMN_USER_EMAIL_VERIFIED + " INTEGER " +
                " );";

        /* Create a table to store a tag */
        final String SQL_CREATE_TAG_TABLE = "CREATE TABLE " + TagEntry.TABLE_NAME + " (" +
                PostEntry._ID + " INTEGER PRIMARY KEY," +
                TagEntry.COLUMN_TAG_NAME + " TEXT UNIQUE NOT NULL, " +
                TagEntry.COLUMN_TAG_MEANING + " TEXT NOT NULL, " +
                TagEntry.COLUMN_TYPE_ID + " INTEGER NOT NULL, " +
                TagEntry.COLUMN_APPROVED + " INTEGER NOT NULL " +
                " );";

        /* Create a table to store a Post */
        final String SQL_CREATE_POST_TABLE = "CREATE TABLE " + PostEntry.TABLE_NAME + " (" +

                PostEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PostEntry.COLUMN_POST_ID + " TEXT UNIQUE NOT NULL, " +
                PostEntry.COLUMN_USERNAME + " TEXT NOT NULL, " +
                PostEntry.COLUMN_PARENT_USERNAME + " TEXT, " +
                PostEntry.COLUMN_POST_LOCATION + " TEXT, " +

                PostEntry.COLUMN_TIMESTAMP + " INTEGER, " +
                PostEntry.COLUMN_PRICE_CURRENCY + " TEXT, " +

                PostEntry.COLUMN_POST_PRICE + " REAL, " +
                PostEntry.COLUMN_POST_IMAGE_URL + " TEXT NOT NULL, " +
                PostEntry.COLUMN_LOCATION_GEOCODE + " TEXT, " +
                PostEntry.COLUMN_AGGREGATED_VOTE_COUNT + " INTEGER, " +
                PostEntry.COLUMN_RETWEET_COUNT + " INTEGER, " +
                PostEntry.COLUMN_CITY + " TEXT, " +
                PostEntry.COLUMN_STATE + " TEXT, " +
                PostEntry.COLUMN_COUNTRY + " TEXT, " +

                /* Set up the username column as a foreign key to user table */
                " FOREIGN KEY (" + PostEntry.COLUMN_USERNAME + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USERNAME + ") " +

                " )" ;

        /* Below tables need to be created to store fields belonging to User/Post entries
         * which can take multiple values */

        /* All the tags associated with a given post */
        final String SQL_CREATE_POST_TAG_TABLE = "CREATE TABLE " + PostTagEntry.TABLE_NAME + " (" +

                PostTagEntry.COLUMN_POST_ID + " INTEGER NOT NULL, " +
                PostTagEntry.COLUMN_TAG_NAME + " TEXT NOT NULL, " +

                // Set up the username column as a foreign key to user table.
                " FOREIGN KEY (" + PostTagEntry.COLUMN_POST_ID + ") REFERENCES " +
                PostEntry.TABLE_NAME + " (" + PostEntry.COLUMN_POST_ID + "), " +
                // Set up the username column as a foreign key to user table.
                " FOREIGN KEY (" + PostTagEntry.COLUMN_TAG_NAME + ") REFERENCES " +
                TagEntry.TABLE_NAME + " (" + TagEntry.COLUMN_TAG_NAME + "), " +

                " PRIMARY KEY (" + PostTagEntry.COLUMN_POST_ID + ", " +
                PostTagEntry.COLUMN_TAG_NAME + ") " +
                " )" ;

        /* All the tags a user is following */
        final String SQL_CREATE_USER_TAG_TABLE = "CREATE TABLE " + UserTagEntry.TABLE_NAME + " (" +

                UserTagEntry.COLUMN_USERNAME + " TEXT NOT NULL, " +
                UserTagEntry.COLUMN_TAG_NAME + " TEXT NOT NULL, " +

                // Set up the username column as a foreign key to user table.
                " FOREIGN KEY (" + UserTagEntry.COLUMN_USERNAME + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USERNAME + "), " +
                // Set up the username column as a foreign key to user table.
                " FOREIGN KEY (" + UserTagEntry.COLUMN_TAG_NAME + ") REFERENCES " +
                TagEntry.TABLE_NAME + " (" + TagEntry.COLUMN_TAG_NAME + "), " +

                " PRIMARY KEY (" + UserTagEntry.COLUMN_USERNAME + ", " +
                UserTagEntry.COLUMN_TAG_NAME + ") " +
                " )" ;

        /* Users who accompanied the post creator */
        final String SQL_CREATE_POST_ACCOMPANYING_USER_TABLE = "CREATE TABLE " + PostAccompanyingUserEntry.TABLE_NAME + " (" +

                PostAccompanyingUserEntry.COLUMN_POST_ID + " INTEGER NOT NULL, " +
                PostAccompanyingUserEntry.COLUMN_ACCOMPANYING_USERNAME + " TEXT NOT NULL, " +

                // Set up the username column as a foreign key to user table.
                " FOREIGN KEY (" + PostAccompanyingUserEntry.COLUMN_POST_ID + ") REFERENCES " +
                PostEntry.TABLE_NAME + " (" + PostEntry.COLUMN_POST_ID + "), " +
                // Set up the username column as a foreign key to user table.
                " FOREIGN KEY (" + PostAccompanyingUserEntry.COLUMN_ACCOMPANYING_USERNAME + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USERNAME + "), " +

                " PRIMARY KEY (" + PostAccompanyingUserEntry.COLUMN_POST_ID + ", " +
                PostAccompanyingUserEntry.COLUMN_ACCOMPANYING_USERNAME + ") " +
                " )" ;

        /* Users whom a given user is following */
        final String SQL_CREATE_USER_FOLLOWING_TABLE = "CREATE TABLE " + UserFollowingEntry.TABLE_NAME + " (" +

                UserFollowingEntry.COLUMN_USERNAME + " TEXT NOT NULL, " +
                UserFollowingEntry.COLUMN_USERNAME_FOLLOWING + " TEXT NOT NULL, " +

                // Set up the username column as a foreign key to user table.
                " FOREIGN KEY (" + UserFollowingEntry.COLUMN_USERNAME + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USERNAME + "), " +
                // Set up the username column as a foreign key to user table.
                " FOREIGN KEY (" + UserFollowingEntry.COLUMN_USERNAME_FOLLOWING + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USERNAME + "), " +

                " PRIMARY KEY (" + UserFollowingEntry.COLUMN_USERNAME + ", " +
                UserFollowingEntry.COLUMN_USERNAME_FOLLOWING + ") " +
                " )" ;

        /* Followers of a given user */
        final String SQL_CREATE_USER_FOLLOWER_TABLE = "CREATE TABLE " + UserFollowerEntry.TABLE_NAME + " (" +

                UserFollowerEntry.COLUMN_USERNAME + " TEXT NOT NULL, " +
                UserFollowerEntry.COLUMN_USERNAME_FOLLOWER + " TEXT NOT NULL, " +

                // Set up the username column as a foreign key to user table.
                " FOREIGN KEY (" + UserFollowerEntry.COLUMN_USERNAME + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USERNAME + "), " +
                // Set up the username column as a foreign key to user table.
                " FOREIGN KEY (" + UserFollowerEntry.COLUMN_USERNAME_FOLLOWER + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLUMN_USERNAME + "), " +

                " PRIMARY KEY (" + UserFollowerEntry.COLUMN_USERNAME + ", " +
                UserFollowerEntry.COLUMN_USERNAME_FOLLOWER + ") " +
                " )" ;

        Log.v(LOG_TAG, "Listing SQL create table statements:");

        Log.v(LOG_TAG, SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);

        Log.v(LOG_TAG, SQL_CREATE_TAG_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TAG_TABLE);

        Log.v(LOG_TAG, SQL_CREATE_POST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_POST_TABLE);

        Log.v(LOG_TAG, SQL_CREATE_POST_TAG_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_POST_TAG_TABLE);

        Log.v(LOG_TAG, SQL_CREATE_USER_TAG_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_TAG_TABLE);

        Log.v(LOG_TAG, SQL_CREATE_POST_ACCOMPANYING_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_POST_ACCOMPANYING_USER_TABLE);

        Log.v(LOG_TAG, SQL_CREATE_USER_FOLLOWING_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_FOLLOWING_TABLE);

        Log.v(LOG_TAG, SQL_CREATE_USER_FOLLOWER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_FOLLOWER_TABLE);

        Log.v(LOG_TAG, "Done creating SQL tables");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        Log.v(LOG_TAG, "onUpgrade for appDbHelper");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TagEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PostEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PostTagEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserTagEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PostAccompanyingUserEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserFollowingEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserFollowerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
