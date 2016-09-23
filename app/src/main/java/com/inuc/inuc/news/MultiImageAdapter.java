package com.inuc.inuc.news;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.inuc.inuc.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * Created by 景贝贝 on 2016/9/22.
 */
public class MultiImageAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<String> mImages;
    private static final int REQUEST_IMAGE = 2;
    //用来判断是否是刚刚进入，刚进入只显示添加按钮，也就是上面java代码中只传this的时候
    private boolean is = false;
//    ImagePicker imagePicker = ImagePicker.getInstance();

    public MultiImageAdapter(Activity activity, ArrayList images) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.mImages = images;
//        initImagePicker();//设置图片选择的一些属性
    }

    public MultiImageAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        is = true;//设置为true表示第一次初始化
//        initImagePicker();//设置图片选择的一些属性
    }

    @Override
    public int getCount() {
        if (!is) {
            //这里判断数据如果有9张就size等于9,否则就+1，+1是为按钮留的位置
            return mImages.size() == 9 ? mImages.size() : mImages.size() + 1;
        }
        //没有数据就是1，1是为按钮留的位置
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return mImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        ViewHolder holder = null;
        if (null == view) {
            view = inflater.inflate(R.layout.item_ulti_image, null);
            holder = new ViewHolder();
            holder.ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
            holder.ibAdd = (ImageButton) view.findViewById(R.id.ibAdd);
//            holder.ibDelete = (ImageButton) view.findViewById(R.id.ibDelete);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (!is) {
            //选了图片后会进入这里，先判断下position 是否等于size
            if (position == mImages.size()) {
                //执行到这里就说明是最后一个位置，判断是否有9张图
                if (mImages.size() != 9) {
                    //没有9张图就显示添加按钮
                    holder.ibAdd.setVisibility(View.VISIBLE);
                } else {
                    //有就隐藏
                    holder.ibAdd.setVisibility(View.GONE);
                }
            } else {
                //还不是最后一个位置的时候执行这里
                //隐藏添加按钮，要设置图片嘛~
                holder.ibAdd.setVisibility(View.GONE);
                //根据条目位置设置图片
                String path = mImages.get(position);
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                holder.ivIcon.setImageBitmap(bitmap);
               Glide.with(activity)
                        .load(path)
                        .into(holder.ivIcon);
            }
//            //删除按钮的点击事件
//            holder.ibDelete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //移除图片
//                    mImages.remove(position);
//                    //更新
//                    notifyDataSetChanged();
//                }
//            });
        } else {
            //初次初始化的时候显示添加按钮
            holder.ibAdd.setVisibility(View.VISIBLE);
        }
        //添加按钮点击事件
        holder.ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否是初始化进入
//                if (!is) {
//                    //到这里表示已经选过了，然后用9-size算出还剩几个图的位置
//                    imagePicker.setSelectLimit(9 - mImages.size());//选中数量限制
//                }
                //跳转到图片选择
                MultiImageSelector.create(activity)
                        .showCamera(true) // 是否显示相机. 默认为显示
                        .count(9) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
//                        .single() // 单选模式
                         .multi() // 多选模式, 默认模式;
                        .origin(mImages) // 默认已选择图片. 只有在选择模式为多选时有效
                        .start(activity, REQUEST_IMAGE);
            }
        });
        return view;
    }

    protected class ViewHolder {
        /**
         * icon
         */
        protected ImageView ivIcon;
        /**
         * 移除
         */
        protected ImageButton ibDelete;
        /**
         * 添加
         */
        protected ImageButton ibAdd;
    }

//    private void initImagePicker() {
//        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
//        imagePicker.setShowCamera(true);//显示拍照按钮
//        imagePicker.setCrop(true);//允许裁剪（单选才有效）
//        imagePicker.setSaveRectangle(true);//是否按矩形区域保存
//        imagePicker.setSelectLimit(9);//选中数量限制
//        imagePicker.setStyle(CropImageView.Style.CIRCLE);//裁剪框的形状
//        imagePicker.setFocusWidth(100);//裁剪框的宽度。单位像素（圆形自动取宽高最小值）
//        imagePicker.setFocusHeight(100);//裁剪框的高度。单位像素（圆形自动取宽高最小值）
//        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
//        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
//    }
}
