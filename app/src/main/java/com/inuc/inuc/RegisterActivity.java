package com.inuc.inuc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.inuc.inuc.main.MainActivity;
import com.inuc.inuc.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

public class RegisterActivity extends AppCompatActivity {
    private EditText phoneET;
    private EditText nicknameET;
    private EditText passwordET;
    private EditText repasswordET;
    private EditText idYanzhengmaEdit;
    private RadioGroup rGroup;
    private Button registerBT;
    private Button idGetnumBtn;
    private String phoneNum;
    private String nickname;
    private String password;
    private String repassword;
    private String sex="男";
    private String VerificationCode;
    private ImageView backIV;

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private String token;

    private TimeCount mTiemTimeCount;
    //短信验证码内容 验证码是6位数字的格式
    private String strContent;
    private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";
    //填写服务器号码
    private static final String SERVICECHECKNUM = "";

    //更新界面
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (idYanzhengmaEdit != null) {
                idYanzhengmaEdit.setText(strContent);
            }
        }

    };
    //监听短信广播
    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                byte[] pdu = (byte[]) obj;
                SmsMessage sms = SmsMessage.createFromPdu(pdu);
                // 短信的内容
                String message = sms.getMessageBody();
//                Log.i("短信",message);
                Log.d("TAG", "message     " + message);
                String from = sms.getOriginatingAddress();
//                Log.i("短信",from);
                Log.d("TAG", "from     " + from);
                if (SERVICECHECKNUM.equals(from.toString().trim()) || TextUtils.isEmpty(SERVICECHECKNUM)) {
                    Time time = new Time();
                    time.set(sms.getTimestampMillis());
                    String time2 = time.format3339(true);
                    Log.d("TAG", from + "   " + message + "  " + time2);
                    strContent = from + "   " + message;
                    //mHandler.sendEmptyMessage(1);
                    if (!TextUtils.isEmpty(from)) {
                        String code = patternCode(message);
                        if (!TextUtils.isEmpty(code)) {
                            strContent = code;
                            mHandler.sendEmptyMessage(1);
                        }
                    }
                } else {
                    return;
                }
            }

        }
    };

    /**
     * 匹配短信中间的6个数字（验证码等）
     *
     * @param patternContent
     * @return
     */
    private String patternCode(String patternContent) {
        if (TextUtils.isEmpty(patternContent)) {
            return null;
        }
        Pattern p = Pattern.compile(patternCoder);
        Matcher matcher = p.matcher(patternContent);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        editor = pref.edit();
        initView();
        initEvent();
        mTiemTimeCount = new TimeCount(60000, 1000);
    }

    private void initView() {
        phoneET = (EditText) findViewById(R.id.id_phone_edit);
        nicknameET = (EditText) findViewById(R.id.id_nickname_edit);
        passwordET = (EditText) findViewById(R.id.id_password_edit);
        repasswordET = (EditText) findViewById(R.id.id_repassword_edit);
        rGroup = (RadioGroup) findViewById(R.id.radioGroup);
        registerBT = (Button) findViewById(R.id.id_register_btn);
        backIV = (ImageView) findViewById(R.id.id_back_arrow_image);
        idYanzhengmaEdit = (EditText) findViewById(R.id.id_yanzhengma_edit);
        idGetnumBtn = (Button) findViewById(R.id.id_getvnum_btn);
    }

    private void initEvent() {
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.male:
                        sex = "男";
                        break;
                    case R.id.female:
                        sex = "女";
                        break;
                    default:
                        break;
                }
            }
        });
        registerBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNum = phoneET.getText().toString();
                nickname = nicknameET.getText().toString();
                password = passwordET.getText().toString();
                repassword = repasswordET.getText().toString();
                VerificationCode = idYanzhengmaEdit.getText().toString();
                if (phoneNum.equals("") || nickname.equals("") || password.equals("") || repassword.equals("") || VerificationCode.equals("")) {
                    Toast.makeText(RegisterActivity.this, "注册信息不完整，请重新输入", Toast.LENGTH_LONG);
                } else {
                    if (password.equals(repassword)) {
//                       注册请求
                        OkHttpUtils
                                .post()
                                .url(Urls.CreateUserUrl)
                                .addParams("mobilePhone", phoneNum)
                                .addParams("password", password)
                                .addParams("nickname", nickname)
                                .addParams("sex", sex)
                                .addParams("code", VerificationCode)
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_LONG);

                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        if (response != null || response.equals("")) {//注册成功
                                            getToken();
                                            Log.v("注册",response);
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
//                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_LONG);
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(RegisterActivity.this, "两次密码不一致，请重新输入", Toast.LENGTH_LONG);
                    }
                }
            }
        });
        //返回事件
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //发送短信验证码
        idGetnumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNum = phoneET.getText().toString();
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.provider.Telephony.SMS_RECEIVED");
                filter.setPriority(Integer.MAX_VALUE);
                registerReceiver(smsReceiver, filter);
                mTiemTimeCount.start();
                OkHttpUtils.post().url(Urls.GetVerificationCodeUrl)
                        .addParams("mobilePhone", phoneNum).build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Toast.makeText(RegisterActivity.this, "亲，网络连接失败", Toast.LENGTH_LONG);
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                if (response.equals("false")) {
                                    Toast.makeText(RegisterActivity.this, "未知错误", Toast.LENGTH_LONG);
                                }
                            }
                        });
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
                            token = "bearer " + token;
                            editor.putString("token", token);
                            editor.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }


    //计时重发
    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            idGetnumBtn.setClickable(false);
            idGetnumBtn.setText(millisUntilFinished / 1000 + "秒后重新发送");
            Spannable span = new SpannableString(idGetnumBtn.getText().toString());//获取按钮的文字
            span.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//讲倒计时时间显示为红色
            idGetnumBtn.setText(span);
        }

        @Override
        public void onFinish() {
            idGetnumBtn.setText("获取验证码");
            idGetnumBtn.setClickable(true);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(smsReceiver);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onDestroy();
    }


}
