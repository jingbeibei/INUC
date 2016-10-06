package com.inuc.inuc.zone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inuc.inuc.R;
import com.inuc.inuc.beans.Personnel;
import com.inuc.inuc.beans.ZoneBean;
import com.inuc.inuc.utils.Urls;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Describe：
 * Created by H、z on 2016/7/14.
 * qq：956439103
 */
public class ZoneMainActivity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {




    private ImageView image_back;
    private ImageView image_publish;

    private Context mContext;
    public static User sUser = new User(999999,"爱中北");

    private int pageIndex = 1;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout refreshLayout;
    private List<ZoneBean> zoneBeanList ;
    private ZoneRecycleViewAdapter zoneRecycleAdapter;
    private TextView BarTitle;


    private SharedPreferences pref;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone_main);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        token = pref.getString("token", "");
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BarTitle.setText("微空间");
        mContext = this;
        initView();
        onRefresh();
        testData();//测试
    }

    private void initView() {
        image_back = (ImageView) findViewById(R.id.id_back_arrow_image);
        image_publish = (ImageView) findViewById(R.id.id_image_publish);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe);
        mRecyclerView = (RecyclerView) findViewById(R.id.id_recycleview_zone);
        image_back.setOnClickListener(this);
        image_publish.setOnClickListener(this);
        refreshLayout.setOnRefreshListener(this);
    }

    /**
     * 测试
     */
    private void testData() {
        zoneBeanList = new ArrayList<>();
        //评论数据

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        zoneRecycleAdapter = new ZoneRecycleViewAdapter(mContext.getSharedPreferences("data",MODE_APPEND), mRecyclerView,zoneBeanList,mContext,new CustomTagHandler(mContext,new CustomTagHandler.OnCommentClickListener(){

            @Override
            public void onCommentatorClick(View view, User commentator) {
                Toast.makeText(mContext, commentator.mName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReceiverClick(View view, User receiver) {
                Toast.makeText(mContext, receiver.mName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onContentClick(View view, User commentator, User receiver) {
                if (commentator != null && commentator.mId == sUser.mId) { // 不能回复自己的评论
                    return;
                }
                inputComment(view, commentator,mRecyclerView);
            }
        }));
        zoneRecycleAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(zoneRecycleAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    private void inputComment(View view) {
        inputComment(view, null,null);
    }
    public void inputComment(final View v, User receiver, RecyclerView mRecyclerView) {
            new ZoneRecycleViewAdapter(mContext.getSharedPreferences("data",MODE_APPEND)).inputComment(ZoneMainActivity.this, mRecyclerView, v, receiver, new ZoneRecycleViewAdapter.InputCommentListener() {
            @Override
            public void onCommitComment() {
                zoneRecycleAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_back_arrow_image:
                finish();break;
            case R.id.id_image_publish:
                startActivity(new Intent(this,ZonePublishActivity.class));break;
        }
    }

    @Override
    public void onRefresh() {
        pageIndex = 1;
        if (zoneBeanList != null) {
            zoneBeanList.clear();
        }
        if (pageIndex == 1) {
            showProgress();
        }
        loadDate(pageIndex,8,1,20);
    }

    public void showProgress() {
        refreshLayout.setRefreshing(true);
    }

    public void hideProgress() {
        refreshLayout.setRefreshing(false);
    }




    private ZoneRecycleViewAdapter.OnItemClickListener mOnItemClickListener = new ZoneRecycleViewAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            //进入单个微空间；
        }
    };

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        private int lastVisibleItem;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItem + 1 == zoneRecycleAdapter.getItemCount()
                    && zoneRecycleAdapter.isShowFooter()) {
                //加载更多
                loadDate(pageIndex,Urls.PAZE_SIZE,1,20);
            }
        }
    };
    //通过网络获取数据
    public void loadDate(int pageIndex,int pageSize,int remarksPageIndex,int remarksPageSize) {
        OkHttpUtils.get()
                .url(Urls.GetTalkingsUrl)
                .addParams("pageIndex", String.valueOf(pageIndex))
                .addParams("pageSize", String.valueOf(pageSize))
                .addParams("remarksPageIndex", String.valueOf(remarksPageIndex))
                .addParams("remarksPageSize", String.valueOf(remarksPageSize))
                .addHeader("Authorization",token)
                .build()
                .execute(new StringCallback(){

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        String error = "";
                        if (e.toString().contains("java.net.ConnectException")) {
                            error = "网络连接失败";
                        } else {
                            error = "未知错误" + e.toString();
                        }
                        addonFailure(error);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response == null){
                            Toast.makeText(ZoneMainActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                            zoneRecycleAdapter.isShowFooter(false);
                        }else{
                            List<ZoneBean> zbList=new Gson().fromJson(response, new TypeToken<List<ZoneBean>>() {
                            }.getType());
                            addonSuccess(zbList);
                        }
                    }
                });

        }


        public void addonSuccess(List<ZoneBean> list) {
            hideProgress();
            addZone(list);
    }


    public void addonFailure(String msg) {
        hideProgress();
        showLoadFailMsg(msg);
    }
    public void addZone(List<ZoneBean> newsList) {
        zoneRecycleAdapter.isShowFooter(true);
        if (zoneBeanList == null) {
            zoneBeanList = new ArrayList<ZoneBean>();
        }
        zoneBeanList.addAll(newsList);
        if (pageIndex == 1) {
            if (newsList.size() < 10) {
                zoneRecycleAdapter.isShowFooter(false);
            }
            zoneRecycleAdapter.setmDate(zoneBeanList);
        } else {
            //如果没有更多数据了,则隐藏footer布局
            if (newsList == null || newsList.size() == 0) {
                zoneRecycleAdapter.isShowFooter(false);
                Toast.makeText(ZoneMainActivity.this, "暂无更多...", Toast.LENGTH_SHORT).show();
            }
            zoneRecycleAdapter.notifyDataSetChanged();
        }
        pageIndex += 1;
    }

    public void showLoadFailMsg(String error) {
        if (pageIndex == 1) {
            zoneRecycleAdapter.isShowFooter(false);
            zoneRecycleAdapter.notifyDataSetChanged();
        }
        View v1 = mRecyclerView.getRootView();
        Toast.makeText(ZoneMainActivity.this, error, Toast.LENGTH_SHORT).show();
    }

}