package com.inuc.inuc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.inuc.inuc.beans.FlashPicture;
import com.inuc.inuc.main.MainActivity;

import com.inuc.inuc.utils.Urls;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class WelcomeActivity extends AppCompatActivity {
    private ImageView flashIV;
    private int version;
    private int state;//是否为第一次使用
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private String token;
    private String imei = "";
    private SimpleDateFormat sdf;
    private String times;
    private FlashPicture flashPicture;
    private Thread mSplashThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);

        flashIV = (ImageView) findViewById(R.id.flashIV);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        editor = pref.edit();
        state = pref.getInt("state", 0);
        token = pref.getString("token", "");

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();//获取手机唯一标识
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = info.versionCode;//获取版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);// 获取代表联网状态的NetWorkInfo对象
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();  // 获取当前的网络连接是否可用
        if (state == 0) {
            editor.putInt("state", 1);
        }
        if (networkInfo != null) {
            sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            times = sdf.format(new Date());
            OkHttpUtils.post().url(Urls.AppLoginUrl).addParams("state", state + "")//打开APP时注册
                    .addParams("imei",imei)
                    .addParams("applicationTime", times)
                    .addParams("username", "")
                    .addParams("versionID", version + "").build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.i("e", e.toString());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.i("打开app", response);
                        }
                    });
        }
        state = 1;
        //获取闪图
        if (state != 0 && networkInfo != null) {

            OkHttpUtils.get().url(Urls.GetLastFlashPictureUrl)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.i("e", e.toString());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            flashPicture = new Gson().fromJson(response, FlashPicture.class);
                            if (flashPicture != null) {
                                try {
                                    Date startTime = sdf.parse(flashPicture.getStartTime());
                                    Date endTime = sdf.parse(flashPicture.getEndTime());
                                    int flag1 = sdf.parse(times).compareTo(startTime);
                                    int flag2 = endTime.compareTo(sdf.parse(times));
                                    if (flag1 >= 0 && flag2 >= 0) {
                                        Picasso.with(WelcomeActivity.this)
                                                .load(flashPicture.getPageUrl())
                                                .placeholder(R.mipmap.newestflashpic)
                                                .error(R.mipmap.newestflashpic)
                                                .into(flashIV);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            Log.i("打开app", response);
                        }
                    });

        }
        // 启动闪屏界面的线程
        mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        // 闪屏的停留时间
                        wait(3000);
                    }
                } catch (InterruptedException ex) {
                }

                finish();

                editor.commit();
                //启动下一个Activity
                if (token.equals("")) {
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));

                } else {
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                }

                try {
                    stop();
                } catch (Exception e) {
                    e.printStackTrace();
                    // System.out.println(e);
                }
            }
        };

        mSplashThread.start();


    }

}
