package com.inuc.inuc.zone;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.inuc.inuc.R;
import com.inuc.inuc.zone.imagebrowse.view.ImageBrowseActivity;

import java.util.ArrayList;

/**
 * Created by 景贝贝 on 2016/10/22.
 */

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> mImages;
    int width=0;
    int height=0;

    public ImageAdapter(Context context, ArrayList images) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mImages = images;
    }
    public ImageAdapter(Context context, ArrayList images,int width,int height) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mImages = images;
        this.width=width;
        this.height=height;
    }

    @Override
    public int getCount() {
        if(mImages==null){
            return 0;
        }
        return mImages.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageAdapter.ViewHolder holder = null;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_image, null);
            holder = new ImageAdapter.ViewHolder();
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.item_image);
            if (height!=0) {
                holder.ivIcon.getLayoutParams().height = height;
                holder.ivIcon.getLayoutParams().width = width;
            }
            convertView.setTag(holder);
        } else {
            holder = (ImageAdapter.ViewHolder) convertView.getTag();
        }
                String path = mImages.get(position);

                Glide.with(context)
                        .load(path)
                        .error(R.mipmap.banner_error)
                        .placeholder(R.mipmap.banner_error)
                        .into(holder.ivIcon);
        holder.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageBrowseActivity.startActivity(context,mImages,position);
            }
        });
        return convertView;
    }

    protected class ViewHolder {

        protected ImageView ivIcon;

    }
}