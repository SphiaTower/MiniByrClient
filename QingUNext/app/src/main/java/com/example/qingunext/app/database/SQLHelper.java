package com.example.qingunext.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.qingunext.app.QingUApp;

/**
 * Created by Rye on 4/5/2015.
 * 处理数据库交互的辅助类
 * todo ContentProvider
 */

public class SQLHelper extends SQLiteOpenHelper {
    public final static String LOCK = "LOCK";
    private final static String DATABASE_NAME = "pp_db";
    private final static int DATABASE_VERSION = 5;
    private TableHandler[] tableHandlers = new TableHandler[]{
            new JsonTableHelper(this),
            new BookmarkTableHelper(this),
            new BoardTableHelper(this)
    };

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if(QingUApp.DEBUG) Log.v("SQLHelper", "Constructor");

    }

    public static String addSpaceToSql(String... strings) {
        String result = "";
        for (String s : strings) {
            result += s;
            result += " ";
        }
        return result;
    }

    public static String bracket(String string) {
        return "(" + string + ")";
    }

    public JsonTableHelper getJsonTableHelper() {
        return (JsonTableHelper) tableHandlers[0];
    }

    public BookmarkTableHelper getBookmarkTableHelper() {
        return (BookmarkTableHelper) tableHandlers[1];
    }

    public BoardTableHelper getBoardTableHelper() {
        return (BoardTableHelper) tableHandlers[2];
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if(QingUApp.DEBUG) Log.v("SQLHelper", "onCreate");
        for (TableHandler handler : tableHandlers) {
            sqLiteDatabase.execSQL(handler.getTableCreationSql());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(QingUApp.DEBUG) Log.v("SQLHelper", "onUpgrade");
        for (TableHandler handler : tableHandlers) {
            sqLiteDatabase.execSQL(handler.getTableUpgradeSql());
        }
        onCreate(sqLiteDatabase);
    }


    public void clear() {
        for (TableHandler handler : tableHandlers) {
            getWritableDatabase().execSQL(handler.getTableUpgradeSql());
        }
        onCreate(getWritableDatabase());
        getWritableDatabase().close();
    }

    /**
     * Created by Rye on 6/7/2015.
     */
    public interface TableHandler {
        String getTableCreationSql();

        String getTableUpgradeSql();
    }
}
