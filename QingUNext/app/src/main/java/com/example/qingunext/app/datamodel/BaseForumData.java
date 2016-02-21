package com.example.qingunext.app.datamodel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rye on 2/23/2015.
 * <p>BaseForumData is the super class for all the data got from API. Its subclasses must satisfy that</p>
 * <li>Could be constructed via json or jsonObject</li>
 * <li>Return json string at method toString</li>
 * <li>Provide the parser or builder for the url of itself</li>
 * todo 这个基类的设计可能有问题，需要重新设计架构
 * TODO: parse called before subclass init
 */
public abstract class BaseForumData {
    public JSONObject getJsonObject() {
        return jsonObject;
    }

    private JSONObject jsonObject;

    public BaseForumData(String json) throws JSONException {
        jsonObject = new JSONObject(json);
        parse();
    }

    public BaseForumData(JSONObject jsonObject) throws JSONException {
        this.jsonObject = jsonObject;
        parse();
    }

    protected abstract void parse() throws JSONException;

    @Override
    public String toString() {
        return jsonObject.toString();
    }
}
