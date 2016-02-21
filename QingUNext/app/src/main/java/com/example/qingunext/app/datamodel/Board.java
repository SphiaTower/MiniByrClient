package com.example.qingunext.app.datamodel;

import com.example.qingunext.app.util_global.QingUNetworkCenter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import tower.sphia.auto_pager_recycler.lib.Page;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Rye on 2/20/2015.
 */
public class Board extends BaseForumData implements Page<BoardItem> {
    private String description;
    private String manager;
    private int pageCurrentCount;
    private String name;
    private int pageAllCount;
    private ArrayList<BoardItem> mBoardItems;

    public Board(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public Board(String json) throws JSONException {
        super(json);
    }

    public String getDescription() {
        return description;
    }

    public String getManager() {
        return manager;
    }


    @Override
    public int index() {
        return pageCurrentCount;
    }

    @Override
    public int last() {
        return pageAllCount;
    }

    public ArrayList<BoardItem> getBoardItems() {
        return mBoardItems;
    }

    public String getName() {
        return name;
    }

    @Override
    protected void parse() throws JSONException {
        JSONObject jsonObject = getJsonObject();
        mBoardItems = new ArrayList<>();
        description = jsonObject.getString("description");
        manager = jsonObject.getString("manager");
        name = jsonObject.getString("name");
        pageCurrentCount = jsonObject.getJSONObject("pagination").getInt("page_current_count");
        pageAllCount = jsonObject.getJSONObject("pagination").getInt("page_all_count");
        JSONArray array = jsonObject.getJSONArray("article");
        for (int i = 0; i < array.length(); i++) {
            mBoardItems.add(new BoardItem(array.getJSONObject(i)));
        }
    }

    @Override
    public Iterator<BoardItem> iterator() {
        return mBoardItems.iterator();
    }


    public static class UrlBuilder {
        private String boardName;
        private int page = 1;
        private int count = 10;

        public UrlBuilder(String boardName) {
            this.boardName = boardName;
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
            return new QingUNetworkCenter.UrlBuilder("Board", boardName).optional("page=" + page, "count=" + count).build();
        }
    }
//
//        public String build() {
//            return QingUApp.buildOptionalAPI("page="+page,"Board",boardName);
//        }
}
