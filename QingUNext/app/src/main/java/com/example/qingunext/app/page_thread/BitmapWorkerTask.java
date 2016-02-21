package com.example.qingunext.app.page_thread;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.util_global.QingUImageCenter;
import com.example.qingunext.app.util_global.QingUNetworkCenter;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by Voyager on 8/21/2015.
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> mImageViewWeakReference;
    private String mSrc;

    public BitmapWorkerTask(ImageView imageView) {
        mImageViewWeakReference = new WeakReference<>(imageView);
    }

    public static void loadBitmap(String src, ImageView imageView) {
        if (cancelPotentialWork(src, imageView)) {
            // load from memory 1st, or AsyncTask is started and placeholder is shown unnecessarily
            Bitmap bitmapFromMemCache = QingUImageCenter.getInstance().getBitmapFromMemCache(src);
            if (bitmapFromMemCache != null) {
                imageView.setImageBitmap(bitmapFromMemCache);
                return;
            }
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            Resources resources = imageView.getResources();
            final AsyncDrawable asyncDrawable = new AsyncDrawable(resources, QingUImageCenter.getInstance().getPlaceHolder(), task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(src);
        }
    }

    private static boolean cancelPotentialWork(String src, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            final String bitmapSrc = bitmapWorkerTask.mSrc;
            if (bitmapSrc == null || !bitmapSrc.equals(src)) {
                bitmapWorkerTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        mSrc = params[0];

        try {
            return QingUImageCenter.getInstance().downloadBitmap(mSrc);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (isCancelled()) {
            bitmap = null;
        }
        if (mImageViewWeakReference != null && bitmap != null) {
            final ImageView imageView = mImageViewWeakReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}
