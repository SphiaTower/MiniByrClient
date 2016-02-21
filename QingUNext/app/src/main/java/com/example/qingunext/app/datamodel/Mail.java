package com.example.qingunext.app.datamodel;

import com.example.qingunext.app.util.TimeWrapper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rye on 5/25/2015.
 */
public class Mail extends BaseForumData {
    private User user;
    private String title;
    private long postTime;
    private boolean isRead;
    private int index;
    private String content;

    public Mail(String json) throws JSONException {
        super(json);
    }

    public Mail(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public String getContent() {
        return content;
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
        title = json.getString("title");
        content = json.getString("content");
        /*
        user = new User(json.getJSONObject("user"));
        postTime = json.getLong("postTime");
        isRead = json.getBoolean("is_read");
        index = json.getInt("index");*/
    }
}
