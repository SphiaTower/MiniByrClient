package com.example.qingunext.app.datamodel;

/**
 * Created by Rye on 7/21/2015.
 */
public interface DataVisitor {
    void visit(PostInfo postInfo);


    void visit(BoardInfo boardInfo);
}
