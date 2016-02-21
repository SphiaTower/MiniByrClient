package com.example.qingunext.app.page_home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.pages_other.SignInActivity;
import com.example.qingunext.app.util.AccountVerifierAsyncTask;
import com.example.qingunext.app.util_global.QingUNetworkCenter;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by Voyager on 9/14/2015.
 */
public class NewQingUActivity extends NavigatorActivity {

    public static final String
            TAG = "NewQingUActivity",
            TAG_USER = "USER: ";
    public static final int LOADER_FACE = 1;
    public static final String RESTORE_KEY_USERNAME = "username";
    private String mUsername;
    private String mPassword;
    private String mFaceUrl;
    private LoaderManager.LoaderCallbacks<Bitmap> mUserFaceLoaderCallbacks = new LoaderManager.LoaderCallbacks<Bitmap>() {
        @Override
        public Loader<Bitmap> onCreateLoader(int i, Bundle bundle) {
            return new UserFaceLoader(NewQingUActivity.this, mFaceUrl);
        }

        @Override
        public void onLoadFinished(Loader<Bitmap> loader, Bitmap bitmap) {
            if (bitmap != null) {
                ImageView ivUserFace = (ImageView) findViewById(R.id.ivDrawerUserFace);
                ivUserFace.setImageBitmap(bitmap);
            }
        }

        @Override
        public void onLoaderReset(Loader<Bitmap> loader) {

        }
    };
    private WeakReference<AsyncTask<?, ?, ?>> mAsyncTaskWeakReference;

    public static void from(Context from) {
        Intent intent = new Intent(from, NewQingUActivity.class);
        from.startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (QingUApp.DEBUG) Log.i(TAG, "onSaveInstanceState");
        outState.putString(RESTORE_KEY_USERNAME, mUsername);
        super.onSaveInstanceState(outState);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (QingUApp.DEBUG) Log.d(TAG, "onCreate() called with " + "savedInstanceState = [" + savedInstanceState + "]");
//        startService(new Intent(this, NotificationService.class));//todo multiple start

        if (savedInstanceState == null) {
            /* starting a new app, load splash screen */
            loadSplash();
        } else {
            String username = savedInstanceState.getString(RESTORE_KEY_USERNAME);
            if (username == null) {
                loadSplash();
            } else {
                mUsername = username;
                onVerificationPassed();
            }
        }
    }

    private void loadSplash() {
        if (QingUApp.DEBUG) Log.i(TAG, "savedInstanceState == null");
            /* show title screen */
        ImageView titleScreen = new ImageView(this);
        titleScreen.setImageResource(R.drawable.title_screen);
        setContentView(titleScreen);
        mUsername = QingUApp.getInstance().getUsername();
        mPassword = QingUApp.getInstance().getPassword();
        if (QingUApp.DEBUG) Log.i(TAG, TAG_USER + "load from App: " + mUsername + " " + mPassword);

        if (mUsername == null) {
            if (QingUApp.DEBUG) Log.i(TAG, TAG_USER + "found nada");
            SignInActivity.from(this);
        } else {
            if (QingUApp.DEBUG) Log.i(TAG, TAG_USER + "start verifying user identity");
            UserAccountVerifierAsyncTask userAccountVerifierAsyncTask = new UserAccountVerifierAsyncTask();
            mAsyncTaskWeakReference = new WeakReference<>(userAccountVerifierAsyncTask);
            userAccountVerifierAsyncTask.execute(mUsername);
        }
    }

    /**
     * only called when building WidgetList for the first time
     * create fragment after data got
     */
    private void onVerificationPassed() {
        if (QingUApp.DEBUG) Log.d(TAG, "onVerificationPassed ");
        setContentView(R.layout.page_home_viewpager);


        ViewPager viewPager = (ViewPager) findViewById(R.id.pagerHomePage);
        viewPager.setPageTransformer(false, new ZoomOutPageTransformer());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                switch (i) {
                    case 0:
                        return BookmarkFragment.newInstance();
                    case 1:
                        return WidgetListFragment.newInstance();
                    case 2:
                        return SectionListFragment.newInstance();
                    default:
                        throw new IndexOutOfBoundsException();

                }
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Bookmarks";
                    case 1:
                        return "What's hot";
                    case 2:
                        return "Sections";
                    default:
                        throw new IndexOutOfBoundsException();
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager.setCurrentItem(1);
        initLoaders();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (QingUApp.DEBUG) Log.d(TAG, "onStop ");
        if (mAsyncTaskWeakReference != null) {
            AsyncTask<?, ?, ?> asyncTask = mAsyncTaskWeakReference.get();
            if (asyncTask != null) {
                asyncTask.cancel(true);
            }
        }
    }


    private void initLoaders() {
        LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(LOADER_FACE, null, mUserFaceLoaderCallbacks);
    }


/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case (R.id.switchAccount):
                new SignInPage.SignInIntent(this).start();
                break;
            case (R.id.throwBug):
                int t = 1 / 0;
                break;
            case (R.id.mailBox):
                MailBoxPage.MailBoxIntent.createMailBoxIntent(this).start();
                break;
            case (R.id.notification):
                new NotificationPage.Intent(this).start();
                break;
            case (R.id.clearCache):
                QingUApp.getInstance().getSqlHelper().clear();
                break;
            case (R.id.test):
                Intent intent = new Intent(this, TestActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }*/

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_page_menu, menu);
        return true;
    }*/


    private static class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                // small near the boarder, normal near the center
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                // ??
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }


    private class UserAccountVerifierAsyncTask extends AccountVerifierAsyncTask {

        /**
         * @param json user data replied from server
         */
        @Override
        protected void onPostExecute(@Nullable String json) {
            if (json == null) {
                if (QingUApp.DEBUG) Log.i(TAG, TAG_USER + "no response from server");
                // network unavailable, restore data from database
                onVerificationPassed();
            } else {
                if (QingUApp.DEBUG) Log.i(TAG, TAG_USER + json);
                try {
                    JSONObject response = new JSONObject(json);
                    try {
                        mFaceUrl = response.getString("face_url");
                        QingUApp.getInstance().setUsername(mUsername);
                        if (QingUApp.DEBUG) Log.i(TAG, TAG_USER + "verification passed");
                        onVerificationPassed();
                    } catch (JSONException e) {
                        if (QingUApp.DEBUG) Log.i(TAG, TAG_USER + "wrong response from server");
                        /* go to sign in page when verification failed */
                        switch (response.getString("code")) {
                            case "0104":
                                QingUNetworkCenter.getInstance().say(R.string.sign_in_ip_banned);
                                break;
                            case "0101":
                                QingUNetworkCenter.getInstance().say(R.string.sign_in_wrong_identity);
                                break;
                        }
                        SignInActivity.from(NewQingUActivity.this);
                    }
                } catch (JSONException ignored) {
                }
            }
        }
    }
}
