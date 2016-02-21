package com.example.qingunext.app.database;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Rye on 6/7/2015.
 */
public class JsonTableHelper implements SQLHelper.TableHandler {

    private final static String TABLE_NAME = "table_json";
    private SQLHelper helper;

    public JsonTableHelper(SQLHelper helper) {
        this.helper = helper;
    }

    @Override
    public String getTableCreationSql() {
        return SQLHelper.addSpaceToSql("create table", TABLE_NAME,
                SQLHelper.bracket(SQLHelper.addSpaceToSql(
                        TableField.URL.getName(), "text not null primary key,",
                        TableField.JSON.getName(), "text not null,",
                        TableField.ABSOLUTE_TIME.getName(), "long"
                )));
    }

    @Override
    public String getTableUpgradeSql() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    // todo not init exception
    public String query(String url) throws DataNotFoundException {
        try {
            Cursor cursor = helper.getReadableDatabase().rawQuery(
                    SQLHelper.addSpaceToSql("select", TableField.JSON.getName(), "from", TABLE_NAME,
                            "where", TableField.URL.getName(), "=", "\"" + url + "\""), null);
            cursor.moveToNext();
            String json = cursor.getString(0);
            cursor.close(); //用不用finally处理呢？
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataNotFoundException();
        }
    }

    public Cursor select() {
        return helper.getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, null);
    }

    public long insert(String jsonName, String jsonText) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableField.URL.getName(), jsonName);
        contentValues.put(TableField.JSON.getName(), jsonText);
        contentValues.put(TableField.ABSOLUTE_TIME.getName(), System.currentTimeMillis());
        return helper.getWritableDatabase().insert(TABLE_NAME, null, contentValues);
    }

    public void delete(int id) {
    }

    public void update(String url, String json) {
        String where = TableField.URL.getName() + " = ?";
        String[] whereValue = {url};
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableField.URL.getName(), url);
        contentValues.put(TableField.JSON.getName(), json);
        contentValues.put(TableField.ABSOLUTE_TIME.getName(), System.currentTimeMillis());
        helper.getWritableDatabase().update(TABLE_NAME, contentValues, where, whereValue);
    }

    /**
     * @param url  must be set as primary key, or {@code replace} -> insert
     * @param json json str
     */
    public void replace(String url, String json) throws DatabaseNotInitException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableField.URL.getName(), url);
        contentValues.put(TableField.JSON.getName(), json);
        contentValues.put(TableField.ABSOLUTE_TIME.getName(), System.currentTimeMillis());
        long replace = helper.getWritableDatabase().replace(TABLE_NAME, null, contentValues);
        if (replace == -1) {
            throw new DatabaseNotInitException();
        }

    }

    public enum TableField {
        URL(0, "JSON_NAME"), JSON(1, "JSON_TEXT"), ABSOLUTE_TIME(2, "ABSOLUTE_TIME");
        private int index;

        public String getName() {
            return name;
        }

        private String name;

        TableField(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }
    }
}
