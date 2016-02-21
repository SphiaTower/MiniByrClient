package com.example.qingunext.app.datamodel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rye on 2/16/2015.
 */
public class WidgetItem extends BaseForumData implements PostInfo {
    private int color;
    private int id;
    private String title;
    private String boardName;
    private int replyCount;
    private boolean hasAttachment;
    private boolean isTop;
    private boolean isFresh;
    private boolean isDeleted;

    public WidgetItem(String json) throws JSONException {
        super(json);
    }

    public WidgetItem(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public int getId() {
        return id;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public boolean isTop() {
        return isTop;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public boolean isFresh() {
        return isFresh;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public String getBoardName() {
        return boardName;
    }

    public int getReplyCount() {
        return replyCount;
    }

    protected void parse() throws JSONException {
        JSONObject json = getJsonObject();
        isTop = false;
        isDeleted = false;
        id = json.getInt("id");
        title = json.getString("title");
        boardName = json.getString("board_name");
        replyCount = json.getInt("reply_count");
        isFresh = --replyCount == 0;
        hasAttachment = json.getBoolean("has_attachment");
        accept(DataColorVisitor.getInstance());
    }

    public void accept(DataVisitor dataVisitor) {
        dataVisitor.visit(this);
    }

}
