package com.example.yc.saying.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yc.saying.R;
import com.example.yc.saying.model._User;
import com.example.yc.saying.model.message_fans;
import com.example.yc.saying.model.notification;
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
 * Created by yc on 2018/3/1.
 */

public class MessageNotificationActivity extends AppCompatActivity {
    private _User current_user = BmobUser.getCurrentUser(_User.class);
    private Integer notification_sum;
    private ImageView back;
    private ListView listview;
    private List<Map<String,Object>> data = new ArrayList<>();
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        Intent it = getIntent();
        String sum = it.getStringExtra("notification_sum");
        notification_sum = Integer.parseInt(sum);

        findView();
        initialization();
        clickEvents();

    }

    public void findView() {
        back = (ImageView) findViewById(R.id.back);
        listview = (ListView) findViewById(R.id.listview);
    }

    public void initialization() {

        BmobQuery<notification> query = new BmobQuery<notification>();
        query.order("-createdAt");
        query.findObjects(new FindListener<notification>() {
            @Override
            public void done(List<notification> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        Toast.makeText(getApplicationContext(), "当前未有系统通知~", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < list.size(); i++) {
                            Map<String,Object> temp = new LinkedHashMap<>();
                            temp.put("title", list.get(i).getTitle());
                            temp.put("content", list.get(i).getContent());
                            temp.put("id", list.get(i).getObjectId());
                            temp.put("time", list.get(i).getCreatedAt().toString());
                            data.add(temp);
                        }
                        simpleAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.notification_item, new String[] {"title","content","id","time"}, new int[] {R.id.title, R.id.content, R.id.id, R.id.time});
                        listview.setAdapter(simpleAdapter);
                    }
                } else {
                    //Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BmobQuery<user_followers> query2 = new BmobQuery<user_followers>();
        query2.addWhereEqualTo("user_id", current_user.getObjectId());
        query2.findObjects(new FindListener<user_followers>() {
            @Override
            public void done(List<user_followers> list, BmobException e) {
                if (e == null) {
                    if (list.get(0).getNotification_read() != notification_sum) {
                        list.get(0).setNotification_read(notification_sum);
                        list.get(0).increment("message_fans_read",0);
                        list.get(0).increment("follower_read",0);
                        list.get(0).increment("message_fans_sum",0);
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
                setResult(3, i);
                finish();
            }
        });

    }

}
