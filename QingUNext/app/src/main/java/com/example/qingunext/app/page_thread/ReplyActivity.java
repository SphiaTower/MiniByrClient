package com.example.qingunext.app.page_thread;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.notifications.MailBoxActivity;
import com.example.qingunext.app.page_home.NewQingUActivity;
import com.example.qingunext.app.pages_other.InputActivity;
import com.example.qingunext.app.pages_other.SignInActivity;
import com.example.qingunext.app.pull_to_refresh_auto_pager.LiteRefreshableAutoPagerActivity;
import com.example.qingunext.app.util.IntentHelper;
import com.example.qingunext.app.util_global.QingUDataCenter;
import tower.sphia.auto_pager_recycler.lib.AutoPagerRefreshableFragment;

import java.util.List;

/**
 * Created by Rye on 2/17/2015.
 */
public class ReplyActivity extends LiteRefreshableAutoPagerActivity {
    public static final String
            ARG_BOARD_NAME = "board_name",
            ARG_GROUP_ID = "id",
    /**
     * Added for instant display on new Reply Page
     */
    ARG_TITLE = "title";


    private String mBoardName;
    private int mGroupID;
    private String mTitle;


    @Override
    protected String getCustomTitle() {
        return QingUApp.getInstance().getBoardFullDescription(mBoardName);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_TITLE, mTitle);
        outState.putString(ARG_BOARD_NAME, mBoardName);
        outState.putInt(ARG_GROUP_ID, mGroupID);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Uri data = getIntent().getData();
            if (data != null) {
                List<String> pathSegments = data.getPathSegments();
                mBoardName = pathSegments.get(1);
                mGroupID = Integer.parseInt(pathSegments.get(2));
                mTitle = "I don't know the Title";
            } else {
                Bundle bundle = getIntent().getExtras();
                mBoardName = bundle.getString(ARG_BOARD_NAME);
                mGroupID = bundle.getInt(ARG_GROUP_ID);
                mTitle = bundle.getString(ARG_TITLE);
            }
        } else {
            mBoardName = savedInstanceState.getString(ARG_BOARD_NAME);
            mGroupID = savedInstanceState.getInt(ARG_GROUP_ID);
            mTitle = savedInstanceState.getString(ARG_TITLE);
        }
        super.onCreate(savedInstanceState);
        ((Toolbar) findViewById(R.id.subToolbar)).setTitle(mTitle);
    }

    @Override
    protected int getContentView() {
        return R.layout.page_reply;
    }


    @Override
    protected AutoPagerRefreshableFragment initFragment() {
        return ReplyGroupFragment.newInstance(mBoardName, mGroupID, mTitle);
    }

//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case (android.R.id.home):
//                new NewQingUActivity.HomeIntent(this).start();
//                break;
//            case (R.id.switchAccount):
//                new SignInActivity.SignInIntent(this).start();
//                break;
//
//            case (R.id.mailBox):
//                MailBoxActivity.MailBoxIntent.createMailBoxIntent(this).start();
//                break;
//
//            case (R.id.clearCache):
//                QingUDataCenter.getInstance().getSqlHelper().clear();
//                break;
//            case (R.id.menu_item_share):
//                // Fetch and store ShareActionProvider
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "From QingU\n" + mTitle + ": http://bbs.byr.cn/#!article/" + mBoardName + "/" + mGroupID);
//                sendIntent.putExtra(Intent.EXTRA_TITLE, mTitle);
//                sendIntent.setType("text/plain");
//                startActivity(Intent.createChooser(sendIntent, "my chooser"));
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onClickNew(View view) {
        InputActivity.InputIntent.startNewReplyIntent(this, mBoardName, mGroupID, mTitle);

    }

    @Override
    public void onClickBookmark(View view) {
        new Thread(
                () -> QingUDataCenter.getInstance().getSqlHelper().getBookmarkTableHelper().replace(mTitle, mBoardName, mGroupID)
        ).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_page_menu, menu);
        return true;
    }


    public static class ReplyIntent extends IntentHelper {

        private ReplyIntent(Context from, @NonNull String boardName, int replyGroupId, String title) {
            super(from, ReplyActivity.class);
            putString(ARG_BOARD_NAME, boardName);
            putInt(ARG_GROUP_ID, replyGroupId);
            putString(ARG_TITLE, title);
        }

        public static void start(Context from, @NonNull String boardName, int replyGroupId, String title) {
            new ReplyIntent(from, boardName, replyGroupId, title).start();
        }
    }

}
