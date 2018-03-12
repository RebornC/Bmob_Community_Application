package com.example.yc.saying.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.yc.saying.R;
import com.example.yc.saying.model._User;
import com.example.yc.saying.model.notification;
import com.example.yc.saying.model.user_followers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;


public class MessageAlertActivity extends AppCompatActivity {

    private ImageView back;
    private ListView listView;
    private List<Map<String,Object>> data = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    //查询是否有未读消息，没有则图标为">"，有则图标为红点
    private int icon_1 = R.drawable.ic_keyboard_arrow_right_24dp;
    private int icon_2 = R.drawable.ic_keyboard_arrow_right_24dp;
    private int icon_3 = R.drawable.ic_keyboard_arrow_right_24dp;
    private int icon_4 = R.drawable.ic_keyboard_arrow_right_24dp;

    private _User current_user = BmobUser.getCurrentUser(_User.class);
    private Integer notification_sum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_alert);
        findView();
        initialization();
        clickEvents();
    }

    private void findView() {
        back = (ImageView) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.listview);
        listView.addFooterView(new ViewStub(this));//底部分割线

    }

    public void initialization() {
        //查询系统通知数量
        BmobQuery<notification> query_3 = new BmobQuery<notification>();
        query_3.count(notification.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if(e==null){
                    notification_sum = count;
                    initialization_2();
                }else{
                    //Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void initialization_2() {
        //查询是否有未读消息
        BmobQuery<user_followers> query = new BmobQuery<user_followers>();
        query.addWhereEqualTo("user_id", current_user.getObjectId());
        query.findObjects(new FindListener<user_followers>() {
            @Override
            public void done(List<user_followers> list, BmobException e) {
                if (e == null) {
                    if (list.get(0).getMessage_fans_sum() != list.get(0).getMessage_fans_read()) {
                        //Toast.makeText(getApplicationContext(), "“新粉丝”有未读消息", Toast.LENGTH_SHORT).show();
                        icon_1 = R.drawable.ic_message;
                    }
                    if (list.get(0).getMessage_sayings_sum() != list.get(0).getMessage_sayings_read()) {
                        //Toast.makeText(getApplicationContext(), "“语录喜欢”有未读消息", Toast.LENGTH_SHORT).show();
                        icon_2 = R.drawable.ic_message;
                    }
                    if (list.get(0).getMessage_books_sum() != list.get(0).getMessage_books_read()) {
                        //Toast.makeText(getApplicationContext(), "“笔记喜欢”有未读消息", Toast.LENGTH_SHORT).show();
                        icon_3 = R.drawable.ic_message;
                    }
                    if (list.get(0).getNotification_read() != notification_sum) {
                        //Toast.makeText(getApplicationContext(), "“系统通知”有未读消息", Toast.LENGTH_SHORT).show();
                        icon_4 = R.drawable.ic_message;
                    }
                } else {
                    //Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }
                init_list();
            }
        });
    }

    public void init_list() {
        data.clear();
        //初始化列表
        Integer[] images = {R.drawable.ic_person_add_24dp, R.drawable.ic_love, R.drawable.ic_free_breakfast_24dp, R.drawable.ic_volume_up_24dp};
        String[] message = {"新的粉丝", "Ta收藏了我的语录", "Ta收藏了我的笔记", "系统通知"};
        Integer[] icons = {icon_1, icon_2, icon_3, icon_4};
        for (int i = 0; i < 4; i++) {
            Map<String,Object> temp = new LinkedHashMap<>();
            temp.put("image", images[i]);
            temp.put("message", message[i]);
            temp.put("icon", icons[i]);
            data.add(temp);
        }
        simpleAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.message_alert_item, new String[] {"image","message","icon"}, new int[] {R.id.image, R.id.message, R.id.icon});
        listView.setAdapter(simpleAdapter);
    }

    public void clickEvents() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent it_0 = new Intent(MessageAlertActivity.this, MessageFansActivity.class);
                        startActivityForResult(it_0, 0);
                        break;
                    case 1:
                        Intent it_1 = new Intent(MessageAlertActivity.this, MessageSayingsActivity.class);
                        startActivityForResult(it_1, 1);
                        break;
                    case 2:
                        Intent it_2 = new Intent(MessageAlertActivity.this, MessageBooksActivity.class);
                        startActivityForResult(it_2, 2);
                        break;
                    case 3:
                        Intent it_3 = new Intent(MessageAlertActivity.this, MessageNotificationActivity.class);
                        it_3.putExtra("notification_sum", notification_sum+"");
                        startActivityForResult(it_3, 3);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                icon_1 = R.drawable.ic_keyboard_arrow_right_24dp;
                init_list();
                break;
            case 1:
                icon_2 = R.drawable.ic_keyboard_arrow_right_24dp;
                init_list();
                break;
            case 2:
                icon_3 = R.drawable.ic_keyboard_arrow_right_24dp;
                init_list();
                break;
            case 3:
                icon_4 = R.drawable.ic_keyboard_arrow_right_24dp;
                init_list();
                break;
            default:
                break;
        }
    }

}
