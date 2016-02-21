package com.example.qingunext.app.page_home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Voyager on 8/19/2015.
 */
public class FavBoardObserver extends BroadcastReceiver {
    public static final String ACTION_BOARD_BOOKMARKED = "com.example.qingunext.app.BOARD_BOOKMARKED";
    private FavBoardLoader mLoader;

    public FavBoardObserver(FavBoardLoader loader) {
        this.mLoader = loader;
        IntentFilter filter = new IntentFilter(ACTION_BOARD_BOOKMARKED);
        LocalBroadcastManager.getInstance(mLoader.getContext()).registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mLoader.onContentChanged();
    }
}
