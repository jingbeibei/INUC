package com.inuc.inuc.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inuc.inuc.R;
import com.inuc.inuc.beans.BannerPic;
import com.inuc.inuc.mailbox.ChiefMailboxActivity;
import com.inuc.inuc.mailbox.TabLayoutTopActivity;
import com.inuc.inuc.main.ImageSlideshow.ImageSlideshow;
import com.inuc.inuc.news.NewsListActivity;
import com.inuc.inuc.office.OfficeAutomationActivity;
import com.inuc.inuc.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by 景贝贝 on 2016/8/27.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private ImageSlideshow imageSlideshow;
    private List<String> imageUrlList;
    private List<String> titleList;
    private String getBannerURL;
    private List<BannerPic> bannerPicsList = null;
    private SharedPreferences pref;
    private Button newsBt;
    private Button principalMailboxBt;
    private Button officeAutomationBt;




    public HomeFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences("data", getContext().MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        imageSlideshow = (ImageSlideshow) view.findViewById(R.id.is_gallery);

        imageUrlList = new ArrayList<>();
        titleList = new ArrayList<>();
        OkHttpUtils.get().url(Urls.GetPictureNewsUrl).addParams("pageSize","5").build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response.indexOf("TitlePicture") > 0) {
                            bannerPicsList = new Gson().fromJson(response, new TypeToken<List<BannerPic>>() {
                            }.getType());
                            // 初始化数据
                            initData();
                        }
                    }
                });
        initView(view);
        return view;
    }

    /**
     * 初始化数据
     */
    private void initData() {

        String[] imageUrls = {"http://pic3.zhimg.com/b5c5fc8e9141cb785ca3b0a1d037a9a2.jpg",
                "http://pic2.zhimg.com/551fac8833ec0f9e0a142aa2031b9b09.jpg",
                "http://pic2.zhimg.com/be6f444c9c8bc03baa8d79cecae40961.jpg",
                "http://pic1.zhimg.com/b6f59c017b43937bb85a81f9269b1ae8.jpg",
                "http://pic2.zhimg.com/a62f9985cae17fe535a99901db18eba9.jpg"};
        String[] titles = {"读读日报 24 小时热门 TOP 5 · 余文乐和「香港贾玲」乌龙绯闻",
                "写给产品 / 市场 / 运营的数据抓取黑科技教程",
                "学做这些冰冰凉凉的下酒宵夜，简单又方便",
                "知乎好问题 · 有什么冷门、小众的爱好？",
                "欧洲都这么发达了，怎么人均收入还比美国低"};
        if (bannerPicsList != null) {
            for (int i = 0; i < bannerPicsList.size(); i++) {
//            imageSlideshow.addImageTitle(imageUrls[i], titles[i]);
                imageSlideshow.addImageTitle(bannerPicsList.get(i).getTitlePicture(), bannerPicsList.get(i).getTitle());
            }
        } else {
            for (int i = 0; i < imageUrls.length; i++) {//以防获取不到数据
                imageSlideshow.addImageTitle(imageUrls[i], titles[i]);
//                imageSlideshow.addImageTitle(bannerPicsList.get(i).getTitlePicture(), bannerPicsList.get(i).getTitle());
            }
        }

        // 为ImageSlideshow设置数据        imageSlideshow.setDotSpace(12);
        imageSlideshow.setDotSize(12);
        imageSlideshow.setDelay(3000);
        imageSlideshow.setOnItemClickListener(new ImageSlideshow.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        Toast.makeText(getActivity(), "0", Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(MainActivity.this,Activity_1.class));
                        break;
                    case 1:
                        Toast.makeText(getContext(), "1", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(getContext(), "2", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(getContext(), "3", Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        Toast.makeText(getContext(), "4", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
        imageSlideshow.commit();
    }

    private void initView(View view) {
        newsBt= (Button) view.findViewById(R.id.news_bt);
        principalMailboxBt= (Button) view.findViewById(R.id.principal_mailbox_bt);
        officeAutomationBt= (Button) view.findViewById(R.id.office_automation_bt);
        principalMailboxBt.setOnClickListener(this);
        newsBt.setOnClickListener(this);
        officeAutomationBt.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        // 释放资源
        imageSlideshow.releaseResource();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.news_bt://校内新闻
                Intent intent=new Intent(getActivity(), NewsListActivity.class);
                startActivity(intent);
                break;
            case R.id.principal_mailbox_bt:
                Intent boxIntent=new Intent(getActivity(), TabLayoutTopActivity.class);
                startActivity(boxIntent);
                break;
            case R.id.office_automation_bt:
                Intent oaIntent=new Intent(getActivity(), OfficeAutomationActivity.class);
                startActivity(oaIntent);
                break;
        }

    }
}
