package com.example.qingunext.app.page_home;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.database.DataNotFoundException;
import com.example.qingunext.app.datamodel.Widget;
import com.example.qingunext.app.util_global.QingUDataCenter;
import com.example.qingunext.app.util_global.QingUNetworkCenter;
import org.json.JSONException;
import tower.sphia.auto_pager_recycler.lib.AsyncTaskLoaderImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;

/**
 * Created by Voyager on 8/18/2015.
 */
public class WidgetLoader extends AsyncTaskLoaderImpl<List<Widget>> {

    public static final int DATA_OUTDATE_THRESHOLD = 1000 * 60 * 5; // 5 miniutes
    public static final int
            WIDGET_START_NUMBER = 2,
            WIDGET_END_NUMBER = 9,
            WIDGETS_NUMBER = 8;
    private boolean mIgnoreCache = false;

    public WidgetLoader(Context context) {
        super(context);
    }

    public WidgetLoader setIgnoreCache(boolean ignoreCache) {
        this.mIgnoreCache = ignoreCache;
        return this;
    }

    @Override
    public List<Widget> loadInBackground() {
        // todo is this effective to restart the loading when data is not got?
//        if (widgets.size()==0) {
//            forceLoad();
//        }
        return downloadWidgetListData(mIgnoreCache);
    }

    @Override
    protected void releaseResources(List<Widget> data) {

    }


    /**
     * must be called in a non-UI Thread
     * using CountDownLatch and Future & Callable to get results of all tasks
     * Future::get will block the main thread, so it must be called after the CountDownLatch finishes
     *
     * @return data got or not -> if false: data is not found via either network or database
     */
    private List<Widget> downloadWidgetListData(boolean ignoreCache) {
        boolean allTasksComplete = true;
        boolean dataLoadedFromServer = true;
        TreeMap<Integer, Widget> widgetTreeMap = new TreeMap<>();
        List<Callable<WidgetDownloadResult>> widgetDownloaders = new ArrayList<>();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(QingUApp.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        DataSource dataSource;
        if (ignoreCache) {
            dataSource = DataSource.FROM_SERVER;
        } else {
            long lastDownloadTime = sharedPreferences.getLong(QingUApp.SHARED_PREF_KEY_LAST_DOWNLOAD_TIME, 0L);
            long interval = System.currentTimeMillis() - lastDownloadTime;
            dataSource = interval > DATA_OUTDATE_THRESHOLD ? DataSource.FROM_SERVER : DataSource.FROM_DB;
        }
        widgetDownloaders.add(new WidgetDownloader(widgetTreeMap, -1, dataSource));
        for (int i = WIDGET_START_NUMBER; i <= WIDGET_END_NUMBER; i++) {
            widgetDownloaders.add(new WidgetDownloader(widgetTreeMap, i, dataSource));
        }
        ExecutorService executor = Executors.newFixedThreadPool(widgetDownloaders.size());
        List<Future<WidgetDownloadResult>> futures = new ArrayList<>();
        for (Callable<WidgetDownloadResult> task : widgetDownloaders) {
            Future<WidgetDownloadResult> future = executor.submit(task);
            futures.add(future);
        }
        for (Future<WidgetDownloadResult> future : futures) {
            WidgetDownloadResult taskResult;
            try {
                taskResult = future.get(); // block method
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                taskResult = WidgetDownloadResult.FAILED;
            }
            switch (taskResult) {
                case FROM_DB:
                    dataLoadedFromServer = false;
                    break;
                case FROM_SERVER:
                    break;
                case FAILED:
                    allTasksComplete = false;
                    break;
            }
        }
        if (dataLoadedFromServer) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putLong(QingUApp.SHARED_PREF_KEY_LAST_DOWNLOAD_TIME, System.currentTimeMillis());
            edit.apply();
        }
        List<Widget> widgets = new ArrayList<>();
        if (allTasksComplete) {
            for (Map.Entry<Integer, Widget> integerWidgetEntry : widgetTreeMap.entrySet()) {
                Widget widget = integerWidgetEntry.getValue();
                widgets.add(widget);
            }
        }
        return widgets;

    }

    private enum DataSource {
        FROM_DB, FROM_SERVER
    }

    private enum WidgetDownloadResult {
        FROM_DB, FROM_SERVER, FAILED
    }

    private class WidgetDownloader implements Callable<WidgetDownloadResult> {
        private DataSource from;

        private TreeMap<Integer, Widget> dataContainer;
        private int id;


        public WidgetDownloader(TreeMap<Integer, Widget> dataContainer, int id, DataSource from) {
            this.dataContainer = dataContainer;
            this.id = id;
            this.from = from;
        }

        @Override
        public WidgetDownloadResult call() throws Exception {
            String url;
            if (id == -1) {
                url = QingUNetworkCenter.buildAPI("widget", "topten");
            } else {
                url = QingUNetworkCenter.buildAPI("widget", "section-" + id);
            }
            try {
                String strWidget = null;
                WidgetDownloadResult result = null;
                switch (from) {
                    case FROM_DB:
                        strWidget = QingUDataCenter.getInstance().getSqlHelper().getJsonTableHelper().query(url);
                        result = WidgetDownloadResult.FROM_DB;
                        break;
                    case FROM_SERVER:
                        try {
                            strWidget = QingUNetworkCenter.getInstance().downloadJsonAndSave(url);
                            result = WidgetDownloadResult.FROM_SERVER;
                        } catch (IOException e) {
                            strWidget = QingUDataCenter.getInstance().getSqlHelper().getJsonTableHelper().query(url);
                            result = WidgetDownloadResult.FROM_DB;
                        }
                        break;
                }
                synchronized (this) {
                    dataContainer.put(id, new Widget(strWidget));
                }
                return result;
            } catch (DataNotFoundException | JSONException e) {
                e.printStackTrace();
                return WidgetDownloadResult.FAILED;
            }
        }
    }

}