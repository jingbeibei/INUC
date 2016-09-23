package com.inuc.inuc.mailbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.inuc.inuc.R;
import com.inuc.inuc.utils.ActivityCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yifeng on 16/8/3.
 *
 */
public class TabLayoutTopActivity extends AppCompatActivity {

    private TabLayout mTabTl;
    private ViewPager mContentVp;

    private TextView BarTitle;
    private ImageView BackImage;
    private TextView BarRight;

    private List<String> tabIndicators;
    private List<Fragment> tabFragments;
    private ContentPagerAdapter contentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout_top);

        mTabTl = (TabLayout) findViewById(R.id.tl_tab);
        mContentVp = (ViewPager) findViewById(R.id.vp_content);
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BarRight = (TextView)findViewById(R.id.bar_right_tv);
        BarTitle.setText("校长信箱");
//        BarRight.setVisibility(View.VISIBLE);
//        BarRight.setText("写信");
        BackImage = (ImageView)findViewById(R.id.id_back_arrow_image);

        initContent();
        initTab();
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(TabLayoutTopActivity.this);
            }
        });
        BarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TabLayoutTopActivity.this, WriteLetterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initTab(){
        mTabTl.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabTl.setTabTextColors(ContextCompat.getColor(this, R.color.gray), ContextCompat.getColor(this, R.color.colorWhite));
        mTabTl.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorWhite));
        ViewCompat.setElevation(mTabTl, 10);//提供兼容性
        mTabTl.setupWithViewPager(mContentVp);
    }

    private void initContent(){
        tabIndicators = new ArrayList<>();

            tabIndicators.add("教学" );
            tabIndicators.add("科研" );
            tabIndicators.add("人事" );
            tabIndicators.add("党建");
            tabIndicators.add("后勤" );

        tabFragments = new ArrayList<>();
        for (String s : tabIndicators) {
            tabFragments.add(TabContentFragment.newInstance(s));
        }
        contentAdapter = new ContentPagerAdapter(getSupportFragmentManager());
        mContentVp.setAdapter(contentAdapter);
    }

    class ContentPagerAdapter extends FragmentPagerAdapter {

        public ContentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return tabFragments.get(position);
        }

        @Override
        public int getCount() {
            return tabIndicators.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabIndicators.get(position);
        }

    }

}
