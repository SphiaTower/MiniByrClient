package com.example.qingunext.app.database;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Rye on 6/7/2015.
 */
public class BookmarkTableHelper implements SQLHelper.TableHandler {

    public static final String TABLE_NAME = "table_bookmark";
    private SQLHelper helper;

    public BookmarkTableHelper(SQLHelper helper) {
        this.helper = helper;
    }

    @Override
    public String getTableUpgradeSql() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    @Override
    public String getTableCreationSql() {
        return SQLHelper.addSpaceToSql("create table", TABLE_NAME,
                SQLHelper.bracket(SQLHelper.addSpaceToSql(
                        TableField.GROUP_ID.FIELD_NAME, "integer primary key,",
                        TableField.BOARD_NAME.FIELD_NAME, "text not null,",
                        TableField.TITLE.FIELD_NAME, "text not null")));
    }

/*    public String query(String id) throws DataNotFoundException {
        try {
            Cursor cursor = helper.getReadableDatabase().rawQuery(
                    SQLHelper.addSpaceToSql("select", TableField.URL.FIELD_NAME, "from", TABLE_NAME,
                            "where", TableField.ID.FIELD_NAME, "=", id), null);
            cursor.moveToNext();
            String url = cursor.getString(0);
            cursor.close(); //用不用finally处理呢？
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataNotFoundException();
        }
    }*/

    /**
     * @return
     * @throws DatabaseNotInitException if method throws sqlException or any other RuntimeException, neither are checked
     */
    public Cursor select() throws DatabaseNotInitException {
        try {
            return helper.getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, null);
        } catch (Exception e) {
            throw new DatabaseNotInitException();
        }
    }

    public void replace(String title, String boardName, int groupID) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableField.GROUP_ID.FIELD_NAME, groupID);
        contentValues.put(TableField.BOARD_NAME.FIELD_NAME, boardName);
        contentValues.put(TableField.TITLE.FIELD_NAME, title);
        helper.getWritableDatabase().replace(TABLE_NAME, null, contentValues);
    }

    public enum TableField {
        TITLE(2, "TITLE"), BOARD_NAME(1, "BOARD_NAME"), GROUP_ID(0, "GROUP_ID");

        public final int INDEX;
        public final String FIELD_NAME;

        TableField(int index, String name) {
            this.INDEX = index;
            this.FIELD_NAME = name;
        }

    }
}
