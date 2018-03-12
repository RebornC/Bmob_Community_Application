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
 * Created by yc on 2018/2/27.
 */

public class MessageFansActivity extends AppCompatActivity {
    private _User current_user = BmobUser.getCurrentUser(_User.class);
    private ImageView back;
    private ListView listview;
    private List<Map<String,Object>> data = new ArrayList<>();
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_fans);

        findView();
        initialization();
        clickEvents();

    }

    public void findView() {
        back = (ImageView) findViewById(R.id.back);
        listview = (ListView) findViewById(R.id.listview);
    }

    public void initialization() {

        BmobQuery<message_fans> query = new BmobQuery<message_fans>();
        query.order("-createdAt");
        query.include("initiator");
        query.addWhereEqualTo("acceptor_id", current_user.getObjectId());
        query.findObjects(new FindListener<message_fans>() {
            @Override
            public void done(List<message_fans> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        Toast.makeText(getApplicationContext(), "当前未有消息提示~", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < list.size(); i++) {
                            Map<String,Object> temp = new LinkedHashMap<>();
                            temp.put("image", list.get(i).getInitiator().getHeadPortrait().getFileUrl());
                            temp.put("name", list.get(i).getInitiator().getNickName().toString());
                            temp.put("user_id", list.get(i).getInitiator().getObjectId().toString());
                            temp.put("time", list.get(i).getCreatedAt().toString().split(" ")[0]);
                            data.add(temp);
                        }
                        simpleAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.message_fans_item, new String[] {"image","name","user_id","time"}, new int[] {R.id.image, R.id.name, R.id.user_id, R.id.time});
                        // 在SimpleAdapter中需要一个数据源，用来存储数据的，在显示图片时我们要用HashMap<>存储一个url转为string的路径；
                        // 利用imageloader框架，对SimpleAdapter进行处理
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
                    if (list.get(0).getMessage_fans_sum() != list.get(0).getMessage_fans_read()) {
                        list.get(0).setMessage_fans_read(list.get(0).getMessage_fans_sum());
                        list.get(0).increment("notification_read",0);
                        list.get(0).increment("follower_sum",0);
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

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 点击获得该语录的objectId
                TextView t = (TextView) v.findViewById(R.id.user_id);
                // 将该用户的objectId传递给用户详细主页
                Intent it = new Intent(MessageFansActivity.this, UserHomepageActivity.class);
                it.putExtra("objectId", t.getText().toString());
                startActivity(it);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(0, i);
                finish();
            }
        });

    }

}
