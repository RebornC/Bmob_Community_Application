package com.example.yc.saying.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.yc.saying.R;
import com.example.yc.saying.ui.SayingActivity;
import com.example.yc.saying.ui.UserHomepageActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by yc on 2018/3/5.
 */

public class Message_Sayings_SimpleAdapter extends SimpleAdapter {

    Context context;
    List<? extends Map<String, ?>> Data;

    public Message_Sayings_SimpleAdapter(Context context, List<? extends Map<String, ?>> data,
                                         int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.Data = data; //获取各项数值用于不同intent的传值
        // TODO Auto-generated constructor stub
    }

    public View getView(int position, final View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = super.getView(position, convertView, parent);

        int p = position;
        ImageView image = (ImageView) v.findViewById(R.id.user_image);
        TextView name = (TextView) v.findViewById(R.id.user_name);
        TextView content = (TextView) v.findViewById(R.id.saying_content);

        final String user_id = Data.get(p).get("user_id").toString();
        final String saying_id = Data.get(p).get("saying_id").toString();

        // 点击不同控件,获取不同控件的值，触发不同事件(即进入不同界面)

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent it = new Intent(v.getContext(), UserHomepageActivity.class);
                it.putExtra("objectId", user_id);
                v.getContext().startActivity(it);
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent it = new Intent(v.getContext(), UserHomepageActivity.class);
                it.putExtra("objectId", user_id);
                v.getContext().startActivity(it);
            }
        });

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent it = new Intent(v.getContext(), SayingActivity.class);
                it.putExtra("objectId", saying_id);
                v.getContext().startActivity(it);
            }
        });

        return v;
    }

}
