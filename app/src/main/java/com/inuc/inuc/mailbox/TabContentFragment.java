package com.inuc.inuc.mailbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.inuc.inuc.R;
import com.inuc.inuc.beans.Letter;
import com.inuc.inuc.utils.ActivityCollector;
import com.inuc.inuc.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by yifeng on 16/8/3.
 *
 */
public class TabContentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String EXTRA_CONTENT = "content";
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;
    private List<Letter> mData;
    private int pageIndex = 1;
    private LetterAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private SharedPreferences pref;
    private String token;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences("data", getContext().MODE_PRIVATE);
        token = pref.getString("token", "");
    }

    public static TabContentFragment newInstance(String content){
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_CONTENT, content);
        TabContentFragment tabContentFragment = new TabContentFragment();
        tabContentFragment.setArguments(arguments);
        return tabContentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.activity_chief_mailbox, null);

        initView(contentView);
        onRefresh();
        return contentView;
    }
    private void initView(View contentView) {

        mSwipeRefreshWidget = (SwipeRefreshLayout) contentView.findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.primary_light, R.color.colorAccent);
        mSwipeRefreshWidget.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView)contentView. findViewById(R.id.recycle_View);
        mLayoutManager = new LinearLayoutManager(getContext());//设置布局管理器,默认垂直
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//增加或删除条目动画
        //添加分割线
        // mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST));

        mAdapter = new LetterAdapter(getContext());
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);

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
        loadDate(pageIndex, Urls.PAZE_SIZE, "");
    }

    public void showProgress() {
        mSwipeRefreshWidget.setRefreshing(true);
    }

    public void hideProgress() {
        mSwipeRefreshWidget.setRefreshing(false);
    }

    //通过网络获取数据
    public void loadDate(int Index, int size, String url) {
        OkHttpUtils.get().url(Urls.GetLettersUrl).addHeader("Authorization", token)
                .addParams("title", "")
                .addParams("letterType", getArguments().getString(EXTRA_CONTENT))
                .addParams("pageIndex", Index + "")
                .addParams("pageSize", size + "").build()
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
                            List<Letter> letterList = new GsonBuilder().serializeNulls().create().fromJson(response, new TypeToken<List<Letter>>() {
                            }.getType());
                            addonSuccess(letterList);
                        } else {
                            List<Letter> LetterList1 = new ArrayList<Letter>();
                            addonSuccess(LetterList1);
                            // listener.onFailure("load report list failure.");
                        }
                    }
                });

    }

    public void addonSuccess(List<Letter> list) {
        hideProgress();
        addNews(list);
    }


    public void addonFailure(String msg) {
        hideProgress();
        showLoadFailMsg(msg);
    }

    public void addNews(List<Letter> letterList) {
        mAdapter.isShowFooter(true);
        if (mData == null) {
            mData = new ArrayList<Letter>();
        }
        mData.addAll(letterList);
        if (pageIndex == 1) {
            if (letterList.size() <= Urls.PAZE_SIZE) {
                mAdapter.isShowFooter(false);
            }
            mAdapter.setmDate(mData);

        } else {
            //如果没有更多数据了,则隐藏footer布局
            if (letterList == null || letterList.size() == 0) {
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
    private LetterAdapter.OnItemClickListener mOnItemClickListener = new LetterAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Letter letter = mAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), LetterDetailedActivity.class);
            intent.putExtra("letterID", letter.getLetterID() + "");
            int i = letter.getLetterID();
            startActivity(intent);

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
                    ) {
                //加载更多
                loadDate(pageIndex, Urls.PAZE_SIZE, "");
            }
        }
    };

}
