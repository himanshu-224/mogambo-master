package sachan.dheeraj.mebeerhu.viewHolders;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import de.hdodenhof.circleimageview.CircleImageView;
import sachan.dheeraj.mebeerhu.FeedsActivity;
import sachan.dheeraj.mebeerhu.R;
import sachan.dheeraj.mebeerhu.cache.MemDiskCache;
import sachan.dheeraj.mebeerhu.customFlowLayout.FlowLayout;
import sachan.dheeraj.mebeerhu.model.Post;
import sachan.dheeraj.mebeerhu.model.Tag;
import sachan.dheeraj.mebeerhu.utils.ImageUtils;
import sachan.dheeraj.mebeerhu.utils.Utils;

import android.view.ViewGroup.LayoutParams;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by naveen.goel on 01/08/15.
 */
public class PostViewHolder {
    static final String LOG_TAG = PostViewHolder.class.getSimpleName();

    private final static float PROFILE_IMAGE_WIDTH = 40.0f;
    private final static float PROFILE_IMAGE_HEIGHT = 40.0f;

    private final static int LOAD_PROFILE_PIC = 0b1;
    private final static int LOAD_MAIN_IMAGE = 0b10;
    private CircleImageView profileCircleImageView;
    private TextView posterNameTextView;
    private TextView withTextView;
    private TextView xOthersTextView;
    private TextView postFollowersTextView;
    private TextView timeTextView;
    private ImageView mainImageView;
    private ImageView likeImageView;
    private FlowLayout flowLayout;
    private TextView locationTextView;
    private TextView likesTextView;
    private Post post;
    private ImageLoaderAsyncTask imageLoaderAsyncTask;
    private TextView moreTextView;

    /* dimensions of imageViewHolders */
    private int mainImageH, mainImageW, profileImageH, profileImageW;
    private int windowWidth;
    private int loadImageMask = 0;

    private static View.OnLongClickListener LONG_CLICK_LISTENER;

    public CircleImageView getProfileCircleImageView() {
        return profileCircleImageView;
    }

    public TextView getPosterNameTextView() {
        return posterNameTextView;
    }

    public TextView getWithTextView() {
        return withTextView;
    }

    public TextView getxOthersTextView() {
        return xOthersTextView;
    }

    public TextView getPostFollowersTextView() {
        return postFollowersTextView;
    }

    public TextView getTimeTextView() {
        return timeTextView;
    }

    public ImageView getMainImageView() {
        return mainImageView;
    }

    public TextView getLocationTextView() {
        return locationTextView;
    }

