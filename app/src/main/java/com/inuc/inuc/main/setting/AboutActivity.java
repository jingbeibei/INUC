package com.inuc.inuc.main.setting;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.inuc.inuc.R;
import com.inuc.inuc.utils.ActivityCollector;


public class AboutActivity extends AppCompatActivity {
    private TextView versionTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActivityCollector.addActivity(this);
        versionTV= (TextView) findViewById(R.id.guanyuVersionnameTv);
        PackageInfo info = null;
        try {
            info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version = info.versionCode;//获取版本号
        versionTV.setText("爱中北"+version);
    }
}
