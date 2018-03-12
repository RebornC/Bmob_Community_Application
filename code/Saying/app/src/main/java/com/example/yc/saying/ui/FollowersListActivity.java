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
import com.example.yc.saying.model.user_followers;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yc on 2018/2/8.
 */

public class FollowersListActivity extends AppCompatActivity {
    private String objectId;
    private ImageView back;
    private CircleImageView headView;
    private TextView name;
    private TextView user_id;
    private ListView listview;
    private List<Map<String,Object>> data = new ArrayList<>();
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followers_list);

        Intent it = getIntent();
        objectId = it.getStringExtra("objectId");

        findView();
        initialization();
        clickEvents();

    }

    public void findView() {
        back = (ImageView) findViewById(R.id.back);
        listview = (ListView) findViewById(R.id.focusListview);
    }

    public void initialization() {

        // 查询当前用户的粉丝列表
        user_followers fo = new user_followers();
        fo.setObjectId(objectId);
        BmobQuery<_User> q = new BmobQuery<_User>();
        q.addWhereRelatedTo("followerId", new BmobPointer(fo));
        q.findObjects(new FindListener<_User>() {
            @Override
            public void done(List<_User> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0)
                        Toast.makeText(getApplicationContext(), "当前还未有粉丝，请多发语录攒人气吧~", Toast.LENGTH_SHORT).show();
                    else {
                        for (int i = 0; i < list.size(); i++) {
                            Map<String,Object> temp = new LinkedHashMap<>();
                            temp.put("image", list.get(i).getHeadPortrait().getFileUrl());
                            temp.put("name", list.get(i).getNickName().toString());
                            temp.put("user_id", list.get(i).getObjectId().toString());
                            data.add(temp);
                        }
                        simpleAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.peoplelist_item, new String[] {"image","name","user_id"}, new int[] {R.id.image, R.id.name, R.id.user_id});
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
                    //Toast.makeText(getApplicationContext(), "粉丝列表查询失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*
        BmobQuery<user_followers> query = new BmobQuery<user_followers>();
        query.include("followerId");
        query.getObject(objectId, new QueryListener<user_followers>() {
            @Override
            public void done(user_followers user_followers, BmobException e) {
                if (e == null) {
                    BmobQuery<_User> query2 = new BmobQuery<_User>();
                    query2.addWhereRelatedTo("followerId", new BmobPointer(user_followers));
                    query2.findObjects(new FindListener<_User>() {
                        @Override
                        public void done(List<_User> list, BmobException e) {
                            if (e == null) {
                                if (list.size() == 0)
                                    Toast.makeText(getApplicationContext(), "当前还未有粉丝，请多发语录攒人气吧~", Toast.LENGTH_SHORT).show();
                                else {
                                    for (int i = 0; i < list.size(); i++) {
                                        Map<String,Object> temp = new LinkedHashMap<>();
                                        temp.put("image", list.get(i).getHeadPortrait().getFileUrl());
                                        temp.put("name", list.get(i).getNickName().toString());
                                        temp.put("user_id", list.get(i).getObjectId().toString());
                                        data.add(temp);
                                    }
                                    simpleAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.peoplelist_item, new String[] {"image","name","user_id"}, new int[] {R.id.image, R.id.name, R.id.user_id});
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
                                //Toast.makeText(getApplicationContext(), "粉丝列表查询失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    //Toast.makeText(getApplicationContext(), "粉丝列表查询失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        */

    }

    public void clickEvents() {

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 点击获得该语录的objectId
                TextView t = (TextView) v.findViewById(R.id.user_id);
                // 将该用户的objectId传递给用户详细主页
                Intent it = new Intent(FollowersListActivity.this, UserHomepageActivity.class);
                it.putExtra("objectId", t.getText().toString());
                startActivity(it);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
