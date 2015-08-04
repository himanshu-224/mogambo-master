package sachan.dheeraj.mebeerhu.viewHolders;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import de.hdodenhof.circleimageview.CircleImageView;
import sachan.dheeraj.mebeerhu.R;
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
    private FlowLayout flowLayout;
    private TextView locationTextView;
    private TextView likesTextView;
    private Post post;
    private AsyncTask<Void,Void,Void> voidVoidVoidAsyncTask;

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

    public FlowLayout getFlowLayout() {
        return flowLayout;
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
        postViewHolder.flowLayout = (FlowLayout) view.findViewById(R.id.flow_layout);
        postViewHolder.locationTextView = (TextView) view.findViewById(R.id.location);
        postViewHolder.likesTextView = (TextView) view.findViewById(R.id.likes);

        return postViewHolder;
    }

    public void autoScaleImage(){

    }

    public void loadImages(final Context context){
        voidVoidVoidAsyncTask = new AsyncTask<Void, Void, Void>() {
            private Bitmap profileBitmap;
            private Bitmap mainBitmap;

            @Override
            protected Void doInBackground(Void... params) {
                profileBitmap = ImageUtils.getBitmapFromUrl(context,post.getUserImageURL());
                mainBitmap = ImageUtils.getBitmapFromUrl(context,post.getPostImageURL());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(profileBitmap != null){
                    profileCircleImageView.setImageBitmap(profileBitmap);
                }
                if(mainBitmap != null){
                    mainImageView.setImageBitmap(mainBitmap);
                    ViewGroup.LayoutParams layoutParams = mainImageView.getLayoutParams();
                    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    DisplayMetrics dimension = new DisplayMetrics();
                    windowManager.getDefaultDisplay().getMetrics(dimension);
                    int w = layoutParams.width;
                    layoutParams.height = dimension.widthPixels * mainBitmap.getHeight() / mainBitmap.getWidth();
                    mainImageView.setLayoutParams(layoutParams);
                }
            }
        };
        voidVoidVoidAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



}
