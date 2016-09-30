package com.inuc.inuc.zone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.inuc.inuc.R;
import com.inuc.inuc.beans.ZoneBean;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/22.
 */
public class ZoneRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String head = "<html><head></head><body>";
    String end = "</body></html>";

    private boolean mShowFooter = true;
    private Context mContext;
    private static final int TYPE_ITEM = 0;  //普通Item
    private static final int FOOTER_ITEM = 1;  //底部FooterView

    private CustomTagHandler mTagHandler;


    private List<ZoneBean> mData;
    private ArrayList<Moment> commentList;


    private OnItemClickListener mOnItemClickListener;


    public ZoneRecycleViewAdapter(List<ZoneBean> dataList, ArrayList<Moment> commentList, Context mContext, CustomTagHandler mTagHandler) {
        this.mData = dataList;
        this.mContext = mContext;
        this.commentList = commentList;
        this.mTagHandler = mTagHandler;
    }


    public void setmDate(List<ZoneBean> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_zone, null);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
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
        if (holder instanceof ViewHolder) {
            String contentString = head + mData.get(position).getContents() + end;
//            ((ViewHolder) holder).image_head.setImageResource(R.drawable.icon__head);
            Picasso.with(mContext).load(mData.get(position).getUserPictureUrl()).placeholder(R.mipmap.icon__head).error(R.mipmap.icon__head).into(((ViewHolder) holder).image_head);
            ((ViewHolder) holder).text_name.setText(mData.get(position).getNickname());
            String date=mData.get(position).getPublishTime().toString().substring(0,10)+" "
                    +mData.get(position).getPublishTime().toString().substring(11,16);
            ((ViewHolder) holder).text_time.setText(date);
            ((ViewHolder) holder).webView.loadDataWithBaseURL(null, contentString, "text/html", "utf-8", null);
            ((ViewHolder) holder).text_look_count.setText("浏览" + mData.get(position).getHits() + "次");
//            CommentFun.parseCommentList(mContext, mData.get(position).getRemarksArrayList(),
//                    ((ViewHolder) holder).mCommentList, ((ViewHolder) holder).mBtnInput, mTagHandler);
            CommentFun.parseCommentList(mContext, commentList.get(position).mComment,
                    ((ViewHolder) holder).mCommentList, ((ViewHolder) holder).mBtnInput, mTagHandler);
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

    public ZoneBean getItem(int position) {

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        ImageView image_head;
        public TextView text_name;
        public TextView text_time;
        public WebView webView;
        public TextView text_look_count;//浏览次数
        public ImageView image_support;
        public ImageView image_remark;//评论
        public TextView text_final;//多少人觉得很赞
        LinearLayout mCommentList;
        TextView mBtnInput;
        TextView mContent;
        public EditText edit_remark;//我来说两句吧
        private Button button_send;//发送评论

        public ViewHolder(View itemView) {
            super(itemView);
            image_head = (ImageView) itemView.findViewById(R.id.id_image_head);
            text_name = (TextView) itemView.findViewById(R.id.id_text_name);
            text_time = (TextView) itemView.findViewById(R.id.id_text_time);
            webView = (WebView) itemView.findViewById(R.id.id_webview);
            text_look_count = (TextView) itemView.findViewById(R.id.id_text_look_count);
            image_support = (ImageView) itemView.findViewById(R.id.id_image_support);
            image_support.setOnClickListener(this);
            image_remark = (ImageView) itemView.findViewById(R.id.id_image_remark);
            image_remark.setOnClickListener(this);
            text_final = (TextView) itemView.findViewById(R.id.id_final);
            mCommentList = (LinearLayout) itemView.findViewById(R.id.comment_list);
            mBtnInput = (TextView) itemView.findViewById(R.id.btn_input_comment);
            mBtnInput.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.id_image_support:
                    Toast.makeText(mContext, "点赞", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.id_image_remark:
                    Toast.makeText(mContext, "评论", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }

    }
}
