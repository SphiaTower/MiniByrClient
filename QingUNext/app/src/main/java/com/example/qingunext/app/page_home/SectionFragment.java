package com.example.qingunext.app.page_home;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.page_board.BoardActivity;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Rye on 2/20/2015.
 */
public class SectionFragment extends ListFragment {
    private static final String SECTION_ID = "id";
    private List<String> mBoardNames;

    public static SectionFragment newInstance(int id) {
        SectionFragment sectionFragment = new SectionFragment();
        Bundle args = new Bundle();
        args.putInt(SECTION_ID, id);
        sectionFragment.setArguments(args);
        return sectionFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String arrayResName = "section_" + getArguments().getInt(SECTION_ID);
        String[] stringArray = getResources().getStringArray(
                getResources().getIdentifier(arrayResName, "array", "com.example.qingunext.app"));
        mBoardNames = Arrays.asList(stringArray);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        View header = View.inflate(getActivity(), R.layout.section_header, null);
//        getListView().addHeaderView(header);
        getListView().setDivider(null);
        BoardListAdapter adapter = new BoardListAdapter(
                getActivity(),
                R.layout.item_section,
                mBoardNames
        );
        setListAdapter(adapter);

    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        BoardActivity.from(getActivity(),mBoardNames.get(position));
    }


    private class BoardListAdapter extends ArrayAdapter<String> {
        private int resource;

        public BoardListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            this.resource = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            RelativeLayout layout;
            ViewHolder holder;
            if (convertView == null) {
                layout = new RelativeLayout(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                inflater.inflate(resource, layout, true);
                holder = new ViewHolder(layout);
                layout.setTag(holder);
            } else {
                layout = (RelativeLayout) convertView;
                holder = (ViewHolder) layout.getTag();
            }
            String boardName = getItem(position);
            String boardDescription = QingUApp.getInstance().getBoardFullDescription(boardName);
            if (boardDescription == null) {
                boardDescription = boardName;
            }
            holder.sectionName.setText(boardDescription);
            return layout;
        }

        private class ViewHolder {
            private TextView sectionName;

            public ViewHolder(View view) {
                sectionName = (TextView) view.findViewById(R.id.tvSectionListItem);
            }
        }
    }
}
