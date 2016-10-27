package com.inuc.inuc.zone.imagebrowse.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;


import com.inuc.inuc.R;
import com.inuc.inuc.zone.imagebrowse.view.adapter.ViewPageAdapter;
import com.inuc.inuc.zone.imagebrowse.view.presenter.ImageBrowsePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jelly on 2016/9/3.
 */
public class ImageBrowseActivity extends Activity implements ViewPager.OnPageChangeListener,View.OnClickListener,ImageBrowseView{

    private ViewPager vp;
    private TextView hint;
    private TextView save;
    private ViewPageAdapter adapter;
    private ImageBrowsePresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_image_browse);
        vp = (ViewPager) this.findViewById(R.id.viewPager);
        hint = (TextView) this.findViewById(R.id.hint);
        save = (TextView) this.findViewById(R.id.save);
        save.setOnClickListener(this);
        initPresenter();
        presenter.loadImage();
    }

    public void initPresenter(){
        presenter = new ImageBrowsePresenter(this);
    }

    @Override
    public Intent getDataIntent() {
        return getIntent();
    }

    @Override
    public Context getMyContext() {
        return this;
    }

    @Override
    public void setImageBrowse(List<String> images, int position) {
        if(adapter == null && images != null && images.size() != 0){
            adapter = new ViewPageAdapter(this,images);
            vp.setAdapter(adapter);
            vp.setCurrentItem(position);
            vp.addOnPageChangeListener(this);
            hint.setText(position + 1 + "/" + images.size());
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        presenter.setPosition(position);
        hint.setText(position + 1 + "/" + presenter.getImages().size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        presenter.saveImage();
    }


    public static void startActivity(Context context, ArrayList<String> images, int position){
        Intent intent = new Intent(context,ImageBrowseActivity.class);
        intent.putStringArrayListExtra("images",images);
        intent.putExtra("position",position);
        context.startActivity(intent);
    }


}
//<p><img src=\"http://www.i-nuc.com/UploadFiles/Images/32a5442e-cb6f-484f-857d-aac188a16f2b.jpg\" style=\"max-width:100%; width=100%\"/></p>" +
//        "<p><img src=\"http://www.i-nuc.com/UploadFiles/Images/3e895f86-3b8f-45e1-91f4-7f315b589a4a.jpg\" style=\"max-width:100%; width=100%\"/></p>" +
//        "<p><img src=\"http://www.i-nuc.com/UploadFiles/Images/0fcf8f5f-7e6e-41ba-b9f8-22ca33cfab1e.jpg\" style=\"max-width:100%; width=100%\"/></p>" +
//        "<p><img src=\"http://www.i-nuc.com/UploadFiles/Images/db06808c-edd2-465e-a236-5617ab1a485b.jpg\" style=\"max-width:100%; width=100%\"/></p>" +
//        "<p><img src=\"http://www.i-nuc.com/UploadFiles/Images/6640f798-57a4-4605-b20e-e81d4a9045f8.jpg\" style=\"max-width:100%; width=100%\"/></p>" +
//        "<p><img src=\"http://www.i-nuc.com/UploadFiles/Images/bc407003-1915-4966-b057-8146b3b7b609.jpg\" style=\"max-width:100%; width=100%\"/></p>" +
//        "<p><img src=\"http://www.i-nuc.com/UploadFiles/Images/fe3b0cc8-51a0-40a5-9a53-bcc2d5a42903.jpg\" style=\"max-width:100%; width=100%\"/></p>" +
//        "<p><img src=\"http://www.i-nuc.com/UploadFiles/Images/df857390-7109-4ae6-a559-57cd8606313f.jpg\" style=\"max-width:100%; width=100%\"/></p>" +
//        "<p><img src=\"http://www.i-nuc.com/UploadFiles/Images/d5701245-deb6-4f97-85d1-d43cae8e634a.jpg\" style=\"max-width:100%; width=100%\"/></p>哈哈哈"