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
 * Created by Rye on 2/16/2015.
 */
public class ReplyGroup extends BaseForumData implements Page<Reply> {
    private int pageCurrentCount;
    private int pageAllCount;
    private int id;
    private String boardName;
    private String title;
    private List<Reply> replies;

    public ReplyGroup(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public ReplyGroup(String json) throws JSONException {
        super(json);
    }

    @Override
    public int last() {
        return pageAllCount;
    }

    public String getTitle() {
        return title;
    }


    @Override
    protected void parse() throws JSONException {
        JSONObject jsonObject = getJsonObject();
        pageCurrentCount = jsonObject.getJSONObject("pagination").getInt("page_current_count");
        pageAllCount = jsonObject.getJSONObject("pagination").getInt("page_all_count");
        JSONArray jsonArray = jsonObject.getJSONArray("article");
        boardName = jsonObject.getString("board_name");
        title = jsonObject.getString("title");
        id = jsonObject.getInt("id");
        replies = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            replies.add(Reply.newInstance(jsonArray.getJSONObject(i)));
        }
    }

    public int getId() {
        return id;
    }

    public String getBoardName() {
        return boardName;
    }

    @Override
    public int index() {
        return pageCurrentCount;
    }

    @Override
    public Iterator<Reply> iterator() {
        return replies.iterator();
    }

    public static class UrlBuilder {
        private String boardName;
        private int groupID;
        private int page = 1;
        private int count = 10;

        public UrlBuilder(String boardName, int groupID) {
            this.boardName = boardName;
            this.groupID = groupID;
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
            return new QingUNetworkCenter.UrlBuilder("Threads", boardName, "" + groupID).optional("page=" + page, "count=" + count).build();
        }
    }

}
