package com.example.qingunext.app.page_home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import com.example.qingunext.app.R;

/**
 * Created by Voyager on 9/14/2015.
 */
public class SectionGridFragment extends Fragment {
    public String[] mSectionNames;
    private GridView mGridView;

    public static SectionGridFragment newInstance() {
        return new SectionGridFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.frag_section_grid, container, false);
        mGridView = (GridView) inflate.findViewById(R.id.gridview);
        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSectionNames = getActivity().getResources().getStringArray(R.array.sections2);

        mGridView.setAdapter(new BaseAdapter() {

            @Override
            public int getCount() {
                return mSectionNames.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView;
                if (convertView == null) {
                    textView = ((TextView) getLayoutInflater(null).inflate(R.layout.item_tile, parent, false));
                } else {
                    textView = (TextView) convertView;
                }
                textView.setText(mSectionNames[position]);
                return textView;
            }
        });
        mGridView.setOnItemClickListener((parent, view, position, id) -> Snackbar.make(parent, position + "", Snackbar.LENGTH_SHORT).show());
    }
}
