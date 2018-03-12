package com.example.yc.saying.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.yc.saying.R;
import com.example.yc.saying.adapter.Message_Sayings_SimpleAdapter;
import com.example.yc.saying.model._User;
import com.example.yc.saying.model.message_sayings;
import com.example.yc.saying.model.user_followers;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by yc on 2018/3/5.
 */

public class MessageSayingsActivity extends AppCompatActivity {
    private _User current_user = BmobUser.getCurrentUser(_User.class);
    private ImageView back;
    private ListView listview;
    private List<Map<String,Object>> data = new ArrayList<>();
    private Message_Sayings_SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_sayings);

        findView();
        initialization();
        clickEvents();

    }

    public void findView() {
        back = (ImageView) findViewById(R.id.back);
        listview = (ListView) findViewById(R.id.listview);
    }

    public void initialization() {

        BmobQuery<message_sayings> query = new BmobQuery<message_sayings>();
        query.order("-createdAt");
        query.include("initiator");
        query.addWhereEqualTo("acceptor_id", current_user.getObjectId());
        query.findObjects(new FindListener<message_sayings>() {
            @Override
            public void done(List<message_sayings> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        Toast.makeText(getApplicationContext(), "当前未有消息提示~", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < list.size(); i++) {
                            Map<String,Object> temp = new LinkedHashMap<>();
                            temp.put("user_image", list.get(i).getInitiator().getHeadPortrait().getFileUrl());
                            temp.put("user_name", list.get(i).getInitiator().getNickName().toString());
                            temp.put("user_id", list.get(i).getInitiator().getObjectId().toString());
                            temp.put("create_time", list.get(i).getCreatedAt().toString().split(" ")[0]);
                            temp.put("saying_id", list.get(i).getSaying_id());
                            temp.put("saying_content", list.get(i).getSaying_content());
                            data.add(temp);
                        }
                        simpleAdapter = new Message_Sayings_SimpleAdapter(getApplicationContext(), data, R.layout.saying_item_6, new String[] {"user_image","user_name","user_id","create_time","saying_id","saying_content"}, new int[] {R.id.user_image, R.id.user_name, R.id.user_id, R.id.create_time,R.id.saying_id,R.id.saying_content});
                        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                            public boolean setViewValue(View view, Object data,
                                                        String textRepresentation) {
                                //判断是否为我们要处理的对象
                                if(view instanceof ImageView && data instanceof String){
                                    ImageView iv = (ImageView) view;
                                    ImageLoader.getInstance().displayImage((String) data, iv);
                                    return true;
                                }else
                                    return false;
                            }
                        });
                        listview.setAdapter(simpleAdapter);
                        //Toast.makeText(getApplicationContext(), "查询成功", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Toast.makeText(getApplicationContext(), "查询失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        BmobQuery<user_followers> query2 = new BmobQuery<user_followers>();
        query2.addWhereEqualTo("user_id", current_user.getObjectId());
        query2.findObjects(new FindListener<user_followers>() {
            @Override
            public void done(List<user_followers> list, BmobException e) {
                if (e == null) {
                    if (list.get(0).getMessage_sayings_sum() != list.get(0).getMessage_sayings_read()) {
                        list.get(0).setMessage_sayings_read(list.get(0).getMessage_sayings_sum());
                        list.get(0).increment("notification_read",0);
                        list.get(0).increment("follower_sum",0);
                        list.get(0).increment("message_fans_sum",0);
                        list.get(0).increment("message_fans_read",0);
                        list.get(0).increment("message_books_read",0);
                        list.get(0).increment("message_books_sum",0);
                        list.get(0).update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                //if (e == null)
                                //Toast.makeText(getApplicationContext(), "赋值成功", Toast.LENGTH_SHORT).show();
                                //else
                                //Toast.makeText(getApplicationContext(), "赋值失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    //Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void clickEvents() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(1, i);
                finish();
            }
        });

    }


}
