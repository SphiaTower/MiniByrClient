package com.example.qingunext.app.datamodel;

/**
 * Created by Rye on 7/21/2015.
 */
public interface PostInfo {
    boolean isTop();

    boolean isHasAttachment();

    boolean isFresh();

    boolean isDeleted();

    int getColor();

    void setColor(int color);
}
