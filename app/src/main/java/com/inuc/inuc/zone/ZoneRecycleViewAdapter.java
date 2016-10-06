package com.inuc.inuc.zone;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inuc.inuc.R;
import com.inuc.inuc.beans.Remarks;
import com.inuc.inuc.beans.ZoneBean;
import com.inuc.inuc.utils.DeviceUtil;
import com.inuc.inuc.utils.Urls;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/22.
 */
public class ZoneRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int KEY_COMMENT_SOURCE_COMMENT_LIST = -200162;

    private SharedPreferences pref;
    private String token;

    String head = "<html><head></head><body>";
    String end = "</body></html>";

    private boolean mShowFooter = true;
    private Context mContext;
    private static final int TYPE_ITEM = 0;  //普通Item
    private static final int FOOTER_ITEM = 1;  //底部FooterView

    private CustomTagHandler mTagHandler;

    int position;
    private List<ZoneBean> mData;

    private RecyclerView mRecyclerView;
    ZoneRecycleViewAdapter zoneRecycleViewAdapter;

    private OnItemClickListener mOnItemClickListener;
    List<Comment> mCommentList = new ArrayList<>();

    public ZoneRecycleViewAdapter(SharedPreferences pref) {
        this.pref = pref;
        token = pref.getString("token", "");
        zoneRecycleViewAdapter = this;
    }

    public ZoneRecycleViewAdapter(SharedPreferences pref, RecyclerView mRecyclerView, List<ZoneBean> dataList, Context mContext, CustomTagHandler mTagHandler) {
        this.mData = dataList;
        this.mContext = mContext;
        this.mTagHandler = mTagHandler;
        this.mRecyclerView = mRecyclerView;
        zoneRecycleViewAdapter = this;
        this.pref = pref;
        token = pref.getString("token", "");

    }

    public SharedPreferences getSharedPreferences(String name, int mode) {
        return mContext.getSharedPreferences(name, mode);
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
            this.position = position;
            Log.i("@@@",position+"");
            String contentString = head + mData.get(position).getContents() + end;
//            ((ViewHolder) holder).image_head.setImageResource(R.drawable.icon__head);
            Picasso.with(mContext).load(mData.get(position).getUserPictureUrl()).placeholder(R.mipmap.icon__head).error(R.mipmap.icon__head).into(((ViewHolder) holder).image_head);
            ((ViewHolder) holder).text_name.setText(mData.get(position).getNickname());
            String date = mData.get(position).getPublishTime().toString().substring(0, 10) + " "
                    + mData.get(position).getPublishTime().toString().substring(11, 16);
            ((ViewHolder) holder).text_time.setText(date);
            ((ViewHolder) holder).webView.loadDataWithBaseURL(null, contentString, "text/html", "utf-8", null);
            ((ViewHolder) holder).text_look_count.setText("浏览" + mData.get(position).getHits() + "次");
            if (mData.get(position).getPositive() == 0) {
                ((ViewHolder) holder).text_final.setText("还没有人赞说说哦");
            } else {
                ((ViewHolder) holder).text_final.setText(mData.get(position).getPositive() + "个人觉得很赞");
            }
//                    ((ViewHolder) holder).mCommentList, ((ViewHolder) holder).mBtnInput, mTagHandler);
//            CommentFun.parseCommentList(mContext, mData.get(position).getRemarksArrayList(),
            mCommentList = analyze(mData.get(position).getRemarksArrayList());
            Log.i("@@@", "mCommentList" + mCommentList.size());
            CommentFun.parseCommentList(mContext, mCommentList,
                    ((ViewHolder) holder).mCommentList, ((ViewHolder) holder).mBtnInput, mTagHandler);
        }
    }


    //将remarks转换成ListView<Commnet>;
    private List<Comment> analyze(List<Remarks> remarks) {
        List<Comment> mCommentList = new ArrayList<Comment>();
        for (int i = 0; i < remarks.size(); i++) {
            Log.i("@@@", "analyze" + remarks.size());
            String A = remarks.get(i).getNiCheng();
            String B = remarks.get(i).getRemarks();
            Comment commnet = new Comment(new User(2, A), B, null);
            mCommentList.add(commnet);
        }

        return mCommentList;
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

        public ViewHolder(View itemView) {
            super(itemView);
            image_head = (ImageView) itemView.findViewById(R.id.id_image_head);
            text_name = (TextView) itemView.findViewById(R.id.id_text_name);
            text_time = (TextView) itemView.findViewById(R.id.id_text_time);
            webView = (WebView) itemView.findViewById(R.id.id_webview);
            text_look_count = (TextView) itemView.findViewById(R.id.id_text_look_count);
            image_support = (ImageView) itemView.findViewById(R.id.id_image_support);
            image_support.setOnClickListener(this);
            text_final = (TextView) itemView.findViewById(R.id.id_final);
            mCommentList = (LinearLayout) itemView.findViewById(R.id.comment_list);
            mBtnInput = (TextView) itemView.findViewById(R.id.btn_input_comment);
            mBtnInput.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.id_image_support:
                    positive();
                    break;

                case R.id.btn_input_comment:
                    inputComment(mContext, mRecyclerView, v, null, new InputCommentListener() {
                        @Override
                        public void onCommitComment() {
                            zoneRecycleViewAdapter.notifyDataSetChanged();
                        }
                    });
                    break;
            }
        }

        //点赞请求网络
        private void positive() {
            boolean is_positive = true;
            final int[] i = new int[1];
            OkHttpUtils.post().url(Urls.AddTalkingPoint)
                    .addHeader("Authorization", token)
                    .addParams("talkingID", mData.get(position).getID() + 1 + "")
                    .addParams("positive", is_positive + "")
                    .addParams("imei", DeviceUtil.getIMEIDeviceId(mContext))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            String error = "";
                            if (e.toString().contains("java.net.ConnectException")) {
                                error = "网络连接失败";
                            } else {
                                error = "未知错误" + e.toString();
                            }
                            Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.i("@@@", "positive");
                            Toast.makeText(mContext, "点赞成功", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }

    }

    public void inputComment(final Context mContext, final RecyclerView mRecycleView,
                             final View btnComment, final User receiver,
                             final InputCommentListener listener) {

        final ArrayList<Comment> commentList = (ArrayList) btnComment.getTag(KEY_COMMENT_SOURCE_COMMENT_LIST);

        String hint;
        if (receiver != null) {
            if (receiver.mId == ZoneMainActivity.sUser.mId) {
                hint = "我也说一句..";
            } else {
//                hint = "回复 " + receiver.mName;
                hint = "我也说一句..";
            }
        } else {
            hint = "我也说一句";
        }
        // 获取评论的位置,不要在CommentDialogListener.onShow()中获取，onShow在输入法弹出后才调用，
        // 此时btnComment所属的父布局可能已经被ListView回收
        final int[] coord = new int[2];
        if (mRecycleView != null) {
            btnComment.getLocationOnScreen(coord);
        }

        showInputComment((Activity) mContext, hint, new CommentFun.CommentDialogListener() {
            @Override
            public void onClickPublish(final Dialog dialog, EditText input, final TextView btn) {
                final String content = input.getText().toString();
                if (content.trim().equals("")) {
                    Toast.makeText(mContext, "评论不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                btn.setClickable(false);
                final long receiverId = receiver == null ? -1 : receiver.mId;
                Comment comment = new Comment(ZoneMainActivity.sUser, content, receiver);
                commentList.add(comment);
                remarkHttp(content);//将评论内容发送服务器
                if (listener != null) {
                    listener.onCommitComment();
                }
                dialog.dismiss();
            }

            private void remarkHttp(String content) {
                String token;
                token = pref.getString("token", "");
                OkHttpUtils.post().url(Urls.AddTalkingRemark)
                        .addHeader("Authorization", token)
                        .addParams("talkingID", mData.get(position).getID() + 1 + "")
                        .addParams("remarks", content)
                        .addParams("imei", DeviceUtil.getIMEIDeviceId(mContext))
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        String error = "";
                        if (e.toString().contains("java.net.ConnectException")) {
                            error = "网络连接失败";
                        } else {
                            error = "未知错误" + e.toString();
                        }
                        Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("@@@", response + "Remark");
                        Toast.makeText(mContext, "评论成功", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onShow(int[] inputViewCoordinatesInScreen) {
                if (mRecycleView != null) {
                    // 点击某条评论则这条评论刚好在输入框上面，点击评论按钮则输入框刚好挡住按钮
                    int span = btnComment.getId() == R.id.btn_input_comment ? 0 : btnComment.getHeight();
                    mRecycleView.smoothScrollBy(1000, coord[1] + span - inputViewCoordinatesInScreen[1]);
                }
            }

            @Override
            public void onDismiss() {

            }
        });

    }

    public static class InputCommentListener {
        //　评论成功时调用
        public void onCommitComment() {

        }
    }


    /**
     * 弹出评论对话框
     */
    public static Dialog showInputComment(Activity activity, CharSequence hint, final CommentFun.CommentDialogListener listener) {
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.view_input_comment);
        dialog.findViewById(R.id.input_comment_dialog_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onDismiss();
                }
            }
        });
        final EditText input = (EditText) dialog.findViewById(R.id.input_comment);
        input.setHint(hint);
        final TextView btn = (TextView) dialog.findViewById(R.id.btn_publish_comment);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickPublish(dialog, input, btn);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    int[] coord = new int[2];
                    dialog.findViewById(R.id.input_comment_container).getLocationOnScreen(coord);
                    // 传入 输入框距离屏幕顶部（不包括状态栏）的长度
                    listener.onShow(coord);
                }
            }
        }, 300);
        return dialog;
    }




}