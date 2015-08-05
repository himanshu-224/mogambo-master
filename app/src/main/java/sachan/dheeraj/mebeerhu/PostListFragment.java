package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import sachan.dheeraj.mebeerhu.model.Feeds;
import sachan.dheeraj.mebeerhu.model.Post;
import sachan.dheeraj.mebeerhu.model.Tag;
import sachan.dheeraj.mebeerhu.viewHolders.PostViewHolder;


public class PostListFragment extends Fragment {
    public static PostListFragment newInstance(String param1, String param2) {
        PostListFragment fragment = new PostListFragment();
        return fragment;
    }

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
                            postViewHolder = PostViewHolder.getInstance(convertView);
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
                        postViewHolder.initAndLoadImages(getContext());
                        postViewHolder.getFlowLayoutFull().removeAllViews();
                        if (post.getTagList() != null && post.getTagList().size() > 0) {
                            for (Tag tag : post.getTagList()) {
                                View view1 = getLayoutInflater(null).inflate(R.layout.list_item_tag, null);
                                TextView textView = (TextView) view1.findViewById(R.id.tv);
                                textView.setText(tag.getTagName());
                                textView.setTextColor(getActivity().getResources().getColor(R.color.white));
                                if (tag.getTypeId() == Tag.TYPE_NOUN) {
                                    textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                                } else {
                                    textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
                                }
                                postViewHolder.getFlowLayoutFull().addView(view1);
                            }
                        }
                        return convertView;
                    }
                };

                listView.setAdapter(postArrayAdapter);
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            protected Feeds doInBackground(Void... params) {
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
