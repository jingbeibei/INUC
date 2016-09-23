package com.inuc.inuc.main.setting;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.inuc.inuc.R;
import com.inuc.inuc.beans.LastApp;
import com.inuc.inuc.main.setting.receiver.UpdateReceiver;
import com.inuc.inuc.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;


/**
 * Created by 景贝贝 on 2016/9/6.
 */
public class SettingFragment extends Fragment {
    private TextView BarTitle;
    private ImageView BackImage;
    private LinearLayout updateLy;
    private LinearLayout aboutLy;

    private SharedPreferences pref;


    private String myGetLastAppUrl;

    LastApp lastApp;
    UpdateReceiver mUpdateReceiver;
    IntentFilter mIntentFilter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences("data", getContext().MODE_PRIVATE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_setting,container,false);
        BackImage = (ImageView) view.findViewById(R.id.id_back_arrow_image);
        BarTitle = (TextView) view.findViewById(R.id.id_bar_title);
        BackImage.setVisibility(View.INVISIBLE);
        BarTitle.setText("设置");
        updateLy= (LinearLayout) view.findViewById(R.id.update_data_layout);
        aboutLy= (LinearLayout) view.findViewById(R.id.about_layout);
        initEvent();
        return view;
    }

    private void initEvent() {
        updateLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OkHttpUtils.get().url(Urls.GetLastAppUrl).addParams("appID","1").build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Snackbar.make(updateLy, "网络连接错误", Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                if (response.indexOf("VersionID") > 0) {
                                    lastApp = new Gson().fromJson(response,LastApp.class);
                                    Intent intent=new Intent(UpdateReceiver.UPDATE_ACTION);
                                    intent.putExtra("LastApp",lastApp);
                                    getActivity().sendBroadcast(intent);
                                } else {
                                    Snackbar.make(updateLy, response, Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });

        aboutLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),AboutActivity.class);
                startActivity(intent);
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
        getActivity().registerReceiver(mUpdateReceiver, mIntentFilter);
    }

    /**
     * 广播卸载
     */
    private void unRegisterBroadcast() {
        try {
            getActivity().unregisterReceiver(mUpdateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
