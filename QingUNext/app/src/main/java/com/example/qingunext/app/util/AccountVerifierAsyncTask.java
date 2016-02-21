package com.example.qingunext.app.util;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import com.example.qingunext.app.util_global.NetworkBase;
import com.example.qingunext.app.util_global.QingUNetworkCenter;

import java.io.IOException;

/**
 * Created by Rye on 6/10/2015.
 * must override onPostExecute()
 */
public abstract class AccountVerifierAsyncTask extends AsyncTask<String, Void, String> {

    /**
     * @param params params[0]:username
     * @return json from server or null
     */
    @Override
    @Nullable
    protected String doInBackground(String... params) {
        String username = params[0];
        try {
            return NetworkBase.downloadJson(
                    QingUNetworkCenter.buildAPI("user", "query", username));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected abstract void onPostExecute(@Nullable String json);
}
