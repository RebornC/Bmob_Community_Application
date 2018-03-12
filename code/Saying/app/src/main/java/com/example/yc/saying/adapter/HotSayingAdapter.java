package com.example.yc.saying.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yc.saying.R;
import com.example.yc.saying.ui.SayingActivity;
import com.example.yc.saying.model.hot_sayings;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class HotSayingAdapter extends RecyclerView.Adapter<HotSayingAdapter.ViewHolder>{

    private List<hot_sayings> cardList;
    private OnItemClickListener mOnItemClickListener = null;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.upload) // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.mipmap.upload) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.mipmap.upload) // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
            .build(); // 构建完成

    static class ViewHolder extends RecyclerView.ViewHolder {
        View cardView;
        TextView id;
        TextView content;
        ImageView image;

        public ViewHolder(View view) {
            super(view);
            cardView = view;
            id = (TextView) view.findViewById(R.id.id);
            content = (TextView) view.findViewById(R.id.content);
            image = (ImageView) view.findViewById(R.id.image);
        }
    }

    public HotSayingAdapter(List<hot_sayings> CardList) {
        cardList = CardList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hot_sayings_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接在adapter设置监听，点击后进行传值、页面跳转
                int position = holder.getAdapterPosition();
                String objectId = cardList.get(position).getId();
                Intent it = new Intent(v.getContext(), SayingActivity.class);
                it.putExtra("objectId", objectId);
                v.getContext().startActivity(it);
            }
        });
        return holder;
    }

    public interface OnItemClickListener {//回调接口
        void onClick(int position);//单击，设置为view是因为我想获得子控件的值
        void onLongClick(int position);//长按
    }

    //定义这个接口的set方法，便于调用

    public void setOnClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(final HotSayingAdapter.ViewHolder holder, final int position) {
        hot_sayings TEMP = cardList.get(position);
        holder.id.setText(TEMP.getId());
        holder.content.setText(TEMP.getContent());
        //获取图片的uri（string格式），利用imageLoader进行显示
        if (TEMP.getImage() != "no_image") {
            holder.image.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(TEMP.getImage(), holder.image, options);
        }
        else
            holder.image.setVisibility(View.GONE);

        //设置点击和长按事件
        if (mOnItemClickListener != null) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(holder.getAdapterPosition());
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

}