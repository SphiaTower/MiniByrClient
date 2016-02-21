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
 * Created by Rye on 5/24/2015.
 */
public class MailBox extends BaseForumData implements Page<MailInfo> {
    private String mDescription;
    private int mLast;
    private int mIndex;
    private List<MailInfo> mMailInfos;

    public MailBox(String json) throws JSONException {
        super(json);
    }

    public MailBox(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public List<MailInfo> getMailInfos() {
        return mMailInfos;
    }


    @Override
    public int index() {
        return mIndex;
    }

    @Override
    public int last() {
        return mLast;
    }

    public String getDescription() {
        return mDescription;
    }

    @Override
    public void parse() throws JSONException {
        JSONObject json = getJsonObject();
        mDescription = json.getString("description");
        mLast = json.getJSONObject("pagination").getInt("page_all_count");
        mIndex = json.getJSONObject("pagination").getInt("page_current_count");
        JSONArray mailArray = json.getJSONArray("mail");
        mMailInfos = new ArrayList<>();
        for (int i = 0; i < mailArray.length(); i++) {
            mMailInfos.add(new MailInfo(mailArray.getJSONObject(i)));
        }
    }

    @Override
    public Iterator<MailInfo> iterator() {
        return mMailInfos.iterator();
    }

    public enum MailBoxName {
        INBOX, OUTBOX, DELETED;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public static class UrlBuilder {
        private MailBoxName boxName;
        private int page = 1;
        private int count = 10;

        public UrlBuilder(MailBoxName boxName) {
            this.boxName = boxName;
        }

        public UrlBuilder page(int page) {
            this.page = page;
            return this;
        }

        public UrlBuilder count(int count) {
            this.count = count;
            return this;
        }

        public String build() {
            return new QingUNetworkCenter.UrlBuilder("mail", boxName.toString()).optional("page=" + page, "count=" + count).build();
        }

    }
}
