package com.example.qingunext.app.database;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Rye on 6/30/2015.
 */
public class BoardTableHelper implements SQLHelper.TableHandler {
    public final static String
            FIELD_id = "_id",
            FIELD_BOARD_NAME = "TITLE",
            TABLE_NAME = "table_board";
    private SQLHelper helper;

    public BoardTableHelper(SQLHelper helper) {
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
                        FIELD_id, "integer primary key autoincrement,",
                        FIELD_BOARD_NAME, "text not null")));
    }

    public boolean contains(String boardName) {
        try {
            helper.getReadableDatabase().rawQuery(
                    SQLHelper.addSpaceToSql("select", FIELD_BOARD_NAME, "from", TABLE_NAME,
                            "where", FIELD_BOARD_NAME, "=", boardName), null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

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

    public boolean replace(String boardName) {
        if (contains(boardName)) return false; // todo could be changed to delete when false
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_BOARD_NAME, boardName);
        helper.getWritableDatabase().replace(TABLE_NAME, null, contentValues);
        return true;
    }
    // todo delete
}
