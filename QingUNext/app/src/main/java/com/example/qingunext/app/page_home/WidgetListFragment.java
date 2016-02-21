package com.example.qingunext.app.page_home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.datamodel.Widget;
import com.example.qingunext.app.datamodel.WidgetItem;
import com.example.qingunext.app.page_board.BoardActivity;
import com.example.qingunext.app.page_thread.ReplyActivity;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rye on 6/8/2015.
 */
public class WidgetListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Widget>> {
    public static final String TAG = "WidgetListFragment";
    private static final int LOADER_ID = 2;
    public WidgetsAdapter mAdapter;
    public Toolbar mToolbar;
    private ExpandableListView mExpandableListView;
    private PullToRefreshLayout mPullToRefreshLayout;
    private int mFailCount = 0;


    //    public static WeakReference<PullToRefreshLayout> pullToRefreshLayoutWeakReference;
    private CrossfadeManager mCrossfadeManager;

    public static WidgetListFragment newInstance() {
        return new WidgetListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (QingUApp.DEBUG) Log.i(TAG, "onCreateView");

        // This is the View which is created by ListFragment

        mExpandableListView = new ExpandableListView(getActivity());
        mExpandableListView.setDivider(null);
        // We need to create a PullToRefreshLayout manually
        mPullToRefreshLayout = new PullToRefreshLayout(getActivity());
        mPullToRefreshLayout.addView(mExpandableListView);
        // We can now setup the PullToRefreshLayout
//        pullToRefreshLayoutWeakReference = new WeakReference<>(mPullToRefreshLayout);
        View textView = inflater.inflate(R.layout.empty_text, mPullToRefreshLayout, false);
        mCrossfadeManager = CrossfadeManager.setup(mPullToRefreshLayout, textView, mExpandableListView);
        // hacking
        View fill = new View(getActivity());
        fill.setVisibility(View.INVISIBLE);
        container.addView(fill);
        return mPullToRefreshLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionBarPullToRefresh.from(getActivity())
//                .useViewDelegate(ExpandableListView.class, new AbsListViewDelegate())
                // We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
//                .insertLayoutInto(mPullToRefreshLayout)

                // We need to mark the ListView and it's Empty View as pullable
                // This is because they are not direct children of the ViewGroup
                .theseChildrenArePullable(mExpandableListView, mExpandableListView.getEmptyView())

                // We can now complete the setup as desired
                .listener(v -> {
                    Loader<?> loader = getLoaderManager().getLoader(LOADER_ID);
                    ((WidgetLoader) loader).setIgnoreCache(true).onContentChanged();
                })
                .setup(mPullToRefreshLayout);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /* data acquiring and following work must be executed after Activity Created */

        getLoaderManager().initLoader(LOADER_ID, null, this);

        if (QingUApp.DEBUG) Log.i(TAG, "onActivityCreated");
        mAdapter = new WidgetsAdapter();
        mExpandableListView.setAdapter(mAdapter);
        mExpandableListView.setGroupIndicator(null);

        /* 设置ExAdapter的方法 */
//        getPullToRefreshListView().getRefreshableView().setAdapter(new WidgetsAdapter2());
    }

    private void onDataReady() {
        if (QingUApp.DEBUG) Log.v("wlist", mAdapter.getWidgets().size() + "");

        for (int i = 0; i < mAdapter.getWidgets().size(); i++) {
            mExpandableListView.expandGroup(i);
            // todo remember collapse or expand
        }
        mExpandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            WidgetItem postInfo = mAdapter.getWidgets().get(groupPosition).getWidgetItems().get(childPosition);
            ReplyActivity.ReplyIntent.start(
                    getActivity(),
                    postInfo.getBoardName(),
                    postInfo.getId(),
                    postInfo.getTitle()
            );
            return false;
        });

    }

    @Override
    public Loader<List<Widget>> onCreateLoader(int i, Bundle bundle) {
        return new WidgetLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Widget>> loader, List<Widget> widgets) {
        if (widgets.size() == 0) {
            final int TRY_COUNT = 3;
            if (mFailCount == TRY_COUNT) {
                // in fact this should rarely happen cuz data should be loaded from db when available
                Toast.makeText(getActivity(), "loading failed", Toast.LENGTH_SHORT).show();
                mFailCount = 0;
            } else {
                mFailCount++;
                loader.forceLoad();
            }
            return;
        }
        if (mAdapter.getGroupCount() == 0) {
            mCrossfadeManager.startAnimation();
            mAdapter.setData(widgets);
            onDataReady();
        } else {
            mAdapter.setData(widgets);
            mPullToRefreshLayout.setRefreshComplete();
        }
        mFailCount = 0;
    }

    @Override
    public void onLoaderReset(Loader<List<Widget>> loader) {
        mAdapter.setData(null);
    }


    private class WidgetsAdapter extends GenericBaseExpandableListAdapter<Widget, WidgetItem> {

        private List<Widget> mWidgets;

        private List<Widget> getWidgets() {
            if (mWidgets == null) {
                mWidgets = new ArrayList<>();
            }
            return mWidgets;
        }

        public void setData(List<Widget> widgets) {
            mWidgets = widgets;
            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return getWidgets().size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return getWidgets().get(groupPosition).getWidgetItems().size();
        }

        @Override
        public Widget getGroup(int groupPosition) {
            return getWidgets().get(groupPosition);
        }

        @Override
        public WidgetItem getChild(int groupPosition, int childPosition) {
            return getWidgets().get(groupPosition).getWidgetItems().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            // todo viewHolder

            View linearLayout = getLayoutInflater(null).inflate(R.layout.tv_widget_name, parent, false);
            TextView tvWidgetName = (TextView) linearLayout.findViewById(R.id.tvWidgetName);
            tvWidgetName.setText(getGroup(groupPosition).getSectionName());
            return linearLayout;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view;   /*画不出线的原因是背景没有设为transparent*/
            final WidgetItem postInfo = getChild(groupPosition, childPosition);
            ChildViewHolder holder;
            if (convertView == null) {
                view = getLayoutInflater(null).inflate(R.layout.item_widget, parent, false);
                holder = new ChildViewHolder(view);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ChildViewHolder) view.getTag();
            }
            holder.boardName.setBackgroundColor(postInfo.getColor());
            String boardDescription = QingUApp.getInstance().getBoardSimplifiedDescription(postInfo.getBoardName());
            if (boardDescription == null) {
                holder.boardName.setText(postInfo.getBoardName());
            } else {
                holder.boardName.setText(boardDescription);
            }
            // todo change back
            holder.boardName.setOnClickListener(v -> BoardActivity.from(
                    getActivity(),
                    postInfo.getBoardName()
            ));
            holder.title.setText(postInfo.getTitle());
            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


        private class ChildViewHolder {
            private TextView boardName;
            private TextView title;

            public ChildViewHolder(View view) {
                title = (TextView) view.findViewById(R.id.tvWidgetItemTitle);
                boardName = (TextView) view.findViewById(R.id.tvWidgetItemBoardName);
            }
        }
    }


}