    public TextView getLikesTextView() {
        return likesTextView;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public static PostViewHolder getInstance(View view, Activity activity) {
        final PostViewHolder postViewHolder = new PostViewHolder();
        Log.v(LOG_TAG, "GetInstance called for PostViewHolder");
        postViewHolder.profileCircleImageView = (CircleImageView) view.findViewById(R.id.circular_image);

        postViewHolder.posterNameTextView = (TextView) view.findViewById(R.id.poster_name);
        postViewHolder.withTextView = (TextView) view.findViewById(R.id.with);
        postViewHolder.xOthersTextView = (TextView) view.findViewById(R.id.x_others);
        postViewHolder.postFollowersTextView = (TextView) view.findViewById(R.id.post_followers);
        postViewHolder.timeTextView = (TextView) view.findViewById(R.id.time);
        postViewHolder.mainImageView = (ImageView) view.findViewById(R.id.main_image);
        postViewHolder.likeImageView = (ImageView) view.findViewById(R.id.like_image);

        postViewHolder.locationTextView = (TextView) view.findViewById(R.id.location);
        postViewHolder.likesTextView = (TextView) view.findViewById(R.id.likes);
        postViewHolder.flowLayout = (FlowLayout) view.findViewById(R.id.flow_layout);

        postViewHolder.moreTextView = (TextView) view.findViewById(R.id.more);
        postViewHolder.setMoreOnClickListener(activity);
        postViewHolder.moreTextView.setVisibility(View.GONE);

        final Activity fActivity = activity;

        postViewHolder.likeImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                postViewHolder.getPost().setIsLiked(
                        !postViewHolder.getPost().getIsLiked());

                Bitmap bitmap = null;
                String key;
                int resId;
                MemDiskCache mCache = MemDiskCache.getInstance();

                if(postViewHolder.getPost().getIsLiked()) {
                    resId = R.drawable.thumb_up_red;
                }
                else {
                    resId = R.drawable.thumb_up_grey_unliked;
                }
                key = Utils.getKeyFromDrawable(resId);
                if (mCache != null) {
                    bitmap = mCache.getBitmapFromCache(key);
                    if (bitmap == null) {
                        bitmap = BitmapFactory.decodeResource(fActivity.getResources(), resId);
                        mCache.addBitmapToCache(key, bitmap);
                    }
                }
                if( bitmap == null){
                    bitmap = BitmapFactory.decodeResource(fActivity.getResources(), resId);
                }
                postViewHolder.likeImageView.setImageBitmap(bitmap);
            }
        });

        /* Get the width of the window which will be useful in determining the size of images to be displayed */
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dimension = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dimension);
        postViewHolder.windowWidth = dimension.widthPixels;
        Log.v(LOG_TAG, String.format("Window dimensions : height: %d, width: %d",dimension.heightPixels, dimension.widthPixels));

        return postViewHolder;
    }

    private void setMoreOnClickListener(final Activity activity) {
        moreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreTextView.setVisibility(View.GONE);
                flowLayout.removeAllViews();
                flowLayout.setMaxLinesSupported(Integer.MAX_VALUE);
                if (post.getTagList() != null && post.getTagList().size() > 0) {
                    for (Tag tag : post.getTagList()) {
                        View view1 = activity.getLayoutInflater().inflate(R.layout.list_item_tag, null);
                        TextView textView = (TextView) view1.findViewById(R.id.tv);
                        textView.setText(tag.getTagName());
                        textView.setTextColor(activity.getResources().getColor(R.color.white));
                        if (tag.getTypeId() == Tag.TYPE_NOUN) {
                            textView.getBackground().setColorFilter(activity.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                        } else {
                            textView.getBackground().setColorFilter(activity.getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
                        }
                        flowLayout.addView(view1);
                    }
                }
            }
        });
    }

    public void initAndLoadImages(final Context context) {
        if (imageLoaderAsyncTask != null) {
            if (!imageLoaderAsyncTask.getCurrentRunningPost().equals(post)) {
                imageLoaderAsyncTask.cancel(true);
            }
        }

        profileImageH = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PROFILE_IMAGE_HEIGHT, context.getResources().getDisplayMetrics());
        profileImageW = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PROFILE_IMAGE_WIDTH, context.getResources().getDisplayMetrics());


        //Log.v(LOG_TAG, String.format("Profile image Width %d, Height %d", profileImageW, profileImageH));

        mainImageW = windowWidth;
        mainImageH = 20; /* giving a dummy value */

        //Log.v(LOG_TAG, String.format("Main image Width %d, Height %d", mainImageW, mainImageH));

        Bitmap profileBitmap = null;
        Bitmap mainBitmap = null;

        MemDiskCache mCache = MemDiskCache.getInstance();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (mCache!= null){
                profileBitmap =  mCache.getBitmapFromMemCache(Utils.toHex(md.digest(post.getUserImageURL().getBytes())));
                md.reset();
                mainBitmap = mCache.getBitmapFromMemCache(Utils.toHex(md.digest(post.getPostImageURL().getBytes())));
            }
        }
        catch (NoSuchAlgorithmException e) {
            Log.e(LOG_TAG, "MD5 algorithm not recognized : " + e.getMessage());
        }

        loadImageMask = 0;
        try {
            if (profileBitmap != null) {
                profileCircleImageView.setImageBitmap(profileBitmap);
            }
            else{
                loadImageMask = loadImageMask|LOAD_PROFILE_PIC;
            }
            if (mainBitmap != null) {
                mainImageView.setImageBitmap(mainBitmap);
            }
            else{
                loadImageMask = loadImageMask|LOAD_MAIN_IMAGE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(loadImageMask != 0) {
            imageLoaderAsyncTask = new ImageLoaderAsyncTask(post, context);
            imageLoaderAsyncTask.execute();
        }
        else {

            Bitmap bitmap = null;
            String key;
            int resId;
            mCache = MemDiskCache.getInstance();

            if(getPost().getIsLiked()) {
                resId = R.drawable.thumb_up_red;
            }
            else {
                resId = R.drawable.thumb_up_grey_unliked;
            }
            key = Utils.getKeyFromDrawable(resId);
            if (mCache != null) {
                bitmap = mCache.getBitmapFromCache(key);
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                    mCache.addBitmapToCache(key, bitmap);
                }
            }
            if( bitmap == null){
                bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            }
            likeImageView.setImageBitmap(bitmap);
        }
    }

    public void loadTagsInThreeLines(final Activity activity, View.OnLongClickListener LONG_CLICK_LISTENER) {
        moreTextView.setVisibility(View.GONE);
        flowLayout.removeAllViews();
        flowLayout.setMaxLinesSupported(2);
        if (post.getTagList() != null && post.getTagList().size() > 0) {
            for (Tag tag : post.getTagList()) {
                View view1 = activity.getLayoutInflater().inflate(R.layout.list_item_tag, null);
                TextView textView = (TextView) view1.findViewById(R.id.tv);
                textView.setText(tag.getTagName());
                textView.setTextColor(activity.getResources().getColor(R.color.white));
                if (tag.getTypeId() == Tag.TYPE_NOUN) {
                    textView.getBackground().setColorFilter(activity.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                } else {
                    textView.getBackground().setColorFilter(activity.getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
                }
                textView.setOnLongClickListener(LONG_CLICK_LISTENER);
                textView.setHapticFeedbackEnabled(true);
                flowLayout.addView(view1);
            }
        }

        ViewTreeObserver vto = flowLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                flowLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (flowLayout.isSomeThingHidden()) {
                    moreTextView.setVisibility(View.VISIBLE);
                } else {
                    moreTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    private class ImageLoaderAsyncTask extends AsyncTask<Void, Void, Void> {

        private ImageLoaderAsyncTask(Post currentRunningPost, Context context) {
            this.currentRunningPost = currentRunningPost;
            this.context = context;
        }

        private Bitmap profileBitmap;
        private Bitmap mainBitmap;
        private Post currentRunningPost;
        private Context context;

        public Post getCurrentRunningPost() {
            return currentRunningPost;
        }

        @Override
        protected void onPreExecute() {
            if ( (loadImageMask & LOAD_PROFILE_PIC) != 0 )
            {
                ColorDrawable tattiColorDrawable = new ColorDrawable(context.getResources().getColor(R.color.tatti));
                profileCircleImageView.setImageDrawable(tattiColorDrawable);
            }
            if((loadImageMask & LOAD_MAIN_IMAGE) != 0)
            {
                ColorDrawable redColorDrawable = new ColorDrawable(context.getResources().getColor(R.color.red));
                mainImageView.setImageDrawable(redColorDrawable);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (!isCancelled()) {
                //Log.v(LOG_TAG, "UserImage Url: " + post.getUserImageURL() );
                //Log.v(LOG_TAG, "PostImage Url: " + post.getPostImageURL() );
                if ( (loadImageMask & LOAD_PROFILE_PIC) != 0 ) {
                    profileBitmap = ImageUtils.getBitmapFromUrl(post.getUserImageURL(), profileImageH, profileImageW);
                }
                if ( (loadImageMask & LOAD_MAIN_IMAGE) != 0 ){
                    mainBitmap = ImageUtils.getBitmapFromUrl(post.getPostImageURL(), mainImageH, mainImageW);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!isCancelled()) {
                try {
                    if ((loadImageMask & LOAD_PROFILE_PIC) != 0 && profileBitmap != null) {
                        profileCircleImageView.setImageBitmap(profileBitmap);
                    }
                    if ((loadImageMask & LOAD_MAIN_IMAGE) != 0 && mainBitmap != null) {
                        mainImageView.setImageBitmap(mainBitmap);
                    }
                    Bitmap bitmap = null;
                    String key;
                    int resId;
                    MemDiskCache mCache = MemDiskCache.getInstance();

                    if(getPost().getIsLiked()) {
                        resId = R.drawable.thumb_up_red;
                    }
                    else {
                        resId = R.drawable.thumb_up_grey_unliked;
                    }
                    key = Utils.getKeyFromDrawable(resId);
                    if (mCache != null) {
                        bitmap = mCache.getBitmapFromCache(key);
                        if (bitmap == null) {
                            bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                            mCache.addBitmapToCache(key, bitmap);
                        }
                    }
                    if( bitmap == null){
                        bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                    }
                    likeImageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.e("", "", e);
                }
            }
        }
    }


}
