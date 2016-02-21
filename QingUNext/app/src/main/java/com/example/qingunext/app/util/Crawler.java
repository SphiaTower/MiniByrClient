package com.example.qingunext.app.util;

import android.util.Log;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.database.DataNotFoundException;
import com.example.qingunext.app.datamodel.BoardInfo;
import com.example.qingunext.app.datamodel.Section;
import com.example.qingunext.app.util_global.QingUNetworkCenter;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rye on 4/10/2015.
 * A crawler for all board information
 *
 */
public class Crawler implements Runnable {
    private static final String LOCK = "lock";
    private static final String LOCK2 = "lock2";
    public static int finishedCount = 0;
    private String number;

    public String getSectionName() {
        return sectionName;
    }

    private String sectionName;

    public Map<String, String> getMap() {
        return map;
    }

    private Map<String, String> map;


    private Crawler(Map<String, String> map, String number) {
        this.number = number;
        this.map = map;
    }

    public static List<Crawler> getPool() {
        return pool;
    }

    private static List<Crawler> pool = new ArrayList<>();

    public Crawler(String number) {
        this.number = number;
        this.map = new HashMap<>();
        pool.add(this);
    }

    @Override
    public void run() {
        try {
            String json = QingUNetworkCenter.getInstance().downloadJsonIfOkSaveElseQueryDB(QingUNetworkCenter.buildAPI("section", "" + number));
            Section section;
            try {
                section = new Section(json);
            } catch (JSONException e) {
                // todo
                return;
            }
            sectionName = number;
            if (!section.getSubSectionNames().isEmpty()) {
                for (String subSectionName : section.getSubSectionNames()) {
                    synchronized (LOCK2) {
                        finishedCount--;
                    }
                    Crawler runnable = new Crawler(subSectionName);
                    new Thread(runnable).start();
                }
            }
            if (section.getBoardInfos() == null) {
                if(QingUApp.DEBUG) Log.v("CRAWLER", "RETURN " + number);
                if(QingUApp.DEBUG) Log.v("CRAWLER", section.toString());
            } else {
                for (BoardInfo boardInfo : section.getBoardInfos()) {
                    synchronized (LOCK) {
                        map.put(boardInfo.getName(), boardInfo.getDescription());
                    }
                }
            }
            synchronized (LOCK2) {
                finishedCount++;
            }
        } catch (DataNotFoundException e) {
            e.printStackTrace();
        }
    }
}
