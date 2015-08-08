package sachan.dheeraj.mebeerhu.viewHolders;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import de.hdodenhof.circleimageview.CircleImageView;
import sachan.dheeraj.mebeerhu.R;
import sachan.dheeraj.mebeerhu.customFlowLayout.FlowLayout;
import sachan.dheeraj.mebeerhu.model.Post;
import sachan.dheeraj.mebeerhu.model.Tag;
import sachan.dheeraj.mebeerhu.utils.ImageUtils;

/**
 * Created by naveen.goel on 01/08/15.
 */
public class PostViewHolder {
    private CircleImageView profileCircleImageView;
    private TextView posterNameTextView;
    private TextView withTextView;
    private TextView xOthersTextView;
    private TextView postFollowersTextView;
    private TextView timeTextView;
    private ImageView mainImageView;
    private FlowLayout flowLayout;
    private TextView locationTextView;
    private TextView likesTextView;
    private Post post;
    private ImageLoaderAsyncTask imageLoaderAsyncTask;
    private TextView moreTextView;

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

    public static PostViewHolder getInstance(View view,Activity activity) {
        final PostViewHolder postViewHolder = new PostViewHolder();
        postViewHolder.profileCircleImageView = (CircleImageView) view.findViewById(R.id.circular_image);
        postViewHolder.posterNameTextView = (TextView) view.findViewById(R.id.poster_name);
        postViewHolder.withTextView = (TextView) view.findViewById(R.id.with);
        postViewHolder.xOthersTextView = (TextView) view.findViewById(R.id.x_others);
        postViewHolder.postFollowersTextView = (TextView) view.findViewById(R.id.post_followers);
        postViewHolder.timeTextView = (TextView) view.findViewById(R.id.time);
        postViewHolder.mainImageView = (ImageView) view.findViewById(R.id.main_image);
        postViewHolder.locationTextView = (TextView) view.findViewById(R.id.location);
        postViewHolder.likesTextView = (TextView) view.findViewById(R.id.likes);
        postViewHolder.flowLayout = (FlowLayout) view.findViewById(R.id.flow_layout);
        postViewHolder.moreTextView = (TextView) view.findViewById(R.id.more);
        postViewHolder.setMoreOnClickListener(activity);
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
        imageLoaderAsyncTask = new ImageLoaderAsyncTask(post, context);
        imageLoaderAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void loadTagsInThreeLines(final Activity activity) {
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
                flowLayout.addView(view1);
            }
        }
        if (flowLayout.isSomeThingHidden()) {
            moreTextView.setVisibility(View.VISIBLE);
        } else {
            moreTextView.setVisibility(View.GONE);
        }
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
            ColorDrawable tattiColorDrawable = new ColorDrawable(context.getResources().getColor(R.color.tatti));
            ColorDrawable redColorDrawable = new ColorDrawable(context.getResources().getColor(R.color.red));

            profileCircleImageView.setImageDrawable(tattiColorDrawable);
            mainImageView.setImageDrawable(redColorDrawable);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (!isCancelled()) {
                profileBitmap = ImageUtils.getBitmapFromUrl(post.getUserImageURL());
                mainBitmap = ImageUtils.getBitmapFromUrl(post.getPostImageURL());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!isCancelled()) {
                try {
                    if (profileBitmap != null) {
                        profileCircleImageView.setImageBitmap(profileBitmap);
                    }
                    if (mainBitmap != null) {
                        mainImageView.setImageBitmap(mainBitmap);
                        ViewGroup.LayoutParams layoutParams = mainImageView.getLayoutParams();
                        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                        DisplayMetrics dimension = new DisplayMetrics();
                        windowManager.getDefaultDisplay().getMetrics(dimension);
                        layoutParams.height = dimension.widthPixels * mainBitmap.getHeight() / mainBitmap.getWidth();
                        mainImageView.setLayoutParams(layoutParams);
                    }
                } catch (Exception e) {
                    Log.e("", "", e);
                }
            }
        }
    }


}
