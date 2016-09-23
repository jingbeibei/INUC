package com.inuc.inuc.main.personalcenter;

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
import com.inuc.inuc.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;


public class ModifyPasswordActivity extends AppCompatActivity {
    private EditText oldPasswordET;
    private EditText newPasswordET;
    private EditText confirmPasswordET;
    private TextView barTitle;
    private TextView barRight;
    private ImageView backIV;

    private SharedPreferences pref;
    private String token;

    private Button modifyPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        ActivityCollector.addActivity(this);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        token = pref.getString("token", "");
        initView();
        initEvent();
    }

    private void initView() {
        oldPasswordET = (EditText) findViewById(R.id.password_old_et);
        newPasswordET = (EditText) findViewById(R.id.password_new_et);
        confirmPasswordET = (EditText) findViewById(R.id.password_confirm_et);
        modifyPasswordBtn = (Button) findViewById(R.id.modify_password_btn);
        barTitle = (TextView) findViewById(R.id.id_bar_title);
        barRight = (TextView) findViewById(R.id.bar_right_tv);
        backIV = (ImageView) findViewById(R.id.id_back_arrow_image);
        barTitle.setText("修改密码");
        barRight.setText("");
    }

    private void initEvent() {
        modifyPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassworld = oldPasswordET.getText().toString();
                String newPassword = newPasswordET.getText().toString();
                String confirmPassword = confirmPasswordET.getText().toString();
                if (oldPassworld.equals("") || newPassword.equals("") || confirmPassword.equals("")) {
                    Snackbar.make(confirmPasswordET, "亲，密码不能为空哦！", Snackbar.LENGTH_LONG);
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    Snackbar.make(confirmPasswordET, "亲，密码不一致哦！", Snackbar.LENGTH_LONG);
                    return;
                }
                OkHttpUtils.post().url(Urls.ChangePasswordUrl)
                        .addHeader("Authorization", token)
                        .addParams("oldPassword", oldPassworld)
                        .addParams("newPassword", newPassword).build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Snackbar.make(newPasswordET, "网络连接失败", Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                if (response.equals("true") ) {
                                    Toast.makeText(ModifyPasswordActivity.this, "亲，密码修改成功，要记住哦！", Toast.LENGTH_SHORT).show();

                                    finish();
                                } else {
                                    Snackbar.make(newPasswordET, "修改密码失败", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(ModifyPasswordActivity.this);
            }
        });
    }

}
