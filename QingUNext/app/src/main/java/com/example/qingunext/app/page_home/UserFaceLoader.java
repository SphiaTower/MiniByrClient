package com.example.qingunext.app.page_home;

import android.content.Context;
import android.graphics.Bitmap;
import com.squareup.picasso.Picasso;
import tower.sphia.auto_pager_recycler.lib.AsyncTaskLoaderImpl;

import java.io.IOException;

/**
 * Created by Voyager on 8/18/2015.
 */
public class UserFaceLoader extends AsyncTaskLoaderImpl<Bitmap> {
    private String mUrl;

    public UserFaceLoader(Context ctx, String url) {
        super(ctx);
        mUrl = url;
    }

    @Override
    protected void releaseResources(Bitmap data) {
        data = null;
    }

    @Override
    public Bitmap loadInBackground() {
        try {
            return Picasso.with(getContext()).load(mUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;// todo

        }
//        try {
//            return QingUImageCenter.getInstance().downloadBitmap(mUrl);
//        } catch (IOException e) {
//            return QingUImageCenter.getInstance().getPlaceHolder();
//        }
    }
}
