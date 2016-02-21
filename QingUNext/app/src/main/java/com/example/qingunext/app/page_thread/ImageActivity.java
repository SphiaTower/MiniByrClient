package com.example.qingunext.app.page_thread;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.qingunext.app.R;

/**
 * Created by Voyager on 9/17/2015.
 */
public class ImageActivity extends FragmentActivity implements ImageMenuFragment.ImageProvider {

    public static final String KEY_URL = "url";
    public ImageView mImageView;
//    private WeakReference<Bitmap> mBitmapWeakReference;

    public static void start(Context from, String url) {
        Intent intent = new Intent(from, ImageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        intent.putExtras(bundle);
        from.startActivity(intent);
    }

    @Override
    public Bitmap getBitmap() {
        return mImageView.getDrawingCache();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_image);
        mImageView = (ImageView) findViewById(R.id.photoView);
//        mImageView = new PhotoView(this);
//        setContentView(mImageView);
        mImageView.setOnLongClickListener(
                v -> {
                    new ImageMenuFragment().show(getSupportFragmentManager(), null);
                    return true;
                }
        );
        Glide.with(this).load(getIntent().getExtras().getString(KEY_URL)).into(mImageView);

//
//        Bitmap bitmap = QingUImageCenter.getInstance().getBitmapFromMemCache(getIntent().getExtras().getString(KEY_URL));
//        mBitmapWeakReference = new WeakReference<>(bitmap);
//        mImageView.setImageBitmap(bitmap);

//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        We get width and height in pixels here
//        final int width = metrics.widthPixels;


//        Picasso.with(this).load(url).into(imageView, new Callback() {
//            @Override
//            public void onSuccess() {
//                Drawable drawable = imageView.getDrawable();
//                drawable = ConcreteImageGetter.zoomDrawable(drawable, width);
//                imageView.setImageDrawable(drawable);
//                //  new PhotoViewAttacher(imageView);
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });
    }

}
