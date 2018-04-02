package com.weather.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by MXY on 2018/3/27.
 */


public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String[] mTab;
    private List<Fragment> list;


    public ViewPagerAdapter(FragmentManager fm, List<Fragment> list, String[] mTab) {
        super(fm);
        this.list = list;
        this.mTab = mTab;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position < list.size()) {
            fragment = list.get(position);
        } else {
            fragment = list.get(0);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTab[position];
    }
}
