package com.inuc.inuc.meetings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.inuc.inuc.R;

import java.util.Calendar;
import java.util.Date;

public class OAMeetingsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oameetings_list);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        Log.d("第几周",c.get(Calendar.WEEK_OF_YEAR)+"");
        Log.d("哪一年",c.get(Calendar.YEAR)+"");
        Log.d("哪一年",getWeekNumByYear(2016)+"");

        // 初始化控件
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // 建立数据源
        String[] mItems = new String[10];
//        = getResources().getStringArray(R.array.languages);
        for(int i=0;i<10;i++){
            mItems[i]=i+"";
        }
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       //绑定 Adapter到控件
        spinner .setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

//                String[] languages = getResources().getStringArray(R.array.languages);
//                Toast.makeText(MainActivity.this, "你点击的是:"+languages[pos], 2000).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

    }
    public  int getWeekNumByYear(final int year){
        if(year<1900 || year >9999){
            throw new NullPointerException("年度必须大于等于1900年小于等于9999年");
        }
        int result = 52;//每年至少有52个周 ，最多有53个周。
        String date = new Date().toString();
        if(date.substring(0, 4).equals(year+"")){ //判断年度是否相符，如果相符说明有53个周。
            result = 53;
        }
        return result;
    }
}
