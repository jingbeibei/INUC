package com.inuc.inuc.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
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
    private TextView BarRight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detailed);
        ActivityCollector.addActivity(this);
        newsId = getIntent().getStringExtra("id");

        title = (TextView) findViewById(R.id.news_title_tv);
        content = (WebView) findViewById(R.id.news_content_web);
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        BarRight= (TextView) findViewById(R.id.bar_right_tv);
        BarRight.setText("发布");
        BarRight.setVisibility(View.VISIBLE);
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
                Intent intent=new Intent(NewsDetailedActivity.this,PublishNewsActivity.class);
                startActivity(intent);
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
//        //	contentWebView.getSettings().setLoadsImagesAutomatically(true);
//        content.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
//        //		contentWebView.getSettings().setJavaScriptEnabled(true);
//        content.getSettings().setDefaultTextEncodingName("utf-8");
//        content.getSettings().setDefaultFontSize(18);
//        content.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        WebSettings settings = content.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        if (contentString != null) {
            contentString = contentString.replace(";", "");
        }
        contentString = head + contentString + end;
        content.loadDataWithBaseURL(null, contentString, "text/html", "utf-8", null);

    }
}
