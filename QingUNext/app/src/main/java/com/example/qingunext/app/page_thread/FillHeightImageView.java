package com.example.qingunext.app.page_thread;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Voyager on 8/23/2015.
 */
public class FillHeightImageView extends ImageView {

    public FillHeightImageView(Context context) {
        super(context);
    }

    public FillHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FillHeightImageView(Context context, AttributeSet attrs,
                               int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            int height = View.MeasureSpec.getSize(heightMeasureSpec);
            int dih = drawable.getIntrinsicHeight();
            if (dih > 0) {
                int width = height * drawable.getIntrinsicWidth() / dih;
                setMeasuredDimension(width, height);
            } else
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
