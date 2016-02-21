package com.example.qingunext.app.notifications;

import android.content.Context;
import android.content.Intent;
import com.example.qingunext.app.pull_to_refresh_auto_pager.LiteRefreshableAutoPagerActivity;
import com.example.qingunext.app.util.IntentHelper;
import tower.sphia.auto_pager_recycler.lib.AutoPagerRefreshableFragment;

public class NotiActivity extends LiteRefreshableAutoPagerActivity {

    @Override
    protected String getCustomTitle() {
        return "回复我的文章";
    }

    @Override
    protected AutoPagerRefreshableFragment initFragment() {
        return new NotiFragment();
    }

    public static void from(Context from) {
        Intent intent = new Intent(from, NotiActivity.class);
        from.startActivity(intent);
    }
/*

    @Override
    protected ReplyNotificationPool initGroup(String json) throws JSONException {
        return ReplyNotificationPool.valueOf(json);
    }
*/

    public enum NotificationType {
        MAIL, REPLY, AT
    }

}
