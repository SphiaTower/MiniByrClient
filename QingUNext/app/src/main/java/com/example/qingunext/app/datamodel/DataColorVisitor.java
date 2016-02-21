package com.example.qingunext.app.datamodel;

import android.graphics.Color;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;

/**
 * Created by Rye on 5/17/2015.
 * 根据论坛数据类型的一些属性来设置其颜色
 */
public class DataColorVisitor implements DataVisitor {
    private static DataColorVisitor singleton;

    private DataColorVisitor() {
    }

    public static DataColorVisitor getInstance() {
        if (singleton == null) {
            singleton = new DataColorVisitor();
        }
        return singleton;
    }


    @Override
    public void visit(PostInfo postInfo) {
        int color;
        if (postInfo.isTop())
            color = Color.BLACK;
        else if (postInfo.isDeleted())
            color = Color.GRAY;
        else if (postInfo.isFresh())
            color = QingUApp.getInstance().getResources().getColor(R.color.fresh_dark_green);
        else if (postInfo.isHasAttachment())
            color = Color.RED;
        else
            color = QingUApp.getInstance().getResources().getColor(R.color.background_dark_blue);
        postInfo.setColor(color);
    }

    @Override
    public void visit(BoardInfo boardInfo) {
        int index = boardInfo.getPostTodayCount();
        int color;

        if (index > 25) {
            color = Color.RED;
        } else {
            color = QingUApp.getInstance().getResources().getColor(R.color.background_dark_blue);
        }
        boardInfo.setColor(color);
    }

}
