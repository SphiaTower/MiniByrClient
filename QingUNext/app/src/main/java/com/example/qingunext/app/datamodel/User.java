package com.example.qingunext.app.datamodel;

import android.util.Log;
import com.example.qingunext.app.QingUApp;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rye on 2/16/2015.
 * ATTENTION: Some user is 'dead', they don't have properties like username or faceUrl
 */
public class User extends BaseForumData {

    private final static Map<String, SoftReference<User>> userIDCacheMap = new HashMap<>();
    private String id;
    private String username;
    private String faceURL;
    private boolean isMale;
    private boolean isOnline;

    private User(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    private User(String json) throws JSONException {
        super(json);
    }

    public static User valueOf(String json) throws JSONException {
        return valueOf(new JSONObject(json));
    }

    public static User valueOf(JSONObject jsonObject) throws JSONException {
        String id = jsonObject.getString("id");
        // can also use hashSet, but maybe this is more effective
        SoftReference<User> userSoftReference = userIDCacheMap.get(id);
        if (userSoftReference != null) {
            User user = userSoftReference.get();
            if (user != null) {
                if (QingUApp.DEBUG) Log.v("User", "User restored from cache: " + id);
                return user;
            }
        }
        User user = new User(jsonObject);
        if (QingUApp.DEBUG) Log.v("User", "new User Instantiated: " + id);
        userIDCacheMap.put(id, new SoftReference<>(user));
        return user;
    }

    public String getId() {
        return id;
    }


    public String getUsername() {
        return username;
    }

    public String getFaceURL() {
        return faceURL;
    }


    @Override
    protected void parse() throws JSONException {
        JSONObject jsonObject = getJsonObject();
        id = jsonObject.getString("id");
        try {
            username = jsonObject.getString("user_name");
            faceURL = jsonObject.getString("face_url");
//            isMale = json.getString("gender").equals("m");
//            isOnline = json.getBoolean("is_online");
        } catch (JSONException e) {
            username = "[用户已注销]";
            faceURL = "http://static.byr.cn/img/face_default_m.jpg";
        }
    }
}
