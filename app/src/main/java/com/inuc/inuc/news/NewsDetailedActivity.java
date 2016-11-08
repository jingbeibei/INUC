package com.inuc.inuc.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.inuc.inuc.R;
import com.inuc.inuc.beans.News;
import com.inuc.inuc.utils.ActivityCollector;
import com.inuc.inuc.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.Call;


public class NewsDetailedActivity extends AppCompatActivity {
    private String newsId;
    private TextView title;
    private WebView content;
    private String contentString;
    String head = "<html><head><style>img{width:100%;}</style></head><body>";
    String end = "</body></html>";
    private TextView BarTitle;
    private ImageView BackImage;
    private FloatingActionButton BarRight;
    private ImageView shareImageview;
    // 获取src路径的正则
    private static final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detailed);
        ActivityCollector.addActivity(this);
        ShareSDK.initSDK(this,"185a827012b40");
        newsId = getIntent().getStringExtra("id");

        title = (TextView) findViewById(R.id.news_title_tv);
        content = (WebView) findViewById(R.id.news_content_web);
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        BarRight = (FloatingActionButton) findViewById(R.id.news_release_fab_button);
        shareImageview= (ImageView) findViewById(R.id.bar_right_iv);

        OkHttpUtils.get().url(Urls.GetInfomationUrl)
                .addParams("id", newsId).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        News news = new Gson().fromJson(response, News.class);
                        title.setText(news.getTitle());
                        contentString = news.getContents();
                        initWeb();
                    }
                });


        BarTitle.setText("校园新闻");

        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(NewsDetailedActivity.this);
            }
        });
        BarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsDetailedActivity.this, PublishNewsActivity.class);
                startActivity(intent);
            }
        });
        shareImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
    }

    private void initWeb() {

        WebSettings settings = content.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setDisplayZoomControls(false);
        if (contentString != null) {
            contentString = contentString.replace(";", "");
        }
        contentString = head + contentString + end;
        content.loadDataWithBaseURL(null, contentString, "text/html", "utf-8", null);
//        getImageUrl(contentString);
    }

    private List<String> getImageUrl(String HTML) {
        Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(HTML);
        List<String> listImgUrl = new ArrayList<String>();
        String s = "";
        while (matcher.find()) {
            Log.i("图片路径", matcher.group().substring(0,matcher.group().length()-1));
            s = s + matcher.group();
            listImgUrl.add(matcher.group());
        }
        Log.i("图片路径", s);
        return listImgUrl;
    }
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle(title.getText().toString());
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://www.i-nuc.com/iNUC/News/Home/Details/"+newsId);
        // text是分享文本，所有平台都需要这个字段
        oks.setText("爱中北："+title.getText().toString()+"http://www.i-nuc.com/iNUC/News/Home/Details/"+newsId);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("http://img.daimg.com/uploads/allimg/161023/3-1610231PZ4.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.i-nuc.com/iNUC/News/Home/Details/"+newsId);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("评论无");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.i-nuc.com/iNUC/News/Home/Details/"+newsId);
        oks.setImageUrl("https://raw.githubusercontent.com/jingbeibei/JS/master/banner_error.png");

// 启动分享GUI
        oks.show(this);
    }

}
