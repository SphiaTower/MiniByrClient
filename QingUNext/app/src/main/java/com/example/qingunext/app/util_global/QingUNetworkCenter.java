package com.example.qingunext.app.util_global;

import android.content.Context;
import android.widget.Toast;
import com.example.qingunext.app.R;
import com.example.qingunext.app.database.DataNotFoundException;
import com.example.qingunext.app.database.DatabaseNotInitException;

import java.io.IOException;

/**
 * Created by Voyager on 9/26/2015.
 */
public class QingUNetworkCenter {
    public static final String APP_KEY = "7a282a1a9de5b450",
            API_HEADER = "http://api.byr.cn";
    private static Context mCtx;
    private static QingUNetworkCenter mSingleton;

    public QingUNetworkCenter(Context context) {
        mCtx = context;
    }

    public synchronized static QingUNetworkCenter getInstance() {
        return mSingleton;
    }

    public static void initialize(Context context) {
        mSingleton = new QingUNetworkCenter(context);
    }

    public static String buildURL(String... strings) {
        String result = "";
        for (String s : strings) {
            result += "/";
            result += s;
        }
        return API_HEADER + result;
    }

    public static String buildAPI(String... strings) {
        return buildURL(strings) + ".json?appkey=" + APP_KEY;
    }

    @Deprecated
    public static String buildOptionalAPI(String optional, String... strings) {
        return buildURL(strings) + ".json?" + optional + "&appkey=" + APP_KEY;
    }

    public static String postArticle(String boardName, String title, String content) throws IOException {
        return NetworkBase.post(buildAPI("article", boardName, "post"), "title", title, "content", content);
    }

    public static String postArticle(String boardName, String title, String content, int id) throws IOException {
        return NetworkBase.post(buildAPI("article", boardName, "post"), "title", title, "content", content, "reid", "" + id);
    }

    public static String postEditArticle(String boardName, String title, String content, int id) throws IOException {
        return NetworkBase.post(buildAPI("article", boardName, "update", "" + id), "title", title, "content", content);
    }

    public static String postDeleteArticle(String boardName, int id) throws IOException {
        return NetworkBase.post(buildAPI("article", boardName, "delete", "" + id));
    }

    public static String postNewMail(String userID, String title, String content, boolean backUp) throws IOException {
        return NetworkBase.post(buildAPI("mail", "send"), "id", userID, "title", title, "content", content, "backup", backUp ? "1" : "0");
    }

    public void say(int strId) {
        Toast.makeText(mCtx, strId, Toast.LENGTH_SHORT).show();
    }

    public void popNetworkError() {
        say(R.string.network_unavailable);
    }

    public void popDataNotFoundError() {
        say(R.string.no_data_available);
    }

    public void popJsonError() {
        say(R.string.json_exception);
    }

    public void popUnknownError() {
        say(R.string.unknown_exception);
    }

    /**
     * @param url the url address containing appkey
     * @return json from server
     * @throws IOException TODO: Appkey of this app may change, leaving all the url stored invalid. Appkey should be detached from URL.
     *                     DataNotFoundException When the db has not been created
     */
    public String downloadJsonAndSave(final String url) throws IOException {
        final String json = NetworkBase.downloadJson(url);
        QingUDataCenter.getInstance().getThreadExecutor().execute(() -> {
            try {
                QingUDataCenter.getInstance().getSqlHelper().getJsonTableHelper().replace(url, json);
            } catch (DatabaseNotInitException ignored) {

            }
        });
        return json;
    }

    /**
     * TODO: 应该把网络访问与数据库读取分开，以提升响应速度，减少耦合; 读写同步的问题
     *
     * @param url The full url of API
     * @return Json string from server
     * @throws DataNotFoundException 返回null不是一种合理的结果，故应抛出异常而非返回null
     *                               几乎所有的网络访问，都可以产生数据库缓存，所以加入了写数据库
     */
    public String downloadJsonIfOkSaveElseQueryDB(String url) throws DataNotFoundException {
        try {
            return downloadJsonAndSave(url);
        } catch (IOException e) {
            return QingUDataCenter.getInstance().getSqlHelper().getJsonTableHelper().query(url);
        }
    }

    public String queryJsonFromDBIfFailedDownloadAndSave(String url) throws DataNotFoundException {
        try {
            return QingUDataCenter.getInstance().getSqlHelper().getJsonTableHelper().query(url);
        } catch (DataNotFoundException e) {
            try {
                return downloadJsonAndSave(url);
            } catch (IOException e1) {
                throw new DataNotFoundException();
            }
        }
    }

    public static class UrlBuilder {

        private String necessary;
        private String optional = "";

        public UrlBuilder(String... strings) {
            necessary = buildURL(strings) + ".json?";
        }

        public UrlBuilder optional(String... strings) {
            for (String str : strings) {
                optional += str + "&";
            }
            return this;
        }

        public String build() {
            return necessary + optional + "appkey=" + APP_KEY;
        }
    }
}
