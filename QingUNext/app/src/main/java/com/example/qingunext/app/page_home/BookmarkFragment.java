package com.example.qingunext.app.page_home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.database.BookmarkRecord;
import com.example.qingunext.app.page_thread.ReplyActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rye on 7/18/2015.
 */
public class BookmarkFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<BookmarkRecord>> {
    public static final int LOADER_ID = 1;
    public BookmarkAdapter mAdapter;
    private RecyclerView mRecyclerView;

    public static BookmarkFragment newInstance() {
        return new BookmarkFragment();
    }

    @Override
    public Loader<List<BookmarkRecord>> onCreateLoader(int i, Bundle bundle) {
        return new BookmarkLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<BookmarkRecord>> loader, List<BookmarkRecord> bookmarkRecords) {
        mAdapter.setData(bookmarkRecords);
    }

    @Override
    public void onLoaderReset(Loader<List<BookmarkRecord>> loader) {
        mAdapter.setData(null);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRecyclerView = new RecyclerView(getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return mRecyclerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        mAdapter = new BookmarkAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private class BookmarkViewHolder extends RecyclerView.ViewHolder {

        TextView boardName;
        TextView title;

        public BookmarkViewHolder(View itemView) {
            super(itemView);
            boardName = (TextView) itemView.findViewById(R.id.tvWidgetItemBoardName);
            title = (TextView) itemView.findViewById(R.id.tvWidgetItemTitle);
        }
    }

    private class BookmarkAdapter extends RecyclerView.Adapter<BookmarkViewHolder> {
        private List<BookmarkRecord> mBookmarkRecords = new ArrayList<>();

        public void setData(List<BookmarkRecord> bookmarkRecords) {
            if (bookmarkRecords == null) {
                mBookmarkRecords.clear();
            }
            mBookmarkRecords = bookmarkRecords;
            notifyDataSetChanged(); // this line is necessary
        }

        @Override
        public BookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_widget, parent, false);
            view.setOnClickListener(v -> {
                BookmarkRecord bookmarkRecord = mBookmarkRecords.get(mRecyclerView.getChildLayoutPosition(v));
                ReplyActivity.ReplyIntent.start(getActivity(), bookmarkRecord.boardName, bookmarkRecord.groupID, bookmarkRecord.title);
            });
            return new BookmarkViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BookmarkViewHolder holder, int position) {
            BookmarkRecord bookmarkRecord = mBookmarkRecords.get(position);
            holder.title.setText(bookmarkRecord.title);
            holder.boardName.setText(QingUApp.getInstance().getBoardSimplifiedDescription(bookmarkRecord.boardName));
        }

        @Override
        public int getItemCount() {
            return mBookmarkRecords.size();
        }
    }
}
