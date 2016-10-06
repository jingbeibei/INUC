package com.inuc.inuc.main;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.inuc.inuc.R;
import com.inuc.inuc.beans.LastApp;
import com.inuc.inuc.beans.Personnel;
import com.inuc.inuc.main.setting.receiver.UpdateReceiver;
import com.inuc.inuc.utils.ActivityCollector;
import com.inuc.inuc.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {
    private SimpleFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Personnel personnel;
    private SharedPreferences pref;
    LastApp lastApp;
    UpdateReceiver mUpdateReceiver;
    IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollector.addActivity(this);
        pref = getSharedPreferences("data", MODE_PRIVATE);

        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this, personnel);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this, personnel);
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
        initEvent();
    }

    private void initEvent() {


        OkHttpUtils.get().url(Urls.GetLastAppUrl).addParams("appID", "1").build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Snackbar.make(viewPager, "网络连接错误", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response.indexOf("VersionID") > 0) {
                            lastApp = new Gson().fromJson(response, LastApp.class);
                            Intent intent = new Intent(UpdateReceiver.UPDATE_ACTION);
                            intent.putExtra("LastApp", lastApp);
                            sendBroadcast(intent);
                        } else {
                            Snackbar.make(viewPager, response, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    @Override
    public void onStart() {
        super.onStart();
        registerBroadcast();
    }

    @Override
    public void onStop() {
        super.onStop();
        unRegisterBroadcast();
    }

    /**
     * 广播注册
     */
    private void registerBroadcast() {
        mUpdateReceiver = new UpdateReceiver(false);
        mIntentFilter = new IntentFilter(UpdateReceiver.UPDATE_ACTION);
        registerReceiver(mUpdateReceiver, mIntentFilter);
    }

    /**
     * 广播卸载
     */
    private void unRegisterBroadcast() {
        try {
            unregisterReceiver(mUpdateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
