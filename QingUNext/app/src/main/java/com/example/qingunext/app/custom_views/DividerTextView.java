package com.example.qingunext.app.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;
import com.example.qingunext.app.R;

/**
 * Created by Rye on 5/16/2015.
 */
public class DividerTextView extends TextView {
    private Paint paint = new Paint();
    private Divider mDivider;

    public DividerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DividerTextView,
                0, 0);
        float start = a.getFloat(R.styleable.DividerTextView_line_start, 0);
        float end = a.getFloat(R.styleable.DividerTextView_line_end, 1);
        mDivider = new Divider(this, start, end);
    }

    public DividerTextView(Context context) {
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