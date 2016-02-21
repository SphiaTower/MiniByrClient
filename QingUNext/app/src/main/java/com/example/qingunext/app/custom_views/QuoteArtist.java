package com.example.qingunext.app.custom_views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Voyager on 9/26/2015.
 */
public class QuoteArtist {
    private Paint mPaint;
    private View mView;

    public QuoteArtist(View view) {
        mPaint = new Paint();
        mPaint.setStrokeWidth(5);
        mPaint.setColor(0x2222FF);
        mPaint.setAlpha(160); // todo dp px
        mView = view;
        mView.setPadding(50 + mView.getPaddingLeft(), mView.getPaddingTop() + 20, mView.getPaddingRight() + 10, mView.getPaddingBottom() + 20);
    }

    public void onDraw(Canvas canvas) {
        canvas.drawLine(25, 0, 25, mView.getHeight(), mPaint);
    }
}
