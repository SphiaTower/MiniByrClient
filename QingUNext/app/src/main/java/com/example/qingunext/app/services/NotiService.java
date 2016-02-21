package com.example.qingunext.app.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.datamodel.*;
import com.example.qingunext.app.notifications.NotiActivity;
import com.example.qingunext.app.util_global.NetworkBase;
import com.example.qingunext.app.util_global.QingUNetworkCenter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rye on 6/17/2015.<br/><br/>
 * <p/>
 * The service is for all the notifications available from the API, including Mail, Refer and At.<br/><br/>
 * <p/>
 * After the Activity is terminated, the QingUApp Application and this Service invoke onCreate again,
 * which leads to the problem that some init tasks in QingU Activity is not executed,
 * httpClient not available to get json.<br/><br/>
 * To solve the bug, global init tasks must be executed during QingUApp.onCreate()
 */
public class NotiService extends Service {
    // http://api.byr.cn/refer/reply/info.json?appkey=7a282a1a9de5b450

    private final static String TAG = "NotificationService";
    //    private IBinder iBinder = new MyBinder();
    private ExecutorService mExecutorService;

    @Override
    public IBinder onBind(Intent intent) {
        if (QingUApp.DEBUG) Log.v(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        if (QingUApp.DEBUG) Log.v(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (QingUApp.DEBUG) Log.v(TAG, "onStartCommand");

        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadExecutor();
            mExecutorService.execute(new MyRunnable());
        }

        return START_STICKY;
    }

    private void notifyNewMail(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, NotiActivity.class), 0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(-1, notification);
    }

    private void notifyNewReply(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, NotiActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(-1, notification);
    }

 /*   public class MyBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }*/

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    checkNewMail();
                    checkNewReply();
                } catch (IOException e) {
                    // todo when activity closed
                    if (QingUApp.DEBUG) Log.v(TAG, "network");
                    e.printStackTrace();
                    break;
//                        QingUApp.getInstance().popNetworkError();
                } catch (JSONException e) {
                    if (QingUApp.DEBUG) Log.v(TAG, "json bug");
                    e.printStackTrace();
                    break;
//                        QingUApp.getInstance().popUnknownError();
                }
                try {
                    // todo speed up connection when new notification like a item_reply comes.
                    TimeUnit.MINUTES.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void checkNewReply() throws IOException, JSONException {
            String json = NetworkBase.downloadJson(QingUNetworkCenter.buildAPI("refer", "reply", "info"));
            JSONObject jsonObject = new JSONObject(json);
            int newCount = jsonObject.getInt("new_count");
            if (newCount > 0) {
                new ReplyDetailGettingTask().execute(newCount);
                String post = NetworkBase.post(
                        new QingUNetworkCenter.UrlBuilder("refer",
                                NotiActivity.NotificationType.REPLY.toString().toLowerCase(), "setRead").build());
                if (QingUApp.DEBUG) Log.v("SetRead", post);
            }
        }

        private void checkNewMail() throws IOException, JSONException {
            String json = NetworkBase.downloadJson(QingUNetworkCenter.buildAPI("mail", "info"));
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.getBoolean("new_mail")) {
                new MailDetailGettingTask().execute();
            }
        }

        private class ReplyDetailGettingTask extends AsyncTask<Integer, Void, String> {
            private int mNewCount;

            @Override
            protected String doInBackground(Integer... params) {
                mNewCount = params[0];
                String url = new NotiGroup.UrlBuilder().page(1).count(1).build();
                try {
                    String json = NetworkBase.downloadJson(url);
                    NotiGroup notiGroup = NotiGroup.valueOf(json);
                    NotiItem replyInfo = notiGroup.getNotiItems().get(0);
                    return NetworkBase.downloadJson(new Reply.UrlBuilder(replyInfo.getBoardName(), replyInfo.getId()).build());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();//todo
                }
                return null;
            }

            @Override
            protected void onPostExecute(String reply) {
                if (reply != null) {
                    try {
                        Reply newReply = Reply.newInfoInstance(reply);
                        String title = newReply.getUser().getUsername() + " 回复了您的文章";
                        if (mNewCount >= 1) {
                            title += "（还有其他" + (mNewCount - 1) + "条回复）";
                        }
                        notifyNewReply(title, newReply.getUbbContent());
                    } catch (JSONException e) {
                        e.printStackTrace();//todo
                    }
                }
            }
        }

        private class MailDetailGettingTask extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... params) {
                String url = QingUNetworkCenter.buildOptionalAPI("count=1", "mail", "inbox");
                try {
                    String mailBox = NetworkBase.downloadJson(url);
                    MailBox singleMailBox = new MailBox(mailBox);
                    int index = singleMailBox.getMailInfos().get(0).getIndex();
                    return NetworkBase.downloadJson(QingUNetworkCenter.buildAPI("mail", "inbox", "" + index));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();//todo
                }
                return null;
            }

            @Override
            protected void onPostExecute(String mail) {
                if (mail != null) {
                    try {
                        Mail newMail = new Mail(mail);
                        notifyNewMail(/*newMail.getUser().getId() + ": " + */newMail.getTitle(), newMail.getContent());
                    } catch (JSONException e) {
                        e.printStackTrace();//todo
                    }
                }
            }
        }
    }
}
