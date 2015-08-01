package sachan.dheeraj.mebeerhu;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import sachan.dheeraj.mebeerhu.model.Feeds;
import sachan.dheeraj.mebeerhu.model.Post;


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

        new AsyncTask<Void,Void,Feeds>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Feeds feeds) {
                ArrayAdapter<Post> postArrayAdapter = new ArrayAdapter<Post>(getActivity(),0,feeds){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                          Post post = getItem(position);
                        if(convertView == null){
                            convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_post,null);
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
                String feedString = HttpAgent.get(UrlConstants.GET_FEED,getActivity());
                Feeds feeds =  JsonHandler.parseNormal(feedString,Feeds.class);
                if(feeds == null){
                    throw new RuntimeException("json parse failed");
                }else{
                  return feeds;
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }


}
