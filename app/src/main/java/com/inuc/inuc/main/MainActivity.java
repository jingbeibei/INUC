package com.inuc.inuc.main;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.inuc.inuc.R;
import com.inuc.inuc.beans.Personnel;
import com.inuc.inuc.utils.ActivityCollector;

public class MainActivity extends AppCompatActivity {
    private SimpleFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Personnel personnel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollector.addActivity(this);
        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this,personnel);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this,personnel);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab one = tabLayout.getTabAt(0);
        TabLayout.Tab two = tabLayout.getTabAt(1);
        TabLayout.Tab three = tabLayout.getTabAt(2);

        one.setCustomView(R.layout.item_tab_layout_custom);
        two.setCustomView(R.layout.item_tab_two_layout_custom);
        three.setCustomView(R.layout.item_tab_three_layout_custom);
    }

}
