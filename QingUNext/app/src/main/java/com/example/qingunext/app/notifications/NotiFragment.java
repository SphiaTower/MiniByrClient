package com.example.qingunext.app.notifications;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.qingunext.app.R;
import com.example.qingunext.app.datamodel.NotiItem;
import com.example.qingunext.app.datamodel.NotiGroup;
import com.example.qingunext.app.page_thread.ReplyActivity;
import com.example.qingunext.app.pull_to_refresh_auto_pager.AutoPagerLoaderImpl;
import com.example.qingunext.app.util.TimeWrapper;
import tower.sphia.auto_pager_recycler.lib.AutoPagerAdapter;
import tower.sphia.auto_pager_recycler.lib.AutoPagerRefreshableFragment;

import java.util.TreeMap;

/**
 * Created by Rye on 6/20/2015.
 */
public class NotiFragment extends AutoPagerRefreshableFragment<NotiGroup, NotiItem> {


    @Override
    public Loader<TreeMap<Integer, NotiGroup>> onCreateLoader(int i, Bundle bundle) {
        return new AutoPagerLoaderImpl<NotiGroup>(getActivity()) {
            @Override
            protected Class<NotiGroup> getGroupClass() {
                return NotiGroup.class;
            }

            @Override
            protected String getUrl(int page) {
                return new NotiGroup.UrlBuilder().page(page).build();
            }

        };
    }

    @Override
    protected AutoPagerAdapter<NotiGroup, NotiItem> onCreateAdapter() {
        return new NotificationAdapter();
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView author;
        private TextView time;
        private ImageView isRead;

        public ItemViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tvMailBoxPageTitle);
            author = (TextView) view.findViewById(R.id.tvMailBoxPageUser);
            time = (TextView) view.findViewById(R.id.tvMailBoxPageTime);
            isRead = (ImageView) view.findViewById(R.id.tvMailBoxPageIsRead);
        }
    }

    private class NotificationAdapter extends AutoPagerAdapter<NotiGroup, NotiItem> {


        @Override
        protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_refer, viewGroup, false);
            view.setOnClickListener(v -> {
                NotiItem replyInfo = getAutoPagerManager().findItemByView(v);
                ReplyActivity.ReplyIntent.start(getActivity(), replyInfo.getBoardName(), replyInfo.getGroupId(), replyInfo.getTitle());

            });
            return new ItemViewHolder(view);
        }

        @Override
        protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            ItemViewHolder holder = (ItemViewHolder) viewHolder;
            NotiItem replyInfo = getItem(position);
            holder.title.setText(replyInfo.getTitle());
            holder.author.setText(replyInfo.getUser().getId());
            if (!replyInfo.isRead()) {
                holder.isRead.setImageDrawable(getResources().getDrawable(R.drawable.ic_markunread_black_24dp));
            }
            holder.time.setText(TimeWrapper.valueOf(replyInfo.getPostTime()).toString());
        }
    }
}
