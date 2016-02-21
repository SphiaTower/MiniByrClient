package com.example.qingunext.app.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * Created by Rye on 2/23/2015.
 * 根据各个Activity的需求包装Intent并迅速启动
 */
public abstract class IntentHelper {
    private Context from;
    private Class to;
    private Bundle bundle;

    @Deprecated
    protected IntentHelper(Context from, Class to, @Nullable Bundle bundle) {
        this.from = from;
        this.to = to;
        this.bundle = bundle;
    }

    @Deprecated
    protected IntentHelper(Context from, Class to, String key, String value) {
        this.from = from;
        this.to = to;
        this.bundle = new Bundle();
        bundle.putString(key, value);
    }

    @Deprecated
    protected IntentHelper(Context from, Class to, String key, Serializable value) {
        this.from = from;
        this.to = to;
        this.bundle = new Bundle();
        bundle.putSerializable(key, value);
    }

    public IntentHelper(Context from, Class to) {
        this.from = from;
        this.to = to;
        this.bundle = new Bundle();
    }

    /*using delegation*/
    protected void putInt(String key, int value) {
        bundle.putInt(key, value);
    }


    protected void putString(String key, String value) {
        bundle.putString(key, value);
    }
    protected void putInt(Enum<?> key, int value) {
        bundle.putInt(key.name(), value);
    }


    protected void putString(Enum<?> key, String value) {
        bundle.putString(key.name(), value);
    }

    protected void putCharSequence(String key, CharSequence value) {
        bundle.putCharSequence(key, value);
    }

    protected void putSerializable(String key, Serializable value) {
        bundle.putSerializable(key, value);
    }

    protected void putParcelable(String key, Parcelable value) {
        bundle.putParcelable(key, value);
    }

    public void start() {
        Intent intent = getIntent();
        from.startActivity(intent);
    }

    public Intent getIntent() {
        Intent intent = new Intent(from, to);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        return intent;
    }


}
