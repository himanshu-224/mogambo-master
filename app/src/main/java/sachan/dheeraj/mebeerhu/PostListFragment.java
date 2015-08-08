package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.getbase.floatingactionbutton.FloatingActionButton;

import sachan.dheeraj.mebeerhu.model.Feeds;
import sachan.dheeraj.mebeerhu.model.Post;
import sachan.dheeraj.mebeerhu.viewHolders.PostViewHolder;


public class PostListFragment extends Fragment {
    private ListView listView;
    private ProgressBar progressBar;

    public PostListFragment() {
    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        listView = (ListView) view.findViewById(R.id.list_view);
        progressBar = (ProgressBar) view.findViewById(R.id.loader);

        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CreatePostActivityNew.class);
                startActivity(intent);
            }
        });

        new AsyncTask<Void, Void, Feeds>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Feeds feeds) {
                ArrayAdapter<Post> postArrayAdapter = new ArrayAdapter<Post>(getActivity(), 0, feeds) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        Post post = getItem(position);
                        PostViewHolder postViewHolder;
                        if (convertView == null) {
                            convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_post, null);
                            postViewHolder = PostViewHolder.getInstance(convertView,getActivity());
                            convertView.setTag(postViewHolder);
                        } else {
                            postViewHolder = (PostViewHolder) convertView.getTag();
                        }
                        postViewHolder.getPosterNameTextView().setText(post.getParentUsername());
                        if (post.getAccompaniedWith() != null && post.getAccompaniedWith().size() > 0) {
                            postViewHolder.getWithTextView().setText("with");
                            postViewHolder.getxOthersTextView().setText(post.getAccompaniedWith().size() + " others");
                        } else {
                            postViewHolder.getWithTextView().setVisibility(View.GONE);
                            postViewHolder.getxOthersTextView().setVisibility(View.GONE);
                        }

                        postViewHolder.getLocationTextView().setText(post.getPostLocation());
                        postViewHolder.getLikesTextView().setText(post.getAggregatedVoteCount() + " likes");
                        postViewHolder.setPost(post);
                        postViewHolder.loadTagsInThreeLines(getActivity());
                        postViewHolder.initAndLoadImages(getContext());
                        return convertView;
                    }
                };

                listView.setAdapter(postArrayAdapter);
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            protected Feeds doInBackground(Void... params) {
                if(true) return Feeds.feedsBuilder();
                String feedString = HttpAgent.get(UrlConstants.GET_FEED, getActivity());
                Feeds feeds = JsonHandler.parseNormal(feedString, Feeds.class);
                if (feeds == null) {
                    throw new RuntimeException("json parse failed");
                } else {
                    return feeds;
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }


}
