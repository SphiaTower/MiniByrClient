package com.example.qingunext.app.page_board;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.qingunext.app.R;
import com.example.qingunext.app.datamodel.Board;
import com.example.qingunext.app.datamodel.BoardItem;
import com.example.qingunext.app.page_thread.ReplyActivity;
import com.example.qingunext.app.pull_to_refresh_auto_pager.AutoPagerLoaderImpl;
import tower.sphia.auto_pager_recycler.lib.AutoPagerAdapter;
import tower.sphia.auto_pager_recycler.lib.AutoPagerRefreshableFragment;

import java.util.TreeMap;

/**
 * Created by Rye on 2/20/2015.
 */
public class BoardFragment extends AutoPagerRefreshableFragment<Board, BoardItem> {
    public static final String ARG_BOARD_NAME = "board_name";

    public static BoardFragment newInstance(String boardName) {
        BoardFragment boardFragment = new BoardFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_BOARD_NAME, boardName);
        boardFragment.setArguments(bundle);
        return boardFragment;
    }

    @Override
    protected AutoPagerAdapter<Board, BoardItem> onCreateAdapter() {
        return new BoardAdapter();
    }

    @Override
    public Loader<TreeMap<Integer, Board>> onCreateLoader(int i, Bundle bundle) {
        return new AutoPagerLoaderImpl<Board>(getActivity()) {
            @Override
            protected Class<Board> getGroupClass() {
                return Board.class;
            }

            @Override
            protected String getUrl(int page) {
                return new Board.UrlBuilder(getArguments().getString(ARG_BOARD_NAME)).page(page).count(10).build();
            }
        };
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView author;
        TextView lastReplyTime;
        TextView lastReplyUserID;
        TextView replyCnt;


        public ItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvBPPostTitle);
            author = (TextView) itemView.findViewById(R.id.tvBPPostAuthor);
            lastReplyTime = (TextView) itemView.findViewById(R.id.tvBPLastReplyTime);
            lastReplyUserID = (TextView) itemView.findViewById(R.id.tvBPLastReplyAuthor);
            replyCnt = (TextView) itemView.findViewById(R.id.tvBPReplyCnt);
        }
    }

    private class BoardAdapter extends AutoPagerAdapter<Board, BoardItem> {


        @Override
        protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_board, viewGroup, false);
            view.setOnClickListener(v -> {
//                if (QingUApp.DEBUG) Log.v(TAG, "List item " + itemPosition + " is clicked");
//                PostInfoForBoard postInfo = getAutoPagerLoader().getData().get(itemPosition / 10).getElements().get(itemPosition - itemPosition / 10 * 10);
                BoardItem postInfo = getAutoPagerManager().findItemByView(v);
                ReplyActivity.ReplyIntent.start(
                        getActivity(),
                        getArguments().getString(ARG_BOARD_NAME),
                        postInfo.getID(),
                        postInfo.getTitle()
                );
            });
            return new ItemViewHolder(view);
        }

        @Override
        protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            ItemViewHolder holder = (ItemViewHolder) viewHolder;
//            PostInfoForBoard postInfo = getAutoPagerLoader().getData().get(position / 10).getElements().get(position - position / 10 * 10);
            BoardItem postInfo = getItem(position);
            holder.replyCnt.setText("" + postInfo.getReplyCount());
            holder.replyCnt.setBackgroundColor(postInfo.getColor());
            holder.title.setText(postInfo.getTitle());
            holder.author.setText(postInfo.getUserID());
            holder.lastReplyUserID.setText(postInfo.getLastReplyUserID());
            holder.lastReplyTime.setText("" + position);
//            holder.lastReplyTime.setText(postInfo.getLastReplyTime() != null ? postInfo.getLastReplyTime() + "ǰ" : "");
        }
    }


}
