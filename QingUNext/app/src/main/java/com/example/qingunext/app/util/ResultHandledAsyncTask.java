package com.example.qingunext.app.util;

import android.os.AsyncTask;
import com.example.qingunext.app.R;
import com.example.qingunext.app.database.DataNotFoundException;
import com.example.qingunext.app.util_global.QingUNetworkCenter;
import org.json.JSONException;

/**
 * Created by Rye on 6/19/2015.
 */
public abstract class ResultHandledAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private TaskResult mResult;

    protected abstract Result doInBackgroundThrowException(Params... params) throws Exception;

    protected abstract void onSuccess(Result result);

    @SafeVarargs
    @Override
    protected final Result doInBackground(Params... params) {
        try {
            Result result = doInBackgroundThrowException(params);
            mResult = TaskResult.OK;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof DataNotFoundException) {
                mResult = TaskResult.DATA_NOT_FOUND;
            } else if (e instanceof JSONException) {
                mResult = TaskResult.JSON_EXCEPTION;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        switch (mResult) {
            case OK:
                onSuccess(result);
                break;
            case JSON_EXCEPTION:
                QingUNetworkCenter.getInstance().popJsonError();
                break;
            case DATA_NOT_FOUND:
                QingUNetworkCenter.getInstance().popDataNotFoundError();
                break;
            case NETWORK_EXCEPTION:
                QingUNetworkCenter.getInstance().popNetworkError();
                break;
            default:
                QingUNetworkCenter.getInstance().popUnknownError();
        }
    }

    // TODO delete on release
    @Override
    protected void onCancelled() {
        super.onCancelled();
        QingUNetworkCenter.getInstance().say(R.string.task_cancelled);
    }

    /**
     * Created by Rye on 6/19/2015.
     */
    public enum TaskResult {
        OK, NETWORK_EXCEPTION, JSON_EXCEPTION, DATA_NOT_FOUND
    }
}
