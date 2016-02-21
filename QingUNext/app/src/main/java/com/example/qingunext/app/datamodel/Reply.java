package com.example.qingunext.app.datamodel;

import android.text.Html;
import com.example.qingunext.app.page_thread.SimpleTagHandler;
import com.example.qingunext.app.page_thread.UBBDecoder;
import com.example.qingunext.app.util_global.QingUImageCenter;
import com.example.qingunext.app.util_global.QingUNetworkCenter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rye on 2/16/2015.
 * 论坛中的每条回复，也就是每个楼层
 */
public class Reply extends BaseForumData implements Serializable {
    private int position;
    private User user;
    private long postTime;
    private int id;
    private int groupID;
    private String mUbbContent;
    private List<Partition> mPartitions;

    private Reply(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        parseContent();
    }

    /**
     * Only used for Reflect!
     *
     * @param json
     * @throws JSONException
     */
    private Reply(String json) throws JSONException {
        super(json);
        parseContent();
    }

    private Reply(String json, boolean parseFurther) throws JSONException {
        super(json);
        if (parseFurther) parseContent();
    }

    private Reply(JSONObject jsonObject, boolean parseFurther) throws JSONException {
        super(jsonObject);
        if (parseFurther) parseContent();
    }

    /**
     * parse the json simply, without conversion for html
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static Reply newInfoInstance(String json) throws JSONException {
        return new Reply(json, false);
    }

    public static Reply newInfoInstance(JSONObject jsonObject) throws JSONException {
        return new Reply(jsonObject, false);
    }

    public static Reply newInstance(String json) throws JSONException {
        Reply reply = new Reply(json, false);
        reply.parseContent();
        return reply;
    }

    public static Reply newInstance(JSONObject jsonObject) throws JSONException {
        Reply reply = new Reply(jsonObject, false);
        reply.parseContent();
        return reply;
    }

    private static PieceType parseUrlType(String name) {
        PieceType type;
        if (name.endsWith(".jpg") || name.endsWith(".JPG") ||
                name.endsWith(".png") || name.endsWith(".PNG") ||
                name.endsWith(".gif") || name.endsWith(".GIF") ||
                name.endsWith(".jpeg") || name.endsWith(".JPEG")) {
            // todo support for animated gif
            type = PieceType.IMAGE;
        } else {
            // todo support for music etc.
            type = PieceType.OTHER;
        }
        return type;
    }

    private static String replaceUrl(String attachmentUrl) {
        return attachmentUrl.replace("api", "bbs").replace("attachment", "att");
    }

    private static TreeMap<Integer, Partition> findPartitions(String ubbContent, JSONArray fileArray) {
        // milstone recording the start point for each piece
        TreeMap<Integer, Partition> partitions = new TreeMap<>();
        // starts with a text, if not it will be ignored later
        partitions.put(0, Partition.newText());
        // find all the 'upload' tags first
        Pattern pattern = Pattern.compile("\\[upload=(.*?)\\]\\[/upload\\]");
        Matcher matcher = pattern.matcher(ubbContent);
        // using a set as container to prevent duplicated ubb tags
        Set<Integer> fileIndexSet = new HashSet<>();
        while (matcher.find()) {
            // retrieve the index attr of 'upload' tag and convert into index of attachment jsons
            int fileNumber = Integer.parseInt(matcher.group(1)) - 1;
            // eliminate duplicates
            if (fileIndexSet.add(fileNumber)) {
                try {
                    JSONObject fileJson = fileArray.getJSONObject(fileNumber);
                    // replace api call to html link
                    String url = replaceUrl(fileJson.getString("url"));
                    // get filename to decide the type
                    String name = fileJson.getString("name");
                    partitions.put(matcher.start(), Partition.newAttachment(url, name));
                    partitions.put(matcher.end(), Partition.newText());
                } catch (JSONException e) {
                    break;
                }
            }

        }

        // start finding the quote

        // start index of the quote
        int quoteStartIndex = ubbContent.indexOf("【 在");
        if (quoteStartIndex != -1) {
            // if quote exists
            // start index of the ending line of the quote
            int quoteLastLineIndex = ubbContent.lastIndexOf("\n:");
            // start index of the next line in the substring
            int quoteEndIndexOffset = ubbContent.substring(quoteLastLineIndex + 1).indexOf("\n");
            // end index of the quote
            int quoteEndIndex = quoteEndIndexOffset + quoteLastLineIndex;
            partitions.put(quoteStartIndex, Partition.newQuote());
            partitions.put(quoteEndIndex + 1, Partition.newText());

        }
        // check if all att are referenced by the ubb tag
        if (fileIndexSet.size() != fileArray.length()) {
            for (int i = 0; i < fileArray.length(); i++) {
                if (!fileIndexSet.contains(i)) {
                    try {
                        // if not referenced, set a milestone at end
                        JSONObject jsonObject = fileArray.getJSONObject(i);
                        partitions.put(ubbContent.length() + i, Partition.newAttachment(replaceUrl(jsonObject.getString("url")), jsonObject.getString("name")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return partitions;
    }

    private static TreeMap<Integer, Partition> inflatePartitions(String ubbContent, TreeMap<Integer, Partition> partitions) throws JSONException {
        List<Integer> indices = new ArrayList<>();
        indices.addAll(partitions.keySet());
        int size = indices.size();
        for (int i = 0; i < size; i++) {
            Integer start = indices.get(i);
            Partition partition = partitions.get(start);
            String text;
            switch (partition.type) {
                case TEXT:
                case QUOTE:
                    // could use trim() here to cut the space
                    if (i != size - 1) {
                        text = ubbContent.substring(start, indices.get(i + 1));
                    } else {
                        text = ubbContent.substring(start);
                    }
                    // cut empty text piece
                    if (text.equals("") || text.equals(" ") || text.equals("\n")) {
                        partitions.remove(start);
                        continue;
                    }
                    String decode = UBBDecoder.decode(text, new SimpleTagHandler(),
                            UBBDecoder.MODE_CLOSE, true);
                    partition.inflateText(decode);
            }
        }
        return partitions;
    }

    public String getUbbContent() {
        return mUbbContent;
    }

    public String getPrompt() {
        return "[在 " + getUser().getId() + " 的大作中提到:]\n" + mUbbContent;
    }

    public int getGroupID() {
        return groupID;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public long getPostTime() {
        return postTime;
    }

    public int getPosition() {
        return position;
    }

    @Override
    protected void parse() throws JSONException {
        JSONObject jsonObject = getJsonObject();
        user = User.valueOf(jsonObject.getJSONObject("user"));
        position = jsonObject.getInt("position");
        postTime = jsonObject.getLong("post_time");
        id = jsonObject.getInt("id");
        groupID = jsonObject.getInt("group_id");
        mUbbContent = jsonObject.getString("content");
    }

    public List<Partition> getPartitions() {
        return mPartitions;
    }

    private void parseContent() throws JSONException {
        // delete the redundant `\n` at end
        int end = mUbbContent.length() - 1;
        if (end >= 1) {
            mUbbContent = mUbbContent.substring(0, end);
        }
        // retrieve the attachment list
        JSONArray fileArray = getJsonObject().getJSONObject("attachment").getJSONArray("file");
        mPartitions = new ArrayList<>();
        TreeMap<Integer, Partition> partitions = inflatePartitions(mUbbContent, findPartitions(mUbbContent, fileArray));
        for (Map.Entry<Integer, Partition> entry : partitions.entrySet()) {
            mPartitions.add(entry.getValue());
        }
    }


    public enum PieceType {
        IMAGE, TEXT, OTHER, QUOTE
    }

    public static class Partition {
        public final PieceType type;
        public String url;
        public CharSequence text;
        public String name;

        private Partition(PieceType type) {
            this.type = type;
        }

        public Partition(PieceType type, String url, String name) {
            this.type = type;
            this.url = url;
            this.name = name;
        }

        public static Partition newText() {
            return new Partition(PieceType.TEXT);
        }

        public static Partition newQuote() {
            return new Partition(PieceType.QUOTE);
        }

        public static Partition newAttachment(String url, String name) {
            return new Partition(Reply.parseUrlType(name), url, name);
        }

        public void inflateText(String text) {
            this.text = Html.fromHtml(text, QingUImageCenter.getInstance().getImageGetter(), null);
        }

        @Override
        public String toString() {
            switch (type) {
                case IMAGE:
                case OTHER:
                    return type.toString() + " " + url;
                case QUOTE:
                case TEXT:
                    return type.toString() + " " + text.toString();
                default:
                    return super.toString();
            }
        }
    }


    public static class UrlBuilder {
        private String boardName;

        private int id;

        public UrlBuilder(String boardName, int id) {
            this.boardName = boardName;
            this.id = id;
        }

        public String build() {
            return new QingUNetworkCenter.UrlBuilder("Article", boardName, "" + id).build();
        }
    }
}
