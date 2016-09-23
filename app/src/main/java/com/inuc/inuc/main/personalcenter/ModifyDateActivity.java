package com.inuc.inuc.main.personalcenter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.inuc.inuc.R;
import com.inuc.inuc.beans.Personnel;
import com.inuc.inuc.utils.ActivityCollector;
import com.inuc.inuc.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ModifyDateActivity extends AppCompatActivity {
    private EditText nicknameET;
    private EditText nameET;
    private EditText studentNoET;
    private EditText sexET;
    private Button modifyBtn;
    private TextView barTitle;
    private TextView barRight;
    private ImageView backIV;

    private Personnel personnel;
    private SharedPreferences pref;
    private String token;

    private String myUpdatePersonnelUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_date);
        ActivityCollector.addActivity(this);
        personnel = (Personnel) getIntent().getSerializableExtra("personnel");
        pref = getSharedPreferences("data", MODE_PRIVATE);
        token = pref.getString("token", "");
        initView();
        initEvent();
    }

    private void initView() {
        nicknameET = (EditText) findViewById(R.id.date_nickname_et);
        nameET = (EditText) findViewById(R.id.data_name_et);
        studentNoET = (EditText) findViewById(R.id.data_studentNo_et);
        sexET= (EditText) findViewById(R.id.data_sex_et);
        modifyBtn = (Button) findViewById(R.id.modify_data_btn);
        barTitle = (TextView) findViewById(R.id.id_bar_title);
        barRight = (TextView) findViewById(R.id.bar_right_tv);
        backIV = (ImageView) findViewById(R.id.id_back_arrow_image);
        nicknameET.setText(personnel.getNickname());
        nameET.setText(personnel.getName());
        studentNoET.setText(personnel.getStudentNo());
        sexET.setText(personnel.getSex());
        barTitle.setText("修改资料");
        barRight.setText("");
    }

    private void initEvent() {
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameET.getText().toString();
                String nickname = nicknameET.getText().toString();
                String studentNo = studentNoET.getText().toString();
                String sex=sexET.getText().toString();
                modifyBtn.setText("修改中...");
                modifyBtn.setEnabled(false);
                if (nickname.equals("")) {
                    Snackbar.make(nicknameET, "昵称和姓名不能为空！", Snackbar.LENGTH_SHORT).show();
                    modifyBtn.setText("修改");
                    modifyBtn.setEnabled(true);
                    return;
                }
                if (!sex.equals("男")&&!sex.equals("女")){
                    Snackbar.make(nicknameET, "亲，性别格式不对，请重新输入！", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Log.i("token",token);
                OkHttpUtils.post().url(Urls.UpdateUserUrl).addHeader("Authorization", token)
                        .addParams("mobilePhone", "")
                        .addParams("name", name)
                        .addParams("studentNo", studentNo)
                        .addParams("nickname", nickname)
                        .addParams("sex",sex)
                        .addParams("picCode", "").build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Snackbar.make(nameET, "网络连接失败", Snackbar.LENGTH_SHORT).show();
                                modifyBtn.setText("修改");
                                modifyBtn.setEnabled(true);
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                if (Integer.parseInt(response) > 0) {
                                    Toast.makeText(ModifyDateActivity.this, "亲，修改个人信息成功哦！", Toast.LENGTH_SHORT).show();
                                    nicknameET.setText("");
                                    nameET.setText("");
                                    finish();
                                } else {
                                    Snackbar.make(nameET, response, Snackbar.LENGTH_SHORT).show();
                                    modifyBtn.setText("修改");
                                    modifyBtn.setEnabled(true);
                                }
                            }
                        });


            }
        });
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(ModifyDateActivity.this);

            }
        });
    }


}
