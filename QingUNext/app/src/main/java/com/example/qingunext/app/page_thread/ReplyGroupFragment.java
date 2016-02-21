package com.example.qingunext.app.page_thread;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.datamodel.Reply;
import com.example.qingunext.app.datamodel.ReplyGroup;
import com.example.qingunext.app.datamodel.User;
import com.example.qingunext.app.pages_other.InputActivity;
import com.example.qingunext.app.pages_other.UserActivity;
import com.example.qingunext.app.pull_to_refresh_auto_pager.AutoPagerLoaderImpl;
import com.example.qingunext.app.util.DpPxConverter;
import com.example.qingunext.app.util.TimeWrapper;
import com.example.qingunext.app.util_global.QingUNetworkCenter;
import tower.sphia.auto_pager_recycler.lib.AutoPagerAdapter;
import tower.sphia.auto_pager_recycler.lib.AutoPagerRefreshableFragment;

import java.io.IOException;
import java.util.TreeMap;
import java.util.concurrent.Executors;

/**
 * Created by Rye on 4/5/2015.
 */
public class ReplyGroupFragment extends AutoPagerRefreshableFragment<ReplyGroup, Reply> {
    public static final int PERMISSION_READER = 0;
    public static final int PERMISSION_AUTHOR = 1;
    public static final int PERMISSION_ADMIN = 2; // i don't think it will be used one day:)
    private PopupWindow mMorePopupWindow;

    public static ReplyGroupFragment newInstance(String boardName, int groupID, String title) {
        ReplyGroupFragment recyclerFragment = new ReplyGroupFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ReplyActivity.ARG_BOARD_NAME, boardName);
        bundle.putInt(ReplyActivity.ARG_GROUP_ID, groupID);
        bundle.putString(ReplyActivity.ARG_TITLE, title);
        recyclerFragment.setArguments(bundle);
        return recyclerFragment;
    }

    public static View setupPopupView(Activity context, Reply reply, Bundle arguments) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_reply_options, null);
        ImageButton re = (ImageButton) dialogView.findViewById(R.id.bnReplyReply);
        ImageButton edit = (ImageButton) dialogView.findViewById(R.id.bnReplyEdit);
        ImageButton del = (ImageButton) dialogView.findViewById(R.id.bnReplyDelete);
        ImageButton mail = (ImageButton) dialogView.findViewById(R.id.bnReplyMsg);
        // todo check null
        int permission = reply.getUser().getUsername().equals(QingUApp.getInstance().getUsername()) ? PERMISSION_AUTHOR : PERMISSION_READER;
        switch (permission) {
            case PERMISSION_READER:
                del.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                break;
            case PERMISSION_AUTHOR:
                del.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                break;
        }
        re.setOnClickListener(v -> InputActivity.InputIntent.startReplyIntent(
                context,
                arguments.getString(ReplyActivity.ARG_BOARD_NAME),
                arguments.getString(ReplyActivity.ARG_TITLE),
                reply.toString()
        ));
        edit.setOnClickListener(v -> InputActivity.InputIntent.startEditIntent(
                context,
                arguments.getString(ReplyActivity.ARG_BOARD_NAME),
                arguments.getString(ReplyActivity.ARG_TITLE),
                reply.toString()
        ));
        del.setOnClickListener(v -> {
            View parent = context.findViewById(R.id.flRecyclerContainer);
            Snackbar.make(parent, "确定要删除吗？", Snackbar.LENGTH_LONG)
                    .setAction("删除",
                            v1 -> Executors.newSingleThreadExecutor().execute(() -> {
                                        try {
                                            QingUNetworkCenter.postDeleteArticle(
                                                    arguments.getString(ReplyActivity.ARG_BOARD_NAME),
                                                    reply.getId());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            QingUNetworkCenter.getInstance().popNetworkError();
                                        }
                                    }
                            )
                    )
                    .show();

        });
        mail.setOnClickListener(v -> InputActivity.InputIntent.startSendMailIntent(context, reply.getUser().getId()));
        return dialogView;
    }

    @Override
    protected void setItemDecoration() {

    }

    @Override
    public Loader<TreeMap<Integer, ReplyGroup>> onCreateLoader(int i, Bundle bundle) {
        return new AutoPagerLoaderImpl<ReplyGroup>(getActivity()) {
            @Override
            protected Class<ReplyGroup> getGroupClass() {
                return ReplyGroup.class;
            }

            @Override
            protected String getUrl(int page) {
                return new ReplyGroup.UrlBuilder(getArguments().getString(ReplyActivity.ARG_BOARD_NAME), getArguments().getInt(ReplyActivity.ARG_GROUP_ID)).page(page).count(10).build();
            }
        };
    }

    @Override
    protected AutoPagerAdapter<ReplyGroup, Reply> onCreateAdapter() {
        return new ReplyAdapter();
    }


    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout userSpan;
        private TextView floorCnt;
        private TextView username;
        private TextView id;
        private ReplyView content;
        private TextView time;
        private ImageView face;
        private ImageButton options;


        public ItemViewHolder(View itemView) {
            super(itemView);
            content = (ReplyView) itemView.findViewById(R.id.wvReplyContent);
            userSpan = (RelativeLayout) itemView.findViewById(R.id.rlUserSpan);
            floorCnt = (TextView) itemView.findViewById(R.id.tvReplyFloorCount);
            time = (TextView) itemView.findViewById(R.id.tvReplyTime);
            username = (TextView) itemView.findViewById(R.id.tvReplyUserName);
            id = (TextView) itemView.findViewById(R.id.tvReplyUserID);
            face = (ImageView) itemView.findViewById(R.id.ivReplyFace);

            options = (ImageButton) itemView.findViewById(R.id.reply_options);
        }


    }


    public static class OptionsPopupWindow extends PopupWindow {
        public static int widthCache;
        public static int heightCache;
        public static int marginLeftCache;

        public OptionsPopupWindow(Activity context, Reply reply, Bundle arguments) {
            super(context);
            View dialogView = ReplyGroupFragment.setupPopupView(context, reply, arguments);
            setContentView(dialogView);
            // MUST set width and height
            if (widthCache == 0) {
                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                widthCache = displayMetrics.widthPixels / 4 * 3;
                heightCache = DpPxConverter.dpToPX(displayMetrics, 40);
                marginLeftCache = DpPxConverter.dpToPX(displayMetrics, 6);
            }
            setWidth(widthCache);
            setHeight(heightCache);
            setAnimationStyle(R.style.PopupWindowAnimation);
        }
    }

    private class ReplyAdapter extends AutoPagerAdapter<ReplyGroup, Reply> {


        @Override
        protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup viewGroup, int viewType) {

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_reply2, viewGroup, false);
            ItemViewHolder holder = new ItemViewHolder(view);
            switch (viewType) {
                case AutoPagerAdapter.ITEM:
                    holder.content.addTextView();
                    break;
                default:
                    if (QingUApp.DEBUG) Log.v("CompoundView", "Initializing CompoundView");
                    holder.content.initView(getItem(-viewType));
            }
            return holder;
        }

        @Override
        public int getItemViewType(int position) {
            int itemViewType = super.getItemViewType(position);
            if (itemViewType != AutoPagerAdapter.ITEM) {
                return itemViewType;
            } else {
                Reply item = getItem(position);
                int size = item.getPartitions().size();
                if (size == 1) {
                    return AutoPagerAdapter.ITEM;
                } else {
                    return -position;
                }
            }

        }

        @Override
        protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            ItemViewHolder holder = (ItemViewHolder) viewHolder;
            Reply reply = getItem(position);
            User user = reply.getUser();
            Glide.with(getActivity()).load(user.getFaceURL()).fitCenter().placeholder(R.drawable.ic_placeholder).into(holder.face);
