package com.example.qingunext.app.datamodel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rye on 6/20/2015.
 */
public class NotiItem extends BaseForumData {
    private int groupId;
    private int id;
    private User user;
    private String title;

    public boolean isRead() {
        return isRead;
    }

    private boolean isRead;

    public String getBoardName() {
        return boardName;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public int getPostTime() {
        return postTime;
    }

    private String boardName;
    private int postTime;



    public NotiItem(String json) throws JSONException {
        super(json);
    }

    public NotiItem(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    @Override
    protected void parse() throws JSONException {
        JSONObject jsonObject = getJsonObject();
        groupId = jsonObject.getInt("group_id");
        id = jsonObject.getInt("id");
        boardName = jsonObject.getString("board_name");
        postTime = jsonObject.getInt("time");
        user =  User.valueOf(jsonObject.getJSONObject("user"));
        isRead = jsonObject.getBoolean("is_read");
        title = jsonObject.getString("title");
    }

    public String getTitle() {
        return title;
    }
}
