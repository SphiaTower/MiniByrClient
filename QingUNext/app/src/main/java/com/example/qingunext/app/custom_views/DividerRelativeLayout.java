package com.example.qingunext.app.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


public class DividerRelativeLayout extends RelativeLayout {
    private Paint paint = new Paint();
    private Divider mDivider;

    public DividerRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDivider = new Divider(this);
    }

    public DividerRelativeLayout(Context context) {
        super(context);
        mDivider = new Divider(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        mDivider.onDraw(canvas, paint);
    }
}