//            BitmapWorkerTask.loadBitmap(user.getFaceURL(), face);
            holder.content.setData(reply);
            holder.floorCnt.setText("#" + reply.getPosition());
            String username = user.getUsername();
            SpannableStringBuilder usernameAndID = new SpannableStringBuilder(username + " @" + user.getId());
            usernameAndID.setSpan(
                    new ForegroundColorSpan(Color.GRAY),
                    username.length(),
                    usernameAndID.length(),
                    SpannableStringBuilder.SPAN_INCLUSIVE_INCLUSIVE
            );
            usernameAndID.setSpan(
                    new StyleSpan(Typeface.ITALIC),
                    username.length(),
                    usernameAndID.length(),
                    SpannableStringBuilder.SPAN_INCLUSIVE_INCLUSIVE);
            holder.username.setText(usernameAndID);

            holder.time.setText("" + TimeWrapper.valueOf(reply.getPostTime()));
            holder.userSpan.setOnClickListener(view -> new UserActivity.UserIntent(getActivity(), reply.getUser().toString()).start());
            holder.options.setOnClickListener(v -> {
                if (mMorePopupWindow == null) {
                    mMorePopupWindow = new OptionsPopupWindow(getActivity(), reply, getArguments());
                    mMorePopupWindow.setOnDismissListener(() -> mMorePopupWindow = null);
                    // a series of setters to make the window do not intercept a outside scroll event on dismiss
                    mMorePopupWindow.setTouchable(true);
                    mMorePopupWindow.setOutsideTouchable(true);
                    mMorePopupWindow.setFocusable(false);
                    mMorePopupWindow.setTouchInterceptor((v1, event) -> {
                        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                            mMorePopupWindow.dismiss();
                            return true;
                        }
                        return false;
                    });
                    mMorePopupWindow.showAsDropDown(v, -OptionsPopupWindow.widthCache - OptionsPopupWindow.marginLeftCache, -v.getHeight());
                    mMorePopupWindow.update(); // todo maybe useless
                }
            });

        }
    }

}
