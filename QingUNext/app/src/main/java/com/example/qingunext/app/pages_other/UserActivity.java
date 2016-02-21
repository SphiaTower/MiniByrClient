package com.example.qingunext.app.pages_other;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import com.example.qingunext.app.util.IntentHelper;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.util.WeakReferenceHandler;
import com.example.qingunext.app.datamodel.User;
import org.json.JSONException;

/**
 * Created by Rye on 2/18/2015.
 */
// todo rewrite all
public class UserActivity extends FragmentActivity implements UserFragment.UserSupplier {
    public static final String ARG_USER = "user";
    private User user;
    private Handler handler;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_USER, user.toString());
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_user);
        handler = new UserPageHandler(this);
        String userJson;
        if (savedInstanceState == null) {
            userJson = getIntent().getExtras().getString(ARG_USER);
        } else {
            userJson = savedInstanceState.getString(ARG_USER);
        }
        try {
            user =  User.valueOf(userJson);
        } catch (JSONException e) {
            // todo dead users
            e.printStackTrace();
        }
        UserFragment userFragment = new UserFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.llUserPage, userFragment).commit();

        new Thread(() -> {
            Message msg = new Message();
            try {
//                user.loadFace();
                msg.what = QingUApp.LOADING_COMPLETE;
            } finally {
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public User getUser() {
        return user;
    }

    private static class UserPageHandler extends WeakReferenceHandler<UserActivity> {
        public UserPageHandler(UserActivity reference) {
            super(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QingUApp.LOADING_COMPLETE:
                    Drawable face = null;
                    if (face != null) {
                        ImageView imageView = (ImageView) getReference().findViewById(R.id.ivUserPageFace);
                        imageView.setImageDrawable(face);
                        imageView.invalidate();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public static class UserIntent extends IntentHelper {

        public UserIntent(Context from, String userJson) {
            super(from, UserActivity.class);
            putString(ARG_USER, userJson);
        }
    }
}
