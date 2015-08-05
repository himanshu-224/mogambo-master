package sachan.dheeraj.mebeerhu;


import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import sachan.dheeraj.mebeerhu.customFlowLayout.FlowLayout;
import sachan.dheeraj.mebeerhu.model.Tag;
import sachan.dheeraj.mebeerhu.model.TagArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectTagsFragment extends Fragment {
    private HashSet<Tag> tagHashSet = new HashSet<Tag>();
    private TextView continueTextView;
    Typeface typeface;

    public SelectTagsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        typeface = Typeface.createFromAsset(getActivity().getBaseContext().getResources().getAssets(), "gothic.ttf");
        final View view = inflater.inflate(R.layout.fragment_follow_tags, container, false);
        ((TextView) view.findViewById(R.id.header)).setTypeface(typeface);
        ((TextView) view.findViewById(R.id.hook)).setTypeface(typeface);
        ((TextView) view.findViewById(R.id.fine)).setTypeface(typeface);

        final FlowLayout flowLayout = (FlowLayout) view.findViewById(R.id.flow_layout);
        continueTextView = (TextView) view.findViewById(R.id.continue_button);
        continueTextView.setTypeface(typeface);

        continueTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tagHashSet.size() <= 4){
                    return;
                }

                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        ArrayList<String> stringArrayList = new ArrayList<String>();
                        for (Tag tag : tagHashSet) {
                            stringArrayList.add(tag.getTagName());
                        }
                        String data = HttpAgent.postGenericData(UrlConstants.FOLLOW_TAGS_URL, JsonHandler.stringifyNormal(stringArrayList), getActivity());
                        if (data != null) {
                            return true;
                        }
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if (aBoolean) {
                            Toast.makeText(getActivity(), "saved", Toast.LENGTH_LONG).show();
                        }
                    }

                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            ;
        });

        new AsyncTask<Void, Void, TagArrayList>() {
            @Override
            protected TagArrayList doInBackground(Void... params) {
                String data = HttpAgent.get(UrlConstants.GET_TRENDY_TAGS_URL, getActivity());
                TagArrayList tagArrayList = JsonHandler.parseNormal(data, TagArrayList.class);
                return tagArrayList;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(TagArrayList tagArrayList) {
                if (tagArrayList != null) {
                        for (Tag tag : tagArrayList) {
                            View view1 = inflater.inflate(R.layout.list_item_tag, null);
                            TextView textView = (TextView) view1.findViewById(R.id.tv);
                            FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout);
                            textView.setText(tag.getTagName());
                            textView.setTypeface(typeface);
                            view1.setOnClickListener(ON_CLICK_LISTENER);
                            view1.setTag(new TagHolder(tag, textView));
                            flowLayout.addView(view1);
                        }
                    continueTextView.setEnabled(true);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



        return view;
    }

    private final View.OnClickListener ON_CLICK_LISTENER = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TagHolder tagHolder = (TagHolder) v.getTag();
            if (tagHashSet.contains(tagHolder.tag)) {
                tagHolder.textView.setTextColor(getActivity().getResources().getColor(R.color.purple));
                tagHashSet.remove(tagHolder.tag);
                tagHolder.textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            } else {
                tagHolder.textView.setTextColor(getActivity().getResources().getColor(R.color.white));
                tagHashSet.add(tagHolder.tag);
                if (tagHolder.tag.getTypeId() == Tag.TYPE_NOUN) {
                    tagHolder.textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
                } else {
                    tagHolder.textView.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
                }
            }
            if (tagHashSet.size() > 4) {
                continueTextView.setBackground(getResources().getDrawable(R.drawable.button_smooth));
            } else {
                continueTextView.setBackground(getResources().getDrawable(R.drawable.button_smooth_tatti));
            }
        }
    };

    private static final class TagHolder {
        private Tag tag;
        private TextView textView;

        private TagHolder(Tag tag, TextView textView) {
            this.tag = tag;
            this.textView = textView;
        }

    }
}
