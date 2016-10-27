package com.inuc.inuc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.design.widget.Snackbar;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.inuc.inuc.main.MainActivity;

import com.inuc.inuc.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class LoginActivity extends AppCompatActivity {
    private EditText phoneET;
    private EditText passwordET;
    private String phoneNum;
    private String password;
    private Button loginBtn;
    private TextView registerTv;
    private TextView forgetTv;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneET = (EditText) findViewById(R.id.id_phonel_edit);
        passwordET = (EditText) findViewById(R.id.id_passwordl_edit);
        loginBtn = (Button) findViewById(R.id.id_login_btn);
        registerTv = (TextView) findViewById(R.id.id_register_tv);
        forgetTv = (TextView) findViewById(R.id.forget_password_tv);

        pref = getSharedPreferences("data", MODE_PRIVATE);
        editor = pref.edit();

        initEvent();
    }

    private void initEvent() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNum = phoneET.getText().toString();
                password = passwordET.getText().toString();
                loginBtn.setText("登陆....");
                if (phoneNum.equals("") || password.equals("")) {
                    Toast.makeText(getApplicationContext(), "手机号或密码为空", Toast.LENGTH_LONG).show();
                    loginBtn.setText("登陆");
                    
                } else {//请求网络
                    OkHttpUtils
                            .post()
                            .url(Urls.ValidateUserUrl)
                            .addParams("mobilePhone", phoneNum)
                            .addParams("password", password)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG);
                                    call.toString();
                                    Snackbar.make(loginBtn,"网络连接失败",Snackbar.LENGTH_LONG).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    if (response.equals("true")) {//登陆成功
                                        getToken();
                                        loginBtn.setText("登陆");
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
//                                        finish();
                                    } else {
                                        Snackbar.make(loginBtn,"用户名或密码错误",Snackbar.LENGTH_LONG).show();
//                                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG);
                                        loginBtn.setText("登陆");
                                    }
                                }
                            });
                }
            }
        });

        //注册事件
        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //找回密码
        forgetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater layoutInflaterFactory=  LayoutInflater.from(LoginActivity.this);
                final View dialogview=layoutInflaterFactory.inflate(R.layout.dialog_layout,null);
                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this,R.style.DIYMaterialDialog);
                builder.setTitle("找回密码");
//                builder.setMessage("请输入手机号");
                builder.setView(dialogview,20,20,20,20);

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        EditText editText= (EditText) dialogview.findViewById(R.id.dialog);
                        String s=editText.getText().toString();

                        OkHttpUtils.post().url(Urls.RecoveryPasswordUrl)
                        .addParams("mobilePhone",s).build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Snackbar.make(loginBtn, "密码发送失败", Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                    if(response.equals("true")){
                                        Snackbar.make(loginBtn, "已将密码发送至您的手机", Snackbar.LENGTH_SHORT).show();
                                    }else {
                                        Snackbar.make(loginBtn, "密码发送失败", Snackbar.LENGTH_SHORT).show();
                                    }
                            }
                        });
                    }
                });
                builder.create().show();
//                forgetTv.setTextColor(getResources().getColor(R.color.red));
//
            }
        });
    }

    private void getToken() {
        OkHttpUtils
                .post()
                .url(Urls.tokenUrl)
                .addParams("username", phoneNum)
                .addParams("password", password)
                .addParams("grant_type", "password")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            token = jsonObject.getString("access_token");
                            token="bearer "+token;
                            editor.putString("token",token);
                            editor.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }
}
