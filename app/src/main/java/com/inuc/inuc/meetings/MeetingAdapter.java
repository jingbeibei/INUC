package com.inuc.inuc.meetings;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inuc.inuc.R;
import com.inuc.inuc.beans.News;
import com.inuc.inuc.beans.OAMeeting;
import com.inuc.inuc.news.NewsAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 景贝贝 on 2016/10/15.
 */

public class MeetingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<OAMeeting> mData;
    private boolean mShowFooter = true;
    private Context mContext;
    private static final int TYPE_ITEM = 0;  //普通Item
    private static final int FOOTER_ITEM = 1;  //底部FooterView

    private MeetingAdapter.OnItemClickListener mOnItemClickListener;

    public MeetingAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setmDate(List<OAMeeting> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_oameeting_layout, parent, false);
            MeetingAdapter.ItemViewHolder vh = new MeetingAdapter.ItemViewHolder(v);
            return vh;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.footer, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new MeetingAdapter.FooterViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        holder.setIsRecyclable(false);
        if (holder instanceof MeetingAdapter.ItemViewHolder) {

            OAMeeting news = mData.get(position);
            if (news == null) {
                return;
            }
            ((MeetingAdapter.ItemViewHolder) holder).Title.setText(news.getTitle());
            ((MeetingAdapter.ItemViewHolder) holder).Address.setText(news.getAddress());
            ((MeetingAdapter.ItemViewHolder) holder).Time.setText(news.getTime().substring(0, 10)+" "+news.getTime().substring(11, 16));
            ((ItemViewHolder) holder).Attendees.setText(news.getAttendees());

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

    public OAMeeting getItem(int position) {
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

    public void setOnItemClickListener(MeetingAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public TextView Title, Address, Time,Attendees;

        public ItemViewHolder(View v) {
            super(v);
            Title = (TextView) v.findViewById(R.id.meeting_title_tv);
           Address = (TextView) v.findViewById(R.id.meeting_address_tv);
            Time = (TextView) v.findViewById(R.id.meeting_time_tv);
           Attendees = (TextView) v.findViewById(R.id.meeting_attendees_tv);

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
    public class HeaderViewHolder extends RecyclerView.ViewHolder{

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
