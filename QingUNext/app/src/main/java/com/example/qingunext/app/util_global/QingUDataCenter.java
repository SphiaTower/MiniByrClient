package com.example.qingunext.app.util_global;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.database.SQLHelper;
import com.example.qingunext.app.page_home.FavBoardObserver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Voyager on 9/26/2015.
 */
public class QingUDataCenter {
    private static QingUDataCenter sInstance;
    private ExecutorService mExecutor;
    private Context mContext;
    private SQLHelper mSqlHelper;

    private QingUDataCenter(Context context) {
        mContext = context;
        mSqlHelper = new SQLHelper(context);
    }

    // todo select one way to handle sync problem
    public static QingUDataCenter getInstance() {
        return sInstance;
    }

    public static void initialize(Context context) {
        sInstance = new QingUDataCenter(context);
    }

    public SQLHelper getSqlHelper() {
        return mSqlHelper;
    }

    public void asyncAddFavBoard(String boardName) {
        if (QingUApp.DEBUG) Log.i("BroadCast", "asyncAddFavBoard " + boardName);
        mExecutor.execute(() -> {
            mSqlHelper.getBoardTableHelper().replace(boardName);
            Intent intent = new Intent();
            intent.setAction(FavBoardObserver.ACTION_BOARD_BOOKMARKED);
            LocalBroadcastManager.getInstance(mContext.getApplicationContext()).sendBroadcast(intent);
        });
    }

    /**
     * use singleThreadExecutor to sync all the database writing tasks
     * how about reading tasks?
     *
     * @return singleton of singleThreadExecutor
     */
    public ExecutorService getThreadExecutor() {
        if (mExecutor == null) {
            mExecutor = Executors.newCachedThreadPool();
        }
        return mExecutor;
    }
}
