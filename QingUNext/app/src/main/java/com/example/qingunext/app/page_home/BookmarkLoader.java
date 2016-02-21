package com.example.qingunext.app.page_home;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.database.BookmarkRecord;
import com.example.qingunext.app.database.BookmarkTableHelper;
import com.example.qingunext.app.database.DatabaseNotInitException;
import com.example.qingunext.app.util_global.QingUDataCenter;
import tower.sphia.auto_pager_recycler.lib.AsyncTaskLoaderImpl;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Voyager on 8/18/2015.
 */
public class BookmarkLoader extends AsyncTaskLoaderImpl<List<BookmarkRecord>> {
    public BookmarkLoader(Context ctx) {
        super(ctx);
    }

    @Override
    protected void releaseResources(List<BookmarkRecord> data) {

    }

    @Override
    public List<BookmarkRecord> loadInBackground() {
        List<BookmarkRecord> bookmarkRecords = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = QingUDataCenter.getInstance().getSqlHelper().getBookmarkTableHelper().select();
            cursor.moveToLast();
            if (cursor.getCount() != 0) {
                do {
                    String boardName = cursor.getString(BookmarkTableHelper.TableField.BOARD_NAME.INDEX);
                    String title = cursor.getString(BookmarkTableHelper.TableField.TITLE.INDEX);
                    int groupID = cursor.getInt(BookmarkTableHelper.TableField.GROUP_ID.INDEX);
                    bookmarkRecords.add(new BookmarkRecord(title, boardName, groupID));
                    if (QingUApp.DEBUG) Log.v("DateBase", title);
                } while (cursor.moveToPrevious());
            }
        } catch (DatabaseNotInitException ignored) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bookmarkRecords;
    }
}
