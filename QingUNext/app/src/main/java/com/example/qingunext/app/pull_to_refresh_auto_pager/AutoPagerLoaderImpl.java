package com.example.qingunext.app.pull_to_refresh_auto_pager;

import android.content.Context;
import android.support.annotation.NonNull;
import com.example.qingunext.app.database.DataNotFoundException;
import com.example.qingunext.app.util_global.QingUNetworkCenter;
import tower.sphia.auto_pager_recycler.lib.AutoPagerLoader;
import tower.sphia.auto_pager_recycler.lib.DataNotLoadedException;
import tower.sphia.auto_pager_recycler.lib.Page;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Voyager on 8/17/2015.
 */
public abstract class AutoPagerLoaderImpl<P extends Page<?>> extends AutoPagerLoader<P> {
    private Constructor<P> mGroupConstructor;

    {
        try {
            mGroupConstructor = getGroupClass().getDeclaredConstructor(String.class);
            if (!mGroupConstructor.isAccessible()) {
                mGroupConstructor.setAccessible(true);
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("No constructor found");
        }
    }

    public AutoPagerLoaderImpl(Context ctx) {
        super(ctx);
    }

    @Override
    @NonNull
    protected P newPage(int page) throws DataNotLoadedException {
        return newPage(getGroupString(page));
    }

    protected abstract Class<P> getGroupClass();


    protected P newPage(String string) {
        try {
            @SuppressWarnings("unchecked")
            P p = mGroupConstructor.newInstance(string);
            return p;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("generic error");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            e.getTargetException().printStackTrace();
            throw new RuntimeException(e.getTargetException());
        }
    }


    protected String getGroupString(int page) throws DataNotLoadedException {
        try {
            return QingUNetworkCenter.getInstance().downloadJsonIfOkSaveElseQueryDB(getUrl(page));
        } catch (DataNotFoundException e) {
            throw new DataNotLoadedException();
        }
    }

    protected abstract String getUrl(int page);
}
