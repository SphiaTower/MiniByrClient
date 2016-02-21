package com.example.qingunext.app.page_thread;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import com.example.qingunext.app.custom_views.QuoteArtist;

/**
 * Created by Voyager on 9/26/2015.
 */
public class QuoteTextView extends AppCompatTextView {
    private QuoteArtist mQuoteArtist;

    public QuoteTextView(Context context) {
        super(context);
        mQuoteArtist = new QuoteArtist(this);
    }

    public QuoteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mQuoteArtist = new QuoteArtist(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mQuoteArtist.onDraw(canvas);
    }
}
