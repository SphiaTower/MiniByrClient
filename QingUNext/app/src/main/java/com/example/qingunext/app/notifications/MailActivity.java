package com.example.qingunext.app.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.example.qingunext.app.util.IntentHelper;
import com.example.qingunext.app.R;
import com.example.qingunext.app.util.ResultHandledAsyncTask;
import com.example.qingunext.app.database.DataNotFoundException;
import com.example.qingunext.app.datamodel.Mail;
import com.example.qingunext.app.util_global.QingUNetworkCenter;
import org.json.JSONException;

/**
 * Created by Rye on 5/25/2015.
 */
public class MailActivity extends AppCompatActivity {
    public static final String
            ARG_BOX_NAME = "box",
            ARG_INDEX = "index";
    private String mBoxName;
    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_mail);
        Bundle bundle = getIntent().getExtras();
        mBoxName = bundle.getString(ARG_BOX_NAME);
        mIndex = bundle.getInt(ARG_INDEX);
        new ResultHandledAsyncTask<Void, Void, Mail>() {

            @Override
            protected Mail doInBackgroundThrowException(Void... params) throws JSONException, DataNotFoundException {
                String json = QingUNetworkCenter.getInstance().downloadJsonIfOkSaveElseQueryDB(QingUNetworkCenter.buildAPI("mail", mBoxName, "" + mIndex));
                return new Mail(json);
            }

            @Override
            protected void onSuccess(Mail mail) {
                ((TextView) findViewById(R.id.tvMailPageTitle)).setText(mail.getTitle());
                ((TextView) findViewById(R.id.tvMailPageContent)).setText(mail.getContent());
            }
        }.execute();

    }

    public static void from(Context from, String boxName, int index) {
        Intent intent = new Intent(from, MailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ARG_BOX_NAME, boxName);
        bundle.putInt(ARG_INDEX, index);
        intent.putExtras(bundle);
        from.startActivity(intent);
    }
}
