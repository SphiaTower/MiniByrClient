package com.example.qingunext.app.datamodel;

import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.util_global.QingUNetworkCenter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rye on 2/20/2015.
 */
public class BoardInfo extends BaseForumData {
    private String name;
    private String description;
    private int color;

    public int getPostTodayCount() {
        return postTodayCount;
    }

    private int postTodayCount;

    public BoardInfo(String json) throws JSONException {
        super(json);
    }

    public BoardInfo(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    @Override
    protected void parse() throws JSONException {
        JSONObject jsonObject = getJsonObject();
        name = jsonObject.getString("name");
        description = jsonObject.getString("description");
        postTodayCount = jsonObject.getInt("post_today_count");
        accept(DataColorVisitor.getInstance());
    }

    public void accept(DataVisitor visitor){
        visitor.visit(this);
    }


    public String getUrl() {
        return QingUNetworkCenter.buildOptionalAPI("page=1", "board", name);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}
