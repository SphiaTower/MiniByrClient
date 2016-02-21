package com.example.qingunext.app.util;

import android.os.Handler;
import android.os.Message;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.util_global.QingUNetworkCenter;

import java.lang.ref.WeakReference;

/**
 * Created by Rye on 4/3/2015.
 * Handler的替代，解决可能的内存泄漏问题
 */
abstract public class WeakReferenceHandler<T> extends Handler {
    protected WeakReference<T> weakReference;

    protected WeakReferenceHandler(T reference) {
        weakReference = new WeakReference<>(reference);
    }

    protected T getReference() {
        return weakReference.get();
    }

    /**
     * @param msg 传入的msg
     * 子类在实现的时候，可以通过回调这个方法，处理网络及Json异常
     */
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case QingUApp.LOADING_COMPLETE:
                break;
            case QingUApp.NETWORK_UNAVAILABLE:
                QingUNetworkCenter.getInstance().popNetworkError();
                break;
            case QingUApp.JSON_EXCEPTION:
                QingUNetworkCenter.getInstance().popJsonError();
                break;
            case QingUApp.NO_DATA_AVAILABLE:
                QingUNetworkCenter.getInstance().say(R.string.no_data_available);
            // 在子类中实现的时候，可以在前一个switch中加上return，再跟上super的switch，来实现default
//            default:
//                QingUApp.getInstance().popUnknownError();
        }
    }
}

