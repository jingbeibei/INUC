package com.inuc.inuc.main.personalcenter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.inuc.inuc.R;
import com.inuc.inuc.beans.Personnel;
import com.inuc.inuc.utils.ActivityCollector;
import com.inuc.inuc.utils.ToolBase64;
import com.inuc.inuc.utils.Urls;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import okhttp3.Call;


/**
 * Created by 景贝贝 on 2016/9/6.
 */
public class PersonalCenterFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences pref;
    private String token;


    private SharedPreferences.Editor editor;

    private Personnel personnel;
    private ImageView headImage;
    private TextView nicknameTV;
    private RelativeLayout modifyData;
    private RelativeLayout  modifyPassword;
    private Button exitBtn;

    private static final int REQUEST_IMAGE = 2;
    private ArrayList<String> mSelectPath;


    private TextView BarTitle;
    private ImageView BackImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        personnel = (Personnel) getArguments().getSerializable("personnel");
        pref = getActivity().getSharedPreferences("data", getContext().MODE_PRIVATE);
        token = pref.getString("token", "");
        editor = pref.edit();
        getPersonnel();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_center, container, false);
        headImage = (ImageView) view.findViewById(R.id.setting_head_image);
        nicknameTV = (TextView) view.findViewById(R.id.setting_nickname);
        modifyData = (RelativeLayout ) view.findViewById(R.id.modify_data_layout);
        modifyPassword = (RelativeLayout ) view.findViewById(R.id.modify_password_layout);
        BackImage = (ImageView) view.findViewById(R.id.id_back_arrow_image);
        BarTitle = (TextView) view.findViewById(R.id.id_bar_title);
        BackImage.setVisibility(View.INVISIBLE);
        BarTitle.setText("个人中心");
        exitBtn = (Button) view.findViewById(R.id.id_exit_btn);
        if (personnel != null) {
            Picasso.with(getContext()).load(personnel.getPictureUrl()).placeholder(R.mipmap.protrait).error(R.mipmap.protrait).into(headImage);
            nicknameTV.setText(personnel.getNickname());
        }

        initEvent();
        return view;
    }

    private void initEvent() {
        headImage.setOnClickListener(this);
        modifyData.setOnClickListener(this);
        modifyPassword.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_head_image:
                MultiImageSelector.create(getActivity())
                        .showCamera(true) // 是否显示相机. 默认为显示
                        .count(1) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
                        .single() // 单选模式
                        // .multi() // 多选模式, 默认模式;
                        .origin(mSelectPath) // 默认已选择图片. 只有在选择模式为多选时有效
                        .start(PersonalCenterFragment.this, REQUEST_IMAGE);
                break;

            case R.id.modify_data_layout:
                Intent dataIntent = new Intent(getActivity(), ModifyDateActivity.class);
                dataIntent.putExtra("personnel", personnel);
                startActivity(dataIntent);
                break;
            case R.id.modify_password_layout:
                Intent passwordIntent = new Intent(getActivity(), ModifyPasswordActivity.class);
                startActivity(passwordIntent);
                break;
            case R.id.id_exit_btn:
                editor.clear();
                editor.commit();
                ActivityCollector.finishAll();
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                StringBuilder sb = new StringBuilder();
                for (String p : mSelectPath) {
                    sb.append(p);
                    //sb.append("\n");
                }
                Bitmap bitmap = BitmapFactory.decodeFile(sb.toString());
                headImage.setImageBitmap(bitmap);
                String base64Image = ToolBase64.bitmapToBase64(bitmap);

                updateHeadImage(base64Image);
            }
        }
    }

    private void getPersonnel() {//获取用户信息
        OkHttpUtils.post().url(Urls.GetPersonnelUrl)
                .addHeader("Authorization", token).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        personnel = new Gson().fromJson(response, Personnel.class);
                        Picasso.with(getContext()).load(personnel.getPictureUrl()).placeholder(R.mipmap.protrait).error(R.mipmap.protrait).into(headImage);
                        nicknameTV.setText(personnel.getNickname());
                    }
                });


    }

    private void updateHeadImage(String base64Image) {
        OkHttpUtils.post().url(Urls.UpdateUserUrl).addHeader("Authorization", token)
                .addParams("name", "")
                .addParams("studentNo", "")
                .addParams("nickname", "")
                .addParams("sex","")
                .addParams("picCode", base64Image).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (Integer.parseInt(response) > 0) {
                            Snackbar.make(modifyPassword, "亲，修改头像成功哦！", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(modifyPassword,"头像修改失败", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
