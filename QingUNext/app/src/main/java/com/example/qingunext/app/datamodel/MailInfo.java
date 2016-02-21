package com.example.qingunext.app.datamodel;

import com.example.qingunext.app.util.TimeWrapper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rye on 5/24/2015.
 */
public class MailInfo extends BaseForumData {
    private User user;
    private String title;
    private long postTime;
    private boolean isRead; // if init here, parse is called before this!
    private int index;

    public MailInfo(String json) throws JSONException {
        super(json);
    }

    public MailInfo(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public int getIndex() {
        return index;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getPostTime() {
        return "" + TimeWrapper.valueOf(postTime);
    }

    public boolean isRead() {
        return isRead;
    }

    protected void parse() throws JSONException {
        JSONObject json = getJsonObject();
        user =  User.valueOf(json.getJSONObject("user"));
        title = json.getString("title");
        postTime = json.getLong("post_time");
        isRead = json.getBoolean("is_read");
        index = json.getInt("index");
    }
}

