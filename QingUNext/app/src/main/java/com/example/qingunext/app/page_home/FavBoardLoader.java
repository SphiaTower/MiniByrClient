package com.example.qingunext.app.page_home;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.database.DatabaseNotInitException;
import com.example.qingunext.app.util_global.QingUDataCenter;
import tower.sphia.auto_pager_recycler.lib.AsyncTaskLoaderImpl;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Voyager on 8/19/2015.
 */
public class FavBoardLoader extends AsyncTaskLoaderImpl<List<String>> {
    /* tmp code for restoring metro data*/
    private FavBoardObserver mFavBoardObserver;

    public FavBoardLoader(Context ctx) {
        super(ctx);
    }

    @Override
    protected void releaseResources(List<String> data) {

    }

    @Override
    protected void registerObserver() {
        super.registerObserver();
        if (mFavBoardObserver == null) {
            mFavBoardObserver = new FavBoardObserver(this);
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        if (mFavBoardObserver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mFavBoardObserver);
            mFavBoardObserver = null;
        }
    }

    @Override
    public List<String> loadInBackground() {
        List<String> boardTitles = new LinkedList<>();
        try {
            Cursor cursor = QingUDataCenter.getInstance().getSqlHelper().getBoardTableHelper().select();
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                do {
                    String title = cursor.getString(1);
                    boardTitles.add(title);
                    if (QingUApp.DEBUG) Log.v("database", title);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return boardTitles;
        } catch (DatabaseNotInitException ignored) {
            return null;
        }
    }
}
