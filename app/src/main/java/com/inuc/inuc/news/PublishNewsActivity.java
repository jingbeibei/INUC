package com.inuc.inuc.news;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.inuc.inuc.R;
import com.inuc.inuc.utils.ActivityCollector;
import com.inuc.inuc.utils.ToolBase64;
import com.inuc.inuc.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;


import me.nereo.multi_image_selector.MultiImageSelector;
import okhttp3.Call;

public class PublishNewsActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE = 2;
    private ArrayList<String> mSelectPath;
    private MultiImageAdapter adapter;
    private GridView gridView;
    private EditText titleET;
    private EditText contentET;
    private String picCode = "";
    private Button newsPublishBT;
    private TextView BarTitle;
    private ImageView BackImage;
    private SharedPreferences pref;
    private String token;
    private final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_news);
        ActivityCollector.addActivity(this);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        token = pref.getString("token", "");

        gridView = (GridView) findViewById(R.id.gridView);
        titleET = (EditText) findViewById(R.id.news_release_title_et);
        contentET = (EditText) findViewById(R.id.news_releasw_content_et);
        newsPublishBT = (Button) findViewById(R.id.news_release_button);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BarTitle.setText("发布新闻");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
        adapter = new MultiImageAdapter(this);
        gridView.setAdapter(adapter);

        initEvent();
    }

    private void initEvent() {
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(PublishNewsActivity.this);
            }
        });

        newsPublishBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleET.getText().toString();
                String content = contentET.getText().toString();
                if (title.equals("") || content.equals("")) {
                    Toast.makeText(getApplicationContext(), "亲，标题和内容不能为空！", Toast.LENGTH_LONG).show();
                    return;
                }
                if (mSelectPath != null) {
                    Bitmap bitmap = null;
                    for (int i = 0; i < mSelectPath.size(); i++) {

                        bitmap = BitmapFactory.decodeFile(mSelectPath.get(i));
//                             bitmap = Glide.with(PublishNewsActivity.this).load(mSelectPath.get(i)).asBitmap().centerCrop().into(500, 500).get();
                        String base64Image = ToolBase64.bitmapToBase64(bitmap);
                        picCode = picCode + base64Image + ",";
                        bitmap.recycle();
                    }
                    Log.i("图片1", picCode);
                    picCode = picCode.substring(0, picCode.length() - 1);
                    Log.i("图片2", picCode);

                }
                newsPublishBT.setText("提交...");
                newsPublishBT.setClickable(false);
                OkHttpUtils.post().url(Urls.PublishNewsUrl).addHeader("Authorization", token)
                        .addParams("title", title)
                        .addParams("contents", content)
                        .addParams("picCode", picCode).build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(getApplicationContext(), "未知错误，请联系管理员", Toast.LENGTH_LONG).show();
                                newsPublishBT.setText("提交");
                                newsPublishBT.setClickable(true);
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                if (Integer.parseInt(response) > 0) {
                                    Toast.makeText(getApplicationContext(), "亲，发布成功，请等待审核！", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    newsPublishBT.setText("提交");
                                    newsPublishBT.setClickable(true);
                                    Toast.makeText(getApplicationContext(), "发布失败", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == this.RESULT_OK) {
                if (mSelectPath != null) {
                    mSelectPath.clear();
                }
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                StringBuilder sb = new StringBuilder();
//                for (String p : mSelectPath) {
//                    sb.append(p);
//                    //sb.append("\n");
//                }
                //拿到图片数据后把images传过去
                adapter = new MultiImageAdapter(this, mSelectPath);
                gridView.setAdapter(adapter);
//                Bitmap bitmap = BitmapFactory.decodeFile(sb.toString());
//                headImage.setImageBitmap(bitmap);
//                String base64Image = ToolBase64.bitmapToBase64(bitmap);
//
//                updateHeadImage(base64Image);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode,grantResults);
    }
    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
            } else {
                // Permission Denied
            }
        }
    }
}
