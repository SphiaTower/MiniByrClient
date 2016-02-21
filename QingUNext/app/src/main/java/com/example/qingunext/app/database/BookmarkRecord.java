package com.example.qingunext.app.database;

/**
 * Created by Rye on 7/8/2015.
 */
public final class BookmarkRecord {
    public final String title;
    public final String boardName;

    public final int groupID;

    public BookmarkRecord(String title, String boardName, int groupID) {
        this.title = title;
        this.boardName = boardName;
        this.groupID = groupID;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BookmarkRecord)) {
            return false;
        }
        BookmarkRecord record = ((BookmarkRecord) o);
        return record.title.equals(title) && record.boardName.equals(boardName) && record.groupID == groupID;
    }
}
