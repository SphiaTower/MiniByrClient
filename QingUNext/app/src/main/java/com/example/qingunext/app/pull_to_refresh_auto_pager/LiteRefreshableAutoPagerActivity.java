package com.example.qingunext.app.pull_to_refresh_auto_pager;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.qingunext.app.R;
import com.example.qingunext.app.page_home.NavigatorActivity;
import tower.sphia.auto_pager_recycler.lib.AutoPagerManager;
import tower.sphia.auto_pager_recycler.lib.AutoPagerRefreshableFragment;

/**
 * Created by Voyager on 8/11/2015.
 */
public abstract class LiteRefreshableAutoPagerActivity extends NavigatorActivity
        implements BottomBarListener {

    @Override
    public void onClickNew(View view) {
    }

    @Override
    public void onClickBookmark(View view) {

        ImageButton imageButton = (ImageButton) view;
        ObjectAnimator.ofArgb(imageButton, "backgroundColor", Color.YELLOW).start();
    }

    @Override
    public void onClickElevate(View view) {
        AutoPagerRefreshableFragment fragment = (AutoPagerRefreshableFragment) getSupportFragmentManager().findFragmentById(R.id.flRecyclerContainer);
        int pageAllCount = fragment.getAutoPagerManager().getLastPageIndex();
        fragment.loadPage(pageAllCount);
    }

    public void showElevatorDialog() {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the diaif(QingUApp.DEBUG) Log.
        DialogFragment newFragment = MyDialogFragment.newInstance(((AutoPagerRefreshableFragment) getSupportFragmentManager().findFragmentById(R.id.flRecyclerContainer)).getAutoPagerManager().getLastPageIndex());
        newFragment.show(ft, "dialog");

    }

    protected abstract String getCustomTitle();


    protected int getContentView() {
        return R.layout.page_refreshable_pager;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        // below is a hack to make the empty view centered in a CoordinatorLayout
        FrameLayout container = (FrameLayout) findViewById(R.id.flRecyclerContainer);
        View fill = new View(this);
        fill.setVisibility(View.INVISIBLE);
        container.addView(fill);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.flRecyclerContainer);
        if (fragment == null) {
            AutoPagerRefreshableFragment frag = initFragment();

            getSupportFragmentManager().beginTransaction().add(R.id.flRecyclerContainer, frag).commit();
            AutoPagerManager autoPagerManager = frag.getAutoPagerManager();
            if (autoPagerManager != null) {
                // todo always null
                autoPagerManager.addOnDataAttachedListener(() -> {
                    View bar = findViewById(R.id.bottom_bar);
                    bar.setAlpha(0f);
                    bar.setVisibility(View.VISIBLE);
                    bar.animate().alpha(1f).setDuration(500);
                });
            }
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getCustomTitle());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    protected abstract AutoPagerRefreshableFragment initFragment();

    /**
     * Created by Voyager on 8/4/2015.
     */
    public static class MyDialogFragment extends DialogFragment {
        int mNum;

        /**
         * Create a new instance of MyDialogFragment, providing "num"
         * as an argument.
         */
        public static MyDialogFragment newInstance(int num) {
            MyDialogFragment f = new MyDialogFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments().getInt("num");

            // Pick a style based on the num.
            int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
           /* switch ((mNum-1)%6) {
                case 1: style = DialogFragment.STYLE_NO_TITLE; break;
                case 2: style = DialogFragment.STYLE_NO_FRAME; break;
                case 3: style = DialogFragment.STYLE_NO_INPUT; break;
                case 4: style = DialogFragment.STYLE_NORMAL; break;
                case 5: style = DialogFragment.STYLE_NORMAL; break;
                case 6: style = DialogFragment.STYLE_NO_TITLE; break;
                case 7: style = DialogFragment.STYLE_NO_FRAME; break;
                case 8: style = DialogFragment.STYLE_NORMAL; break;
            }
            switch ((mNum-1)%6) {
                case 4: theme = android.R.style.Theme_Holo; break;
                case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
                case 6: theme = android.R.style.Theme_Holo_Light; break;
                case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
                case 8: theme = android.R.style.Theme_Holo_Light; break;
            }*/
            setStyle(style, theme);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_dialog, container, false);
            View tv = v.findViewById(R.id.tvDialog);
            ((TextView) tv).setText("" + mNum);
            EditText et = (EditText) v.findViewById(R.id.etDialog);
            et.setText(mNum + "");
            // Watch for button clicks.
            Button button = (Button) v.findViewById(R.id.show);
            button.setOnClickListener(v1 -> {
                int page = Integer.valueOf(String.valueOf(et.getText()));
                ((AutoPagerRefreshableFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.flRecyclerContainer)).loadPage(page);
                dismiss();
                // When button is clicked, call up to owning activity.
            });

            return v;
        }
    }
}

