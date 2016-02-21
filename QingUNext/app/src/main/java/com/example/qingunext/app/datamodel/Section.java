package com.example.qingunext.app.datamodel;

import com.example.qingunext.app.util_global.QingUNetworkCenter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rye on 2/23/2015.
 */
public class Section extends BaseForumData {
    private String description;
    private List<String> subSectionNames;
    private List<BoardInfo> boardInfos; //在这里直接初始化会导致父类构造函数中调用时未初始化

    public Section(String json) throws JSONException {
        super(json);
    }

    public List<String> getSubSectionNames() {
        return subSectionNames;
    }

    public List<BoardInfo> getBoardInfos() {
        return boardInfos;
    }

    public String getDescription() {
        return description;
    }

    @Override
    protected void parse() throws JSONException {
        JSONObject jsonObject = getJsonObject();
        description = jsonObject.getString("description");
        JSONArray jsonArray = jsonObject.getJSONArray("board");
        boardInfos = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            boardInfos.add(new BoardInfo(jsonArray.getJSONObject(i)));
        }
        subSectionNames = new ArrayList<>();
        JSONArray subArray = jsonObject.getJSONArray("sub_section");
        for (int i = 0; i < subArray.length(); i++) {
            subSectionNames.add(subArray.get(i).toString());
        }
    }

    public static class UrlBuilder {
        private int index;

        public UrlBuilder(int index) {
            this.index = index;
        }


        public String build() {
            return new QingUNetworkCenter.UrlBuilder("Section", "" + index).build();
        }
    }
}
