package com.example.qingunext.app.page_thread;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.datamodel.Reply;

/**
 * Created by Voyager on 8/19/2015.
 */
public class ReplyView extends LinearLayout {

//    private QuoteArtist mQuoteArtist;

    public ReplyView(Context context) {
        super(context);
        onCreate();
    }

    public ReplyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public ReplyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate();
    }

    private void onCreate() {
        if (QingUApp.DEBUG)
            System.out.println("ReplyView.onCreate");
//        mQuoteArtist = new QuoteArtist(this);
    }

//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        super.dispatchDraw(canvas);
//        mQuoteArtist.onDraw(canvas);
//    }

    public void addTextView() {
        TextView textView = new AppCompatTextView(getContext());
        textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
        textView.setLineSpacing(0, 1.4f);
//        textView.setOnClickListener(v -> Toast.makeText(getContext(), textView.getText().toString(), Toast.LENGTH_SHORT).show());
        addView(textView);
    }
    public void addFileView() {
        TextView textView = new AppCompatTextView(getContext());
        textView.setBackgroundColor(Color.GRAY);
        textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium_Inverse);
        textView.setLineSpacing(0, 1.4f);
//        textView.setOnClickListener(v -> Toast.makeText(getContext(), textView.getText().toString(), Toast.LENGTH_SHORT).show());
        addView(textView);
    }
    public void addImageView() {
        ImageView imageView = new ImageView(getContext());
        imageView.setPadding(0, 0, 0, 10);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        imageView.setImageDrawable(getPlaceHolder());
        addView(imageView);
    }


    public void initView(Reply data) {
        for (Reply.Partition partition : data.getPartitions()) {
            switch (partition.type) {
                case IMAGE:
                    addImageView();
                    break;
                case TEXT:
                    addTextView();
                    break;
                case OTHER:
                    addFileView();
                    break;
                case QUOTE:
                    TextView textView = new QuoteTextView(getContext());
                    textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
                    textView.setLineSpacing(0, 1.4f);
//                    textView.setOnClickListener(v -> Toast.makeText(getContext(), textView.getText().toString(), Toast.LENGTH_SHORT).show());
                    addView(textView);
            }
        }

    }


    public void setData(Reply data) {
        int i = 0;
        for (Reply.Partition partition : data.getPartitions()) {
            switch (partition.type) {
                case IMAGE:
                    ImageView imageView = (ImageView) getChildAt(i);
                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;
                    Glide.with(getContext()).load(partition.url).placeholder(R.drawable.ic_placeholder).override(width,height).fitCenter().into(imageView);
         /*           Picasso.with(getContext()).load(milestone.url).fit().centerInside().placeholder(R.drawable.ic_placeholder).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Log.e("Picasso", milestone.url);
                        }
                    });*/
                    imageView.setOnClickListener(v -> {
                        Toast.makeText(getContext(), partition.url,Toast.LENGTH_SHORT).show();
                        ImageActivity.start(getContext(), partition.url);
                    });
                    break;
                case TEXT:
                    TextView textView = (TextView) getChildAt(i);
                        textView.setText(partition.text);
                    break;
                case OTHER:
                    TextView file = (TextView) getChildAt(i);
                    file.setText(partition.name);
                    file.setOnClickListener(v -> {
                        Toast.makeText(getContext(),"start downloading...",Toast.LENGTH_SHORT).show();
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(partition.url));
                        request.setDescription("Downloading a QingU Attachment");
                        request.setTitle(partition.name);
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, partition.name);
                        DownloadManager manager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);
                    });
                    break;
                case QUOTE:
                    TextView quote = ((TextView) getChildAt(i));
                    quote.setText(partition.text);
                    break;
            }
            i++;
        }
    }

}
