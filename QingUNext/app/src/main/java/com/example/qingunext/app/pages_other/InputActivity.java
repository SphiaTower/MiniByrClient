package com.example.qingunext.app.pages_other;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.datamodel.Reply;
import com.example.qingunext.app.page_thread.ReplyActivity;
import com.example.qingunext.app.util.IntentHelper;
import com.example.qingunext.app.util_global.QingUNetworkCenter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Rye on 5/16/2015.
 * 处理一切发送任务的页面
 * todo 需要的参数太过复杂，需要重新处理
 */
public class InputActivity extends FragmentActivity {

    private static final String ARG_MODE = "mode";
    private Button mBnSend;
    private EditText mEtTitle;
    private EditText mEtContent;
    private ToggleButton mBnBold;
    private ToggleButton mBnItalic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_input);
        mBnSend = (Button) findViewById(R.id.bnIPSend);
        mEtTitle = (EditText) findViewById(R.id.etIPTitle);
        mEtContent = (EditText) findViewById(R.id.etIPContent);
        mBnBold = (ToggleButton) findViewById(R.id.bnIPBold);
        mBnBold.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String bold = "[" + mBnBold.getText() + "]";
            mEtContent.setText(mEtContent.getText().insert(mEtContent.getSelectionStart(), bold));
            mEtContent.setSelection(mEtContent.getText().length());
        });
        mBnItalic = (ToggleButton) findViewById(R.id.bnIPItalic);
        mBnItalic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String italic = "[" + mBnItalic.getText() + "]";
            mEtContent.setText(mEtContent.getText().insert(mEtContent.getSelectionStart(), italic));
            mEtContent.setSelection(mEtContent.getText().length());
        });

        Bundle bundle = getIntent().getExtras();
        Mode mode = Mode.valueOf(bundle.getString(ARG_MODE));
        switch (mode) {
            // todo 这里的代码复用惨不忍睹
            case REPLY:
                try {
                    Reply reply = Reply.newInfoInstance(bundle.getString(ReplyArgs.JSON.name()));
                    int replyID = reply.getId();
                    int groupID = reply.getGroupID();
                    String title = bundle.getString(ReplyArgs.TITLE.name());
                    String boardName = bundle.getString(ReplyArgs.BOARD_NAME.name());
                    mEtTitle.setText("Re:" + title);
                    mEtContent.setText(reply.getPrompt());
                    mBnSend.setOnClickListener(new MyListener() {
                        @Override
                        public void doInNewThread() throws IOException {
                            String serverResponse = QingUNetworkCenter.postArticle(boardName, getPostTitle(), getPostContent(), replyID);
                            if (QingUApp.DEBUG) Log.v("POST_RE", serverResponse);
                            ReplyActivity.ReplyIntent.start(InputActivity.this, boardName, groupID, title);
                        }
                    });
                } catch (JSONException e) {
                    QingUNetworkCenter.getInstance().popUnknownError();
                }

                break;
            case EDIT:
                try {
                    Reply reply = Reply.newInfoInstance(bundle.getString(EditArgs.JSON.name()));
                    int id = reply.getId();
                    int groupID = reply.getGroupID();
                    String title = bundle.getString(EditArgs.TITLE.name());
                    String boardName = bundle.getString(EditArgs.BOARD_NAME.name());
                    mEtTitle.setText(title);
                    mEtContent.setText(reply.getUbbContent());
                    mBnSend.setOnClickListener(new MyListener() {
                        @Override
                        public void doInNewThread() throws IOException {
                            String serverResponse = QingUNetworkCenter.postEditArticle(boardName, getPostTitle(), getPostContent(), id);
                            if (QingUApp.DEBUG) Log.v("POST_RE", serverResponse);
                            ReplyActivity.ReplyIntent.start(InputActivity.this, boardName, groupID, title);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case NEW_POST: {
                String boardName = bundle.getString(NewPostArgs.BOARD_NAME.name());
                mBnSend.setOnClickListener(new MyListener() {
                    @Override
                    public void doInNewThread() throws IOException {
                        String serverResponse = QingUNetworkCenter.postArticle(boardName, getPostTitle(), getPostContent());
                        if (QingUApp.DEBUG) Log.v("POST_RE", serverResponse);
                        try {
                            JSONObject json = new JSONObject(serverResponse);
                            int newPostID = json.getInt("id");
                            ReplyActivity.ReplyIntent.start(InputActivity.this, boardName, newPostID, getPostTitle());
                        } catch (JSONException e) {
                            QingUNetworkCenter.getInstance().say(R.string.wrong_response);
                            e.printStackTrace();
                        }
                    }
                });
                break;
            }

            case NEW_REPLY: {
                String title = bundle.getString(NewReplyArgs.TITLE.name());
                String boardName = bundle.getString(NewReplyArgs.BOARD_NAME.name());
                int groupID = bundle.getInt(NewReplyArgs.GROUP_ID.name());
                mEtTitle.setText("Re:" + title);
                mBnSend.setOnClickListener(new MyListener() {
                    @Override
                    public void doInNewThread() throws IOException {
                        String serverResponse = QingUNetworkCenter.postArticle(boardName, getPostTitle(), getPostContent(), groupID);
                        if (QingUApp.DEBUG) Log.v("POST_RE", serverResponse);
                        ReplyActivity.ReplyIntent.start(InputActivity.this, boardName, groupID, title);
                    }
                });
                break;
            }
            case SEND_MAIL:
                String userID = bundle.getString(MailArgs.USER_ID.name());
                mBnSend.setOnClickListener(new MyListener() {
                    @Override
                    public void doInNewThread() throws IOException {
                        String serverResonse = QingUNetworkCenter.postNewMail(userID, getPostTitle(), getPostContent(), true);
                        //todo analyze the response
                    }
                });
                break;
        }
    }

    private enum Mode {
        NEW_POST, REPLY, NEW_REPLY, EDIT, SEND_MAIL
    }

    private enum ReplyArgs {
        JSON, TITLE, BOARD_NAME
    }

    private enum NewReplyArgs {
        BOARD_NAME, GROUP_ID, TITLE
    }

    private enum EditArgs {
        JSON, TITLE, BOARD_NAME
    }

    private enum NewPostArgs {
        BOARD_NAME
    }

    private enum MailArgs {
        USER_ID
    }

    public static class InputIntent extends IntentHelper {


        private InputIntent(Context from) {
            super(from, InputActivity.class);
        }

        public static void startReplyIntent(Context from, String boardName, String title, String replyJson) {
            InputIntent inputIntent = new InputIntent(from);
            inputIntent.putString(ARG_MODE, Mode.REPLY.name());
            inputIntent.putString(ReplyArgs.JSON, replyJson);
            inputIntent.putString(ReplyArgs.BOARD_NAME, boardName);
            inputIntent.putString(ReplyArgs.TITLE, title);
            inputIntent.start();
        }

        public static void startNewPostIntent(Context from, String boardName) {
            InputIntent inputIntent = new InputIntent(from);
            inputIntent.putString(ARG_MODE, Mode.NEW_POST.name());
            inputIntent.putString(NewPostArgs.BOARD_NAME, boardName);
            inputIntent.start();
        }

        public static void startNewReplyIntent(Context from, String boardName, int groupID, String title) {
            InputIntent inputIntent = new InputIntent(from);
            inputIntent.putString(ARG_MODE, Mode.NEW_REPLY.name());
            inputIntent.putString(NewReplyArgs.BOARD_NAME, boardName);
            inputIntent.putInt(NewReplyArgs.GROUP_ID, groupID);
            inputIntent.putString(NewReplyArgs.TITLE, title);
            inputIntent.start();
        }

        public static void startEditIntent(Context from, String boardName, String title, String replyJson) {
            InputIntent inputIntent = new InputIntent(from);
            inputIntent.putString(ARG_MODE, Mode.EDIT.name());
            inputIntent.putString(EditArgs.BOARD_NAME, boardName);
            inputIntent.putString(EditArgs.JSON, replyJson);
            inputIntent.putString(EditArgs.TITLE, title);
            inputIntent.start();
        }

        public static void startSendMailIntent(Context from, String userID) {
            InputIntent inputIntent = new InputIntent(from);
            inputIntent.putString(ARG_MODE, Mode.SEND_MAIL.name());
            inputIntent.putString(MailArgs.USER_ID, userID);
            inputIntent.start();
        }
    }


    private abstract class MyListener implements View.OnClickListener {

        private String postTitle;
        private String postContent;

        final public String getPostTitle() {
            return postTitle;
        }

        final public String getPostContent() {
            return postContent;
        }

        public abstract void doInNewThread() throws IOException;

        @Override
        final public void onClick(View v) {
            postTitle = mEtTitle.getText().toString();
            postContent = mEtContent.getText().toString();
            if (postTitle.equals("")) {
                QingUNetworkCenter.getInstance().say(R.string.plz_input_title);
                return;
            }
            if (postContent.equals("")) {
                QingUNetworkCenter.getInstance().say(R.string.plz_input_content);
                return;
            }
            if (QingUApp.DEBUG) Log.v("toPost", postTitle + " " + postContent);
            new Thread(() -> {
                try {
                    doInNewThread();
                } catch (IOException e) {
                    QingUNetworkCenter.getInstance().popNetworkError();
                }
            }).start();
        }
    }

}
