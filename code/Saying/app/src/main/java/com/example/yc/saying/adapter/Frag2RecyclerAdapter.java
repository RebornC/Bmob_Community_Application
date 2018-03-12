package com.example.yc.saying.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.yc.saying.ui.AllArticlesActivity;
import com.example.yc.saying.ui.HotBooksActivity;
import com.example.yc.saying.ui.HotSayingsActivity;
import com.example.yc.saying.ui.HotUsersActivity;
import com.example.yc.saying.R;
import java.util.List;

public class Frag2RecyclerAdapter extends RecyclerView.Adapter<Frag2RecyclerAdapter.ViewHolder>{

    private List<Integer> cardList;
    private OnItemClickListener mOnItemClickListener = null;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View cardView;
        ImageView image;

        public ViewHolder(View view) {
            super(view);
            cardView = view;
            image = (ImageView) view.findViewById(R.id.image);
        }
    }

    public Frag2RecyclerAdapter(List<Integer> CardList) {
        cardList = CardList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag2_recycler_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 直接在adapter设置监听，点击后进行传值、页面跳转
                int position = holder.getAdapterPosition();
                switch (position) {
                    case 0:
                        Intent it1 = new Intent(v.getContext(), HotUsersActivity.class);
                        v.getContext().startActivity(it1);
                        break;
                    case 1:
                        Intent it2 = new Intent(v.getContext(), HotSayingsActivity.class);
                        v.getContext().startActivity(it2);
                        break;
                    case 2:
                        Intent it3 = new Intent(v.getContext(), HotBooksActivity.class);
                        v.getContext().startActivity(it3);
                        break;
                    case 3:
                        Intent it4 = new Intent(v.getContext(), AllArticlesActivity.class);
                        v.getContext().startActivity(it4);
                        break;
                    default:
                        break;
                }
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
    public void onBindViewHolder(final Frag2RecyclerAdapter.ViewHolder holder, final int position) {
        Integer image_id = cardList.get(position);
        holder.image.setImageResource(image_id);

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