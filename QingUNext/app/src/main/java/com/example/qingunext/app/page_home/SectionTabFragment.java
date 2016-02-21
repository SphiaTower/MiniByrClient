package com.example.qingunext.app.page_home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;

/**
 * Created by Rye on 7/18/2015.
 */
public class SectionTabFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = ((LinearLayout) inflater.inflate(R.layout.frag_sections_tab, container, false));
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tlSectionTab);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vpSectionTab);
        viewPager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return SectionFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return 10;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getResources().getStringArray(R.array.sections2)[position];
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }


}
