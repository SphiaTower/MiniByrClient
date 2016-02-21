package com.example.qingunext.app.pages_other;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.page_home.NewQingUActivity;
import com.example.qingunext.app.util.AccountVerifierAsyncTask;
import com.example.qingunext.app.util_global.QingUNetworkCenter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rye on 5/18/2015.
 */
public class SignInActivity extends FragmentActivity {
    private static final String TAG = "SignInActivity";
    private String mUsername;
    private String mPassword;
    private SharedPreferences mSharedPreferences;

    public static void from(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_sign_in);
        mSharedPreferences = getSharedPreferences(QingUApp.SHARED_PREF_NAME, MODE_PRIVATE);
        Button bnConfirm = (Button) findViewById(R.id.bnSignInConfirm);
        bnConfirm.setOnClickListener(v -> {
            mUsername = ((EditText) findViewById(R.id.etSignInUsername)).getText().toString();
            mPassword = ((EditText) findViewById(R.id.etSignInPassword)).getText().toString();
            /*TODO bug*/
            new AccountVerifierAsyncTask() {
                @Override
                protected void onPostExecute(@Nullable String json) {
                    if(QingUApp.DEBUG) Log.d(TAG, "onPostExecute() called with " + "json = [" + json + "]");
                    // cases when unable to verify
                    if (json == null) {
                        QingUNetworkCenter.getInstance().popNetworkError();
                        return;
                    }
                    JSONObject response;
                    try {
                        response = new JSONObject(json);
                    } catch (JSONException e) {
                        QingUNetworkCenter.getInstance().popJsonError();
                        return;
                    }
                    // cases when able
                    try {
                        response.getString("code");
                        QingUNetworkCenter.getInstance().say(R.string.sign_in_wrong_identity);
//                                response.getString("face_url");
                        // todo: if succeed, the face_url could be passed to home page
                    } catch (JSONException e) {
                        QingUApp.getInstance().setUsername(mUsername);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(QingUApp.SHARED_PREF_KEY_USERNAME, mUsername);
                        editor.putString(QingUApp.SHARED_PREF_KEY_PASSWORD, mPassword);
                        editor.apply();
                        NewQingUActivity.from(SignInActivity.this);
                    }
                }
            }.execute(mUsername);

        });

    }

}
