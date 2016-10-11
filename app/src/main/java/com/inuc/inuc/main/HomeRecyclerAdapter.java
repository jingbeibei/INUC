package com.inuc.inuc.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inuc.inuc.R;
import com.inuc.inuc.beans.BannerPic;
import com.inuc.inuc.beans.News;
import com.inuc.inuc.mailbox.TabLayoutTopActivity;
import com.inuc.inuc.main.ImageSlideshow.ImageSlideshow;
import com.inuc.inuc.office.OfficeAutomationActivity;
import com.inuc.inuc.utils.Urls;
import com.inuc.inuc.zone.ZoneMainActivity;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<News> mData;
    private boolean mShowFooter = true;
    private Context mContext;
    private static final int BANNER_ITEM = 0;//滑动图片
    private static final int Menues_ITEM = 1;//菜单栏
    private static final int TYPE_ITEM = 2;  //普通Item
    private static final int FOOTER_ITEM = 3;  //底部FooterView

    private OnItemClickListener mOnItemClickListener;

    public HomeRecyclerAdapter(Context mContext) {
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
        } else if (viewType == BANNER_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home_imageslide, parent, false);
            ImageSlideViewHolder vh = new ImageSlideViewHolder(v);
            return vh;
        } else if (viewType == Menues_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home_menus, parent, false);
            MenusViewHolder vh = new MenusViewHolder(v);
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
            } else {
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

//         最后一个item设置为footerView
//        if (!mShowFooter) {
//            return TYPE_ITEM;
//        }
//        if (position + 1 == getItemCount()) {
//            return FOOTER_ITEM;
//        } else {
//            return TYPE_ITEM;
//        }

        if (position == 0){
            return BANNER_ITEM;
        }else if (position == 1){
            return Menues_ITEM;
        }else if (position + 1 == getItemCount()) {
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

    public class ImageSlideViewHolder extends RecyclerView.ViewHolder {
        private ImageSlideshow imageSlideshow;
        private List<String> imageUrlList;
        private List<String> titleList;
        private List<BannerPic> bannerPicsList = null;

        public ImageSlideViewHolder(View view) {
            super(view);
            imageSlideshow = (ImageSlideshow) view.findViewById(R.id.is_gallery);
            initImageSlide();
        }

        private void initImageSlide() {
            imageUrlList = new ArrayList<>();
            titleList = new ArrayList<>();
            OkHttpUtils.get().url(Urls.GetPictureNewsUrl).addParams("pageSize", "5").build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if (response.indexOf("TitlePicture") > 0) {
                                bannerPicsList = new Gson().fromJson(response, new TypeToken<List<BannerPic>>() {
                                }.getType());
                                // 初始化数据
                                initData();
                            }
                        }
                    });
        }

        /**
         * 初始化数据
         */
        private void initData() {

            String[] imageUrls = {"http://pic3.zhimg.com/b5c5fc8e9141cb785ca3b0a1d037a9a2.jpg",
                    "http://pic2.zhimg.com/551fac8833ec0f9e0a142aa2031b9b09.jpg",
                    "http://pic2.zhimg.com/be6f444c9c8bc03baa8d79cecae40961.jpg",
                    "http://pic1.zhimg.com/b6f59c017b43937bb85a81f9269b1ae8.jpg",
                    "http://pic2.zhimg.com/a62f9985cae17fe535a99901db18eba9.jpg"};
            String[] titles = {"读读日报 24 小时热门 TOP 5 · 余文乐和「香港贾玲」乌龙绯闻",
                    "写给产品 / 市场 / 运营的数据抓取黑科技教程",
                    "学做这些冰冰凉凉的下酒宵夜，简单又方便",
                    "知乎好问题 · 有什么冷门、小众的爱好？",
                    "欧洲都这么发达了，怎么人均收入还比美国低"};
            if (bannerPicsList != null) {
                for (int i = 0; i < bannerPicsList.size(); i++) {
//            imageSlideshow.addImageTitle(imageUrls[i], titles[i]);
                    imageSlideshow.addImageTitle(bannerPicsList.get(i).getTitlePicture(), bannerPicsList.get(i).getTitle());
                }
            } else {
                for (int i = 0; i < imageUrls.length; i++) {//以防获取不到数据
                    imageSlideshow.addImageTitle(imageUrls[i], titles[i]);
//                imageSlideshow.addImageTitle(bannerPicsList.get(i).getTitlePicture(), bannerPicsList.get(i).getTitle());
                }
            }

            // 为ImageSlideshow设置数据        imageSlideshow.setDotSpace(12);
            imageSlideshow.setDotSize(12);
            imageSlideshow.setDelay(3000);
            imageSlideshow.setOnItemClickListener(new ImageSlideshow.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    switch (position) {
                        case 0:
                            Toast.makeText(view.getContext(), "0", Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(MainActivity.this,Activity_1.class));
                            break;
                        case 1:
                            Toast.makeText(view.getContext(), "1", Toast.LENGTH_LONG).show();
                            break;
                        case 2:
                            Toast.makeText(view.getContext(), "2", Toast.LENGTH_LONG).show();
                            break;
                        case 3:
                            Toast.makeText(view.getContext(), "3", Toast.LENGTH_LONG).show();
                            break;
                        case 4:
                            Toast.makeText(view.getContext(), "4", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            });
            imageSlideshow.commit();
        }
    }

    public class MenusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout principalMailboxBt;
        private LinearLayout officeAutomationBt;
        private LinearLayout MicroSpaceBt;

        public MenusViewHolder(View view) {
            super(view);
            principalMailboxBt = (LinearLayout) view.findViewById(R.id.mailbox_layout);
            officeAutomationBt = (LinearLayout) view.findViewById(R.id.oa_layout);
            MicroSpaceBt = (LinearLayout) view.findViewById(R.id.zone_layout);
            principalMailboxBt.setOnClickListener(this);
            officeAutomationBt.setOnClickListener(this);
            MicroSpaceBt.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.mailbox_layout://校长信箱
                    Intent boxIntent = new Intent(view.getContext(), TabLayoutTopActivity.class);
                    view.getContext().startActivity(boxIntent);
                    break;
                case R.id.oa_layout://OA
                    Intent oaIntent = new Intent(view.getContext(), OfficeAutomationActivity.class);
                    view.getContext().startActivity(oaIntent);
                    break;
                case R.id.zone_layout://微空间
                    Intent msIntent = new Intent(view.getContext(), ZoneMainActivity.class);
                    view.getContext().startActivity(msIntent);
                    break;
            }

        }
    }
}

