package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import java.util.ArrayList;

import sachan.dheeraj.mebeerhu.model.Post;
import sachan.dheeraj.mebeerhu.viewHolders.PostViewHolder;


/**
 * Created by agarwalh on 9/22/2015.
 */
public class FeedsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_PROGRESSBAR = 0;

    private ArrayList<Post> mDataSet;
    private Context context;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private View.OnLongClickListener LOCATION_LONG_CLICK_LISTENER;
    private View.OnLongClickListener TAG_LONG_CLICK_LISTENER;
    private View.OnClickListener LIKES_CLICK_LISTENER;

    private int lastPosition = -1;

    public FeedsAdapter( Context ctxt,
                         ArrayList<Post> dataSet,
                         RecyclerView recyclerView,
                         View.OnLongClickListener location_long_click_listener,
                         View.OnLongClickListener tag_long_click_listener,
                         View.OnClickListener likes_click_listener
                        )
    {
        context = ctxt;
        mDataSet = dataSet;
        LOCATION_LONG_CLICK_LISTENER = location_long_click_listener;
        TAG_LONG_CLICK_LISTENER = tag_long_click_listener;
        LIKES_CLICK_LISTENER = likes_click_listener;

        if(recyclerView.getLayoutManager()instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if(onLoadMoreListener!=null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_PROGRESSBAR ;
    }

    public void setLoaded(){
        loading = false;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        RecyclerView.ViewHolder vh;
        if(viewType== VIEW_TYPE_ITEM) {
            View thisView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_post, parent, false);
            vh = new PostViewHolder(thisView, context);
        }
        else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_item, parent, false);

            vh = new ProgressViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

        if (holder instanceof PostViewHolder) {
            PostViewHolder postViewHolder = (PostViewHolder)holder;
            Post post = mDataSet.get(i);
            postViewHolder.getPosterNameTextView().setText(post.getParentUsername());
            if (post.getAccompaniedWith() != null && post.getAccompaniedWith().size() > 0) {
                postViewHolder.getWithTextView().setText("with");
                postViewHolder.getxOthersTextView().setText(post.getAccompaniedWith().size() + " others");
            } else {
                postViewHolder.getWithTextView().setVisibility(android.view.View.GONE);
                postViewHolder.getxOthersTextView().setVisibility(android.view.View.GONE);
            }

            postViewHolder.getLocationTextView().setText(post.getPostLocation());
            postViewHolder.getLocationTextView().setOnLongClickListener(LOCATION_LONG_CLICK_LISTENER);
            postViewHolder.getLikesTextView().setText(post.getAggregatedVoteCount() + " likes");
            postViewHolder.setPost(post);
            postViewHolder.getLikesTextView().setOnClickListener(LIKES_CLICK_LISTENER);
            postViewHolder.loadTagsInThreeLines((Activity) context, TAG_LONG_CLICK_LISTENER);
            postViewHolder.initAndLoadImages(context);
            setAnimation(postViewHolder.getLayoutContainerView(), i);
        }
        else {
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
