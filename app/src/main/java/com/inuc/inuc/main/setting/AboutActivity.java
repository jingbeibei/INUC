package com.inuc.inuc.main.setting;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.inuc.inuc.R;
import com.inuc.inuc.main.personalcenter.ModifyPasswordActivity;
import com.inuc.inuc.utils.ActivityCollector;


public class AboutActivity extends AppCompatActivity {
    private TextView versionTV;
    private TextView  BarTitle;
    private ImageView backIV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActivityCollector.addActivity(this);
        BarTitle = (TextView)findViewById(R.id.id_bar_title);
        backIV = (ImageView) findViewById(R.id.id_back_arrow_image);
        BarTitle.setText("关于");
        versionTV= (TextView) findViewById(R.id.guanyuVersionnameTv);
        PackageInfo info = null;
        try {
            info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info.versionName;//获取版本号

        versionTV.setText("爱中北 V"+version);

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(AboutActivity.this);
            }
        });
    }
}
