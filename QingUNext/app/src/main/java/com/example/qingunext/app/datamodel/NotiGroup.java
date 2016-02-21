package com.example.qingunext.app.datamodel;

import com.example.qingunext.app.util_global.QingUNetworkCenter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import tower.sphia.auto_pager_recycler.lib.Page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Rye on 6/20/2015.
 */
public class NotiGroup extends BaseForumData implements Page<NotiItem> {
    private int mIndex;
    private int mLast;
    private List<NotiItem> mNotiItems;
    private String mDescription;
    private NotiGroup(String json) throws JSONException {
        super(json);
    }

    private NotiGroup(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public static NotiGroup valueOf(String json) throws JSONException {
        return new NotiGroup(json);
    }

    public List<NotiItem> getNotiItems() {
        return mNotiItems;
    }

    public String getDescription() {
        return mDescription;
    }

    @Override
    protected void parse() throws JSONException {
        JSONObject jsonObject = getJsonObject();
        mDescription = jsonObject.getString("description");
        mIndex = jsonObject.getJSONObject("pagination").getInt("page_current_count");
        mLast = jsonObject.getJSONObject("pagination").getInt("page_all_count");
        JSONArray array = jsonObject.getJSONArray("article");
        mNotiItems = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            mNotiItems.add(new NotiItem(array.getJSONObject(i)));
        }
    }

    @Override
    public int index() {
        return mIndex;
    }

    @Override
    public int last() {
        return mLast;
    }

    @Override
    public Iterator<NotiItem> iterator() {
        return mNotiItems.iterator();
    }

    public static class UrlBuilder {
        private int page = 1;
        private int count = 10;

        public UrlBuilder page(int page) {
            this.page = page;
            return this;
        }

        public UrlBuilder count(int count) {
            this.count = count;
            return this;
        }

        public String build() {
            return new QingUNetworkCenter.UrlBuilder("refer", "reply").optional("page=" + page, "count=" + count).build();
        }
    }
}
