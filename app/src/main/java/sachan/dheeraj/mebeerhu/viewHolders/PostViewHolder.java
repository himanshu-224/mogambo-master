package sachan.dheeraj.mebeerhu.viewHolders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import de.hdodenhof.circleimageview.CircleImageView;
import sachan.dheeraj.mebeerhu.R;
import sachan.dheeraj.mebeerhu.customFlowLayout.FlowLayout;
import sachan.dheeraj.mebeerhu.model.Post;
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
    private FlowLayout flowLayoutFull;
    private FlowLayout flowLayoutThree;
    private TextView locationTextView;
    private TextView likesTextView;
    private Post post;
    private ImageLoaderAsyncTask imageLoaderAsyncTask;

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

    public FlowLayout getFlowLayoutFull() {
        return flowLayoutFull;
    }

    public FlowLayout getFlowLayoutThree() {
        return flowLayoutThree;
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

    public static PostViewHolder getInstance(View view){
        PostViewHolder postViewHolder = new PostViewHolder();
        postViewHolder.profileCircleImageView = (CircleImageView) view.findViewById(R.id.circular_image);
        postViewHolder.posterNameTextView = (TextView) view.findViewById(R.id.poster_name);
        postViewHolder.withTextView = (TextView) view.findViewById(R.id.with);
        postViewHolder.xOthersTextView = (TextView) view.findViewById(R.id.x_others);
        postViewHolder.postFollowersTextView = (TextView) view.findViewById(R.id.post_followers);
        postViewHolder.timeTextView = (TextView) view.findViewById(R.id.time);
        postViewHolder.mainImageView = (ImageView) view.findViewById(R.id.main_image);
        postViewHolder.flowLayoutFull = (FlowLayout) view.findViewById(R.id.flow_layout_full);
        postViewHolder.flowLayoutThree = (FlowLayout) view.findViewById(R.id.flow_layout_three);
        postViewHolder.locationTextView = (TextView) view.findViewById(R.id.location);
        postViewHolder.likesTextView = (TextView) view.findViewById(R.id.likes);

        return postViewHolder;
    }

    public void initAndLoadImages(final Context context){
            if(imageLoaderAsyncTask != null){
                if(!imageLoaderAsyncTask.getCurrentRunningPost().equals(post)){
                    imageLoaderAsyncTask.cancel(true);
                }
            }
        imageLoaderAsyncTask = new ImageLoaderAsyncTask(post,context);
        imageLoaderAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private class ImageLoaderAsyncTask extends AsyncTask<Void,Void,Void>{

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
            if(!isCancelled()) {
                profileBitmap = ImageUtils.getBitmapFromUrl(post.getUserImageURL());
                mainBitmap = ImageUtils.getBitmapFromUrl(post.getPostImageURL());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(!isCancelled()) {
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
