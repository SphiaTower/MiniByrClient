package com.example.qingunext.app.notifications;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.datamodel.MailBox;
import com.example.qingunext.app.datamodel.MailInfo;
import com.example.qingunext.app.pull_to_refresh_auto_pager.AutoPagerLoaderImpl;
import tower.sphia.auto_pager_recycler.lib.AutoPagerAdapter;
import tower.sphia.auto_pager_recycler.lib.AutoPagerRefreshableFragment;

import java.util.TreeMap;

public class MailBoxFragment extends AutoPagerRefreshableFragment<MailBox, MailInfo> {
    public static final String TAG = "MailBoxFragment";

    @Override
    public Loader<TreeMap<Integer, MailBox>> onCreateLoader(int i, Bundle bundle) {
        return new AutoPagerLoaderImpl<MailBox>(getActivity()) {
            @Override
            protected Class<MailBox> getGroupClass() {
                return MailBox.class;
            }

            @Override
            protected String getUrl(int page) {
                return new MailBox.UrlBuilder(MailBox.MailBoxName.INBOX).page(page).build();
            }
        };
    }

    @Override
    protected AutoPagerAdapter<MailBox, MailInfo> onCreateAdapter() {
        return new MailBoxAdapter();
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

    private class MailBoxAdapter extends AutoPagerAdapter<MailBox, MailInfo> {


        @Override
        protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_refer, viewGroup, false);
            view.setOnClickListener(v -> {
                int itemPosition = getRecyclerView().getChildLayoutPosition(v);
                if (QingUApp.DEBUG) Log.v(TAG, "List item " + itemPosition + " is clicked");
                MailInfo mailInfo = getItem(itemPosition);
                MailActivity.from(getActivity(), MailBox.MailBoxName.INBOX.toString(), mailInfo.getIndex());
            });
            return new ItemViewHolder(view);
        }

        @Override
        protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            ItemViewHolder holder = (ItemViewHolder) viewHolder;
            MailInfo mailInfo = getItem(position);
            holder.title.setText(mailInfo.getTitle());
            holder.author.setText(mailInfo.getUser().getId());
            if (!mailInfo.isRead()) {
                holder.isRead.setImageDrawable(getResources().getDrawable(R.drawable.ic_markunread_black_24dp));
            }
            holder.time.setText(mailInfo.getPostTime());
        }
    }

}
