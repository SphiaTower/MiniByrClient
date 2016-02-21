package com.example.qingunext.app.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Rye on 7/10/2015.
 */
public class DpPxConverter {


    public static int dpToPX(DisplayMetrics displayMetrics, int dpValue) {
        final float scale = displayMetrics.density;
        return (int) (dpValue * scale + 0.5f);
    }
}
