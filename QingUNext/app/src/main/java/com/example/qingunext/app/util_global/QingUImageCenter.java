package com.example.qingunext.app.util_global;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.util.ConcreteImageGetter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Voyager on 9/27/2015.
 */
public class QingUImageCenter {
    private static QingUImageCenter sInstance;
    private LruCache<String, Bitmap> mMemoryCache;

    private Context mContext;
    private ConcreteImageGetter mImageGetter;

    private QingUImageCenter(Context context) {
        mContext = context;

    }

    private void allocateMemory() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
//        final int cacheSize = maxMemory / 8;
        final int cacheSize = maxMemory / 64;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static QingUImageCenter getInstance() {
//        if (sInstance.mMemoryCache == null) {
//            sInstance.allocateMemory();
//        }
        return sInstance;
    }

    public static void initialize(Context context) {
        sInstance = new QingUImageCenter(context);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;

    }

    /**
     * InputStream -> byte[]
     *
     * @param in InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] inputStreamToByte(InputStream in) throws IOException {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024 * 16];
        int count = -1;
        while ((count = in.read(data, 0, 1024 * 16)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return outStream.toByteArray();
    }

    public ConcreteImageGetter getImageGetter() {
        if (mImageGetter == null) {
            mImageGetter = new ConcreteImageGetter();
        }
        return mImageGetter;
    }

    public ConcreteImageGetter getImageGetter(ConcreteImageGetter.ImageType imageType) {
        if (mImageGetter == null) {
            mImageGetter = new ConcreteImageGetter();
        }
        mImageGetter.setImageType(imageType);
        return mImageGetter;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public Bitmap downloadBitmap(String url) throws IOException {
        if (url == null) url = ConcreteImageGetter.KEY_PLACE_HOLDER;
        Bitmap bitmapFromMemCache = getBitmapFromMemCache(url);
        if (bitmapFromMemCache != null) {
            if (QingUApp.DEBUG) Log.v("NetWork", "LOADING FROM MEMORY: " + url);

            return bitmapFromMemCache;
        }
        if (QingUApp.DEBUG) Log.v("NetWork", "GETTING URL: " + url);

        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        try {
            conn.setReadTimeout(NetworkBase.READ_TIMEOUT_MILLIS /* milliseconds */);
            conn.setConnectTimeout(NetworkBase.CONNECT_TIMEOUT_MILLIS /* milliseconds */);
            conn.setDoInput(true);
            // Enable cache
            conn.setUseCaches(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "*/*");
            // add reuqest header
            conn.setRequestMethod("GET");
//        int responseCode = conn.getResponseCode();
            // need buffer?
//            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            InputStream inputStream = conn.getInputStream();


            byte[] data = inputStreamToByte(inputStream);
            InputStream inputStreamCopy1 = byteTOInputStream(data);
            InputStream inputStreamCopy2 = byteTOInputStream(data);


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStreamCopy1, null, options);
            int outHeight = options.outHeight;
            if (QingUApp.DEBUG) System.out.println("outHeight = " + outHeight);
            int outWidth = options.outWidth;
            if (QingUApp.DEBUG) System.out.println("outWidth = " + outWidth);
            String outMimeType = options.outMimeType;
            if (QingUApp.DEBUG) System.out.println("outMimeType = " + outMimeType);
            options.inSampleSize = calculateInSampleSize(options, 480, 480);


            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStreamCopy2, null, options);
            if (bitmap != null) {
                addBitmapToMemoryCache(url, bitmap);
            }
            return bitmap;
        } finally {
            conn.disconnect();
        }
    }
    public Bitmap rawDownloadBitmap(String url) throws IOException {
        if (url == null) url = ConcreteImageGetter.KEY_PLACE_HOLDER;

        Log.v("NetWork", "GETTING URL: " + url);

        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        try {
            conn.setReadTimeout(NetworkBase.READ_TIMEOUT_MILLIS /* milliseconds */);
            conn.setConnectTimeout(NetworkBase.CONNECT_TIMEOUT_MILLIS /* milliseconds */);
            conn.setDoInput(true);
            // Enable cache
            conn.setUseCaches(true);
            conn.setRequestProperty("Accept", "*/*");
            // add reuqest header
            conn.setRequestMethod("GET");
//        int responseCode = conn.getResponseCode();
            // need buffer?
//            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            InputStream inputStream = conn.getInputStream();


            byte[] data = inputStreamToByte(inputStream);
            InputStream inputStreamCopy1 = byteTOInputStream(data);
            InputStream inputStreamCopy2 = byteTOInputStream(data);


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStreamCopy1, null, options);
            int outHeight = options.outHeight;
            if (QingUApp.DEBUG) System.out.println("outHeight = " + outHeight);
            int outWidth = options.outWidth;
            if (QingUApp.DEBUG) System.out.println("outWidth = " + outWidth);
            String outMimeType = options.outMimeType;
            if (QingUApp.DEBUG) System.out.println("outMimeType = " + outMimeType);
            options.inSampleSize = calculateInSampleSize(options, 480, 480);


            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStreamCopy2, null, options);

            return bitmap;
        } finally {
            conn.disconnect();
        }
    }

    public Bitmap getPlaceHolder() {
        Bitmap bitmapFromMemCache = getBitmapFromMemCache(ConcreteImageGetter.KEY_PLACE_HOLDER);
        if (bitmapFromMemCache == null) {
            bitmapFromMemCache = ConcreteImageGetter.drawableToBitmap(mContext.getResources().getDrawable(R.drawable.ic_placeholder));

            addBitmapToMemoryCache(ConcreteImageGetter.KEY_PLACE_HOLDER, bitmapFromMemCache);
        }
        return bitmapFromMemCache;
    }

    /**
     * byte[] -> InputStream
     *
     * @param in
     * @return
     * @throws Exception
     */
    public InputStream byteTOInputStream(byte[] in) {

        ByteArrayInputStream is = new ByteArrayInputStream(in);
        return is;
    }

}
