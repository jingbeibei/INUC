package com.inuc.inuc.main;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.inuc.inuc.beans.Personnel;


/**
 * Created by 景贝贝 on 2016/8/18.
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private static final String[] mTitles = {"ZHUYE", "tab2", "tab3"};
    private Personnel personnel;
    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context, Personnel personnel) {
        super(fm);
        this.context = context;
        this.personnel=personnel;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {

        return PageFragment.newInstance(position + 1,personnel);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
