package com.example.qingunext.app;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import com.example.qingunext.app.util_global.NetworkBase;
import com.example.qingunext.app.util_global.QingUDataCenter;
import com.example.qingunext.app.util_global.QingUImageCenter;
import com.example.qingunext.app.util_global.QingUNetworkCenter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rye on 2/16/2015.
 */
public class QingUApp extends Application {
    public static final boolean DEBUG = false;

    @Deprecated
    public static final byte
            LOADING_COMPLETE = 0x01,
            NETWORK_UNAVAILABLE = 0x02,
            JSON_EXCEPTION = 0x03,
            NO_DATA_AVAILABLE = 0x04;

    public static final String
            SHARED_PREF_NAME = "pref1",
            SHARED_PREF_KEY_USERNAME = "username",
            SHARED_PREF_KEY_PASSWORD = "password",
            SHARED_PREF_KEY_LAST_DOWNLOAD_TIME = "time";


    // PAAD 3.7.1节中，将application处理为singleton
    private static QingUApp singleton;
    private String mUsername, mPassword;
    private Map<String, String> mBoardNameSimplifiedDescriptionMap, mBoardNameFullDescriptionMap;

    public static QingUApp getInstance() {
        return singleton;
    }


    @Deprecated
    public static Toolbar findToolbarIn(Activity activity) {
        return (Toolbar) activity.findViewById(R.id.toolbar);
    }


    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getBoardSimplifiedDescription(String boardName) {
        String description = mBoardNameSimplifiedDescriptionMap.get(boardName);
        if (description == null) {
            description = boardName;
        }
        return description;
    }

    public String getBoardFullDescription(String boardName) {
        String description = mBoardNameFullDescriptionMap.get(boardName);
        if (description == null) {
            description = boardName;
        }
        return description;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (QingUApp.DEBUG) Log.i("QingUApp", "onCreate");
        singleton = this;
/*        GlobalExceptionHandler handler = GlobalExceptionHandler.getInstance();
        Thread.setDefaultUncaughtExceptionHandler(handler);*/
        QingUNetworkCenter.initialize(this);
        QingUDataCenter.initialize(this);
        QingUImageCenter.initialize(this);
        initializeMap();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        mUsername = sharedPreferences.getString(SHARED_PREF_KEY_USERNAME, null);
        mPassword = sharedPreferences.getString(SHARED_PREF_KEY_PASSWORD, null);
        if (mUsername != null) {
            NetworkBase.initCredential(mUsername, mPassword);
        }
    }


    private void initializeMap() {
        mBoardNameSimplifiedDescriptionMap = new HashMap<>();
        mBoardNameFullDescriptionMap = new HashMap<>();
        String[] boardNames = getResources().getStringArray(R.array.board_names);
        String[] simplifiedBoardDescriptions = getResources().getStringArray(R.array.board_descriptions_simple);
        String[] fullBoardDescriptions = getResources().getStringArray(R.array.board_descriptions);
        for (int i = 0; i < boardNames.length; i++) {
            mBoardNameSimplifiedDescriptionMap.put(boardNames[i], simplifiedBoardDescriptions[i]);
            mBoardNameFullDescriptionMap.put(boardNames[i], fullBoardDescriptions[i]);
        }
    }


    @Deprecated
    public JSONObject getLocalJSON(int resource) {
        try {
            InputStream is = getResources().openRawResource(resource);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            //将字节数组转换为以GB2312编码的字符串
            String json = new String(buffer, "UTF8");
            //将字符串json转换为json对象，以便于取出数据
            return new JSONObject(json);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//        String url = "http://api.byr.cn/article/MobileTerminalAT/post.json?appkey=7a282a1a9de5b450";


    public String getPassword() {
        return mPassword;
    }


}
