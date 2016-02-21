package com.example.qingunext.app.util;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.util_global.QingUImageCenter;

import java.net.URL;

/**
 * Created by Rye on 4/5/2015.
 */
public class ConcreteImageGetter implements Html.ImageGetter {
    public static final String KEY_PLACE_HOLDER = "placeholder";
    private ImageType mImageType;

    public ConcreteImageGetter() {
        super();
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getWidth() / 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap drawableToBitmap(@NonNull Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int w) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width == 120) return bitmap; // todo face
        Matrix matrix = new Matrix();
        float scale = ((float) w / width);

        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
    }

    public ConcreteImageGetter setImageType(ImageType imageType) {
        this.mImageType = imageType;
        return this;
    }

    @Override
    @Nullable
    public Drawable getDrawable(@NonNull String source) {
        if (QingUApp.DEBUG) Log.i("RG", "source---?>>>" + source);
        Drawable drawable = loadDrawable(source);
        if (drawable == null) {
            return null;
        }
        return taylorDrawableOnType(source, drawable);
    }
  /*  @Override
    @Nullable
    public Drawable getDrawable(@NonNull String source) {
        if (source == null) {
            source = KEY_PLACE_HOLDER;
        }
        if (QingUApp.DEBUG) Log.i("RG", "source---?>>>" + source);
        Drawable drawable;
        Bitmap bitmapFromMemCache = QingUImageCenter.getInstance().getBitmapFromMemCache(source);
        if (bitmapFromMemCache != null) {
            drawable = new BitmapDrawable(null, bitmapFromMemCache);
        } else {
            drawable = loadDrawable(source);
        }
        if (drawable == null) {
            return null;
        }
        Drawable zoomedDrawable = taylorDrawableOnType(source, drawable);
        QingUImageCenter.getInstance().addBitmapToMemoryCache(source, drawableToBitmap(zoomedDrawable)); // could be moved former
        return zoomedDrawable;
    }*/
/*
    @Override
    public Drawable getDrawable(String source) {
        System.out.println(source);
        PicassoTargetDrawable d = new PicassoTargetDrawable(QingUApp.getInstance());
        Picasso.with(QingUApp.getInstance())
                .load(source)
                .into(d);
        return d;
//        try {
//            Bitmap bitmap = QingUImageCenter.getInstance().rawDownloadBitmap(source);
//            System.out.println(bitmap.getWidth());
//            BitmapDrawable drawable = new BitmapDrawable(QingUApp.getInstance().getResources(), bitmap);
//            drawable.setBounds(0,0,drawable.getIntrinsicHeight(),drawable.getIntrinsicHeight());
//            return drawable;
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new Error();
//        }
    }*/

    @Nullable
    private Drawable loadDrawable(@NonNull String source) {
        switch (source) {
            case "http://static.byr.cn/img/face_default_m.jpg":
                return QingUApp.getInstance().getResources().getDrawable(R.drawable.face_default_m_trans);
            case "http://static.byr.cn/img/face_default_f.jpg":
                return QingUApp.getInstance().getResources().getDrawable(R.drawable.face_default_f_trans);
            default:
                int i = source.lastIndexOf("ubb/em");
                if (i != -1) {
                /* load emotion pics from drawable */
                    String filename = source.substring(i + 4).replace('/', '_');
                    if (QingUApp.DEBUG) Log.v("emotion", filename);
                    String substring = filename.substring(0, filename.indexOf(".gif"));
                    if (QingUApp.DEBUG) Log.v("emotion", substring);
                    try {
                        return QingUApp.getInstance().getResources().getDrawable(QingUApp.getInstance().getResources().getIdentifier(substring, "drawable", "com.example.qingunext.app"));
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
//                        return new BitmapDrawable(QingUApp.getInstance().downloadBitmap(source));
                        URL url = new URL(source);
                        return Drawable.createFromStream(url.openStream(), ""); // 获取网路图片
                    } catch (Exception e) {
                        if (QingUApp.DEBUG) Log.e("RG", "url---?>>>" + source);
                        // i forgot what Runtime Exception was thrown here
                        e.printStackTrace();
                    }
                }
                break;
        }
        return null;
    }

    private Drawable taylorDrawableOnType(String source, @NonNull Drawable drawable) {
        if (source.contains("ubb/em")) {
            if (source.contains("gif")) {
                mImageType = ImageType.EMOTION_GIF;
            } else {
                mImageType = ImageType.EMOTION_JPG;
            }
        } else if (source.contains("uploadFace") || source.contains("img/face_default")) {
            mImageType = ImageType.FACE;
        } else {
            mImageType = ImageType.PHOTO;
        }
        int zoomWidth = mImageType.getZoomedSideLength();
        drawable = zoomDrawable(drawable, zoomWidth);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
//            if(QingUApp.DEBUG) Log.i("RG", "url---?>>>" + (url == null ? "loaded from local" : url));
        return drawable;
    }

    public static Drawable zoomDrawable(Drawable drawable, int w) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap rawBmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float scale = ((float) w / width);

        matrix.postScale(scale, scale);
        Bitmap zoomedBmp = Bitmap.createBitmap(rawBmp, 0, 0, width, height,
                matrix, true);
//        zoomedBmp = getRoundedCornerBitmap(zoomedBmp);
        return new BitmapDrawable(null, zoomedBmp);
    }

    public enum ImageType {
        // todo adapt the screen size and land-screen
        PHOTO(1000), FACE(200), EMOTION_GIF(100), EMOTION_JPG(60);
        private int zoomedSideLength;

        ImageType(int zoomedSideLength) {
            this.zoomedSideLength = zoomedSideLength;
        }

        public int getZoomedSideLength() {
            return zoomedSideLength;
        }
    }
}
