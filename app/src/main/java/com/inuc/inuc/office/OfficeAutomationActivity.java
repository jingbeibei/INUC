package com.inuc.inuc.office;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.inuc.inuc.R;
import com.inuc.inuc.mailbox.TabContentFragment;
import com.inuc.inuc.mailbox.WriteLetterActivity;
import com.inuc.inuc.utils.ActivityCollector;

import java.util.ArrayList;
import java.util.List;

public class OfficeAutomationActivity extends AppCompatActivity {
    private TabLayout mTabTl;
    private ViewPager mContentVp;

    private TextView BarTitle;
    private ImageView BackImage;
    private TextView BarRight;

    private List<String> tabIndicators;
    private List<Fragment> tabFragments;
    private ContentPagerAdapter contentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_automation);
        ActivityCollector.addActivity(this);
        mTabTl = (TabLayout) findViewById(R.id.tl_tab);
        mContentVp = (ViewPager) findViewById(R.id.vp_content);
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BarRight = (TextView)findViewById(R.id.bar_right_tv);
        BarTitle.setText("电子政务");
//        BarRight.setVisibility(View.VISIBLE);
//        BarRight.setText("写信");
        BackImage = (ImageView)findViewById(R.id.id_back_arrow_image);

        initContent();
        initTab();
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(OfficeAutomationActivity.this);
            }
        });
        BarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OfficeAutomationActivity.this, WriteLetterActivity.class);
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

        tabIndicators.add("校内新闻" );
        tabIndicators.add("校内公告" );
        tabIndicators.add("高教动态" );
        tabIndicators.add("学术报告");
        tabIndicators.add("会议纪要" );
        tabIndicators.add("领导讲话" );

        tabFragments = new ArrayList<>();
        for (String s : tabIndicators) {
            tabFragments.add(OfficeAutomationFragment.newInstance(s));
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
