package com.example.qingunext.app.datamodel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rye on 2/16/2015.
 */
public class Widget extends BaseForumData  {
    private String sectionName;
    private ArrayList<WidgetItem> mWidgetItems;

    public Widget(String json) throws JSONException {
        super(json);
    }

    public Widget(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public String getSectionName() {
        return sectionName;
    }

    public ArrayList<WidgetItem> getWidgetItems() {
        return mWidgetItems;
    }

    @Override
    protected void parse() throws JSONException {
        JSONObject jsonObject = getJsonObject();
        sectionName = jsonObject.getString("title");
        mWidgetItems = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("article");
        for (int i = 0; i < jsonArray.length(); i++) {
            mWidgetItems.add(new WidgetItem(jsonArray.getJSONObject(i)));
        }
    }


}
