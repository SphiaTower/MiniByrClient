package com.example.qingunext.app.page_home;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.notifications.MailBoxActivity;
import com.example.qingunext.app.notifications.NotiActivity;
import com.example.qingunext.app.page_board.BoardActivity;

import java.util.List;

/**
 * Created by Voyager on 1/3/2016.
 */
public class NavigatorActivity extends AppCompatActivity {
    public static final int LOADER_BOARD = 2;
    private static final String TAG = "NavigatorActivity";
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private OnBoardClickListener mOnBoardClickListener;
    private LoaderManager.LoaderCallbacks<List<String>> mBoardLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<String>>() {
        @Override
        public Loader<List<String>> onCreateLoader(int i, Bundle bundle) {
            return new FavBoardLoader(NavigatorActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<List<String>> loader, List<String> boardNames) {
            mOnBoardClickListener = item -> {
                String boardName = boardNames.get(item.getItemId());
                BoardActivity.from(NavigatorActivity.this, boardName);
            };
            initDrawerSubMenu(boardNames);
        }

        @Override
        public void onLoaderReset(Loader<List<String>> loader) {

        }
    };

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        onCreateDrawer();
    }

    protected void onCreateDrawer() {
        Toolbar toolbar = initToolbar();
        initNavigationView(toolbar);
        LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(LOADER_BOARD, null, mBoardLoaderCallbacks);
    }

    protected Toolbar initToolbar() {
        if (QingUApp.DEBUG) Log.d(TAG, "initToolbar ");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }
        return toolbar;
    }

    /**
     * Init the subMenu of favorite boards
     */
    private void initNavigationView(Toolbar toolbar) {
        if (QingUApp.DEBUG) Log.d(TAG, "initNavigationView ");
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (this instanceof NewQingUActivity) {
            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
            drawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            switch (item.getItemId()) {
                case R.id.navigation_item_1:
                    // TODO: 1/2/2016 delete
                    NewQingUActivity.from(this);
                    break;
                case R.id.navigation_item_2:
                    MailBoxActivity.from(this);
                    break;
                case R.id.navigation_item_3:
                    NotiActivity.from(this);
                    break;
                default:
                    if (mOnBoardClickListener != null) {
                        mOnBoardClickListener.onClick(item);
                    }
                /* return instead of break to prevent executing code below */
                    return false;
            }
            drawerLayout.closeDrawers();
            return false;
        });
    }

    private void initDrawerSubMenu(List<String> boardNames) {
        /* adding sub items */
        Menu menu = mNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(menu.size() - 1);
        SubMenu subMenu = menuItem.getSubMenu();
        /* clear the sample item */
        subMenu.clear();
        int groupId = menuItem.getGroupId();
        int itemId = 0;
        for (String boardName : boardNames) {
            MenuItem add = subMenu.add(groupId, itemId++, Menu.NONE, QingUApp.getInstance().getBoardFullDescription(boardName));
            add.setIcon(R.drawable.ic_bookmark_border_black_24dp);
        }
        /* a hack for the bug of not updating menu */
        menuItem.setTitle(menuItem.getTitle());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    private interface OnBoardClickListener {
        void onClick(MenuItem item);
    }

}
