package com.inuc.inuc.mailbox;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.inuc.inuc.R;
import com.inuc.inuc.utils.ActivityCollector;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

public class WriteLetterActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private TextView BarTitle;
    private ImageView BackImage;
    private EditText LetterReleaseTitle;
    private EditText LetterReleaseContent;
    private Button LetterReleaseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_letter);
        ActivityCollector.addActivity(this);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        LetterReleaseTitle = (EditText) findViewById(R.id.letter_release_title_et);
        LetterReleaseContent = (EditText) findViewById(R.id.letter_releasw_content_et);
        LetterReleaseBtn = (Button) findViewById(R.id.letter_release_button);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BarTitle.setText("提交");
        initEvent();
    }

    private void initEvent() {
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(WriteLetterActivity.this);
            }
        });
        LetterReleaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = LetterReleaseTitle.getText().toString();
                String content = LetterReleaseContent.getText().toString();
                if (title.equals("") || content.equals("")) {
                    Toast.makeText(getApplicationContext(), "亲，标题和内容不能为空！", Toast.LENGTH_LONG).show();
                    return;
                }

//                String url = Urls.PostLetterURL + "times=" + times + "&code=" + code + "&applicationID=" + applicationid;
//                List<OkHttpUtils.Param> params = new ArrayList<OkHttpUtils.Param>();
//                OkHttpUtils.Param param1 = new OkHttpUtils.Param("title", title);
//                OkHttpUtils.Param param2 = new OkHttpUtils.Param("username", username);
//                OkHttpUtils.Param param3 = new OkHttpUtils.Param("contents", content);
//                OkHttpUtils.Param param4 = new OkHttpUtils.Param("imei", "1");
//                params.add(param1);
//                params.add(param2);
//                params.add(param3);
//                params.add(param4);
//
//                //提交谈话
//
//                OkHttpUtils.ResultCallback<String> postLetterCallback = new OkHttpUtils.ResultCallback<String>() {
//                    @Override
//                    public void onSuccess(String response) {
//                        int flag = Integer.parseInt(response);
//                        if (flag > 0) {
//                            Toast.makeText(WriteLetterActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
//                            LetterReleaseTitle.setText("");
//                            LetterReleaseContent.setText("");
//                            finish();
//                        } else {
//                            Snackbar.make(LetterReleaseContent, "提交失败", Snackbar.LENGTH_SHORT).show();
//                            LetterReleaseBtn.setEnabled(true);
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Exception e) {
//                        String error = "";
//                        if (e.toString().contains("java.net.ConnectException")) {
//                            error = "网络连接失败";
//                        } else {
//                            error = "未知错误" + e.toString();
//                        }
//                        initFailure(error);
//                    }
//                };
//
//                OkHttpUtils.post(url, postLetterCallback, params);

            }
        });
    }

    public void initFailure(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
