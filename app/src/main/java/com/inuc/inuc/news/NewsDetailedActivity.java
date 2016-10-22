package com.inuc.inuc.news;

import android.content.Intent;
import android.os.Bundle;
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
    // 获取src路径的正则
    private static final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";

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
        BarRight = (TextView) findViewById(R.id.bar_right_tv);
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
                Intent intent = new Intent(NewsDetailedActivity.this, PublishNewsActivity.class);
                startActivity(intent);
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
        getImageUrl(contentString);
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

}
