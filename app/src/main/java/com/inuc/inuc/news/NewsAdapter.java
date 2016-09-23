package com.inuc.inuc.news;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.inuc.inuc.R;
import com.inuc.inuc.beans.News;
import com.inuc.inuc.utils.ImageLoaderUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 景贝贝 on 2016/9/1.
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<News> mData;
    private boolean mShowFooter = true;
    private Context mContext;
    private static final int TYPE_ITEM = 0;  //普通Item
    private static final int FOOTER_ITEM = 1;  //底部FooterView

    private OnItemClickListener mOnItemClickListener;

    public NewsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setmDate(List<News> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_news_layout, parent, false);
            ItemViewHolder vh = new ItemViewHolder(v);
            return vh;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.footer, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        holder.setIsRecyclable(false);
        if (holder instanceof ItemViewHolder) {

            News news = mData.get(position);
            if (news == null) {
                return;
            }
            ((ItemViewHolder) holder).Title.setText(news.getTitle());
            ((ItemViewHolder) holder).hit.setText(news.getHits() + "");
            ((ItemViewHolder) holder).Time.setText(news.getPublishTime().substring(5, 10));
            if (news.getTitlePicture() != null) {
                ((ItemViewHolder) holder).titleImageView.setVisibility(View.VISIBLE);
                Picasso.with(mContext)
                        .load(news.getTitlePicture())
                        .placeholder(R.mipmap.ic_image_loading)
                        .error(R.mipmap.ic_image_loadfail)
                        .into(((ItemViewHolder) holder).titleImageView);
            }else {
                ((ItemViewHolder) holder).titleImageView.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        int begin = mShowFooter ? 1 : 0;
        if (mData == null) {
            return begin;
        }
        return mData.size() + begin;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (!mShowFooter) {
            return TYPE_ITEM;
        }
        if (position + 1 == getItemCount()) {
            return FOOTER_ITEM;
        } else {
            return TYPE_ITEM;
        }
    }

    public News getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    public void isShowFooter(boolean showFooter) {
        this.mShowFooter = showFooter;
    }

    public boolean isShowFooter() {
        return this.mShowFooter;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //        public TextView mTitle;
        public ImageView titleImageView;
        public TextView Title, hit, Time;

        public ItemViewHolder(View v) {
            super(v);
            Title = (TextView) v.findViewById(R.id.nuclistItemTitleTV);
            hit = (TextView) v.findViewById(R.id.nucitemSkimTV);
            Time = (TextView) v.findViewById(R.id.nucitempublishTimeTV);
            titleImageView = (ImageView) v.findViewById(R.id.nucitemTitleIV);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, this.getPosition());
            }
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }

    }
}
