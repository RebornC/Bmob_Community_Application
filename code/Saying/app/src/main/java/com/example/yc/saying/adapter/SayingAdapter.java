package com.example.yc.saying.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yc.saying.R;
import com.example.yc.saying.ui.SayingActivity;
import com.example.yc.saying.model.the_saying;

import java.util.List;

public class SayingAdapter extends RecyclerView.Adapter<SayingAdapter.ViewHolder>{

    private List<the_saying> cardList;
    private OnItemClickListener mOnItemClickListener = null;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View cardView;
        TextView Saying_id;
        TextView Content;

        public ViewHolder(View view) {
            super(view);
            cardView = view;
            Saying_id = (TextView) view.findViewById(R.id.saying_id);
            Content = (TextView) view.findViewById(R.id.content);
        }
    }

    public SayingAdapter(List<the_saying> CardList) {
        cardList = CardList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_saying_item_2, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接在adapter设置监听，点击后进行传值、页面跳转
                int position = holder.getAdapterPosition();
                String objectId = cardList.get(position).getSaying_id();
                Intent it = new Intent(v.getContext(), SayingActivity.class);
                it.putExtra("objectId", objectId);
                v.getContext().startActivity(it);
            }
        });
        return holder;
    }

    public interface OnItemClickListener {//回调接口
        void onClick(View v);//单击，设置为view是因为我想获得子控件的值
        void onLongClick(int position);//长按
    }

    //定义这个接口的set方法，便于调用

    public void setOnClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(final SayingAdapter.ViewHolder holder, final int position) {
        the_saying users = cardList.get(position);
        holder.Saying_id.setText(users.getSaying_id());
        holder.Content.setText(users.getContent());

        //设置点击和长按事件
        if (mOnItemClickListener != null) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(holder.Saying_id);
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