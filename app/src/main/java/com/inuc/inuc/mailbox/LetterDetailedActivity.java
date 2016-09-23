package com.inuc.inuc.mailbox;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.inuc.inuc.R;
import com.inuc.inuc.beans.Letter;
import com.inuc.inuc.utils.ActivityCollector;
import com.inuc.inuc.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Response;


public class LetterDetailedActivity extends AppCompatActivity {
    private TextView BarTitle;
    private ImageView BackImage;
    private Letter letter;
    private TextView letterTitleTV, letterTimeTV, letterContentTV, answerTV, answerTimeTV;
    private String letterID;
    private SharedPreferences pref;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_detailed);
        ActivityCollector.addActivity(this);
        letterID = getIntent().getStringExtra("letterID");
        pref = getSharedPreferences("data", MODE_PRIVATE);
        token=pref.getString("token","");

        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BarTitle.setText("校长信箱");
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(LetterDetailedActivity.this);
            }
        });
        initView();

        OkHttpUtils.get().url(Urls.GetLetterUrl).addHeader("Authorization",token).addParams("letterID", letterID).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        letter = new Gson().fromJson(response, Letter.class);
                        if(letter!=null){
                            letterTitleTV.setText(letter.getTitle());
                            letterTimeTV.setText(letter.getSubmittedTime().substring(0, 10));
                            letterContentTV.setText(letter.getContents());
                            String answer = letter.getRepliedContents();
                            String answertime = letter.getRepliedTime();
                            if (answertime != null) {

                                answerTV.setText(answer);
                                answerTimeTV.setText(answertime.substring(0, 10));
                            }
                        }
                    }
                });



    }

    private void initView() {
        letterTitleTV = (TextView) findViewById(R.id.letter_title_tv);
        letterContentTV = (TextView) findViewById(R.id.letter_contents_tv);
        letterTimeTV = (TextView) findViewById(R.id.letter_entrytime_tv);
        answerTV = (TextView) findViewById(R.id.letter_answer_tv);
        answerTimeTV = (TextView) findViewById(R.id.letter_answertime_tv);


    }
}
