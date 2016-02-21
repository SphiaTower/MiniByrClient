package com.example.qingunext.app.datamodel;

import android.support.annotation.Nullable;
import com.example.qingunext.app.util.TimeWrapper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rye on 2/20/2015.
 * 板块页面中的贴子条目
 * <p>
 * 未初始化bool变量：bool混乱
 * 在field初始化bool变量：全部为false
 * 最后在BoardFragment中用else选项来解决
 * 说明了这个继承的设计有问题
 */
public class BoardItem extends BaseForumData implements PostInfo {
    private int id;
    private boolean isTop;
    private boolean isFresh;
    private boolean hasAttachment;
    private boolean isDeleted;
    private String userID;
    private long postTime;
    private int replyCount;
    private String title;
    private int color;
    private String boardName;
    private String lastReplyUserID;
    private String lastReplyTime;

    public BoardItem(String json) throws JSONException {
        super(json);
    }

    public BoardItem(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public boolean isFresh() {
        return isFresh;
    }


    protected void parse() throws JSONException {
        JSONObject jsonObject = getJsonObject();
        title = jsonObject.getString("title");
        boardName = jsonObject.getString("board_name");
        id = jsonObject.getInt("id");
        isTop = jsonObject.getBoolean("is_top");
        hasAttachment = jsonObject.getBoolean("has_attachment");
        try {
            userID = jsonObject.getJSONObject("user").getString("id");
        } catch (JSONException e) {
            isDeleted = true;
            userID = "";
        }
        postTime = jsonObject.getLong("post_time");
        replyCount = jsonObject.getInt("reply_count");
        isFresh = --replyCount == 0;
        lastReplyUserID = jsonObject.getString("last_reply_user_id");
        Long longLastReplyTime = jsonObject.getLong("last_reply_time");
/*        Date date = new Date(longLastReplyTime * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        lastReplyTime = format.format(date);*/
        lastReplyTime = "" + TimeWrapper.valueOf(longLastReplyTime);
        accept(DataColorVisitor.getInstance());
    }

    public void accept(DataVisitor visitor) {
        visitor.visit(this);
    }

    public String getLastReplyUserID() {
        return lastReplyUserID;
    }

    public int getID() {
        return id;
    }

    public boolean isTop() {
        return isTop;
    }

    public String getUserID() {
        return userID;
    }

    public long getPostTime() {
        return postTime;
    }

    public int getReplyCount() {
        return replyCount;
    }

    @Nullable
    public String getLastReplyTime() {
        return lastReplyTime;
    }

    public String getTitle() {
        return title;
    }
}
