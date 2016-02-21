package com.example.qingunext.app.page_board;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.notifications.NotiActivity;
import com.example.qingunext.app.page_home.NewQingUActivity;
import com.example.qingunext.app.pages_other.InputActivity;
import com.example.qingunext.app.pages_other.SignInActivity;
import com.example.qingunext.app.pull_to_refresh_auto_pager.LiteRefreshableAutoPagerActivity;
import com.example.qingunext.app.util.IntentHelper;
import com.example.qingunext.app.util_global.QingUDataCenter;
import tower.sphia.auto_pager_recycler.lib.AutoPagerRefreshableFragment;

/**
 * Created by Rye on 2/20/2015.
 */
public class BoardActivity extends LiteRefreshableAutoPagerActivity {
    public static final String ARG_BOARD_NAME = "board_name";

    private String mBoardName;

    public static void from(Context context, String boardName) {
        Intent intent = new Intent(context, BoardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ARG_BOARD_NAME, boardName);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected String getCustomTitle() {
        return QingUApp.getInstance().getBoardFullDescription(mBoardName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mBoardName = getIntent().getExtras().getString(ARG_BOARD_NAME);
        } else {
            mBoardName = savedInstanceState.getString(ARG_BOARD_NAME);
        }
        super.onCreate(savedInstanceState);
        boolean contains = QingUDataCenter.getInstance().getSqlHelper().getBoardTableHelper().contains(mBoardName);
        System.out.println("contains = " + contains);
        if (contains) {
            findViewById(R.id.ivBottomBookmark).setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.page_board;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_BOARD_NAME, mBoardName);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected AutoPagerRefreshableFragment initFragment() {
        return BoardFragment.newInstance(mBoardName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home):
                NewQingUActivity.from(this);
                break;
            case (R.id.switchAccount):
                SignInActivity.from(this);
                break;
            case (R.id.mailBox):
                NotiActivity.from(this);
                break;
            case (R.id.menu_item_share):
                // Fetch and store ShareActionProvider
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "test");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "my chooser"));
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_page_menu, menu);

        return true;
    }

    @Override
    public void onClickNew(View view) {
        super.onClickNew(view);
        InputActivity.InputIntent.startNewPostIntent(this, mBoardName);
    }

    @Override
    public void onClickBookmark(View view) {
        super.onClickBookmark(view);
//        QingUApp.getInstance().asyncAddFavBoard(mBoardName);
    }


}

