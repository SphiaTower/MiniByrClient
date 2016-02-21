package com.example.qingunext.app.util;

import android.database.Cursor;
import android.util.Log;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.database.JsonTableHelper;
import com.example.qingunext.app.util_global.QingUDataCenter;

/**
 * Created by Voyager on 1/3/2016.
 */
public class Developer {


    /**
     * this method is only used by developer
     */
    private void printDataBase() {
        Cursor cursor = QingUDataCenter.getInstance().getSqlHelper().getJsonTableHelper().select();
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            if (QingUApp.DEBUG) Log.v("SQLHelper", cursor.getString(JsonTableHelper.TableField.URL.getIndex()));
            if (QingUApp.DEBUG) Log.v("SQLHelper", cursor.getString(JsonTableHelper.TableField.JSON.getIndex()));
        }
    }

    /**
     * get & print the BoardName-BoardDescription Map
     * this method is only used by developer
     */
    private void crawlBoardNameDescriptionPairs() {
//        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            Crawler object = new Crawler("" + i);
            Thread thread = new Thread(object);
            thread.start();
        }
        while (true) {
            if (Crawler.finishedCount == 10) {
                for (Crawler crawler : Crawler.getPool()) {
                    System.out.println(">>>>" + crawler.getSectionName());
                    for (String key : crawler.getMap().keySet()) {
                        System.out.println(key);
                    }
                }

                break;
            }
        }
    }
}
