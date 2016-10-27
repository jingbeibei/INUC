package com.inuc.inuc.news;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.inuc.inuc.R;
import com.inuc.inuc.utils.ActivityCollector;
import com.inuc.inuc.utils.DialogUtils;

import com.inuc.inuc.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import okhttp3.Call;

public class PublishNewsActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE = 2;
    private ArrayList<String> mSelectPath;
    private MultiImageAdapter adapter;
    private GridView gridView;
    private EditText titleET;
    private EditText contentET;
    //    private String picCode = "";
    private TextView newsPublishBT;
    private TextView BarTitle;
    private ImageView BackImage;
    private SharedPreferences pref;
    private String token;
    private final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0;
    private Dialog progressDialog;
    private ImageView showImageIV;


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
        newsPublishBT = (TextView) findViewById(R.id.bar_right_tv);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        showImageIV = (ImageView) findViewById(R.id.show_image);

        BarTitle.setText("发布新闻");
        newsPublishBT.setVisibility(View.VISIBLE);
        newsPublishBT.setText("提交");
        progressDialog = DialogUtils.createProgressDialog(this);
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
                final String title = titleET.getText().toString();
                final String content = contentET.getText().toString();
                if (title.equals("") || content.equals("")) {
                    Toast.makeText(getApplicationContext(), "亲，标题和内容不能为空！", Toast.LENGTH_LONG).show();
                    return;
                }
                progressDialog.show();
                newsPublishBT.setText("提交...");
                newsPublishBT.setClickable(false);
                if (mSelectPath != null) {
//                    Bitmap bitmap = null;
//                    for (int i = 0; i < mSelectPath.size(); i++) {
//                        bitmap = BitmapFactory.decodeFile(mSelectPath.get(i));
////                             bitmap = Glide.with(PublishNewsActivity.this).load(mSelectPath.get(i)).asBitmap().centerCrop().into(500, 500).get();
//                        String base64Image = ToolBase64.bitmapToBase64(bitmap);
//                        picCode = picCode + base64Image + ",";
//                        bitmap.recycle();
//                    }
//
//                    picCode = picCode.substring(0, picCode.length() - 1);
                    final List<String> filelist = new ArrayList<String>();
                    for (int i = 0; i < mSelectPath.size(); i++) {
                        File file = new File(mSelectPath.get(i));
                        OkHttpUtils.post().url(Urls.UploadImageUrl).addFile(file.getName(), file.getName(), file).build().execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Log.i("错误url", call.toString());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                filelist.add(response);
                                if (filelist.size() == mSelectPath.size()) {
                                    String image = "";
                                    for (String imageurl : filelist) {
                                        imageurl = imageurl.substring(2, imageurl.length());
                                        image += imageurl;
                                    }
                                    submitPost(title, content + image);

                                }

                            }
                        });
//
                    }
                } else {
                    submitPost(title, content);
                }


            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showImageIV.setVisibility(View.VISIBLE);
                Glide.with(PublishNewsActivity.this)
                        .load(mSelectPath.get(position))
                        .into(showImageIV);
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

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
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

    private void submitPost(String title, String content) {
        OkHttpUtils.post().url(Urls.PublishNewsUrl).addHeader("Authorization", token)
                .addParams("title", title)
                .addParams("contents", content)
                .addParams("picCode", "").build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(getApplicationContext(), "未知错误，请联系管理员", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        newsPublishBT.setText("提交");
                        newsPublishBT.setClickable(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (Integer.parseInt(response) > 0) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "亲，发布成功，请等待审核！", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            progressDialog.dismiss();
                            newsPublishBT.setText("提交");
                            newsPublishBT.setClickable(true);
                            Toast.makeText(getApplicationContext(), "发布失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
