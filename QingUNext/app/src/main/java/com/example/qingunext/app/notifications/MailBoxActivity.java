package com.example.qingunext.app.notifications;

import android.content.Context;
import android.content.Intent;
import com.example.qingunext.app.datamodel.MailBox;
import com.example.qingunext.app.datamodel.MailInfo;
import com.example.qingunext.app.pull_to_refresh_auto_pager.LiteRefreshableAutoPagerActivity;
import com.example.qingunext.app.util.IntentHelper;
import tower.sphia.auto_pager_recycler.lib.AutoPagerRefreshableFragment;

/**
 * Created by Voyager on 7/29/2015.
 */
public class MailBoxActivity extends LiteRefreshableAutoPagerActivity {

    @Override
    protected String getCustomTitle() {
        return "收件箱";
    }

    @Override
    protected AutoPagerRefreshableFragment<MailBox, MailInfo> initFragment() {
        return new MailBoxFragment();
    }


    public static void from(Context from) {
        Intent intent = new Intent(from, MailBoxActivity.class);
        from.startActivity(intent);
    }
}
