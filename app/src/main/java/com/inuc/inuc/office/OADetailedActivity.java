package com.inuc.inuc.office;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.inuc.inuc.R;
import com.inuc.inuc.beans.OANews;
import com.inuc.inuc.utils.ActivityCollector;
import com.inuc.inuc.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.Call;


public class OADetailedActivity extends AppCompatActivity {
    private String newsId;
    private TextView title;
    private WebView content;
    private String contentString;
    String head = "<html><head><style>img{width:100%;}</style></head><body>";
    String end = "</body></html>";
    private TextView BarTitle;
    private ImageView BackImage;
    private SharedPreferences pref;
    private String token;
    private ImageView shareImageview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_detailed);
        ActivityCollector.addActivity(this);
        ShareSDK.initSDK(this,"185a827012b40");
        newsId = getIntent().getStringExtra("ID");
        pref = getSharedPreferences("data", MODE_PRIVATE);
        token=pref.getString("token","");

        title = (TextView) findViewById(R.id.news_title_tv);
        content = (WebView) findViewById(R.id.news_content_web);
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        shareImageview= (ImageView) findViewById(R.id.bar_right_iv);

        OkHttpUtils.get().url(Urls.GetOANewsDetailsUrl).addHeader("Authorization", token)
                .addParams("id", newsId).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                       OANews news = new Gson().fromJson(response, OANews.class);
                        title.setText(news.getTitle());
                        contentString = news.getContents();
                        initWeb();
                    }
                });



        BarTitle.setText("电子政务");

        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(OADetailedActivity.this);
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
//        WebChromeClient m_chromeClient = new WebChromeClient() {
//            @Override
//            public void onShowCustomView(View view, CustomViewCallback callback) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                // TODO Auto-generated method stub
//                super.onProgressChanged(view, newProgress);
//            }
//        };
//
//        //视频设置
//        content.setWebChromeClient(m_chromeClient);
//
//        //	contentWebView.getSettings().setLoadsImagesAutomatically(true);
//        content.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
//        //		contentWebView.getSettings().setJavaScriptEnabled(true);
//        content.getSettings().setDefaultTextEncodingName("utf-8");
//        content.getSettings().setDefaultFontSize(18);
//        content.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

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

    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle(title.getText().toString());
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://www.i-nuc.com/inuc");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("爱中北："+title.getText().toString()+"http://www.i-nuc.com/inuc");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("http://img.daimg.com/uploads/allimg/161023/3-1610231PZ4.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.i-nuc.com/inuc");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("评论无");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.i-nuc.com/inuc");
        oks.setImageUrl("https://raw.githubusercontent.com/jingbeibei/JS/master/banner_error.png");

// 启动分享GUI
        oks.show(this);
    }
}
