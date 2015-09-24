package sachan.dheeraj.mebeerhu;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import sachan.dheeraj.mebeerhu.model.User;
import sachan.dheeraj.mebeerhu.utils.Utils;
import sachan.dheeraj.mebeerhu.viewHolders.LikesViewHolder;

public class LikesListFragment extends Fragment {

    public static final String LOG_TAG = LikesListFragment.class.getSimpleName();
    ProgressBar progressBar;
    ListView listView;

    public LikesListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v(LOG_TAG, "Oncreate for LikesList Fragment");
        View view = inflater.inflate(R.layout.fragment_likes_list, container, false);
        listView = (ListView) view.findViewById(R.id.likes_list_view);
        progressBar = (ProgressBar) view.findViewById(R.id.loader);
        progressBar.setVisibility(View.VISIBLE);

        new AsyncTask<Void, Void, ArrayList<User>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(ArrayList<User> users) {
                try {
                    ArrayAdapter<User> likesArrayAdapter = new ArrayAdapter<User>(getActivity(), 0, users) {

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            User user = getItem(position);
                            LikesViewHolder likesViewHolder;
                            if (convertView == null) {
                                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_likes, null);
                                likesViewHolder = new LikesViewHolder(convertView);
                                convertView.setTag(likesViewHolder);
                            } else {
                                likesViewHolder = (LikesViewHolder) convertView.getTag();
                            }
                            likesViewHolder.getUserName().setText(user.getName());
                            likesViewHolder.loadAndSetProfileImage(user.getProfileImageURL(), getActivity());
                            return convertView;
                        }
                    };

                    listView.setAdapter(likesArrayAdapter);
                    listView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }catch (Exception e){
                    Log.e(LOG_TAG,e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            protected ArrayList<User> doInBackground(Void... params) {

                ArrayList<User> users;
                users = Utils.generateUsers();

                /*String feedString = HttpAgent.get(UrlConstants.GET_FEED, getActivity());
                Feeds feeds = JsonHandler.parseNormal(feedString, Feeds.class);*/
                if (users == null) {
                    throw new RuntimeException("json parse failed");
                } else {
                    return users;
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }


}
