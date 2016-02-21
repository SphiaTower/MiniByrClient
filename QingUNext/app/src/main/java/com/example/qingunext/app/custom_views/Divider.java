package com.example.qingunext.app.custom_views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Rye on 5/16/2015.
 */
public class Divider {
    private View mClient;
    private float mStart;
    private float mEnd;


    public Divider(View client) {
        this(client, 0.25f, 0.9f);
    }

    public Divider(View client, float start, float end) {
        this.mClient = client;
        mStart = start;
        mEnd = end;
    }

    public void onDraw(Canvas canvas, Paint paint) {
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(2);
        float width = mClient.getMeasuredWidth();
        float height = mClient.getMeasuredHeight();
        canvas.drawLine(width * mStart, height - 1, width * mEnd, height - 1, paint);
//         canvas.drawLine(0, 0, width / 10 * 9, height - 1, paint);
    }
}
