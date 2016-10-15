package com.inuc.inuc.meetings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.inuc.inuc.R;
import com.inuc.inuc.beans.News;
import com.inuc.inuc.beans.OAMeeting;
import com.inuc.inuc.news.NewsAdapter;
import com.inuc.inuc.news.NewsDetailedActivity;
import com.inuc.inuc.news.NewsListActivity;
import com.inuc.inuc.news.PublishNewsActivity;
import com.inuc.inuc.utils.ActivityCollector;
import com.inuc.inuc.utils.Urls;
import com.inuc.inuc.utils.WeekUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class OAMeetingsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;
    private List<OAMeeting> mData;
    private int pageIndex = 1;
    private MeetingAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;


    private SharedPreferences pref;
    private String token;
    private TextView BarTitle;
    private ImageView BackImage;
    private TextView BarRight;
    Date today = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oameetings_list);
        ActivityCollector.addActivity(this);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        token = pref.getString("token", "");
today=new Date();
        initView();
        onRefresh();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        Log.d("第几周", c.get(Calendar.WEEK_OF_YEAR) + "");
        Log.d("哪一年", c.get(Calendar.YEAR) + "");
//        Log.d("哪一年", getWeekNumByYear(2016) + "");

        // 初始化控件
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // 建立数据源
        String[] mItems = new String[50];
//        = getResources().getStringArray(R.array.languages);
        for (int i = 0; i < 50; i++) {
            mItems[i] =i + "";
        }
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        today=WeekUtil.getFirstDayOfWeek(2016,position+1);
                onRefresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initView() {
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BarRight = (TextView) findViewById(R.id.bar_right_tv);
        BarRight.setText("发布");
        BarRight.setVisibility(View.VISIBLE);
        BarTitle.setText("每周会议");

        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);

        mSwipeRefreshWidget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.primary_light, R.color.colorAccent);
        mSwipeRefreshWidget.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_View);
        mLayoutManager = new LinearLayoutManager(this);//设置布局管理器,默认垂直
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//增加或删除条目动画
        //添加分割线
        // mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST));

        mAdapter = new MeetingAdapter(this);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(OAMeetingsListActivity.this);
            }
        });
        BarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OAMeetingsListActivity.this, PublishNewsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        pageIndex = 1;
        if (mData != null) {
            mData.clear();
        }
        if (pageIndex == 1) {
            showProgress();
        }
        loadDate(today.toString());
    }

    public void showProgress() {
        mSwipeRefreshWidget.setRefreshing(true);
    }

    public void hideProgress() {
        mSwipeRefreshWidget.setRefreshing(false);
    }

    //通过网络获取数据
    public void loadDate(String date) {


        try {
            Log.i("当前日期",sdf.parse(date).toString());
            Log.i("当前第几周",WeekUtil.getWeekOfYear(sdf.parse(date))+"");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        OkHttpUtils.get().url(Urls.GetOAMeetingsUrl).addParams("day", date)
                .addHeader("Authorization", token).build()
                .execute(new StringCallback() {
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
                        if (response.indexOf("Title") > 0) {
                            List<OAMeeting> newsList = new GsonBuilder().serializeNulls().create().fromJson(response, new TypeToken<List<OAMeeting>>() {
                            }.getType());
                            addonSuccess(newsList);
                        } else {
                            List<OAMeeting> newsList1 = new ArrayList<OAMeeting>();
                            addonSuccess(newsList1);
                            // listener.onFailure("load report list failure.");
                        }
                    }
                });

    }

    public void addonSuccess(List<OAMeeting> list) {
        hideProgress();
//        addNews(list);
    }


    public void addonFailure(String msg) {
        hideProgress();
        showLoadFailMsg(msg);
    }

    public void addNews(List<OAMeeting> newsList) {
        mAdapter.isShowFooter(true);
        if (mData == null) {
            mData = new ArrayList<OAMeeting>();
        }
        mData.addAll(newsList);
        if (pageIndex == 1) {
            if (newsList.size() < Urls.PAZE_SIZE) {
                mAdapter.isShowFooter(false);
            }
            mAdapter.setmDate(mData);
        } else {
            //如果没有更多数据了,则隐藏footer布局
            if (newsList == null || newsList.size() == 0) {
                mAdapter.isShowFooter(false);
                Snackbar.make(mRecyclerView, "暂无更多...", Snackbar.LENGTH_SHORT).show();
            }
            mAdapter.notifyDataSetChanged();
        }
        pageIndex += 1;
    }

    public void showLoadFailMsg(String error) {
        if (pageIndex == 1) {
            mAdapter.isShowFooter(false);
            mAdapter.notifyDataSetChanged();
        }
        View v1 = mRecyclerView.getRootView();
        Snackbar.make(v1, error, Snackbar.LENGTH_SHORT).show();
    }

    //item点击事件监听
    private MeetingAdapter.OnItemClickListener mOnItemClickListener = new MeetingAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            OAMeeting news = mAdapter.getItem(position);
//            Intent intent = new Intent(OAMeetingsListActivity.this, NewsDetailedActivity.class);
//            intent.putExtra("id", news.getID()+"");
//
//            startActivity(intent);

        }
    };

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        private int lastVisibleItem;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItem + 1 == mAdapter.getItemCount()
                    && mAdapter.isShowFooter()) {
                Snackbar.make(mRecyclerView, "暂无更多...", Snackbar.LENGTH_SHORT).show();
                //加载更多
//                loadDate(pageIndex, Urls.PAZE_SIZE);
            }
        }
    };

}
